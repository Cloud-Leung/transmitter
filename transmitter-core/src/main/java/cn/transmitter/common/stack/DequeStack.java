package cn.transmitter.common.stack;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 利用Deque实现的栈结构
 *
 * @author cloud
 */
public class DequeStack<E> implements Stack<E> {

    private Deque<E> deque;

    public DequeStack() {
        this.deque = new LinkedBlockingDeque<>();
    }

    @Override
    public void push(E e) {
        deque.push(e);
    }

    @Override
    public E pop() {
        return deque.pop();
    }

    @Override
    public E peek() {
        return deque.peek();
    }

    @Override
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    @Override
    public void remove() {
        deque.removeFirst();
    }

    @Override
    public void clear() {
        deque.clear();
    }

    @Override
    public void replace(E e) {
        remove();
        push(e);
    }

    @Override
    public int size() {
        return deque.size();
    }
}
