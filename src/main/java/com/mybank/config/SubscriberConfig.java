package com.mybank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/5/16
 */
@Configuration
public class SubscriberConfig {
	/**
	 * 创建连接工厂
	 */
	@Bean
	public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
												   MessageListenerAdapter listenerAdapter){
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter,new PatternTopic("merchant_status_update"));
		return container;
	}

	/**
	 * 绑定消息监听者和接收监听的方法
	 */
	@Bean
	public MessageListenerAdapter listenerAdapter(Receiver receiver){
		return new MessageListenerAdapter(receiver,"receiveMessage");
	}

	/**
	 * 注册订阅者
	 */
	@Bean
	public Receiver receiver(){
		return new Receiver();
	}

//	/**
//	 * 计数器，用来控制线程
//	 * @return
//	 */
//	@Bean
//	public CountDownLatch latch(){
//		return new CountDownLatch(1);//指定了计数的次数 1
//	}
	private Lock lock = new ReentrantLock();
	private int i = 0;
	class Receiver{

		public Receiver() {
		}

		public void receiveMessage(String message) {
			lock.lock();
			try {
				System.out.println("receive a message:" +(i++)+"---" + message);
			}finally {
				lock.unlock();
			}
		}
	}

}