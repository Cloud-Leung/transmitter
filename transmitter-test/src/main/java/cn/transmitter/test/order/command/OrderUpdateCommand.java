package cn.transmitter.test.order.command;

import cn.transmitter.annotation.TargetAggregateIdentifier;

/**
 *
 * @author cloud
 */

public class OrderUpdateCommand {

    @TargetAggregateIdentifier
    private String orderId;

    public OrderUpdateCommand(String orderId) {
        this.orderId = orderId;
    }

}
