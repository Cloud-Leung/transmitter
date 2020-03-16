package cn.transmitter.aggregate.member;

import cn.transmitter.aggregate.event.message.EventMessage;

import java.util.List;
import java.util.Objects;

/**
 * 成员实体事件发布类
 *
 * @author cloud
 */
public abstract class AbstractMemberPublisher {

    private List<MemberEntity> childEntities;

    protected AbstractMemberPublisher(List<MemberEntity> childEntities) {
        this.childEntities = childEntities;
    }

    protected void publishToChildren(EventMessage<?> eventMessage, Object target) throws Exception {
        for (MemberEntity entity : childEntities) {
            entity.pulish(eventMessage, target);
        }
    }

    /**
     * 判断当前实体否能处理该消息
     * 判断两部分 一部分当前实体本身是否能处理该事件
     * 另一部分是该实体的子实体能否处理该事件
     *
     * @param eventMessage 事件消息
     * @return
     */
    protected boolean canHandle(Class targetType, EventMessage<?> eventMessage, int handlerInvocationSize) {
        Boolean canHandle = EventHandlerTypeCache.canHandle(targetType, eventMessage.getPayloadType());
        if (Objects.nonNull(canHandle)) {
            return canHandle;
        }
        canHandle = handlerInvocationSize > 0 || childEntities.stream().anyMatch(o -> o.canHandle(eventMessage));
        return EventHandlerTypeCache.set(targetType, eventMessage.getPayloadType(), canHandle);
    }

    /**
     * 判断当前实体是否能处理该事件
     *
     * @param eventMessage 事件消息
     * @return true 能处理
     */
    boolean childEntiriesCanHandle(EventMessage<?> eventMessage) {
        return childEntities.stream().anyMatch(o -> o.canHandle(eventMessage));
    }
}
