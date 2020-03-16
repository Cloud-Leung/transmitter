package cn.transmitter.aggregate.lock;

/**
 * 可加锁的key
 *
 * @author cloud
 */
public interface Lockable {

    String lockKey();

}
