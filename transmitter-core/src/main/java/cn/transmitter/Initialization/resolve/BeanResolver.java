package cn.transmitter.Initialization.resolve;

/**
 * 聚合bean解析
 *
 * @author cloud
 */
public interface BeanResolver {

    /**
     * 解析bean
     *
     * @param beanName
     * @param bean
     */
    void resolveBean(String beanName, Object bean);

}
