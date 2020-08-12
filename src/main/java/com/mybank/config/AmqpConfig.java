package com.mybank.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/10/16
 */
@Configuration
public class AmqpConfig {

    private static Logger logger = LoggerFactory.getLogger(AmqpConfig.class);

    private static final String KF_PAY_ORDER_CYCLE_QUERY_DELAY_EXCHANGE = "kf.pay.order.cycle.query.delay.exchange";

    private static final String KF_PAY_ORDER_CYCLE_QUERY_DELAY_ROUTING_KEY = "kf.pay.order.cycle.query.delay.routing.key";

    private static final String KF_PREAUTH_ORDER_CYCLE_QUERY_DELAY_EXCHANGE = "kf.preauth.order.cycle.query.delay.exchange";

    private static final String KF_PREAUTH_ORDER_CYCLE_QUERY_DELAY_ROUTING_KEY = "kf.preauth.order.cycle.query.delay.routing.key";

    @Autowired
    private AmqpTemplate amqpTemplate;

    public boolean convertAndSendPreauthMsg(String msg, int delay) {
        try {
            amqpTemplate.convertAndSend(KF_PREAUTH_ORDER_CYCLE_QUERY_DELAY_EXCHANGE,
                    KF_PREAUTH_ORDER_CYCLE_QUERY_DELAY_ROUTING_KEY,
                    msg, message -> {
                        message.getMessageProperties().setExpiration(String.valueOf(delay * 1000));
                        return message;
                    });
            logger.error("预授权消息【{}】MQ发送成功！", msg);
        } catch (AmqpException e) {
            logger.error("预授权消息【" + msg + "】MQ发送失败,失败原因：{}", e);
            return false;
        }
        return true;
    }

    public boolean convertAndSendPayMsg(String msg, int delay) {
        try {
            amqpTemplate.convertAndSend(KF_PAY_ORDER_CYCLE_QUERY_DELAY_EXCHANGE,
                    KF_PAY_ORDER_CYCLE_QUERY_DELAY_ROUTING_KEY,
                    msg, message -> {
                        message.getMessageProperties().setExpiration(String.valueOf(delay * 1000));
                        return message;
                    });
            logger.error("支付消息【{}】MQ发送成功！", msg);
        } catch (AmqpException e) {
            logger.error("支付消息【{}】MQ发送失败,失败原因：{}", msg, e.getMessage());
            return false;
        }
        return true;
    }

}
