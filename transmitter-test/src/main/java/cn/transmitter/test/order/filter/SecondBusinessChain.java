package cn.transmitter.test.order.filter;

import cn.transmitter.aggregate.ChainContextHolder;
import cn.transmitter.chain.IBusinessChain;
import cn.transmitter.test.order.Order;
import cn.transmitter.test.order.command.OrderCreateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author cloud
 */
@Component
public class SecondBusinessChain extends IBusinessChain<Order> {

    private static final Logger log = LoggerFactory.getLogger(SecondBusinessChain.class);

    @Override
    public boolean shouldFilter(ChainContextHolder holder) {
        return OrderCreateCommand.class.equals(holder.getCommand().getClass());
    }

    @Override
    public void run(ChainContextHolder holder) {
        log.info("执行过滤器：SecondBusinessChain");
        String userId = holder.get("userId");
        log.info("执行过滤器：SecondBusinessChain :userId={}", userId);
        holder.setAttribute("userId", "SecondBusinessChain");
    }

    @Override
    public double filterOrder() {
        return 0;
    }

}
