package cn.transmitter.aggregate.member;

import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.HandlerInvocation;

import java.util.List;

/**
 * @author cloud
 */
public interface FieldInvocation<T> {

    Object pulish(EventMessage<?> eventMessage, Object target, List<HandlerInvocation<T>> handlerInvocations)
        throws Exception;

    Class getTargetType();
}
