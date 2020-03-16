package cn.transmitter.aggregate.command.handler;

import cn.transmitter.aggregate.Aggregate;
import cn.transmitter.aggregate.AggregateLifecycle;
import cn.transmitter.aggregate.command.message.CommandMessage;
import cn.transmitter.aggregate.factory.AggregateFactory;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import cn.transmitter.aggregate.lock.AggregateLock;
import cn.transmitter.aggregate.lock.AggregateLockUtil;
import cn.transmitter.aggregate.work.CurrentUnitOfWork;
import cn.transmitter.common.Repository;

/**
 * 构造方法命令处理器
 * 用于创建聚合并持久化
 *
 * @author cloud
 */
public class ConstructorCommandHandler<T> implements CommandMessageHandler {

    private Repository<T> repository;

    private HandlerInvocation<T> handlerInvocation;

    private AggregateFactory aggregateFactory;

    private AggregateLock aggregateLock;

    public ConstructorCommandHandler(Repository<T> repository, HandlerInvocation<T> handlerInvocation,
                                     AggregateFactory aggregateFactory, AggregateLock aggregateLock) {
        this.repository = repository;
        this.handlerInvocation = handlerInvocation;
        this.aggregateFactory = aggregateFactory;
        this.aggregateLock = aggregateLock;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object handle(CommandMessage<?> message) throws Exception {
        // 设置当前工作步骤
        CurrentUnitOfWork.get().initialization();
        Object t;
        Aggregate aggregate = null;
        AggregateLockUtil lockUtil = new AggregateLockUtil(aggregateLock, message);
        try {
            // 加锁
            lockUtil.tryLock();
            // 执行构造方法创建聚合对象
            t = handlerInvocation.handle(message, null);
            // 组装程聚合
            aggregate = aggregateFactory.create(t, t.getClass());
            // 放入当前生命周期
            AggregateLifecycle.put(aggregate);
            // 开始执行后续的事件
            CurrentUnitOfWork.get().ready();
        } finally {
            lockUtil.release();
            AggregateLifecycle.clear(aggregate);
        }
        // 持久化聚合
        repository.create((T)t);
        return t;
    }

}
