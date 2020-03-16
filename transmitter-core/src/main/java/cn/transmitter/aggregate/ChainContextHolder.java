package cn.transmitter.aggregate;

import cn.transmitter.chain.ChainExecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author cloud
 */
public class ChainContextHolder {

    private Map<String, Object> valueMap;

    private Set<String> watchKey;

    private Object command;

    private Class aggregate;

    private BiConsumer<String, Object> attributeChangeConsumer;

    public static ChainContextHolderBuilder builder(Class aggregate, Object command) {
        return new ChainContextHolderBuilder(aggregate, command);
    }

    public ChainContextHolder(Map<String, Object> valueMap, Object command, Class aggregate,
                              BiConsumer<String, Object> attributeChangeConsumer) {
        this.valueMap = valueMap;
        this.watchKey = new HashSet<>(valueMap.keySet());
        this.command = command;
        this.aggregate = aggregate;
        this.attributeChangeConsumer = attributeChangeConsumer;
    }

    /**
     * 设置属性值(非线程安全)
     *
     * @param key   属性key
     * @param value 属性值
     * @return
     */
    public ChainContextHolder setAttribute(String key, Object value) {
        valueMap.put(key, value);
        if (watchKey.contains(key)) {
            this.attributeChangeConsumer.accept(key, value);
        }
        return this;
    }

    /**
     * 开始执行业务链
     */
    public ChainContextHolder execute() {
        ChainExecutor.execute(this);
        return this;
    }

    /**
     * 获取指定key值
     *
     * @param key 属性key
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T)valueMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public <C> C getCommand() {
        return (C)command;
    }

    public Class getAggregate() {
        return aggregate;
    }

    public static class ChainContextHolderBuilder {

        private Object command;

        private Class aggregate;

        private Map<String, Object> valueMap;

        private BiConsumer<String, Object> biConsumer;

        public ChainContextHolderBuilder(Class aggregate, Object command) {
            this.aggregate = aggregate;
            this.command = command;
            this.valueMap = new HashMap<>();
            this.biConsumer = (s, o) -> {};
        }

        /**
         * 添加属性
         *
         * @param key   属性key
         * @param value 属性值
         * @return 返回值
         */
        public ChainContextHolderBuilder addAttribute(String key, Object value) {
            valueMap.put(key, value);
            return this;
        }

        public ChainContextHolderBuilder setChangeConsumer(BiConsumer<String, Object> consumer) {
            this.biConsumer = consumer;
            return this;
        }

        public ChainContextHolder build() {
            return new ChainContextHolder(valueMap, command, aggregate, biConsumer);
        }
    }
}
