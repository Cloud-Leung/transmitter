package cn.transmitter.exception;

/**
 * 事件发布层级过深
 *
 * @author cloud
 */
public class EventDeepTooLargeException extends TransmitterException {

    public EventDeepTooLargeException(String message) {
        super(message);
    }

    public EventDeepTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }
}
