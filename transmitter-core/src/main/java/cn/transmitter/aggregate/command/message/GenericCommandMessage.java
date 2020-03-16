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

package cn.transmitter.aggregate.command.message;

import cn.transmitter.common.message.GenericMessage;
import cn.transmitter.common.message.Message;

import java.util.Map;

/**
 * Implementation of the CommandMessage that takes all properties as constructor parameters.
 *
 * @param <T> The type of payload contained in this Message
 * @author Allard Buijze
 * @since 2.0
 */
public class GenericCommandMessage<T> implements CommandMessage<T> {

    private final String commandName;

    private final Message<T> delegate;

    @SuppressWarnings("unchecked")
    public static <C> CommandMessage<C> asCommandMessage(Object command) {
        if (CommandMessage.class.isInstance(command)) {
            return (CommandMessage<C>)command;
        }
        return new GenericCommandMessage<>((C)command, null);
    }

    public GenericCommandMessage(T payload, Map<String, ?> metaData) {
        this(new GenericMessage<>(payload, metaData), payload.getClass().getName());
    }

    public GenericCommandMessage(Message<T> delegate, String commandName) {
        this.delegate = delegate;
        this.commandName = commandName;
    }

    @Override
    public String getCommandName() {
        return commandName;
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
