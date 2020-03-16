package cn.transmitter.Initialization.resolve;

import cn.transmitter.aggregate.member.MemberEntity;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 成员解析
 *
 * @author cloud
 */
public interface MemberResolver {

    /**
     * 解析字段
     *
     * @param field 字段
     * @return
     */
    List<MemberEntity> resolveBean(Field field);

    /**
     * 解析类型
     *
     * @param targetType 类型
     * @return
     */
    List<MemberEntity> resolveBean(Class targetType);

}
