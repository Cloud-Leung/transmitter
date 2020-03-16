package cn.transmitter.aggregate.command.handler;

import cn.transmitter.aggregate.command.message.CommandMessage;
import cn.transmitter.aggregate.handler.CommandTargetResolver;
import cn.transmitter.annotation.TargetAggregateIdentifier;
import cn.transmitter.common.util.ReflectionUtils;
import cn.transmitter.exception.NoIdentifierException;

import java.lang.reflect.Field;
import java.util.Optional;

import static java.lang.String.format;

/**
 * 命令标识解析器
 *
 * @author cloud
 */
public class AnnotationCommandTargetResolver implements CommandTargetResolver {

    private Field field;

    public AnnotationCommandTargetResolver(Class<?> payLoadType) {
        this.field = findIdentifierField(payLoadType);
    }

    @Override
    public Object resolveTarget(CommandMessage<?> command) {
        Object aggregateIdentifier;
        try {
            aggregateIdentifier =
                Optional.ofNullable(ReflectionUtils.getFieldValue(field, command.getPayload())).orElse(null);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("The value provided for the version is not a number.", e);
        }
        if (aggregateIdentifier == null) {
            throw new IllegalArgumentException(format("Invalid command. It does not identify the target aggregate. " +
                                                      "Make sure at least one of the fields or methods in the [%s] class contains the " +
                                                      "@TargetAggregateIdentifier annotation and that it returns a non-null value.",
                                                      command.getPayloadType().getName()));
        }
        return aggregateIdentifier;
    }

    private Field findIdentifierField(Class<?> payLoadType) {
        Field identifierField = null;
        for (Field f : ReflectionUtils.fieldsOf(payLoadType)) {
            if (f.isAnnotationPresent(TargetAggregateIdentifier.class)) {
                identifierField = f;
                break;
            }
        }
        if (identifierField == null) {
            throw new NoIdentifierException("can't find identifier field on command type : " + payLoadType.getName());
        }
        return identifierField;
    }

}
