package cn.transmitter.aggregate.event.handler;

import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.MessageHandler;

/**
 * 事件消息处理器
 *
 * @author cloud
 */
public interface EventMessageHandler extends MessageHandler<EventMessage<?>> {

}
