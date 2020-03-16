package cn.transmitter.aggregate.lock;

import cn.transmitter.aggregate.command.message.CommandMessage;
import cn.transmitter.exception.AggregateLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 聚合锁工具
 * 如果命令实现了 {#link Lockable} 接口则会执行加锁逻辑
 * 锁方式需要应用自己实现并提供，默认不加锁
 *
 * @author cloud
 */
public class AggregateLockUtil {

    private static final Logger log = LoggerFactory.getLogger(AggregateLockUtil.class);

    private AggregateLock aggregateLock;

    private CommandMessage<?> message;

    private boolean needLock;

    private AggregateLockResult result;

    public AggregateLockUtil(AggregateLock aggregateLockable, CommandMessage<?> message) {
        this.aggregateLock = aggregateLockable;
        this.message = message;
        this.needLock = message.getPayload() instanceof Lockable;
    }

    public void tryLock() {
        if (!needLock) {
            return;
        }
        Lockable lockable = (Lockable)message.getPayload();
        this.result = aggregateLock.tryLock(lockable);
        if (this.result == null) {
            throw new AggregateLockException(
                "try lock command name " + message.getCommandName() + " failed , identifier is " +
                message.getIdentifier() + ", lock result is null .");
        }
        if (!this.result.isSuccess()) {
            throw new AggregateLockException(
                "try lock command name " + message.getCommandName() + " failed , identifier is " +
                message.getIdentifier() + ", lock result : " + this.result);
        }
    }

    public void release() {
        if (!needLock) {
            return;
        }
        try {
            aggregateLock.release(this.result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
