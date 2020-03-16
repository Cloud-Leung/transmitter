package cn.transmitter.test.order.repository;

import cn.transmitter.common.Repository;
import cn.transmitter.exception.NoCommandHanderException;
import cn.transmitter.test.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cloud
 */

@Component
public class ProductRepository implements Repository<Order> {

    private static final Logger log = LoggerFactory.getLogger(ProductRepository.class);

    private static final Map<String, Order> map = new HashMap<>();

    //region public methods
    @Override
    public Order load(Object aggregateIdentifier) {
        log.info("收到加载请求：" + aggregateIdentifier);
        return map.get(aggregateIdentifier.toString());
    }

    @Override
    public Order create(Order object) {
        log.info("收到创建请求：" + object);
        if (map.containsKey(object.getOrderId())) {
            throw new NoCommandHanderException("当前id的产品已经存在了");
        }
        return map.put(object.getOrderId(), object);
    }

    @Override
    public void save(Order object) {
        log.info("收到保存请求：" + object);
        map.put(object.getOrderId(), object);
    }
    //endregion

    //region private methods
    //endregion
}
