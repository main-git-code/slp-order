package com.ai.slp.order.monitor;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai.opt.sdk.components.mcs.MCSClientFactory;
import com.ai.opt.sdk.util.DateUtil;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.slp.order.api.orderrule.param.OrderMonitorBeforResponse;
import com.ai.slp.order.constants.MonitorCoonstants;
import com.ai.slp.order.constants.OrdRuleConstants;
import com.ai.slp.order.dao.mapper.bo.OrdRule;
import com.ai.slp.order.service.atom.interfaces.IOrdRuleAtomSV;
import com.ai.slp.order.util.DateCycleUtil;
@Service
public class MonitorService {
	private static final Logger log = LoggerFactory.getLogger(MonitorService.class);
	@Autowired
	private IOrdRuleAtomSV ordRuleAtomSV;
	/**
	 * 下单前缓存预警服务
	 * 
	 * @author zhangzd
	 * @ApiDocMethod
	 * @ApiCode
	 */
	public OrderMonitorBeforResponse beforSubmitOrder(String ipAddress,String userId){
		//
		OrderMonitorBeforResponse response = new OrderMonitorBeforResponse();
		
		//查询用户规则信息
		OrdRule ordRuleUser = this.ordRuleAtomSV.getOrdRule(OrdRuleConstants.BUY_EMPLOYEE_MONITOR_ID);
		//查询购买Ip规则信息
		OrdRule ordRuleIp = this.ordRuleAtomSV.getOrdRule(OrdRuleConstants.BUY_IP_MONITOR_ID);
		//订单总量规则信息
		OrdRule ordRuleAll = this.ordRuleAtomSV.getOrdRule(OrdRuleConstants.TIME_MONITOR_ID);
		
		//
		Map<String,Object> userCycleDate = DateCycleUtil.getCycleDate(ordRuleUser.getTimeType(), -ordRuleUser.getMonitorTime());
		Map<String,Object> ipCycleDate = DateCycleUtil.getCycleDate(ordRuleIp.getTimeType(), -ordRuleIp.getMonitorTime());
		Map<String,Object> allCycleDate = DateCycleUtil.getCycleDate(ordRuleAll.getTimeType(), -ordRuleAll.getMonitorTime());
		
		//
		ICacheClient cacheClient = MCSClientFactory.getCacheClient(MonitorCoonstants.MONITOR_CACHE_NAMESPACE);
		//
		Set<String> userSet = cacheClient.zrevrangeByScore(userId, userCycleDate.get("startTime").toString(), userCycleDate.get("endTime").toString());
		Set<String> ipSet = cacheClient.zrevrangeByScore(ipAddress, ipCycleDate.get("startTime").toString(), ipCycleDate.get("endTime").toString());
		Set<String> orderAllSet = cacheClient.zrevrangeByScore("order_all", allCycleDate.get("startTime").toString(), allCycleDate.get("endTime").toString());
		//用户预警提示
		if(userSet.size() >= ordRuleUser.getOrderSum() ){
			//throw new BusinessException("999999","当前用户["+userId+"]下,"+ordRuleUser.getMonitorTime()+DateCycleUtil.dateTypeMap.get(ordRuleUser.getTimeType())+"内,已达到"+ordRuleUser.getOrderSum()+"单预警");
			response.setIfWarning(MonitorCoonstants.WARNING_YES);
			response.setWarningType(MonitorCoonstants.WARNING_TYPE_USER_ID);
			response.setWarningDesc("当前用户["+userId+"]下,"+ordRuleUser.getMonitorTime()+DateCycleUtil.dateTypeMap.get(ordRuleUser.getTimeType())+"内,已达到"+ordRuleUser.getOrderSum()+"单预警");
			//
			return response;
		}
		//ip预警提示
		if(ipSet.size() >= ordRuleIp.getOrderSum() ){
			//throw new BusinessException("999999","当前ip["+ipAddress+"]下,"+ordRuleIp.getMonitorTime()+DateCycleUtil.dateTypeMap.get(ordRuleIp.getTimeType())+"内,已达到"+ordRuleIp.getOrderSum()+"单预警");
			response.setIfWarning(MonitorCoonstants.WARNING_YES);
			response.setWarningType(MonitorCoonstants.WARNING_TYPE_IP);
			response.setWarningDesc("当前ip["+ipAddress+"]下,"+ordRuleIp.getMonitorTime()+DateCycleUtil.dateTypeMap.get(ordRuleIp.getTimeType())+"内,已达到"+ordRuleIp.getOrderSum()+"单预警");
			//
			return response;
		}
		//订单总量预警提示
		if(orderAllSet.size() >= ordRuleAll.getOrderSum() ){
			//throw new BusinessException("999999","订单总量,"+ordRuleAll.getMonitorTime()+DateCycleUtil.dateTypeMap.get(ordRuleAll.getTimeType())+"内,已达到"+ordRuleAll.getOrderSum()+"单预警");
			response.setIfWarning(MonitorCoonstants.WARNING_YES);
			response.setWarningType(MonitorCoonstants.WARNING_TYPE_ORDER_SUM);
			response.setWarningDesc("订单总量,"+ordRuleAll.getMonitorTime()+DateCycleUtil.dateTypeMap.get(ordRuleAll.getTimeType())+"内,已达到"+ordRuleAll.getOrderSum()+"单预警");
			//
			return response;
		}
		
		log.info("当前用户下订单数量:"+userSet.size());
		//
		log.info("当前ip下订单数量:"+ipSet.size());
		//
		log.info("订单总量下订单数量:"+orderAllSet.size());
		//
		response.setIfWarning(MonitorCoonstants.WARNING_NO);
		response.setWarningType("");
		response.setWarningDesc("无预警信息");
		//
		return response;
	}
	/**
	 * 下单后缓存清空服务
	 * 
	 * @author zhangzd
	 * @ApiDocMethod
	 * @ApiCode
	 */
	public void afterSubmitOrder(String ipAddress,String userId){
		
		String timeStr = "20000101000000";
		//
		ICacheClient cacheClient = MCSClientFactory.getCacheClient(MonitorCoonstants.MONITOR_CACHE_NAMESPACE);
		//
		long millisTime = DateUtil.getSysDate().getTime();//毫秒
		long time = millisTime/1000;//秒
		
		//1、添加用户规则下的预警信息
		cacheClient.zadd(userId, time, userId+"_"+millisTime);//用户有序集合
		//2、添加ip规则下的预警信息
		cacheClient.zadd(ipAddress, time, ipAddress+"_"+millisTime);//地址有序集合
		//3、添加订单总量集合预警信息
		cacheClient.zadd("order_all", time, "order_all_"+millisTime);//订单总量
		
		//查询用户规则信息
		OrdRule ordRuleUser = this.ordRuleAtomSV.getOrdRule(OrdRuleConstants.BUY_EMPLOYEE_MONITOR_ID);
		//查询购买Ip规则信息
		OrdRule ordRuleIp = this.ordRuleAtomSV.getOrdRule(OrdRuleConstants.BUY_IP_MONITOR_ID);
		//订单总量规则信息
		OrdRule ordRuleAll = this.ordRuleAtomSV.getOrdRule(OrdRuleConstants.TIME_MONITOR_ID);
				
		//
		Map<String,Object> userCycleDate = DateCycleUtil.getCycleDate(ordRuleUser.getTimeType(), -ordRuleUser.getMonitorTime());
		Map<String,Object> ipCycleDate = DateCycleUtil.getCycleDate(ordRuleIp.getTimeType(), -ordRuleIp.getMonitorTime());
		Map<String,Object> allCycleDate = DateCycleUtil.getCycleDate(ordRuleAll.getTimeType(), -ordRuleAll.getMonitorTime());
		
		
		long start = DateCycleUtil.strToDate(timeStr).getTime()/1000;
		
		//1、删除用户规则下的预警信息
		long userDelCount = cacheClient.zremrangeByScore(userId, String.valueOf(start),userCycleDate.get("endTime").toString());
		log.info("用户规则下--开始时间："+String.valueOf(start)+" 和结束时间:"+userCycleDate.get("endTime").toString());
		log.info("根据规则删除当前用户下的信息条数："+userDelCount);
		//2、删除ip规则下的预警信息
		long ipDelCount = cacheClient.zremrangeByScore(ipAddress, String.valueOf(start),ipCycleDate.get("endTime").toString());
		log.info("Ip规则下--开始时间："+String.valueOf(start)+" 和结束时间:"+ipCycleDate.get("endTime").toString());
		log.info("根据规则删除当前Ip下的信息条数："+ipDelCount);
		//3、删除订单总量规则下的预警信息
		long allDelCount = cacheClient.zremrangeByScore("order_all", String.valueOf(start),allCycleDate.get("endTime").toString());
		log.info("订单总量规则下--开始时间："+String.valueOf(start)+" 和结束时间:"+allCycleDate.get("endTime").toString());
		log.info("根据规则删除订单总量下的信息条数："+allDelCount);
	}
	
	
	public static void main(String args[]){
		Timestamp timestamp = DateUtil.getSysDate();
		//
		System.out.println("timestamp to string:"+timestamp.toString());
		long millis = DateUtil.getCurrentTimeMillis();
		System.out.println("millis:"+millis);
		String currentTime = DateUtil.getCurrentTime();
		System.out.println("currentTime:"+currentTime);
		
		Timestamp scurrtest = new Timestamp(System.currentTimeMillis());
		System.out.println("scurrtest = "+scurrtest);
		long sqlLastTime = scurrtest.getTime();// 直接转换成long
		System.out.println("sqlLastTime = "+sqlLastTime); //毫秒数
		System.out.println("sqlLastTime/1000 = "+sqlLastTime/1000);  
		
		
	}

}
