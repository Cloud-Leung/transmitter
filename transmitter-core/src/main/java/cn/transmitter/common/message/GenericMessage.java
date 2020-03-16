package cn.transmitter.common.message;

import cn.transmitter.common.util.UUIDHexGenerator;

import java.util.Map;

/**
 * @author cloud
 */
public class GenericMessage<T> implements Message<T> {

    private final Class<T> payloadType;

    private final T payload;

    private final String identifier;

    private final Map<String, ?> metaData;

    @SuppressWarnings("unchecked")
    public GenericMessage(T payload, Map<String, ?> metaData) {
        this.payloadType = (Class<T>)payload.getClass();
        this.payload = payload;
        this.identifier = UUIDHexGenerator.generate();
        this.metaData = metaData;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public T getPayload() {
        return this.payload;
    }

    @Override
    public Class<T> getPayloadType() {
        return this.payloadType;
    }
}
