package cn.transmitter.aggregate.factory;

import cn.transmitter.aggregate.Aggregate;

/**
 * 聚合创建工厂
 *
 * @author cloud
 */
public interface AggregateFactory {

    Aggregate create(Object target, Class<?> targetType);

    void register(Aggregate<?> aggregate, Class<?> targetType);
}
