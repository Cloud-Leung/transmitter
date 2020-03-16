package cn.transmitter.exception;

/**
 * 没有找到聚合对应的仓储实现
 *
 * @author cloud
 */
public class NoRepositoryFoundException extends TransmitterException {
    public NoRepositoryFoundException(String message) {
        super(message);
    }

    public NoRepositoryFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
