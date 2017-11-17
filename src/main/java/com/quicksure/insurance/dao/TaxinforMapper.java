package com.quicksure.insurance.dao;

import com.quicksure.insurance.entity.Taxinfor;

public interface TaxinforMapper {
    int deleteByPrimaryKey(Integer taxinforid);

    int insert(Taxinfor record);

    int insertSelective(Taxinfor record);

    Taxinfor selectByPrimaryKey(Integer taxinforid);

    int updateByPrimaryKeySelective(Taxinfor record);

    int updateByPrimaryKey(Taxinfor record);
}