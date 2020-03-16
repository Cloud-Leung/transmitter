package cn.transmitter.aggregate.lock;

/**
 * 聚合锁操作
 *
 * @author cloud
 */
public interface AggregateLock {

    /**
     * 对聚合加锁
     *
     * @param command 可加锁的命令命令
     * @return 加锁结果 结果会作为释放锁的参数
     */
    AggregateLockResult tryLock(Lockable command);

    /**
     * 释放锁
     *
     * @param result 加锁结果
     */
    void release(AggregateLockResult result);
}
