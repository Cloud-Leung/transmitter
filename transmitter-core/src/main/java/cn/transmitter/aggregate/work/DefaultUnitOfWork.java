package cn.transmitter.aggregate.work;

import cn.transmitter.common.message.Message;

/**
 *
 * @author cloud
 */
public class DefaultUnitOfWork implements UnitOfWork<Message<?>> {

    private Work work;


    public DefaultUnitOfWork(Work work) {
        this.work = work;
    }

    @Override
    public void commit() {
        try {
            work.run();
        } finally {
            CurrentUnitOfWork.get().clear(this);
        }
    }

}
