package cn.transmitter.aggregate;

import cn.transmitter.Initialization.config.TransmitterConfig;
import cn.transmitter.Initialization.resolve.MemberResolver;
import cn.transmitter.aggregate.event.bus.EventBus;
import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import cn.transmitter.aggregate.member.AbstractMemberPublisher;
import cn.transmitter.aggregate.member.StackEntity;
import cn.transmitter.exception.UnknownAggregateClassException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 聚合
 *
 * @author cloud
 */
public class SimpleAggregate<T> extends AbstractMemberPublisher implements Aggregate<T> {

    private static final Logger log = LoggerFactory.getLogger(SimpleAggregate.class);

    private T target;

    private EventBus eventBus;

    private EventHandlerInvocation<T> eventHandlerInvocation;

    private TransmitterConfig transmitterConfig;

    private final Map<String, HandlerInvocation<T>> commandHandlerInvocations;

    public SimpleAggregate(TransmitterConfig transmitterConfig, Map<String, HandlerInvocation<T>> commandHandlerInvocations,
                           Map<Class<?>, HandlerInvocation<T>> eventHandlerInvocations, EventBus eventBus,
                           MemberResolver memberResolver, Class<?> targetType) {
        super(memberResolver.resolveBean(targetType));
        this.eventHandlerInvocation = new EventHandlerInvocation<>(eventHandlerInvocations);
        this.commandHandlerInvocations = commandHandlerInvocations;
        this.eventBus = eventBus;
        this.transmitterConfig = transmitterConfig;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Aggregate<T> newInstance(Object target) {
        this.target = (T)target;
        return this;
    }

    @Override
    public void pulish(EventMessage<?> eventMessage) throws Exception {
        StackEntity entity = AggregateLifecycle.peekStackEntity();
        if (Objects.isNull(entity)) {
            publishIfNoStack(eventMessage);
        } else {
            AggregateLifecycle.checkEventDeepSize(eventMessage, transmitterConfig.getMaxEventDeep());
            entity.pulish(eventMessage);
        }
        // 向外部注册者发布事件
        eventBus.publish(eventMessage);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Aggregate<T> clone() {
        try {
            return (Aggregate<T>)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnknownAggregateClassException(e.getMessage());
        }
    }

    private void publishIfNoStack(EventMessage<?> eventMessage) throws Exception {
        List<HandlerInvocation<T>> invocations =
            eventHandlerInvocation.findEventInvocation(eventMessage.getPayloadType(), eventMessage.getPayload());
        boolean canHandle = canHandle(target.getClass(), eventMessage, invocations.size());
        if (!canHandle) {
            log.debug("当前实体{}不能处理该事件{}", target.getClass(), eventMessage.getPayloadType());
            return;
        }
        // 向本实体发布事件
        for (HandlerInvocation<T> invocation : invocations) {
            invocation.handle(eventMessage, target);
        }
        // 向子实体发布事件
        super.publishToChildren(eventMessage, target);
    }

}
