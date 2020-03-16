package cn.transmitter.aggregate.command.bus;

import cn.transmitter.aggregate.command.CommandCallback;
import cn.transmitter.aggregate.command.message.CommandMessage;
import cn.transmitter.aggregate.handler.MessageHandler;
import cn.transmitter.common.message.MessageDispatchInterceptor;

/**
 * 命令总线
 * 管理命令的注册和分发
 *
 * @author cloud
 */
public interface CommandBus {

    /**
     * 分发命令,并执行回调
     *
     * @param command  命令
     * @param callback 回调
     * @param <C>      命令类型
     * @param <R>      返回结果类型
     */
    <C, R> void dispatch(CommandMessage<C> command, CommandCallback<? super C, R> callback);

    /**
     * 命令处理订阅
     *
     * @param commandName 命令名称
     * @param handler     命令处理
     */
    void subscribe(String commandName, MessageHandler<? super CommandMessage<?>> handler);

    /**
     * 注册消息分发拦截器
     *
     * @param dispatchInterceptor 拦截器
     */
    void registerDispatchInterceptor(MessageDispatchInterceptor<? super CommandMessage<?>> dispatchInterceptor);
}
