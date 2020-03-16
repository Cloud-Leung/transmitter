package cn.transmitter.aggregate.factory;

import cn.transmitter.exception.AggregateBeanLoadException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.transmitter.common.util.ReflectionUtils.*;

/**
 * 聚合bean装载工厂
 *
 * @author cloud
 */
public class AggregateBeanLoadFactory implements BeanLoadFactory {

    private Map<Field, Object> fieldObjectMap;

    public AggregateBeanLoadFactory(Object bean) {
        this.fieldObjectMap = new HashMap<>();
        resolve(bean);
    }

    @Override
    public <T> T load(T t) {
        if (CollectionUtils.isEmpty(fieldObjectMap)) {
            return t;
        }
        fieldObjectMap.forEach((k, v) -> resetFieldValue(k, t, v));
        if (t instanceof InitializingBean) {
            InitializingBean bean = (InitializingBean)t;
            try {
                bean.afterPropertiesSet();
            } catch (Exception e) {
                throw new AggregateBeanLoadException(e.getMessage(), e);
            }
        }
        return t;
    }

    private <T> void resetFieldValue(Field field, T target, Object object) {
        Object pre = getFieldValue(field, target);
        if (null == pre) {
            setFieldValue(field, target, object);
        }
    }

    private void resolve(Object bean) {
        Iterable<Field> fields = fieldsOf(bean.getClass());
        fields.forEach(o -> resolve(o, bean));
    }

    private void resolve(Field field, Object bean) {
        Autowired autowired = field.getAnnotation(Autowired.class);
        Resource resource = field.getAnnotation(Resource.class);
        if (Objects.nonNull(autowired) || Objects.nonNull(resource)) {
            fieldObjectMap.put(field, getFieldValue(field, bean));
        }
    }
}
