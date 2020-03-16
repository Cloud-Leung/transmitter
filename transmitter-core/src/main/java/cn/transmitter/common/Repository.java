package cn.transmitter.common;

/**
 * 仓库
 *
 * @param <T> The type of aggregate this repository stores.
 */
public interface Repository<T> {

    T load(Object aggregateIdentifier);

    T create(T object);

    void save(T object);
}
