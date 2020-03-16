package cn.transmitter.exception;

/**
 * @author cloud
 */
public class MessageHandlerInvocationException extends TransmitterException {

    public MessageHandlerInvocationException(String message) {
        super(message);
    }

    public MessageHandlerInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

}
