package cn.transmitter.test.order.filter;

import cn.transmitter.aggregate.ChainContextHolder;
import cn.transmitter.chain.IBusinessChain;
import cn.transmitter.test.order.Order;
import cn.transmitter.test.order.command.OrderCreateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author cloud
 */
@Component
public class FirstBusinessChain extends IBusinessChain<Order> {

    private static final Logger log = LoggerFactory.getLogger(FirstBusinessChain.class);

    @Override
    public boolean shouldFilter(ChainContextHolder holder) {
        return OrderCreateCommand.class.equals(holder.getCommand().getClass());
    }

    @Override
    public void run(ChainContextHolder holder) {
        log.info("执行过滤器：FirstBusinessChain");
        String userId = holder.get("userId");
        log.info("执行过滤器：FirstBusinessChain :userId={}", userId);
        holder.setAttribute("userId", "1234");

        holder.setAttribute("text", "text");
    }

    @Override
    public double filterOrder() {
        return 0;
    }

}
