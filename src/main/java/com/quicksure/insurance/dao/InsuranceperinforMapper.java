package com.quicksure.insurance.dao;

import com.quicksure.insurance.entity.Insuranceperinfor;

public interface InsuranceperinforMapper {
    int deleteByPrimaryKey(Integer insuranceperinforid);

    int insert(Insuranceperinfor record);

    int insertSelective(Insuranceperinfor record);

    Insuranceperinfor selectByPrimaryKey(Integer insuranceperinforid);

    int updateByPrimaryKeySelective(Insuranceperinfor record);

    int updateByPrimaryKey(Insuranceperinfor record);
    
    Insuranceperinfor getInsuranceByorderNo(String orderno);
}