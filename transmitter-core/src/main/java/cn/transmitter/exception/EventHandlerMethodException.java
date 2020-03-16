package cn.transmitter.exception;

/**
 * 命令处理方法参数不正确
 *
 * @author cloud
 */
public class EventHandlerMethodException extends TransmitterException {

    public EventHandlerMethodException(String message) {
        super(message);
    }

    public EventHandlerMethodException(String message, Throwable cause) {
        super(message, cause);
    }

}
