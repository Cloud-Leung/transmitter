package cn.transmitter.Initialization.config;

/**
 * transmitter配置
 *
 * @author cloud
 */
public class TransmitterConfig {

    /**
     * 事件的最大调用深度
     * 指在事件中产生事件这种层级深度，深度过深会导致栈或内存溢出，请注意
     */
    private int maxEventDeep = 20;

    /**
     * 事件异步调用的核心线程池大小
     */
    private int threadPoolSize = 5;

    public int getMaxEventDeep() {
        return maxEventDeep;
    }

    public void setMaxEventDeep(int maxEventDeep) {
        this.maxEventDeep = maxEventDeep;
    }

    public int getThreadPoolSize() {
        return threadPoolSize == 0 ? 5 : threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
}
