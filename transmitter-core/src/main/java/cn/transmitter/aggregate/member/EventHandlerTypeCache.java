package cn.transmitter.aggregate.member;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据目标类型和事件类型缓存 目标类型对应事件类型能否处理 能处理则为true 否则为false
 *
 * @author cloud
 */
public class EventHandlerTypeCache {

    /**
     * 处理缓存 key为目标类型和事件类型组成的字符串
     */
    private static Map<String, Boolean> EVENT_HANDLER_TYPE_CACHE = new ConcurrentHashMap<>();

    public static Boolean canHandle(Class targetType, Class eventType) {
        String key = createKey(targetType, eventType);
        return EVENT_HANDLER_TYPE_CACHE.get(key);
    }

    public static boolean set(Class targetType, Class eventType, boolean canHandle) {
        String key = createKey(targetType, eventType);
        EVENT_HANDLER_TYPE_CACHE.put(key, canHandle);
        return canHandle;
    }

    private static String createKey(Class targetType, Class eventType) {
        return targetType.getName() + "_" + eventType.getName();
    }
}
