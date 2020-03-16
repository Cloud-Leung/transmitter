package cn.transmitter.aggregate.event.bus;

import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.MessageHandler;

/**
 * 事件总线
 *
 * @author cloud
 */
public interface EventBus {

    /**
     * 事件发布
     *
     * @param event 事件
     */
    void publish(EventMessage<?> event);

    /**
     * 事件发布
     *
     * @param object 事件对象
     */
    void publishEvent(Object object);

    /**
     * 事件订阅
     *
     * @param payloadType 事件类型
     * @param handler     事件处理器
     */
    void subscribe(Class<?> payloadType, MessageHandler<? super EventMessage<?>> handler);
}
