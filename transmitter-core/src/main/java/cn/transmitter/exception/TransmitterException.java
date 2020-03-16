package cn.transmitter.exception;

/**
 * 异常
 *
 * @author cloud
 */
public class TransmitterException extends RuntimeException {

    public TransmitterException(String message) {
        super(message);
    }

    public TransmitterException(String message, Throwable cause) {
        super(message, cause);
    }
}
