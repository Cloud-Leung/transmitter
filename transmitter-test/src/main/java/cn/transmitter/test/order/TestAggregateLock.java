package cn.transmitter.test.order;

import cn.transmitter.aggregate.lock.AggregateLock;
import cn.transmitter.aggregate.lock.AggregateLockResult;
import cn.transmitter.aggregate.lock.Lockable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 日志跟踪器
 *
 * @author cloud
 */
@Component
public class TestAggregateLock implements AggregateLock {

    private static final String CROWD_LOCK_ = "CROWD_LOCK_";

    private static final Logger log = LoggerFactory.getLogger(TestAggregateLock.class);

    @Override
    public AggregateLockResult tryLock(Lockable command) {
        log.info("收到加锁请求:{}, {}", command.getClass().getName(), command.lockKey());
        return new AggregateLockResult(true, command.lockKey(), "123");
    }

    @Override
    public void release(AggregateLockResult result) {
        log.info("收到释放请求:" + result);
    }
}
