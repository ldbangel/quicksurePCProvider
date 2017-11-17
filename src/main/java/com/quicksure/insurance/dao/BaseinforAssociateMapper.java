package com.quicksure.insurance.dao;

import java.util.List;
import java.util.Map;

import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;

public interface BaseinforAssociateMapper {
	 InsuranceDetailsVO getOrderListinformation3(Map<String,Object> map);
	 List<String> getALLOrderNo(List<Integer> list);
	 List<InsuranceDetailsVO> getListInsurance(Map<String,Object> map);
	 List<Baseinfor> selectAllOrder();
}
