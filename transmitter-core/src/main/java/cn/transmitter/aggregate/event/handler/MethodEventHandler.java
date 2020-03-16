package cn.transmitter.aggregate.event.handler;

import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import cn.transmitter.annotation.EventHandler;
import cn.transmitter.common.TransmitterExecutor;
import cn.transmitter.exception.EventPublishException;

/**
 * 事件方法处理器
 *
 * @author cloud
 */
public class MethodEventHandler<T> implements EventMessageHandler {

    private T target;

    private HandlerInvocation<T> invocation;

    private EventHandler eventHandler;

    public MethodEventHandler(EventHandler eventHandler, HandlerInvocation<T> invocation, T target) {
        this.invocation = invocation;
        this.target = target;
        this.eventHandler = eventHandler;
    }

    @Override
    public Object handle(EventMessage<?> message) throws Exception {
        // 没有返回值
        if (eventHandler.async()) {
            TransmitterExecutor.executeAsync(() -> {
                try {
                    invocation.handle(message, target);
                } catch (Exception e) {
                    throw new EventPublishException(e.getMessage(), e);
                }
            });
            return null;
        } else {
            return invocation.handle(message, target);
        }
    }

}
