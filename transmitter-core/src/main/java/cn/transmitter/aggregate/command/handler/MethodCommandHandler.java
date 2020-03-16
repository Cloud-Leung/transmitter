package cn.transmitter.aggregate.command.handler;

import cn.transmitter.aggregate.Aggregate;
import cn.transmitter.aggregate.AggregateLifecycle;
import cn.transmitter.aggregate.command.message.CommandMessage;
import cn.transmitter.aggregate.factory.AggregateFactory;
import cn.transmitter.aggregate.handler.CommandTargetResolver;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import cn.transmitter.aggregate.lock.AggregateLock;
import cn.transmitter.aggregate.lock.AggregateLockUtil;
import cn.transmitter.aggregate.work.CurrentUnitOfWork;
import cn.transmitter.common.Repository;
import cn.transmitter.exception.CantFindAggregateException;

import java.util.Objects;

/**
 * 普通方法命令处理器
 * 用户触发修改聚合的命令
 *
 * @author cloud
 */
public class MethodCommandHandler<T> implements CommandMessageHandler {

    private Repository<T> repository;

    private HandlerInvocation<T> handlerInvocation;

    private AggregateFactory aggregateFactory;

    private CommandTargetResolver commandTargetResolver;

    private AggregateLock aggregateLock;

    public MethodCommandHandler(Repository<T> repository, HandlerInvocation<T> handlerInvocation,
                                AggregateFactory aggregateFactory, CommandTargetResolver commandTargetResolver,
                                AggregateLock aggregateLock) {
        this.repository = repository;
        this.handlerInvocation = handlerInvocation;
        this.aggregateFactory = aggregateFactory;
        this.commandTargetResolver = commandTargetResolver;
        this.aggregateLock = aggregateLock;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object handle(CommandMessage<?> message) throws Exception {
        // 设置聚合初始化
        CurrentUnitOfWork.get().initialization();
        T target;
        Aggregate aggregate = null;
        Object result;
        AggregateLockUtil lockUtil = new AggregateLockUtil(aggregateLock, message);
        try {
            // 重载聚合
            Object identifier = commandTargetResolver.resolveTarget(message);
            // 加锁
            lockUtil.tryLock();
            // 加载聚合
            target = repository.load(identifier);
            if (Objects.isNull(target)) {
                throw new CantFindAggregateException("can't find aggregate by this identifier: " + identifier);
            }
            aggregate = aggregateFactory.create(target, target.getClass());
            // 放入聚合生周期
            AggregateLifecycle.put(aggregate);
            // 准备
            CurrentUnitOfWork.get().ready();
            // 执行结果
            result = handlerInvocation.handle(message, target);
        } finally {
            lockUtil.release();
            AggregateLifecycle.clear(aggregate);
        }
        // 持久化当前聚合
        repository.save(target);
        return result;
    }
}
