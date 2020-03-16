package cn.transmitter.aggregate.member;

import cn.transmitter.Initialization.resolve.MemberResolver;
import cn.transmitter.aggregate.AggregateLifecycle;
import cn.transmitter.aggregate.EventHandlerInvocation;
import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 简单子实体
 *
 * @author cloud
 */
public class SimpleMemberEntity<T> extends AbstractMemberPublisher implements MemberEntity {

    private static final Logger log = LoggerFactory.getLogger(SimpleMemberEntity.class);

    private FieldInvocation<T> fieldInvocation;

    private EventHandlerInvocation<T> eventHandlerInvocation;

    public SimpleMemberEntity(Map<Class<?>, HandlerInvocation<T>> eventHandlerInvocations, Field field,
                              FieldInvocation<T> fieldInvocation, MemberResolver memberResolver) {
        super(memberResolver.resolveBean(field));
        this.eventHandlerInvocation = new EventHandlerInvocation<>(eventHandlerInvocations);
        this.fieldInvocation = fieldInvocation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void pulish(EventMessage<?> eventMessage, Object target) throws Exception {
        if (Objects.isNull(target)) {
            return;
        }
        List<HandlerInvocation<T>> handlerInvocations =
            eventHandlerInvocation.findEventInvocation(eventMessage.getPayloadType(), eventMessage.getPayload());
        boolean canHandle = canHandle(fieldInvocation.getTargetType(), eventMessage, handlerInvocations.size());
        if (!canHandle) {
            log.debug("当前实体{}不能处理该事件{}", target.getClass(), eventMessage.getPayloadType());
            return;
        }
        Object object;
        try {
            AggregateLifecycle.pushStackEntity(new StackEntity(target, this));
            object = fieldInvocation.pulish(eventMessage, target, handlerInvocations);
        } finally {
            AggregateLifecycle.removeStackEntity();
        }
        super.publishToChildren(eventMessage, object);
    }

    @Override
    public boolean canHandle(EventMessage<?> eventMessage) {
        List<HandlerInvocation<T>> handlerInvocations =
            eventHandlerInvocation.findEventInvocation(eventMessage.getPayloadType(), eventMessage.getPayload());
        return handlerInvocations.size() > 0 || super.childEntiriesCanHandle(eventMessage);
    }

}
