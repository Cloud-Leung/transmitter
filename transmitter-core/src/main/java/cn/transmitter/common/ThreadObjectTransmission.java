package cn.transmitter.common;

/**
 * 线程信息透传
 * 可将当前线程信息透传到子线程
 * 例如透传ThreadLocal 或者透传日志跟踪信息
 *
 * @author cloud
 */
public interface ThreadObjectTransmission {

    /**
     * 获取待透传的对象
     *
     * @return
     */
    Object getObject();

    /**
     * 透传数据
     *
     * @param object
     */
    void pass(Object object);

}
