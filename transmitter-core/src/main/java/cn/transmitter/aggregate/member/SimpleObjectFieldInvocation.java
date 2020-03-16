package cn.transmitter.aggregate.member;

import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

import static cn.transmitter.common.util.ReflectionUtils.getFieldValue;

/**
 * 简单对象字段执行器
 *
 * @author cloud
 */
public class SimpleObjectFieldInvocation<T> implements FieldInvocation<T> {

    private static final Logger log = LoggerFactory.getLogger(SimpleObjectFieldInvocation.class);

    private Field field;

    private Class targetType;

    public SimpleObjectFieldInvocation(Field field, Class targetType) {
        this.field = field;
        this.targetType = targetType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object pulish(EventMessage<?> eventMessage, Object target, List<HandlerInvocation<T>> handlerInvocations)
        throws Exception {
        List<Object> objests = new ArrayList<>();
        iterator(objests, target);

        List<Object> targets = new ArrayList<>();
        for (Object objest : objests) {
            Optional.ofNullable(handObject(eventMessage, objest, handlerInvocations)).ifPresent(targets::add);
        }
        return targets;
    }

    @Override
    public Class getTargetType() {
        return this.targetType;
    }

    private Object handObject(EventMessage<?> eventMessage, Object target,
                              List<HandlerInvocation<T>> handlerInvocations) throws Exception {
        T object = null;
        try {
            object = getFieldValue(field, target);
        } catch (IllegalStateException e) {
            log.error(e.getMessage(), e);
        }
        if (Objects.isNull(object)) {
            log.warn("目标字段{}值为空, 不执行事件:{}", field.getName(), eventMessage.getPayloadType().getSimpleName());
            return null;
        }
        for (HandlerInvocation<T> invocation : handlerInvocations) {
            invocation.handle(eventMessage, object);
        }
        return object;
    }


    @SuppressWarnings("unchecked")
    private void iterator(List<Object> objects, Object object) {
        if (object instanceof Iterable) {
            Iterable iterable = (Iterable)object;
            iterable.forEach(o -> iterator(objects, o));
        } else if (object instanceof Map) {
            Map map = (Map)object;
            map.forEach((k, v) -> iterator(objects, v));
        } else {
            objects.add(object);
        }
    }
}
