package cn.transmitter.Initialization.resolve;

import cn.transmitter.aggregate.handler.HandlerInvocation;
import cn.transmitter.annotation.EventHandler;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * bean解析装饰器，用于收集bean中被解析出的事件处理器
 *
 * @author cloud
 */
public class CollectEventHandlerAnnotationBeanResolverWrapper extends EventHandlerAnnotationBeanResolver {

    private Map<Class<?>, HandlerInvocation<?>> eventHandlerInvocations;

    public CollectEventHandlerAnnotationBeanResolverWrapper(ApplicationContext applicationContext,
                                                            Map<Class<?>, HandlerInvocation<?>> eventHandlerInvocations) {
        super(null, applicationContext);
        this.eventHandlerInvocations = eventHandlerInvocations;
    }
    //endregion

    //region public methods

    @Override
    boolean checkBeanType(String beanName) {
        return false;
    }

    @Override
    void subscribe(EventHandler eventHandler, Object bean, Class<?> parameterType, HandlerInvocation invocation) {
        eventHandlerInvocations.put(parameterType, invocation);
    }
}
