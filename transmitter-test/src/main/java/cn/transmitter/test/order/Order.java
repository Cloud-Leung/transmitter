package cn.transmitter.test.order;

import cn.transmitter.aggregate.ChainContextHolder;
import cn.transmitter.aggregate.command.gateway.CommandGateway;
import cn.transmitter.annotation.Aggregate;
import cn.transmitter.annotation.AggregateMember;
import cn.transmitter.annotation.CommandHandler;
import cn.transmitter.chain.ChainExecutor;
import cn.transmitter.test.order.command.OrderCreateCommand;
import cn.transmitter.test.order.command.OrderUpdateCommand;
import cn.transmitter.test.order.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.transmitter.aggregate.AggregateLifecycle.apply;

/**
 * @author cloud
 */
@Aggregate
public class Order {

    private static final Logger log = LoggerFactory.getLogger(Order.class);

    private String orderId;

    private String userId;

    private String name;

    @AggregateMember
    private List<OrderItem> orderItems;

    @Autowired
    private CommandGateway commandGateway;

    public Order() {}

    @CommandHandler
    public Order(OrderCreateCommand command) {
        this.orderId = command.getOrderId();
        this.userId = command.getUserId();
        this.name = command.getName();
        this.orderItems = command.getOrderItems();

        ChainContextHolder.builder(this.getClass(), command)
            .addAttribute("userId", userId)
            .setChangeConsumer((s, o) -> {
                log.info(s + "的值发生改变了:{}", o);
                if (s.equals("userId")) {
                    userId = o.toString();
                }
            })
            .build()
            .execute();
        // ChainExecutor.execute(holder);

        log.info("userId被改成什么了:{}", this.userId);
        apply(new OrderCreatedEvent(command.getOrderId(), command.getUserId(), command.getName()));
        log.info("收到创建命令请求：" + command);
    }

    @Transactional
    @CommandHandler
    public String on(OrderUpdateCommand command) {
        log.info("收到创建更新请求：" + command);
        System.out.println(Thread.currentThread().getId());
        ChainContextHolder holder = ChainContextHolder.builder(this.getClass(), command)
            .addAttribute("userId", userId)
            .setChangeConsumer((s, o) -> {
                if (s.equals("userId")) {
                    userId = o.toString();
                }
            })
            .build();
        ChainExecutor.execute(holder);
        return this.userId;
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

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    //endregion

    //region private methods

    @Override
    public String toString() {
        return "Order{" + "orderId='" + orderId + '\'' + ", userId='" + userId + '\'' + ", name='" + name + '\'' +
               ", orderItems=" + orderItems + '}';
    }

    //endregion

}
