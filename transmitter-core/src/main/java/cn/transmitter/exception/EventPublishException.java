package cn.transmitter.exception;

/**
 * 事件发布异常
 *
 * @author cloud
 */
public class EventPublishException extends TransmitterException {

    public EventPublishException(String message) {
        super(message);
    }

    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
    }

}
