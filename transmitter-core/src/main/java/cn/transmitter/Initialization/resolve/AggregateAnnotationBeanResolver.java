package cn.transmitter.Initialization.resolve;

import cn.transmitter.Initialization.config.TransmitterConfig;
import cn.transmitter.aggregate.SimpleAggregate;
import cn.transmitter.aggregate.command.bus.CommandBus;
import cn.transmitter.aggregate.command.handler.AnnotationCommandTargetResolver;
import cn.transmitter.aggregate.command.handler.CommandMessageHandler;
import cn.transmitter.aggregate.command.handler.ConstructorCommandHandler;
import cn.transmitter.aggregate.command.handler.MethodCommandHandler;
import cn.transmitter.aggregate.command.message.CommandMessage;
import cn.transmitter.aggregate.event.bus.EventBus;
import cn.transmitter.aggregate.factory.AggregateFactory;
import cn.transmitter.aggregate.handler.AnnotationMethodHandlerInvocation;
import cn.transmitter.aggregate.handler.CommandTargetResolver;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import cn.transmitter.aggregate.lock.AggregateLock;
import cn.transmitter.annotation.Aggregate;
import cn.transmitter.annotation.CommandHandler;
import cn.transmitter.common.Repository;
import cn.transmitter.common.util.ReflectionUtils;
import cn.transmitter.exception.CommandHandlerMethodException;
import cn.transmitter.exception.NoRepositoryFoundException;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聚合注解bean解析器
 *
 * @author cloud
 */
public class AggregateAnnotationBeanResolver implements BeanResolver, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private CommandBus commandBus;

    private EventBus eventBus;

    private AggregateFactory aggregateFactory;

    private Map<String, Repository> repositoryMap = new HashMap<>();

    private MemberResolver memberResolver;

    private TransmitterConfig transmitterConfig;

    private AggregateLock aggregateLock;

    public AggregateAnnotationBeanResolver(CommandBus commandBus, EventBus eventBus, AggregateFactory aggregateFactory,
                                           MemberResolver memberResolver, TransmitterConfig transmitterConfig,
                                           AggregateLock aggregateLock) {
        this.commandBus = commandBus;
        this.eventBus = eventBus;
        this.aggregateFactory = aggregateFactory;
        this.memberResolver = memberResolver;
        this.transmitterConfig = transmitterConfig;
        this.aggregateLock = aggregateLock;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void resolveBean(String beanName, Object bean) {
        Aggregate aggregate = bean.getClass().getAnnotation(Aggregate.class);
        if (Objects.isNull(aggregate)) {
            return;
        }
        ConcurrentHashMap<String, HandlerInvocation<?>> commandHandlerInvocations = new ConcurrentHashMap<>();
        Repository repository = repositoryMap.get(bean.getClass().getName());
        if (Objects.isNull(repository)) {
            throw new NoRepositoryFoundException(
                "can't find Repository for this Aggregate : " + bean.getClass().getName());
        }
        // 解析方法
        Iterable<Method> methods = ReflectionUtils.methodsOf(bean.getClass());
        methods.forEach(o -> resolveMethod(o, bean.getClass(), repository, commandHandlerInvocations));

        // 解析聚合 包含聚合内的事件处理器
        cn.transmitter.aggregate.Aggregate simpleAggregate =
            new SimpleAggregate(transmitterConfig, commandHandlerInvocations,
                                this.resolveEventHandlerInvocation(beanName, bean), eventBus, memberResolver,
                                bean.getClass());
        // 将聚合注册到工厂
        aggregateFactory.register(simpleAggregate, bean.getClass());

        // 解析构造器
        Iterable<Constructor<?>> constructors = ReflectionUtils.constructorsof(bean.getClass());
        constructors.forEach(o -> resolveConstructor(o, bean, repository, commandHandlerInvocations));

    }

    private Map<Class<?>, HandlerInvocation<?>> resolveEventHandlerInvocation(String beanName, Object bean) {
        Map<Class<?>, HandlerInvocation<?>> eventHandlerInvocations = new ConcurrentHashMap<>();
        BeanResolver beanResolver =
            new CollectEventHandlerAnnotationBeanResolverWrapper(applicationContext, eventHandlerInvocations);
        beanResolver.resolveBean(beanName, bean);
        return eventHandlerInvocations;
    }

    @SuppressWarnings("unchecked")
    private void resolveConstructor(Constructor<?> o, Object bean, Repository repository,
                                    ConcurrentHashMap<String, HandlerInvocation<?>> commandHandlerInvocations) {
        CommandHandler commandHandler = o.getAnnotation(CommandHandler.class);
        if (Objects.isNull(commandHandler)) {
            return;
        }
        Class<?>[] types = o.getParameterTypes();
        if (types.length != 1) {
            throw new CommandHandlerMethodException(
                "CommandHandler method" + bean.getClass().getName() + "." + o.getName() +
                " parameter only can be this command, but this is not right!");
        }
        Class<?> parameterType = types[0];

        HandlerInvocation invocation = new AnnotationMethodHandlerInvocation(parameterType, o, CommandMessage.class);
        CommandMessageHandler methodCommandHandler =
            new ConstructorCommandHandler<>(repository, invocation, aggregateFactory, aggregateLock);
        commandHandlerInvocations.put(parameterType.getName(), invocation);
        commandBus.subscribe(parameterType.getName(), methodCommandHandler);
    }

    @SuppressWarnings("unchecked")
    private void resolveMethod(Method o, Class beanClass, Repository repository,
                               ConcurrentHashMap<String, HandlerInvocation<?>> commandHandlerInvocations) {
        CommandHandler commandHandler = o.getAnnotation(CommandHandler.class);
        if (Objects.isNull(commandHandler)) {
            return;
        }
        Class<?>[] types = o.getParameterTypes();
        if (types.length != 1) {
            throw new CommandHandlerMethodException("CommandHandler method" + beanClass.getName() + "." + o.getName() +
                                                    " parameter only can be this command, but this is not right!");
        }
        Class<?> parameterType = types[0];
        HandlerInvocation invocation = new AnnotationMethodHandlerInvocation(parameterType, o, CommandMessage.class);
        CommandTargetResolver commandTargetResolver = new AnnotationCommandTargetResolver(parameterType);
        CommandMessageHandler methodCommandHandler =
            new MethodCommandHandler<>(repository, invocation, aggregateFactory, commandTargetResolver, aggregateLock);
        commandHandlerInvocations.put(parameterType.getName(), invocation);
        commandBus.subscribe(parameterType.getName(), methodCommandHandler);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        initRepositorys();
    }

    private void initRepositorys() {
        String[] names = applicationContext.getBeanNamesForType(Repository.class);
        for (String name : names) {
            Repository bean = applicationContext.getBean(name, Repository.class);
            Class<?> clazz = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
            Type[] types = clazz.getGenericInterfaces();
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType c = (ParameterizedType)type;
                    repositoryMap.put(c.getActualTypeArguments()[0].getTypeName(), bean);
                }
            }
        }
    }

    //endregion

    //region private methods
    //endregion

}
