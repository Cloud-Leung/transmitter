package cn.transmitter.aggregate.work;

import cn.transmitter.common.message.Message;

/**
 * 工作单位
 *
 * @author cloud
 */
public interface UnitOfWork<T extends Message<?>> {

    void commit();

}
