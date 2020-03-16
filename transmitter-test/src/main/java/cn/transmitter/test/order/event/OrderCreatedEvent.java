package cn.transmitter.test.order.event;

/**
 *
 * @author cloud
 */
public class OrderCreatedEvent {

    private String orderId;

    private String userId;

    private String name;

    public OrderCreatedEvent(String orderId, String userId, String name) {
        this.orderId = orderId;
        this.userId = userId;
        this.name = name;
    }





    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
}
