package cn.transmitter.autoconfig;

import cn.transmitter.Initialization.resolve.BeanResolver;
import cn.transmitter.aggregate.AggregateEntityFactory;
import cn.transmitter.annotation.Aggregate;
import cn.transmitter.annotation.AggregateEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * 聚合扫描配置
 *
 * @author cloud
 */
public class AggregateScanConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(AggregateScanConfiguration.class);

    private ApplicationContext applicationContext;

    private BeanResolver aggregateAnnotationBeanResolver;

    /**
     * 可能多次收到该事件，但只处理第一次
     */
    private boolean scan = false;

    public AggregateScanConfiguration(ApplicationContext applicationContext,
                                      BeanResolver aggregateAnnotationBeanResolver) {
        this.applicationContext = applicationContext;
        this.aggregateAnnotationBeanResolver = aggregateAnnotationBeanResolver ;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (scan) {
            return;
        }
        scan = true;
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Aggregate.class);
        log.info("扫描到Aggregate注解类数量：" + beans.size());
        beans.forEach((k, v) -> aggregateAnnotationBeanResolver.resolveBean(k, v));
        AggregateEntityFactory.registAggregateEntity(beans);
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(AggregateEntity.class);
        AggregateEntityFactory.registAggregateEntity(beanMap);
        log.info("Transmitter Started Success ! ");

    }
}
