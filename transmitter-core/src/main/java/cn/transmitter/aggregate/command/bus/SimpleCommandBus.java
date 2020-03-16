package cn.transmitter.aggregate.command.bus;

import cn.transmitter.aggregate.command.CommandCallback;
import cn.transmitter.aggregate.command.message.CommandMessage;
import cn.transmitter.aggregate.handler.MessageHandler;
import cn.transmitter.common.message.MessageDispatchInterceptor;
import cn.transmitter.exception.NoCommandHanderException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 简单命令总线
 *
 * @author cloud
 */
public class SimpleCommandBus implements CommandBus {

    /**
     * 订阅管理 命令只能一对一订阅 管理命令处理器
     * key 命令名称 value 命令处理器
     */
    private final ConcurrentMap<String, MessageHandler<? super CommandMessage<?>>> subscriptions =
        new ConcurrentHashMap<>();

    /**
     * 消息分发拦截器
     */
    private final List<MessageDispatchInterceptor<? super CommandMessage<?>>> dispatchInterceptors = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    public <C, R> void dispatch(CommandMessage<C> command, CommandCallback<? super C, R> callback) {
        MessageHandler<? super CommandMessage<?>> handler = findCommandHander(command.getCommandName());
        CommandMessage<C> commandToDispatch = this.dispatchIntercept(command);
        try {
            Object o = handler.handle(commandToDispatch);
            callback.onSuccess(commandToDispatch, (R)o);
        } catch (Exception e) {
            callback.onFailure(commandToDispatch, e);
        }
    }

    @Override
    public void subscribe(String commandName, MessageHandler<? super CommandMessage<?>> handler) {
        subscriptions.put(commandName, handler);
    }

    @Override
    public void registerDispatchInterceptor(MessageDispatchInterceptor<? super CommandMessage<?>> dispatchInterceptor) {
        dispatchInterceptors.add(dispatchInterceptor);
    }

    private MessageHandler<? super CommandMessage<?>> findCommandHander(String commandName) {
        MessageHandler<? super CommandMessage<?>> handler = subscriptions.get(commandName);
        if (Objects.isNull(handler)) {
            throw new NoCommandHanderException("can't find command handler for command: " + commandName);
        }
        return handler;
    }

    @SuppressWarnings("unchecked")
    private <C> CommandMessage<C> dispatchIntercept(CommandMessage<C> command) {
        CommandMessage<C> commandToDispatch = command;
        for (MessageDispatchInterceptor<? super CommandMessage<?>> interceptor : dispatchInterceptors) {
            commandToDispatch = (CommandMessage<C>)interceptor.handle(commandToDispatch);
        }
        return commandToDispatch;
    }

}
