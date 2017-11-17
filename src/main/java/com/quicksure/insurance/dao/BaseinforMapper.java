package com.quicksure.insurance.dao;

import java.util.List;
import java.util.Map;

import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.Coverageinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;
import com.quicksure.insurance.entity.Vhlinfor;

public interface BaseinforMapper {
    int deleteByPrimaryKey(String orderno);

    int insert(Baseinfor record);

    int insertSelective(Baseinfor record);

    Baseinfor selectByPrimaryKey(String orderno);

    int updateByPrimaryKeySelective(Baseinfor record);

    int updateByPrimaryKey(Baseinfor record);
    
    Baseinfor selectByApplicationNo(Baseinfor baseinfor);
    
    String getOrderNo(Map<String, Object> map);
    
    List<Baseinfor> selectByTimes(Map<String, String> map);
    List<Baseinfor> selectAllOrder();
    
    int updateMultiplePolicyStatus(List<Baseinfor> baseinforList);
    
    int updateOrderState(Baseinfor record);
    
    List<Baseinfor> getBaseinforByVhlinfor(Vhlinfor vhlinfor);
    
    List<Coverageinfor> getListCoverageinfors(Map<String,Object> map);
    
    List<Map<String,Object>> getOrderListinformation(Map<String,Object> map);
    
    List<InsuranceDetailsVO> getOrderListinformation2(Map<String,Object> map);
}