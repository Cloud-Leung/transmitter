package cn.transmitter.aggregate.command.gateway;

import cn.transmitter.aggregate.command.CommandCallback;
import cn.transmitter.aggregate.command.DefaultCallback;
import cn.transmitter.aggregate.command.bus.CommandBus;
import cn.transmitter.aggregate.command.message.GenericCommandMessage;

/**
 * 命令网关
 */
public class DefaultCommandGateway implements CommandGateway {

    private CommandBus commandBus;

    public DefaultCommandGateway(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public <C, R> void send(C command, CommandCallback<? super C, R> callback) {
        commandBus.dispatch(GenericCommandMessage.asCommandMessage(command), callback);
    }

    @Override
    public <C, R> R sendAndWait(C command) {
        DefaultCallback<C, R> callback = new DefaultCallback<>();
        send(command, callback);
        return callback.getResult();
    }

}
