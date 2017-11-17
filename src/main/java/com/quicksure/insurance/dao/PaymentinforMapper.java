package com.quicksure.insurance.dao;


import com.quicksure.insurance.entity.Paymentinfor;

public interface PaymentinforMapper {
    int deleteByPrimaryKey(Integer paymentinforid);

    int insert(Paymentinfor record);

    int insertSelective(Paymentinfor record);

    Paymentinfor selectByPrimaryKey(Integer paymentinforid);

    int updateByPrimaryKeySelective(Paymentinfor record);

    int updateByPrimaryKey(Paymentinfor record);
}