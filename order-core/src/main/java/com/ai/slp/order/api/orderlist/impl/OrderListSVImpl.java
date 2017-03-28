package com.ai.slp.order.api.orderlist.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.opt.base.exception.BusinessException;
import com.ai.opt.base.exception.SystemException;
import com.ai.opt.base.vo.ResponseHeader;
import com.ai.opt.sdk.constants.ExceptCodeConstants;
import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.platform.common.api.cache.interfaces.ICacheSV;
import com.ai.slp.order.api.orderlist.interfaces.IOrderListSV;
import com.ai.slp.order.api.orderlist.param.BehindQueryOrderListRequest;
import com.ai.slp.order.api.orderlist.param.BehindQueryOrderListResponse;
import com.ai.slp.order.api.orderlist.param.QueryOrderRequest;
import com.ai.slp.order.api.orderlist.param.QueryOrderResponse;
import com.ai.slp.order.dao.mapper.bo.OrdOdFeeTotal;
import com.ai.slp.order.dao.mapper.bo.OrdOrder;
import com.ai.slp.order.service.atom.interfaces.IOrdOdFeeTotalAtomSV;
import com.ai.slp.order.service.atom.interfaces.IOrdOrderAtomSV;
import com.ai.slp.order.service.business.interfaces.IOrdOrderBusiSV;
import com.ai.slp.order.util.CommonCheckUtils;
import com.ai.slp.order.util.ValidateUtils;
import com.alibaba.dubbo.config.annotation.Service;

@Service(validation = "true")
@Component
public class OrderListSVImpl implements IOrderListSV {

    @Autowired
    private IOrdOrderBusiSV ordOrderBusiSV;
    @Autowired
    private IOrdOrderAtomSV ordOrderAtomSV;
    @Autowired
    private IOrdOdFeeTotalAtomSV ordOdFeeTotalAtomSV;

    @Override
    public QueryOrderResponse queryOrder(QueryOrderRequest orderRequest) throws BusinessException,
            SystemException {
    	/* 参数校验 */
		ValidateUtils.validateQueryOrder(orderRequest);
		/* 订单主表信息查询*/
		OrdOrder order = ordOrderAtomSV.selectByOrderId(orderRequest.getTenantId(),
				orderRequest.getOrderId());
		/* 订单费用查询*/
		OrdOdFeeTotal ordOdFeeTotal = ordOdFeeTotalAtomSV.selectByOrderId(order.getTenantId(), 
				order.getOrderId());
		ICacheSV iCacheSV = DubboConsumerFactory.getService(ICacheSV.class);
        QueryOrderResponse response = ordOrderBusiSV.queryOrder(ordOdFeeTotal,order,iCacheSV);
        ResponseHeader responseHeader = new ResponseHeader(true,
                ExceptCodeConstants.Special.SUCCESS, "成功");
        response.setResponseHeader(responseHeader);
        return response;
    }

	@Override
	public BehindQueryOrderListResponse behindQueryOrderList(BehindQueryOrderListRequest orderListRequest)
			throws BusinessException, SystemException {
		/* 参数校验*/
		if (orderListRequest == null) {
			throw new BusinessException(ExceptCodeConstants.Special.PARAM_IS_NULL, "参数对象不能为空");
		}
		CommonCheckUtils.checkTenantId(orderListRequest.getTenantId(), ExceptCodeConstants.Special.PARAM_IS_NULL);
		BehindQueryOrderListResponse response = ordOrderBusiSV.behindQueryOrderList(orderListRequest);
        ResponseHeader responseHeader = new ResponseHeader(true,
                ExceptCodeConstants.Special.SUCCESS, "成功");
        response.setResponseHeader(responseHeader);
        return response;
	}
}
