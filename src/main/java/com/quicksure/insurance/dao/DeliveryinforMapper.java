package com.quicksure.insurance.dao;

import com.quicksure.insurance.entity.Deliveryinfor;

public interface DeliveryinforMapper {
    int deleteByPrimaryKey(Integer deliveryid);

    int insert(Deliveryinfor record);

    int insertSelective(Deliveryinfor record);

    Deliveryinfor selectByPrimaryKey(Integer deliveryid);

    int updateByPrimaryKeySelective(Deliveryinfor record);

    int updateByPrimaryKey(Deliveryinfor record);
    
    int getDevlieryIdByCreateTime();
    
    Deliveryinfor getDevlieryByorderNo(String orderno);
}