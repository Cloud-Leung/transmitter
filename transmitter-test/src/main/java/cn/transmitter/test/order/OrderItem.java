package cn.transmitter.test.order;

import cn.transmitter.aggregate.command.gateway.CommandGateway;
import cn.transmitter.annotation.AggregateEntity;
import cn.transmitter.annotation.EventHandler;
import cn.transmitter.test.order.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author cloud
 */



@AggregateEntity
public class OrderItem {

    private static final Logger log = LoggerFactory.getLogger(OrderItem.class);

    private String itemId;

    private String itemName;

    private int num;

    @Autowired
    private CommandGateway commandGateway ;

    public OrderItem(){}

    public OrderItem(String itemId, String itemName, int num) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.num = num;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("OrderItem: 收到订单创建事件");
    }




    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
