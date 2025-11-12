package generic;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
@Component
public class CallBackImpl implements CallBack<Order, List<User>> {
    @Override
    public void call(Order order, List<User> users) {
        System.out.println("订单ID: " + order.getOrderId());
    }

    @Override
    public void call2(Order order, List<User> users) {
        System.out.println("2 订单ID: " + order.getOrderId());
    }
}
