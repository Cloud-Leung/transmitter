/*
 * Copyright (c) 2010-2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.transmitter.aggregate.event.message;

import cn.transmitter.common.message.GenericMessage;
import cn.transmitter.common.message.Message;

import java.util.Map;

/**
 * 事件消息类
 *
 * @param <T> 事件消息类型
 * @author cloud
 * @since 2.0
 */
public class GenericEventMessage<T> implements EventMessage<T> {

    private final Message<T> delegate;

    @SuppressWarnings("unchecked")
    public static <C> EventMessage<C> asEventMessage(Object event) {
        if (event instanceof EventMessage) {
            return (EventMessage<C>)event;
        }
        return new GenericEventMessage<>((C)event, null);
    }

    public GenericEventMessage(T payload, Map<String, ?> metaData) {
        this(new GenericMessage<>(payload, metaData));
    }

    public GenericEventMessage(Message<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getIdentifier() {
        return delegate.getIdentifier();
    }

    @Override
    public T getPayload() {
        return delegate.getPayload();
    }

    @Override
    public Class<T> getPayloadType() {
        return delegate.getPayloadType();
    }
}
