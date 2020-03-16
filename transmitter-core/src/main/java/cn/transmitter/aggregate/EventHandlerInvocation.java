package cn.transmitter.aggregate;

import cn.transmitter.aggregate.handler.HandlerInvocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件执行器缓存
 *
 * @author cloud
 */
public class EventHandlerInvocation<T> {

    /**
     * 处理器执行器
     */
    private final Map<Class<?>, HandlerInvocation<T>> eventHandlerInvocations;

    /**
     * 事件处理器执行器缓存
     */
    private Map<Class<?>, List<HandlerInvocation<T>>> eventHandlerInvocationCache = new ConcurrentHashMap<>();

    public EventHandlerInvocation(Map<Class<?>, HandlerInvocation<T>> eventHandlerInvocations) {
        this.eventHandlerInvocations = eventHandlerInvocations;
    }

    /**
     * 查找当前类的事件处理器执行器
     *
     * @param payloadType 事件类型
     * @param payload     事件对象
     * @return 当前事件类对应的事件处理器 支持继承
     */
    public List<HandlerInvocation<T>> findEventInvocation(Class<?> payloadType, Object payload) {
        if (eventHandlerInvocationCache.containsKey(payloadType)) {
            return eventHandlerInvocationCache.get(payloadType);
        }
        //获取list
        List<HandlerInvocation<T>> list = new LinkedList<>();
        eventHandlerInvocations.forEach((k, v) -> {
            if (k.isInstance(payload)) {
                list.add(v);
            }
        });
        eventHandlerInvocationCache.put(payloadType, list);
        return list;
    }
}
