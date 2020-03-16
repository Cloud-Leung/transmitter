package cn.transmitter.exception;

/**
 * 聚合根加载异常
 *
 * @author cloud
 */
public class AggregateBeanLoadException extends RuntimeException {

    public AggregateBeanLoadException(String message) {
        super(message);
    }

    public AggregateBeanLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
