package cn.transmitter.test;

import cn.transmitter.TransmitterApplication;
import cn.transmitter.aggregate.command.gateway.CommandGateway;
import cn.transmitter.annotation.EnableTransmitter;
import cn.transmitter.test.order.Order;
import cn.transmitter.test.order.OrderItem;
import cn.transmitter.test.order.command.OrderCreateCommand;
import cn.transmitter.test.order.command.OrderUpdateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cloud
 */
@EnableTransmitter
@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ApplicationContext applicationContext = TransmitterApplication.run(Application.class).run(args);

        CommandGateway commandGateway = applicationContext.getBean(CommandGateway.class);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem("itemId3333", "itemName", 1));
        OrderCreateCommand command = new OrderCreateCommand("orderId234234", "userId", "name", orderItems);

        Order order = commandGateway.sendAndWait(command);
        System.out.println(order.toString());

        System.out.println(Thread.currentThread().getId());
        String userId = commandGateway.sendAndWait(new OrderUpdateCommand(order.getOrderId()));
        log.info("OrderUpdateCommand 返回结果:" + userId);

    }
}
