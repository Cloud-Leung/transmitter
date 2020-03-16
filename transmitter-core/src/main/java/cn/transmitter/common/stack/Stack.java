package cn.transmitter.common.stack;

/**
 * 栈
 *
 * @author cloud
 */
public interface Stack<E> {

    /**
     * 把数据压入栈
     *
     * @param e 对象
     */
    void push(E e);

    /**
     * 把栈顶数据取出
     *
     * @return
     */
    E pop();

    /**
     * 获取栈顶对象，但不从栈顶移除
     *
     * @return
     */
    E peek();

    /**
     * 判断当前栈是否为空
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 移除栈顶对象
     *
     * @return
     */
    void remove();

    /**
     * 清空当前栈
     */
    void clear();

    /**
     * 替换栈顶的对象
     *
     * @param e 替换数据
     */
    void replace(E e);

    /**
     * 获取栈大小
     *
     * @return 大小
     */
    int size();
}
