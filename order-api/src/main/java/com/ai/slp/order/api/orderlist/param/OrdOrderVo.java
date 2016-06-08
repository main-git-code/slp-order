package com.ai.slp.order.api.orderlist.param;

import java.sql.Timestamp;
import java.util.List;

import com.ai.opt.base.vo.BaseInfo;

/**
 * 订单表 Date: 2016年5月3日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author zhangqiang7
 */
public class OrdOrderVo extends BaseInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 业务订单ID
     */
    private Long orderId;

    /**
     * 业务类型
     */
    private String busiCode;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单状态(后厂)
     */
    private String state;

    /**
     * 订单状态展示
     */
    private String stateName;

    /**
     * 支付方式
     */
    private String payStyle;

    /**
     * 支付方式显示值
     */
    private String payStyleName;

    /**
     * 支付时间
     */
    private Timestamp payTime;

    /**
     * 下单时间
     */
    private Timestamp orderTime;

    /**
     * 手机个数
     */
    private Integer phoneCount;

    /**
     * 总费用
     */
    private Long totalFee;

    /**
     * 总优惠金额
     */
    private Long discountFee;

    /**
     * 减免金额
     */
    private long operDiscountFee;

    /**
     * 总应收费用
     */
    private Long adjustFee;

    /**
     * 总实收费用
     */
    private Long paidFee;

    /**
     * 总待收费用
     */
    private Long payFee;

    /**
     * 支付信息
     */
    private List<OrderPayVo> payDataList;

    /**
     * 商品集合
     */
    private List<OrdProductVo> productList;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public Long getDiscountFee() {
        return discountFee;
    }

    public void setDiscountFee(Long discountFee) {
        this.discountFee = discountFee;
    }

    public Long getAdjustFee() {
        return adjustFee;
    }

    public void setAdjustFee(Long adjustFee) {
        this.adjustFee = adjustFee;
    }

    public List<OrdProductVo> getProductList() {
        return productList;
    }

    public void setProductList(List<OrdProductVo> productList) {
        this.productList = productList;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    public String getPayStyleName() {
        return payStyleName;
    }

    public void setPayStyleName(String payStyleName) {
        this.payStyleName = payStyleName;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public Long getPaidFee() {
        return paidFee;
    }

    public void setPaidFee(Long paidFee) {
        this.paidFee = paidFee;
    }

    public Long getPayFee() {
        return payFee;
    }

    public void setPayFee(Long payFee) {
        this.payFee = payFee;
    }

    public List<OrderPayVo> getPayDataList() {
        return payDataList;
    }

    public void setPayDataList(List<OrderPayVo> payDataList) {
        this.payDataList = payDataList;
    }

    public Integer getPhoneCount() {
        return phoneCount;
    }

    public void setPhoneCount(Integer phoneCount) {
        this.phoneCount = phoneCount;
    }

    public long getOperDiscountFee() {
        return operDiscountFee;
    }

    public void setOperDiscountFee(long operDiscountFee) {
        this.operDiscountFee = operDiscountFee;
    }

}