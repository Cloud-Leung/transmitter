package cn.transmitter.aggregate;

import cn.transmitter.aggregate.factory.AggregateBeanLoadFactory;
import cn.transmitter.aggregate.factory.BeanLoadFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体工厂
 *
 * @author cloud
 */
public class AggregateEntityFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 类型是key value是bean名称
     */
    private static final Map<Class<?>, String> CLASS_FOR_NAME_CACHE = new ConcurrentHashMap<>();

    private static final Map<Class<?>, BeanLoadFactory> CLASS_FOR_BEANLOADFACTORY_CACHE = new ConcurrentHashMap<>();

    /**
     * 创建一个有AggregateEntity标注的实体
     * 实体会将其中的springbean装配好
     *
     * @param clazz 实体类型
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> clazz) {
        if (!CLASS_FOR_NAME_CACHE.containsKey(clazz)) {
            return null;
        }
        return applicationContext.getBean(CLASS_FOR_NAME_CACHE.get(clazz), clazz);
    }

    /**
     * 装载一个Aggregate聚合标注的实体
     * 装载会将类中的spring bean装载好
     *
     * @param object 待装载对象
     * @param <T>    类型
     * @return 转载完成的结果
     */
    public static <T> T load(T object) {
        if (!CLASS_FOR_BEANLOADFACTORY_CACHE.containsKey(object.getClass())) {
            return object;
        }
        return CLASS_FOR_BEANLOADFACTORY_CACHE.get(object.getClass()).load(object);
    }

    /**
     * 注册实体
     *
     * @param beanMap
     */
    public static void registAggregateEntity(Map<String, Object> beanMap) {
        beanMap.forEach(AggregateEntityFactory::registAggregateEntity);
    }

    /**
     * 注册实体
     *
     * @param beanName
     * @param bean
     */
    public static void registAggregateEntity(String beanName, Object bean) {
        CLASS_FOR_NAME_CACHE.put(bean.getClass(), beanName);
        CLASS_FOR_BEANLOADFACTORY_CACHE.put(bean.getClass(), new AggregateBeanLoadFactory(bean));
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AggregateEntityFactory.applicationContext = applicationContext;
    }
}
