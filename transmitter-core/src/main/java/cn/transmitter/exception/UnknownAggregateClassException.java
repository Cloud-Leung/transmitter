package cn.transmitter.exception;

/**
 * 未知的聚合根实体类
 *
 * @author cloud
 */
public class UnknownAggregateClassException extends TransmitterException {

    public UnknownAggregateClassException(String message) {
        super(message);
    }

    public UnknownAggregateClassException(String message, Throwable cause) {
        super(message, cause);
    }

}
