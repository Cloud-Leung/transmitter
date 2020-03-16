package cn.transmitter.exception;

/**
 * 聚合异常
 *
 * @author cloud
 */
public class AggregateException extends RuntimeException {

    public AggregateException(String message) {
        super(message);
    }

    public AggregateException(String message, Throwable cause) {
        super(message, cause);
    }

}
