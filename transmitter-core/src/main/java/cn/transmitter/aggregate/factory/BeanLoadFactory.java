package cn.transmitter.aggregate.factory;

/**
 *
 * 为实体类装载Spring bean
 *
 * @author cloud
 */
public interface BeanLoadFactory {

    <T>T load(T t);
}
