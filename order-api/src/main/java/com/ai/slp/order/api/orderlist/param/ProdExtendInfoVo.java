package com.ai.slp.order.api.orderlist.param;

import java.io.Serializable;

/**
 * 商品明细拓展值Date: 2016年5月25日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author zhangxw
 */
public class ProdExtendInfoVo implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
     * 拓展信息值
     */
    private String ProdExtendInfoValue;

    public String getProdExtendInfoValue() {
        return ProdExtendInfoValue;
    }

    public void setProdExtendInfoValue(String prodExtendInfoValue) {
        ProdExtendInfoValue = prodExtendInfoValue;
    }

}
