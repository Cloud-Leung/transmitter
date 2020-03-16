package cn.transmitter.common;

import cn.transmitter.common.util.TransmitterSpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 异步执行器
 *
 * @author cloud
 */
public class TransmitterExecutor {

    private static Logger log = LoggerFactory.getLogger(TransmitterExecutor.class);

    private static volatile ExecutorService EXECUTOR_THREAD_POOLS = null;

    private static ThreadObjectTransmission objectTransmission = null;

    //region public methods
    public static synchronized void setup(int nThreads, ThreadObjectTransmission loggerTrace) {
        TransmitterExecutor.objectTransmission = loggerTrace;
        if (Objects.isNull(EXECUTOR_THREAD_POOLS)) {
            EXECUTOR_THREAD_POOLS =
                new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(2000),
                                       (r, executor) -> log.error("当前线程池已满, 任务被抛弃..."));
        }
    }

    public static void executeAsync(Runnable command) {
        checkExecutor();
        Object object  = objectTransmission.getObject();
        EXECUTOR_THREAD_POOLS.execute(() -> {
            try {
                objectTransmission.pass(object);
                command.run();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });

    }

    public static <T> Future<T> submitAsync(Callable<T> c) {
        checkExecutor();
        Object object  = objectTransmission.getObject();
        return EXECUTOR_THREAD_POOLS.submit(() -> {
            try {
                objectTransmission.pass(object);
                return c.call();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        });
    }

    public static ExecutorService getExecutorThreadPools() {
        return EXECUTOR_THREAD_POOLS;
    }

    //endregion

    //region private methods

    private static void checkExecutor() {
        if (Objects.isNull(EXECUTOR_THREAD_POOLS)) {
            setup(5, TransmitterSpringUtil.getBean(ThreadObjectTransmission.class));
        }
    }

    //endregion

}
