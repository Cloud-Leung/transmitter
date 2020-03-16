package cn.transmitter.aggregate.handler;

import cn.transmitter.aggregate.command.message.CommandMessage;

/**
 * 命令目标解析器
 * 解析命令中标注的维一标识
 *
 * @author cloud
 */
public interface CommandTargetResolver {

    Object resolveTarget(CommandMessage<?> command);
}
