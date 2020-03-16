package cn.transmitter.Initialization.resolve;

import cn.transmitter.aggregate.event.bus.EventBus;
import cn.transmitter.aggregate.event.handler.MethodEventHandler;
import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.AnnotationMethodHandlerInvocation;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import cn.transmitter.annotation.EventHandler;
import cn.transmitter.common.util.ReflectionUtils;
import cn.transmitter.exception.EventHandlerMethodException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 运行时单例bean时间处理器解析
 *
 * @author cloud
 */
public class EventHandlerAnnotationBeanResolver implements BeanResolver {

    private EventBus eventBus;

    private ApplicationContext applicationContext;

    public EventHandlerAnnotationBeanResolver(EventBus eventBus, ApplicationContext applicationContext) {
        this.eventBus = eventBus;
        this.applicationContext = applicationContext;
    }

    @Override
    public void resolveBean(String beanName, Object bean) {
        boolean typeNotOk = checkBeanType(beanName);
        if (typeNotOk) {
            return;
        }
        Iterable<Method> iterable = ReflectionUtils.methodsOf(bean.getClass());
        iterable.forEach(o -> resolveMethod(o, bean));
    }

    boolean checkBeanType(String beanName) {
        return (!applicationContext.containsBean(beanName)) || applicationContext.isPrototype(beanName);
    }

    @SuppressWarnings("unchecked")
    void subscribe(EventHandler eventHandler, Object bean, Class<?> parameterType, HandlerInvocation invocation) {
        MethodEventHandler<? super EventMessage<?>> handler = new MethodEventHandler<>(eventHandler, invocation, bean);
        eventBus.subscribe(parameterType, handler);
    }

    private void resolveMethod(Method o, Object bean) {
        EventHandler eventHandler = o.getAnnotation(EventHandler.class);
        if (Objects.isNull(eventHandler)) {
            return;
        }
        Class<?>[] types = o.getParameterTypes();
        if (types.length != 1) {
            throw new EventHandlerMethodException(
                "EventHandler method" + bean.getClass().getName() + "." + o.getName() +
                " parameter only can be this event, but this is not right!");
        }
        Class<?> parameterType = types[0];
        HandlerInvocation invocation = new AnnotationMethodHandlerInvocation(parameterType, o, EventMessage.class);
        subscribe(eventHandler, bean, parameterType, invocation);
    }
}
