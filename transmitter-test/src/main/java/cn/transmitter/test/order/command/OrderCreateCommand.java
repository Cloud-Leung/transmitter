package cn.transmitter.test.order.command;

import cn.transmitter.aggregate.lock.Lockable;
import cn.transmitter.annotation.TargetAggregateIdentifier;
import cn.transmitter.test.order.OrderItem;

import java.util.List;

/**
 * 测试命令
 *
 * @author cloud
 */
public class OrderCreateCommand implements Lockable {

    @TargetAggregateIdentifier
    private OrderIdentifier orderId;

    private String userId;

    private String name;

    private List<OrderItem> orderItems;

    public OrderCreateCommand(String orderId, String userId, String name, List<OrderItem> orderItems) {
        this.orderId = new OrderIdentifier(orderId);
        this.userId = userId;
        this.name = name;
        this.orderItems = orderItems;
    }

    @Override
    public String lockKey() {
        return orderId.toString();
    }

    private class OrderIdentifier {

        private String itemId;

        public OrderIdentifier(String itemId) {
            this.itemId = itemId;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        @Override
        public String toString() {
            return "OrderIdentifier{" + "itemId='" + itemId + '\'' + '}';
        }
    }

    public String getOrderId() {
        return orderId.getItemId();
    }

    public void setOrderId(String orderId) {
        this.orderId = new OrderIdentifier(orderId);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "OrderCreateCommand{" + "orderId=" + orderId + ", userId='" + userId + '\'' + ", name='" + name + '\'' +
               ", orderItems=" + orderItems + '}';
    }
}
