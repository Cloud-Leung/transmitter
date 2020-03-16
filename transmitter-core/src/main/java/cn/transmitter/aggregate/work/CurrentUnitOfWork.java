package cn.transmitter.aggregate.work;

import cn.transmitter.common.stack.DequeStack;
import cn.transmitter.common.stack.Stack;
import cn.transmitter.exception.AggregateException;

import java.util.Objects;

/**
 *
 * @author cloud
 */
public class CurrentUnitOfWork {

    //region variables
    private static final ThreadLocal<CurrentUnitOfWork> CURRENT = new ThreadLocal<>();

    /**
     * 待执行的事件列表
     */
    private final Stack<UnitOfWork<?>> WORK_QUEUE = new DequeStack<>();

    /**
     * 状态栈 对应AggregateLifecycle中的聚合栈
     */
    private Stack<Phase> stack;
    //endregion

    //region public methods

    private CurrentUnitOfWork() {
        stack = new DequeStack<>();
    }

    /**
     * 获取当前工作
     *
     * @return
     */
    public static CurrentUnitOfWork get() {
        if (Objects.isNull(CURRENT.get())) {
            CURRENT.set(new CurrentUnitOfWork());
        }
        return CURRENT.get();
    }

    /**
     * 状态设置为初始化
     */
    public void initialization() {
        stack.push(Phase.INITIALIZATION);
    }

    /**
     * 准备
     */
    public void ready() {
        stack.replace(Phase.READY);
        if (!WORK_QUEUE.isEmpty()) {
            while (!WORK_QUEUE.isEmpty()) {
                WORK_QUEUE.pop().commit();
            }
        }
    }

    /**
     * 添加工作
     * 如果当前已经准备好，则直接执行工作
     * 如果还没准备好则将工作暂存到队列中
     * 增加判断当前是否有聚合正在初始化，如果没有聚合正在初始化则放弃事件发布并抛出异常
     *
     * @param unitOfWork 单位事件
     */
    public void add(UnitOfWork<?> unitOfWork) {
        if (isReady()) {
            unitOfWork.commit();
        } else {
            if (isInitialization()) {
                WORK_QUEUE.push(unitOfWork);
            } else {
                throw new AggregateException("事件发送必须在聚合的生命周期内，在聚合生命周期外不能发布事件");
            }
        }
    }

    /**
     * 清理栈顶工作
     *
     * @param unitOfWork
     */
    public void clear(UnitOfWork<?> unitOfWork) {
        UnitOfWork unit = WORK_QUEUE.peek();
        if (unit == unitOfWork) {
            WORK_QUEUE.remove();
        }
    }

    /**
     * 移除栈顶的状态数据
     */
    public static void remove() {
        get().stack.remove();
    }

    /**
     * 判断当前栈顶聚合状态是否已准备好
     *
     * @return
     */
    private boolean isReady() {
        return Phase.READY.equals(stack.peek());
    }

    /**
     * 判断当前栈顶聚合状态是否正在初始化
     *
     * @return
     */
    private boolean isInitialization() {
        return Phase.INITIALIZATION.equals(stack.peek());
    }

    //endregion

    //region private methods
    //endregion

}
