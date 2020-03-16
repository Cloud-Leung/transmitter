package cn.transmitter.test.other;

import cn.transmitter.annotation.EventHandler;
import cn.transmitter.test.order.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author cloud
 */
@Component
public class ProductOtherBusiness {

    private static final Logger log = LoggerFactory.getLogger(ProductOtherBusiness.class);

    private int threadPoolSize;

    @PostConstruct
    public void init() {
        log.info("=============" + threadPoolSize);
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("ProductOtherBusiness收到订单创建事件：" + event);
    }

}
