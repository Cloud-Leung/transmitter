package cn.transmitter.aggregate.command;

import cn.transmitter.aggregate.command.message.CommandMessage;

/**
 * 命令回调
 *
 * @author cloud
 */
public interface CommandCallback<C, R> {

    /**
     * 成功回调
     *
     * @param commandMessage 命令消息
     * @param result         返回结果
     * @author cloud
     */
    void onSuccess(CommandMessage<? extends C> commandMessage, R result);

    /**
     * 失败回调
     *
     * @param commandMessage 命令消息
     * @param cause          错误
     */
    void onFailure(CommandMessage<? extends C> commandMessage, Throwable cause);

}
