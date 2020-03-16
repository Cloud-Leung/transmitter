package cn.transmitter.aggregate.command.handler;

import cn.transmitter.aggregate.command.message.CommandMessage;
import cn.transmitter.aggregate.handler.MessageHandler;

/**
 * 命令处理器接口
 *
 * @author cloud
 */
public interface CommandMessageHandler extends MessageHandler<CommandMessage<?>> {

}
