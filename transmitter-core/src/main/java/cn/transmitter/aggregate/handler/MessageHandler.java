package cn.transmitter.aggregate.handler;

import cn.transmitter.common.message.Message;

/**
 * 消息处理器
 *
 * @author cloud
 */
public interface MessageHandler<T extends Message<?>> {

    /**
     * 处理消息
     *
     * @param message
     * @return
     * @throws Exception
     */
    Object handle(T message) throws Exception;
}
