package cn.transmitter.exception;

/**
 * 没有identifier属性
 *
 * @author cloud
 */
public class NoIdentifierException extends TransmitterException {

    public NoIdentifierException(String message) {
        super(message);
    }

    public NoIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }
}
