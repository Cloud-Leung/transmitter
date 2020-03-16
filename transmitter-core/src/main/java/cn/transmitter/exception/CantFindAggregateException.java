package cn.transmitter.exception;

/**
 * 没有identifier属性
 *
 * @author cloud
 */
public class CantFindAggregateException extends TransmitterException {

    public CantFindAggregateException(String message) {
        super(message);
    }

    public CantFindAggregateException(String message, Throwable cause) {
        super(message, cause);
    }
}
