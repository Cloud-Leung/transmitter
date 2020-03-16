package cn.transmitter.aggregate;

import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.member.StackEntity;
import cn.transmitter.aggregate.work.CurrentUnitOfWork;
import cn.transmitter.aggregate.work.DefaultUnitOfWork;
import cn.transmitter.common.stack.DequeStack;
import cn.transmitter.common.stack.Stack;
import cn.transmitter.exception.EventDeepTooLargeException;
import cn.transmitter.exception.EventPublishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.transmitter.aggregate.event.message.GenericEventMessage.asEventMessage;

/**
 * 执行事件时需要获取当前线程的聚合
 *
 * @author cloud
 */
public class AggregateLifecycle {

    private static final Logger log = LoggerFactory.getLogger(AggregateLifecycle.class);

    private static final ThreadLocal<AggregateLifecycle> CURRENT = new ThreadLocal<>();

    /**
     * 聚合的栈结构
     */
    private Stack<AggregateModel> stackAggregate;

    private AggregateLifecycle(Aggregate aggregate) {
        this.stackAggregate = new DequeStack<>();
        stackAggregate.push(new AggregateModel(aggregate));
    }

    /**
     * 把当前聚合压入栈顶
     *
     * @param aggregate 聚合
     */
    public static void put(Aggregate aggregate) {
        if (Objects.isNull(CURRENT.get())) {
            CURRENT.set(new AggregateLifecycle(aggregate));
        } else {
            get().stackAggregate.push(new AggregateModel(aggregate));
        }
    }

    /**
     * 获取当前聚合生命周期
     *
     * @return
     */
    public static AggregateLifecycle get() {
        return CURRENT.get();
    }

    /**
     * 接受一个事件
     * 注意接受的事件只所有所有单例bean和当前栈顶聚合内生效
     * <p>
     * **事件发送必须在聚合的生命周期内，在聚合生命周期外不能发布事件**
     *
     * @param payload 事件
     */
    public static void apply(Object payload) {
        doApply(asEventMessage(payload));
    }

    /**
     * 移除栈顶的聚合
     * 同时也会移除栈顶状态
     */
    public static void clear(Aggregate aggregate) {
        CurrentUnitOfWork.remove();
        if (Objects.nonNull(aggregate)) {
            if (get().stackAggregate.peek().aggregate == aggregate) {
                get().stackAggregate.remove();
            }
            if (get().stackAggregate.isEmpty()) {
                CURRENT.remove();
            }
        }
    }

    /**
     * 将事件封装成工作添加到当前工作中 如果当前聚合尚未初始化完成，
     * 会暂存在队列中，等到聚合初始化完成再逐一执行事件，这种情况主要针对通过构造函数接受命令并发布事件的情况
     *
     * @param message 消息
     */
    private static void doApply(EventMessage<?> message) {
        CurrentUnitOfWork work = CurrentUnitOfWork.get();
        work.add(new DefaultUnitOfWork(() -> publish(message)));
    }

    /**
     * 向栈顶聚合发布事件
     *
     * @param payload
     */
    @SuppressWarnings("unchecked")
    private static void publish(EventMessage<?> payload) {
        try {
            AggregateModel aggregateModel = get().stackAggregate.peek();
            aggregateModel.aggregate.pulish(payload);
        } catch (Exception e) {
            throw new EventPublishException(e.getMessage(), e);
        }
    }

    /**
     * 向当前聚合压入栈实体
     *
     * @param stackEntity 栈实体
     */
    public static void pushStackEntity(StackEntity stackEntity) {
        AggregateModel aggregateModel = get().stackAggregate.peek();
        aggregateModel.entityStack.push(stackEntity);
    }

    /**
     * 移除栈顶的栈实体
     */
    public static void removeStackEntity() {
        AggregateModel aggregateModel = get().stackAggregate.peek();
        aggregateModel.entityStack.remove();
    }

    /**
     * 获得栈顶的实体
     *
     * @return 栈实体
     */
    public static StackEntity peekStackEntity() {
        AggregateModel aggregateModel = get().stackAggregate.peek();
        return aggregateModel.entityStack.peek();
    }

    public static void checkEventDeepSize(EventMessage<?> eventMessage, int eventDeepSize) {
        AggregateModel aggregateModel = get().stackAggregate.peek();
        if (aggregateModel.entityStack.size() > eventDeepSize) {
            String errMsg = "current event deep size is too large: " + aggregateModel.entityStack.size() +
                            ", this event message type is " + eventMessage.getPayloadType() +
                            ", maybe they are in a loop, please check it!";
            log.error(errMsg);
            throw new EventDeepTooLargeException(errMsg);
        }
    }

    /**
     * 聚合模型，保存当前运行期的聚合和聚合执行的实体栈
     */
    private static class AggregateModel {

        private Aggregate aggregate;

        private Stack<StackEntity> entityStack;

        private AggregateModel(Aggregate aggregate) {
            this.aggregate = aggregate;
            this.entityStack = new DequeStack<>();
        }
    }
    //endregion

}
