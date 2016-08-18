package com.ai.slp.order.api.deliveryorder.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ai.slp.order.api.deliveryorderprint.interfaces.IDeliveryOrderPrintSV;
import com.ai.slp.order.api.deliveryorderprint.param.DeliveryOrderPrintRequest;
import com.ai.slp.order.api.deliveryorderprint.param.DeliveryOrderPrintResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/context/core-context.xml" })
public class DeliveryOrderPrintSVImplTest {
	
	@Autowired
	private IDeliveryOrderPrintSV deliveryOrderPrintSV;
	@Test
	public void test() {
		DeliveryOrderPrintRequest request=new DeliveryOrderPrintRequest();
		request.setOrderId(35913355l);
		request.setUserId("000000000000000945");
		request.setTenantId("SLP");
		DeliveryOrderPrintResponse response = deliveryOrderPrintSV.print(request);
		System.out.println(response);
	}

}