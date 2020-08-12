package com.mybank.base.repository;

import com.alibaba.fastjson.JSON;
import com.mybank.base.Page;
import com.mybank.base.entity.AlipayApp;
import com.mybank.base.entity.BaseOrder;
import com.mybank.jpa.Criteria;
import com.mybank.jpa.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.rowset.Predicate;
import javax.transaction.Transactional;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/5/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BaseOrderRepositoryTest {

	@Autowired
	private BaseOrderRepository repository;

//	@Test
//	@Transactional//解决 no session
//	public void refund() throws Exception {
//		System.out.println(repository.refund(20180503000007L,2,"正常退款！",new Date(),1));
//	}

	@Test
	@Transactional//解决 no session
	public void findAll() throws Exception {
		Criteria<BaseOrder> criteria = new Criteria<>();
		criteria.add(Restrictions.like("orderNo", "123", true))
				.add(Restrictions.eq("thirdOrderNo", "123", true));
		System.out.println(JSON.toJSONString(Page.toPage(repository.findAll(criteria, PageRequest.of(0, 10)))));
	}
	@Test
	@Transactional//解决 no session
	public void findPage() throws Exception {
		Criteria<Map<String,Object>> criteria = new Criteria<>();
		criteria.add(Restrictions.eq("orderNo", "123456", true));
//		System.out.println(JSON.toJSONString(Page.toPage(repository.findPage(null,"1527572838873",
//				null,null,null,null,null,null,null
//				,PageRequest.of(0, 10)))));
	}

}