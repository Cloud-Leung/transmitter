package cn.transmitter.aggregate.member;

import cn.transmitter.aggregate.event.message.EventMessage;

/**
 *
 * 子成员
 *
 * @author cloud
 */
public interface MemberEntity {

    /**
     * 事件发布
     *
     * @param eventMessage 事件消息
     * @throws Exception
     */
    void pulish(EventMessage<?> eventMessage, Object target) throws Exception;

    boolean canHandle(EventMessage<?> eventMessage);
}
