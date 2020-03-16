package cn.transmitter.aggregate.handler;

import cn.transmitter.aggregate.AggregateEntityFactory;
import cn.transmitter.common.message.Message;
import cn.transmitter.exception.MessageHandlerInvocationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author cloud
 */
public class AnnotationMethodHandlerInvocation implements HandlerInvocation {

    //region variables

    private final Executable executable;

    public AnnotationMethodHandlerInvocation(Class<?> payloadType, Executable executable,
                                             Class<? extends Message> messageType) {
        this.executable = executable;
    }

    //endregion

    //region public methods
    @Override
    public Object handle(Message message, Object target) throws Exception {
        try {
            if (executable instanceof Method) {
                target = AggregateEntityFactory.load(target);
                return ((Method)executable).invoke(target, message.getPayload());
            } else if (executable instanceof Constructor) {
                return AggregateEntityFactory.load(((Constructor)executable).newInstance(message.getPayload()));
            } else {
                throw new IllegalStateException(
                    "handler only can support Method and Constructor ! this" + executable.getName() +
                    "is doesn't support !");
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new MessageHandlerInvocationException(
                String.format("Error handling an object of type [%s]", message.getPayloadType()), e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Exception) {
                throw (Exception)e.getTargetException();
            }
            throw new MessageHandlerInvocationException(
                String.format("Error handling an object of type [%s]", message.getPayloadType()), e);
        }
    }
    //endregion

    //region private methods
    //endregion

}
