package cn.transmitter.aggregate.member;

import cn.transmitter.aggregate.event.message.EventMessage;

/**
 * 栈实体
 *
 * @author cloud
 */
public class StackEntity {

    private Object target;

    private MemberEntity memberEntity;

    public StackEntity(Object target, MemberEntity memberEntity) {
        this.target = target;
        this.memberEntity = memberEntity;
    }

    public void pulish(EventMessage<?> eventMessage) throws Exception {
        memberEntity.pulish(eventMessage, target);
    }
}
