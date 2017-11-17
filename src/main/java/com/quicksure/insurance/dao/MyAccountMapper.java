package com.quicksure.insurance.dao;

import java.util.List;
import java.util.Map;

import com.quicksure.insurance.entity.InsuranceDetailsVO;

public interface MyAccountMapper {
	//获取订单总数
	public int getMyOrdersCount0(Map<String,Object> map);
	
	//获取待支付订单总数
	public int getMyOrdersCount1(Map<String,Object> map);
	
	//获取已支付订单总数
	public int getMyOrdersCount2(Map<String,Object> map);
	
	//获取暂存订单总数
	public int getMyOrdersCount3(Map<String,Object> map);
	
	//获取已撤销订单总数
	public int getMyOrdersCount4(Map<String,Object> map);
	
	//获取订单数据
	public List<InsuranceDetailsVO> getMyOrders(Map<String,Object> map);
	
	//获取订单前十条数据
	public List<InsuranceDetailsVO> getMyOrdersTopTen0(Map<String,Object> map);
		
	//获取待支付前十条数据
	public List<InsuranceDetailsVO> getMyOrdersTopTen1(Map<String,Object> map);
	
	//获取已支付前十条数据
	public List<InsuranceDetailsVO> getMyOrdersTopTen2(Map<String,Object> map);
	
	//获取暂存前十条数据
	public List<InsuranceDetailsVO> getMyOrdersTopTen3(Map<String,Object> map);
	
	//获取已撤销前十条数据
	public List<InsuranceDetailsVO> getMyOrdersTopTen4(Map<String,Object> map);
	
	//继续支付时获取一条订单数据
	public InsuranceDetailsVO getInsuranceByOrderNo(String orderno);
}
