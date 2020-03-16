package cn.transmitter.aggregate.command.gateway;

import cn.transmitter.aggregate.command.CommandCallback;

/**
 * 命令网关
 * 处理命令的转发、命令转发拦截器、命令重试
 *
 * @author cloud
 */
public interface CommandGateway {

    /**
     * 执行命令，并执行回调方法收集结果
     *
     * @param command  命令
     * @param callback 回调方法
     * @param <C>      命令类型
     * @param <R>      返回结果类型
     */
    <C, R> void send(C command, CommandCallback<? super C, R> callback);

    /**
     * 执行命令 等待结构返回
     *
     * @param command   命令
     * @return R 返回结果
     */
    <C, R> R sendAndWait(C command);
}
