package cn.transmitter.exception;

/**
 * 找不到命令对应的处理器
 *
 * @author cloud
 */
public class NoCommandHanderException extends TransmitterException {

    public NoCommandHanderException(String message) {
        super(message);
    }

    public NoCommandHanderException(String message, Throwable cause) {
        super(message, cause);
    }

}
