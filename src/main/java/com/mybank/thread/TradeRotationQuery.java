package com.mybank.thread;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.mybank.alipay.AlipayRequestClient;
import com.mybank.alipay.AlipayXmlConfigs;
import com.mybank.base.entity.AlipayTradeOrderDetail;
import com.mybank.base.entity.BaseOrder;
import com.mybank.base.entity.constant.OrderStatus;
import com.mybank.base.repository.AlipayTradeOrderDetailRepository;
import com.mybank.config.SpringUtil;
import com.mybank.pool.ThreadPoolManager;
import com.mybank.service.AlipayTradePayService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 功能描述: 网商kf接口测试<br/>
 * 支付宝请求发送成功后轮流查询支付宝订单状态
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/6/22
 */
public class TradeRotationQuery implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TradeRotationQuery.class);

    private String alipayAppId;
    private long orderNo;

    public TradeRotationQuery(String alipayAppId, long orderNo) {
        this.alipayAppId = alipayAppId;
        this.orderNo = orderNo;
    }

    @Override
    public void run() {
        try {
            logger.info("开始轮询订单{}", orderNo);
            AlipayTradeQueryResponse alipayTradeQueryResponse = AlipayRequestClient.alipayTradeQueryRequest(
                    AlipayXmlConfigs.getAlipayClientModel(alipayAppId),
                    "{\"out_trade_no\":\"" + orderNo + "\"}"
            );
            AlipayTradePayService service = SpringUtil.getBean(AlipayTradePayService.class);
            BaseOrder<AlipayTradeOrderDetail> order = service.find(orderNo);
            if (alipayTradeQueryResponse.isSuccess()) {
                if (order != null) {
                    //TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）、WAIT_BUYER_PAY（交易创建，等待买家付款）
                    //TRADE_NOT_EXIST 订单不存在存在与订单预创建
                    if (("TRADE_SUCCESS".equals(alipayTradeQueryResponse.getTradeStatus())
                            || "TRADE_FINISHED".equals(alipayTradeQueryResponse.getTradeStatus()))
                            && OrderStatus.PAYED.getCode() != order.getStatus()) {
                        order.setStatus(OrderStatus.PAYED.getCode());
                        service.updateOrderStatus(orderNo, OrderStatus.PAYED);
                        ThreadPoolManager.notifyExecutorService.schedule(new TradeCallBackNotify(order, 0), 0, TimeUnit.SECONDS);
                        logger.info("轮询支付订单【{}】支付成功", orderNo);
                    } else if ("WAIT_BUYER_PAY".equalsIgnoreCase(alipayTradeQueryResponse.getTradeStatus())) {
                        if (order.getDetail() != null && (StringUtils.isBlank(order.getDetail().getAlipayLogonId()) ||
                                StringUtils.isBlank(order.getDetail().getAlipayOrderNo()) || StringUtils.isBlank(order.getDetail().getAlipayUserId()))) {
                            order.getDetail().setAlipayLogonId(alipayTradeQueryResponse.getBuyerLogonId());
                            order.getDetail().setAlipayOrderNo(alipayTradeQueryResponse.getTradeNo());
                            order.getDetail().setAlipayUserId(alipayTradeQueryResponse.getBuyerUserId());
                            AlipayTradeOrderDetailRepository repository = SpringUtil.getBean(AlipayTradeOrderDetailRepository.class);
                            repository.saveAndFlush(order.getDetail());
                        }
                        ThreadPoolManager.scheduledExecutorService.schedule(new TradeRotationQuery(alipayAppId, orderNo), 5, TimeUnit.SECONDS);
                    } else if ("TRADE_CLOSED".equalsIgnoreCase(alipayTradeQueryResponse.getTradeStatus())) {
                        logger.info("轮询支付订单{},订单关闭", orderNo);
                        order.setStatus(OrderStatus.CLOSED.getCode());
                        service.saveOrder(order);
                    }
                }

            } else {
                if (order != null && "ACQ.TRADE_NOT_EXIST".equals(alipayTradeQueryResponse.getSubCode()) && System.currentTimeMillis() - order.getTradeDate().getTime() < 900000L) {
                    logger.info("开始轮询预创建订单{}", orderNo);
                    ThreadPoolManager.scheduledExecutorService.schedule(new TradeRotationQuery(alipayAppId, orderNo), 5, TimeUnit.SECONDS);
                } else {
                    logger.info("轮询订单{},订单关闭", orderNo);
                    if(order != null) {
                        order.setStatus(OrderStatus.CLOSED.getCode());
                        service.saveOrder(order);
                    }
                }
            }
            logger.info("结束轮询订单{}", orderNo);
        } catch (Exception e) {
            logger.info("轮询订单{}异常", e);
        }
    }
}