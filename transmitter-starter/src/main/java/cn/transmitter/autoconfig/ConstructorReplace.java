package cn.transmitter.autoconfig;

import cn.transmitter.annotation.Aggregate;
import cn.transmitter.annotation.AggregateEntity;
import cn.transmitter.exception.ConstructorReplaceException;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author cloud
 */
public class ConstructorReplace {

    private static final Logger log = LoggerFactory.getLogger(ConstructorReplace.class);

    private static ClassPool pool = ClassPool.getDefault();

    /**
     * 被修改过的class
     */
    private static final Map<String, Class<?>> CLASS_FOR_NAME_CACHE = new ConcurrentHashMap<>();

    public static void addClassPath(ClassPath classPath) {
        pool.insertClassPath(classPath);
    }

    public static Class<?> loadReplace(String name) throws NotFoundException {
        return replaceConstructor(pool.get(name), name);
    }

    private static Class<?> replaceConstructor(CtClass ctClass, String name) {
        try {
            Object[] annotations = ctClass.getAnnotations();
            if (!isAggregate(annotations)) {
                return null;
            }
            CtConstructor[] constructors = ctClass.getConstructors();
            for (CtConstructor constructor : constructors) {
                constructor.insertBeforeBody("cn.transmitter.aggregate.AggregateEntityFactory.load(this);");
            }
            return ctClass.toClass();
        } catch (CannotCompileException | ClassNotFoundException e) {
            throw new ConstructorReplaceException(e.getMessage());
        } finally {
            ctClass.detach();
        }
    }

    private static boolean isAggregate(Object[] objects) {
        if (objects.length < 1) {
            return false;
        }
        for (Object object : objects) {
            if (object instanceof Aggregate || object instanceof AggregateEntity) {
                return true;
            }
        }
        return false;
    }

}
