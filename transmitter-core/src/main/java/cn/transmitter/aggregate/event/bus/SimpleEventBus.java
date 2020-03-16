package cn.transmitter.aggregate.event.bus;

import cn.transmitter.aggregate.event.message.EventMessage;
import cn.transmitter.aggregate.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static cn.transmitter.aggregate.event.message.GenericEventMessage.asEventMessage;

/**
 * 基本事件总线
 *
 * @author cloud
 */
public class SimpleEventBus implements EventBus {

    private static final Logger log = LoggerFactory.getLogger(SimpleEventBus.class);

    private final ConcurrentMap<Class<?>, List<MessageHandler<? super EventMessage<?>>>> subscriptions =
        new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<?>, List<MessageHandler<? super EventMessage<?>>>> subscriptionsCache =
        new ConcurrentHashMap<>();

    @Override
    public void publish(EventMessage<?> event) {
        List<MessageHandler<? super EventMessage<?>>> handlerList =
            findEventHandler(event.getPayloadType(), event.getPayload());
        for (MessageHandler<? super EventMessage<?>> aHandlerList : handlerList) {
            try {
                aHandlerList.handle(event);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                //throw new EventPublishException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void publishEvent(Object object) {
        publish(asEventMessage(object));
    }

    @Override
    public void subscribe(Class<?> payloadType, MessageHandler<? super EventMessage<?>> handler) {
        subscriptionsCache.clear();
        List<MessageHandler<? super EventMessage<?>>> list =
            subscriptions.getOrDefault(payloadType, new LinkedList<>());
        list.add(handler);
        subscriptions.put(payloadType, list);
    }

    private List<MessageHandler<? super EventMessage<?>>> findEventHandler(Class<?> payloadType, Object payload) {
        if (subscriptionsCache.containsKey(payloadType)) {
            return subscriptionsCache.get(payloadType);
        }
        List<MessageHandler<? super EventMessage<?>>> list = new LinkedList<>();
        subscriptions.forEach((k, v) -> {
            if (k.isInstance(payload)) {
                list.addAll(v);
            }
        });
        subscriptionsCache.put(payloadType, list);
        return list;
    }
}
