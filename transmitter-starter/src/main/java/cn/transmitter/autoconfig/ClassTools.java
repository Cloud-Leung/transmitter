package cn.transmitter.autoconfig;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类遍历工具
 * Created by liang.q on 2017/11/2.
 */
public class ClassTools {

    private static final Logger log = LoggerFactory.getLogger(ClassTools.class);

    private static final String PROTOCOL_FILE = "file";

    private static final String PROTOCOL_JAR = "jar";

    public static Set<Class<?>> scan(String[] packs) {
        Set<Class<?>> set = new HashSet<>();
        for (String pack : packs) {
            set.addAll(scan(pack));
        }
        return set;
    }

    public static Set<Class<?>> scan(String pack) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 是否循环迭代
        boolean recursive = true;
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            recursive(dirs, packageName, recursive, classes, packageDirName);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return classes;
    }

    private static void recursive(Enumeration<URL> dirs, String packageName, boolean recursive, Set<Class<?>> classes,
                                  String packageDirName) throws UnsupportedEncodingException {
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            // 如果是以文件的形式保存在服务器上
            if (PROTOCOL_FILE.equals(protocol)) {
                // 获取包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                // 以文件的方式扫描整个包下的文件 并添加到集合中
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
            } else if (PROTOCOL_JAR.equals(protocol)) {
                findAndAddClassesInPackageByJar(url, packageDirName, packageName, recursive, classes);
            }
        }
    }

    private static void findAndAddClassesInPackageByJar(URL url, String packageDirName, String packageName,
                                                        boolean recursive, Set<Class<?>> classes) {
        JarFile jar;
        try {
            jar = ((JarURLConnection)url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.charAt(0) == '/') {
                    name = name.substring(1);
                }
                // 如果前半部分和定义的包名相同
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    // 如果以"/"结尾 是一个包
                    if (idx != -1) {
                        packageName = name.substring(0, idx).replace('/', '.');
                    }
                    if ((idx != -1 || recursive) && name.endsWith(".class") && !entry.isDirectory()) {
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        addClasses(classes, packageName, className);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                         final boolean recursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles =
            dir.listFiles((File file) -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        if (null == dirfiles || dirfiles.length == 0) {
            return;
        }
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                                                 classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                addClasses(classes, packageName, className);

            }
        }
    }

    private static void addClasses(Set<Class<?>> classes, String packageName, String className) {
        try {
            //classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
            Class<?> clazz = ConstructorReplace.loadReplace(packageName + '.' + className);
            Optional.ofNullable(clazz).ifPresent(classes::add);
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }
}
