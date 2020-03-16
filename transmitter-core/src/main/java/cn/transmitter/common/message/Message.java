package cn.transmitter.common.message;

import java.io.Serializable;

/**
 * 基础消息定义
 * 命令消息和事件消息均继承自该类
 *
 * @author cloud
 */
public interface Message<T> extends Serializable {

    /**
     * 获取消息的标识
     *
     * @return String 消息标识
     * @author cloud
     */
    String getIdentifier();

    /**
     * 获取消息体
     *
     * @return 消息体
     */
    T getPayload();

    /**
     * 消息体类型
     *
     * @return 消息体类型
     */
    Class<T> getPayloadType();
}
