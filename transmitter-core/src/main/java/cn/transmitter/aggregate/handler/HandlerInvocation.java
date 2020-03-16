package cn.transmitter.aggregate.handler;

import cn.transmitter.common.message.Message;

/**
 * 处理执行器
 *
 * @author cloud
 */
public interface HandlerInvocation<T> {

    Object handle(Message<?> message, T target) throws Exception;

}
