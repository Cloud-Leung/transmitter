package cn.transmitter.aggregate.command.message;

import cn.transmitter.common.message.Message;

/**
 * 命令消息
 *
 * @author cloud
 */
public interface CommandMessage<T> extends Message<T> {

    /**
     * 获取命令名称
     *
     * @return String
     * @author cloud
     */
    String getCommandName();

}
