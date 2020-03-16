package cn.transmitter.aggregate.factory;

import cn.transmitter.aggregate.Aggregate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 普通聚合工厂
 *
 * @author cloud
 */
public class SimpleAggregateFactory implements AggregateFactory {

    private final ConcurrentMap<String, Aggregate<?>> aggregateMap = new ConcurrentHashMap<>();

    public SimpleAggregateFactory() {
    }

    @Override
    public Aggregate create(Object target, Class<?> targetType) {
        return aggregateMap.get(targetType.getName()).clone().newInstance(target);
    }

    @Override
    public void register(Aggregate<?> aggregate, Class<?> targetType) {
        aggregateMap.put(targetType.getName(), aggregate);
    }

}
