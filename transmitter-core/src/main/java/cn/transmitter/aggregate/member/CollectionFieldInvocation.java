package cn.transmitter.aggregate.member;

import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import cn.transmitter.exception.EventPublishException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

import static cn.transmitter.common.util.ReflectionUtils.getFieldValue;

/**
 * 简单对象字段执行器
 *
 * @author cloud
 */
public class CollectionFieldInvocation<T> implements FieldInvocation<T> {

    private Field field;

    private Class targetType;

    public CollectionFieldInvocation(Field field, Class targetType) {
        this.field = field;
        this.targetType = targetType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object pulish(EventMessage<?> eventMessage, Object target, List<HandlerInvocation<T>> handlerInvocations)
        throws Exception {
        if (Objects.isNull(target)) {
            return null;
        }
        // 构造执行方法
        Consumer<T> consumer = execute(eventMessage, handlerInvocations);
        List<Object> objects = new ArrayList<>();
        if (target instanceof List) {
            List<Object> list = (List<Object>)target;
            list.forEach(o -> execute(consumer, objects, o));
        } else {
            // 获取字段值
            execute(consumer, objects, target);
        }
        return objects;
    }

    @Override
    public Class getTargetType() {
        return this.targetType;
    }

    private void execute(Consumer<T> consumer, List<Object> objects, Object targetObject) {
        // 获取字段值
        Object object = getFieldValue(field, targetObject);
        // 解析遍历字段
        iterator(consumer, object);
        objects.add(object);
    }

    @SuppressWarnings("unchecked")
    private void iterator(Consumer<T> consumer, Object object) {
        if (object instanceof Iterable) {
            Iterable iterable = (Iterable)object;
            iterable.forEach(o -> iterator(consumer, o));
        } else if (object instanceof Map) {
            Map map = (Map)object;
            map.forEach((k, v) -> iterator(consumer, v));
        } else {
            consumer.accept((T)object);
        }
    }

    private Consumer<T> execute(EventMessage<?> eventMessage, List<HandlerInvocation<T>> handlerInvocations) {
        return a -> Optional.ofNullable(a).ifPresent(p -> foreach(eventMessage, handlerInvocations, p));
    }

    private void foreach(EventMessage<?> eventMessage, List<HandlerInvocation<T>> handlerInvocations, T a) {
        for (HandlerInvocation<T> invocation : handlerInvocations) {
            try {
                invocation.handle(eventMessage, a);
            } catch (Exception e) {
                throw new EventPublishException(e.getMessage(), e);
            }
        }
    }
}
