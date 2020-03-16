package cn.transmitter.chain;

import cn.transmitter.aggregate.ChainContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cloud
 */
@SuppressWarnings("unchecked")
public class ChainExecutor {

    private static final Logger log = LoggerFactory.getLogger(ChainExecutor.class);

    private static Map<String, List<IBusinessChain>> chainMap = new ConcurrentHashMap<>();

    public static void register(IBusinessChain bean) {
        if (Objects.isNull(bean)) {
            return;
        }
        String beanClass = getBeanClass(bean);

        List<IBusinessChain> list = chainMap.getOrDefault(beanClass, new ArrayList<>());
        if (list.contains(bean)) {
            return;
        }
        list.add(bean);
        Collections.sort(list);
        chainMap.put(beanClass, list);
        chainMap.forEach((k, v) -> v.forEach(
            o -> log.info("待执行业务链, 聚合名：{}, 业务链名：{}, 顺序：{}", k, o.getClass().getName(), o.filterOrder())));
    }

    private static String getBeanClass(IBusinessChain bean) {
        String beanClass = null;
        Class<?> clazz = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType c = (ParameterizedType)type;

            beanClass = c.getActualTypeArguments()[0].getTypeName();
            List<IBusinessChain> list =
                chainMap.getOrDefault(c.getActualTypeArguments()[0].getTypeName(), new ArrayList<>());
            list.add(bean);

            chainMap.put(c.getActualTypeArguments()[0].getTypeName(), list);
        }
        return beanClass;
    }

    public static void execute(ChainContextHolder holder) {
        Class beanClass = holder.getAggregate();
        if (chainMap.containsKey(beanClass.getTypeName())) {
            List<IBusinessChain> list = chainMap.get(beanClass.getTypeName());
            Collections.sort(list);
            for (IBusinessChain commandBizFilter : list) {
                if (commandBizFilter.shouldFilter(holder)) {
                    commandBizFilter.run(holder);
                }
            }
        }
    }
}
