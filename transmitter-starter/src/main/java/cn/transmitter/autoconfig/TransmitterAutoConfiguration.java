package cn.transmitter.autoconfig;

import cn.transmitter.Initialization.config.TransmitterConfig;
import cn.transmitter.Initialization.resolve.*;
import cn.transmitter.aggregate.AggregateEntityFactory;
import cn.transmitter.aggregate.command.bus.CommandBus;
import cn.transmitter.aggregate.command.bus.SimpleCommandBus;
import cn.transmitter.aggregate.command.gateway.CommandGateway;
import cn.transmitter.aggregate.command.gateway.DefaultCommandGateway;
import cn.transmitter.aggregate.event.bus.EventBus;
import cn.transmitter.aggregate.event.bus.SimpleEventBus;
import cn.transmitter.aggregate.factory.AggregateFactory;
import cn.transmitter.aggregate.factory.SimpleAggregateFactory;
import cn.transmitter.aggregate.lock.AggregateLock;
import cn.transmitter.aggregate.lock.AggregateLockResult;
import cn.transmitter.aggregate.lock.Lockable;
import cn.transmitter.chain.ChainExecutor;
import cn.transmitter.chain.IBusinessChain;
import cn.transmitter.common.ThreadObjectTransmission;
import cn.transmitter.common.TransmitterExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

/**
 * Transmitter 自动配置
 *
 * @author cloud
 */
@AutoConfigureAfter(value = {ImportBeanDefinitionRegistrar.class})
public class TransmitterAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(TransmitterAutoConfiguration.class);

    @Bean
    @ConfigurationProperties(prefix = "transmitter", ignoreUnknownFields = true)
    public TransmitterConfig transmitterConfig() {
        return new TransmitterConfig();
    }

    @Bean
    public CommandBus commandBus() {
        return new SimpleCommandBus();
    }

    @Bean
    public EventBus eventBus() {
        return new SimpleEventBus();
    }

    @Bean
    public CommandGateway commandGateway(CommandBus commandBus) {
        return new DefaultCommandGateway(commandBus);
    }

    @Bean
    public AggregateFactory aggregateFactory() {
        return new SimpleAggregateFactory();
    }

    @Bean
    public MemberResolver memberResolver() {
        return new AggregateMemberAnnotationBeanResolver();
    }

    @Bean
    @ConditionalOnMissingBean(ThreadObjectTransmission.class)
    public ThreadObjectTransmission loggerTrace() {
        return new ThreadObjectTransmission() {
            @Override
            public Object getObject() {
                return null;
            }

            @Override
            public void pass(Object object) {

            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(AggregateLock.class)
    public AggregateLock aggregateLock() {
        return new AggregateLock() {
            @Override
            public AggregateLockResult tryLock(Lockable command) {
                return new AggregateLockResult(true, command.lockKey(), "");
            }

            @Override
            public void release(AggregateLockResult result) {

            }
        };
    }

    @Bean
    public BeanResolver eventHandlerAnnotationBeanResolver(TransmitterConfig config, EventBus eventBus,
                                                           ApplicationContext applicationContext,
                                                           ThreadObjectTransmission objectTransmission) {
        TransmitterExecutor.setup(config.getThreadPoolSize(), objectTransmission);
        return new EventHandlerAnnotationBeanResolver(eventBus, applicationContext);
    }

    @Bean(name = "aggregateAnnotationBeanResolver")
    public BeanResolver aggregateAnnotationBeanResolver(CommandBus commandBus, EventBus eventBus,
                                                        AggregateFactory aggregateFactory,
                                                        MemberResolver memberResolver,
                                                        TransmitterConfig transmitterConfig,
                                                        AggregateLock aggregateLock) {
        return new AggregateAnnotationBeanResolver(commandBus, eventBus, aggregateFactory, memberResolver,
                                                   transmitterConfig, aggregateLock);
    }

    @Bean
    public AggregateEntityFactory aggregateEntityFactory() {
        return new AggregateEntityFactory();
    }

    @Bean
    public BeanPostProcessor transmitterBeanPostProcessor(
        @Qualifier("eventHandlerAnnotationBeanResolver") BeanResolver eventHandlerAnnotationBeanResolver) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                try {
                    eventHandlerAnnotationBeanResolver.resolveBean(beanName, bean);
                } catch (Exception e) {
                    log.error(bean.getClass().getName() + "," + beanName + "," + e.getMessage(), e);
                }
                return bean;
            }
        };
    }

    @Bean
    public AggregateScanConfiguration aggregateScanConfiguration(ApplicationContext applicationContext,
                                                                 @Qualifier("aggregateAnnotationBeanResolver") BeanResolver aggregateAnnotationBeanResolver) {
        return new AggregateScanConfiguration(applicationContext, aggregateAnnotationBeanResolver);
    }

    @Bean
    public ClassConstrutorReplaceListener classConstrutorReplaceListener() {
        return new ClassConstrutorReplaceListener();
    }

    @Bean
    public BeanPostProcessor transmitterFilterBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                try {
                    if (bean instanceof IBusinessChain) {
                        ChainExecutor.register((IBusinessChain)bean);
                    }
                } catch (Exception e) {
                    log.error(bean.getClass().getName() + "," + beanName + "," + e.getMessage(), e);
                }
                return bean;
            }
        };
    }

}
