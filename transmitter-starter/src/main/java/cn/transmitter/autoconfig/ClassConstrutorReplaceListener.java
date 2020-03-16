package cn.transmitter.autoconfig;

import javassist.ClassClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;

import java.util.Objects;
import java.util.Set;

/**
 * 替换构造方法 增加依赖注入
 *
 * @author cloud
 */
public class ClassConstrutorReplaceListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ClassConstrutorReplaceListener.class);

    @SuppressWarnings("unchecked")
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        log.info("开始扫描并替换聚合实体类");
        Class<?> clazz = event.getSpringApplication().getMainApplicationClass();
        ComponentScan scan = clazz.getAnnotation(ComponentScan.class);
        Set<Class<?>> classSet;
        ConstructorReplace.addClassPath(new ClassClassPath(clazz));
        if (Objects.isNull(scan)) {
            classSet = ClassTools.scan(clazz.getPackage().getName());
        } else {
            classSet = ClassTools.scan(scan.basePackages());
        }
        classSet.forEach(o -> log.info("msg1=扫描到聚合实体类：{}", o.getName()));
    }

}
