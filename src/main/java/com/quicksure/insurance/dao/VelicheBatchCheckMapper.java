package com.quicksure.insurance.dao;

import java.util.List;
import java.util.Map;

import com.quicksure.insurance.entity.InsuranceDetailsVO;

public interface VelicheBatchCheckMapper {
	/*
	 * 车辆批量查询的映射接口类
	 */
	
	List<InsuranceDetailsVO> velicheBatchQuery(Map<String, Object> map);
	
	int searchPolicyCount(Map<String, Object> map);
	
	List<InsuranceDetailsVO> CSRExportExcel(Map<String, Object> map);
	
	List<InsuranceDetailsVO> interfaceslogsQuery(List<String> list);
	
}
