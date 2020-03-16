package cn.transmitter.aggregate;

import cn.transmitter.aggregate.event.message.EventMessage;

/**
 * 聚合
 *
 * @author cloud
 */
public interface Aggregate<T> extends Cloneable {

    /**
     * 事件发布
     *
     * @param eventMessage 事件消息
     * @throws Exception
     */
    void pulish(EventMessage<?> eventMessage) throws Exception;

    /**
     * 创建一个新聚合
     *
     * @param target 目标对象
     * @return
     */
    Aggregate<T> newInstance(Object target);

    /**
     * 克隆一个聚合
     *
     * @return
     */
    Aggregate<T> clone();
}
