package cn.transmitter.exception;

/**
 * 命令处理方法参数不正确
 *
 * @author cloud
 */
public class CommandHandlerMethodException extends TransmitterException {

    public CommandHandlerMethodException(String message) {
        super(message);
    }

    public CommandHandlerMethodException(String message, Throwable cause) {
        super(message, cause);
    }

}
