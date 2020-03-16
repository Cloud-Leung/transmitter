package cn.transmitter.chain;

import cn.transmitter.aggregate.ChainContextHolder;

/**
 * 业务链
 *
 * @author cloud
 */
public abstract class IBusinessChain<T> implements Comparable<IBusinessChain<T>> {

    /**
     * 过滤顺序
     *
     * @return
     */
    public abstract double filterOrder();

    /**
     * 是否要过滤
     *
     * @param holder
     * @return
     */
    public abstract boolean shouldFilter(ChainContextHolder holder);

    /**
     * 执行过滤器
     *
     * @param holder
     * @return
     */
    public abstract void run(ChainContextHolder holder);

    @Override
    public int compareTo(IBusinessChain o) {
        return Double.compare(this.filterOrder(), o.filterOrder());
    }
}
