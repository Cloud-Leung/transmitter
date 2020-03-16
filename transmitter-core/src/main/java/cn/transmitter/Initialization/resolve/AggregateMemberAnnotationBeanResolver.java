package cn.transmitter.Initialization.resolve;

import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.AnnotationMethodHandlerInvocation;
import cn.transmitter.aggregate.handler.HandlerInvocation;
import cn.transmitter.aggregate.member.*;
import cn.transmitter.annotation.AggregateMember;
import cn.transmitter.annotation.EventHandler;
import cn.transmitter.common.util.ReflectionUtils;
import cn.transmitter.exception.EventHandlerMethodException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 子成员解析器
 *
 * @author cloud
 */
public class AggregateMemberAnnotationBeanResolver implements MemberResolver {

    @Override
    public List<MemberEntity> resolveBean(Field type) {
        return resolveBean(findTargetType(type));
    }

    @Override
    public List<MemberEntity> resolveBean(Class targetType) {
        Iterable<Field> fields = ReflectionUtils.fieldsOf(targetType);
        List<MemberEntity> childEntities = new ArrayList<>();
        fields.forEach(o -> {
            AggregateMember aggregateMember = o.getAnnotation(AggregateMember.class);
            Optional.ofNullable(aggregateMember).ifPresent(p -> {
                Class clazz = findTargetType(o);
                resolveEventHandler(childEntities, o, clazz);
            });
        });
        return childEntities;
    }

    private MemberEntity resolveEventHandler(List<MemberEntity> childEntities, Field field, Class clazz) {
        Iterable<Method> iterable = ReflectionUtils.methodsOf(clazz);
        Map<Class<?>, HandlerInvocation<?>> eventHandlerInvocations = new ConcurrentHashMap<>();
        iterable.forEach(o -> resolveInvocation(o, clazz, eventHandlerInvocations));
        MemberEntity childEntity = this.buildChildEntity(field, clazz, eventHandlerInvocations);
        childEntities.add(childEntity);
        return childEntity;
    }

    @SuppressWarnings("unchecked")
    private MemberEntity buildChildEntity(Field field, Class clazz,
                                          Map<Class<?>, HandlerInvocation<?>> eventHandlerInvocations) {
        FieldInvocation<?> fieldInvocation;
        if (Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType())) {
            fieldInvocation = new CollectionFieldInvocation<>(field, clazz);
        } else {
            fieldInvocation = new SimpleObjectFieldInvocation<>(field, clazz);
        }
        return new SimpleMemberEntity(eventHandlerInvocations, field, fieldInvocation, this);
    }

    private void resolveInvocation(Method method, Class clazz,
                                   Map<Class<?>, HandlerInvocation<?>> eventHandlerInvocations) {
        EventHandler eventHandler = method.getAnnotation(EventHandler.class);
        if (Objects.isNull(eventHandler)) {
            return;
        }
        Class<?>[] types = method.getParameterTypes();
        if (types.length != 1) {
            throw new EventHandlerMethodException(
                "CommandHandler method" + clazz.getName() + "." + method.getName() +
                " parameter only can be this command, but this is not right!");
        }
        Class<?> parameterType = types[0];
        HandlerInvocation<?> handlerInvocation =
            new AnnotationMethodHandlerInvocation(parameterType, method, EventMessage.class);
        eventHandlerInvocations.put(parameterType, handlerInvocation);
    }

    private static Class findTargetType(Field o) {
        ParameterizedType parameterizedType = resolveParameterizedType(o);
        if (Objects.isNull(parameterizedType)) {
            return o.getType();
        }
        return findTargetType(resolveGenricType(o.getType(), parameterizedType));
    }

    private static Class findTargetType(Type o) {
        if (o instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)o;
            Type type = resolveGenricType((Class)parameterizedType.getRawType(), parameterizedType);
            return findTargetType(type);
        }
        return (Class)o;
    }

    private static ParameterizedType resolveParameterizedType(Field field) {
        if (Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType())) {
            return (ParameterizedType)field.getGenericType();
        }
        return null;
    }

    private static Type resolveGenricType(Class type, ParameterizedType parameterizedType) {
        if (Collection.class.isAssignableFrom(type)) {
            return parameterizedType.getActualTypeArguments()[0];
        } else if (Map.class.isAssignableFrom(type)) {
            return parameterizedType.getActualTypeArguments()[1];
        }
        return type;
    }
}
