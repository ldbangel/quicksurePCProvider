package com.quicksure.insurance.dao;

import com.quicksure.insurance.entity.Vhlinfor;

public interface VhlinforMapper {
    int deleteByPrimaryKey(Integer vhiinforid);

    int insert(Vhlinfor record);

    int insertSelective(Vhlinfor record);

    Vhlinfor selectByPrimaryKey(Integer vhiinforid);

    int updateByPrimaryKeySelective(Vhlinfor record);

    int updateByPrimaryKey(Vhlinfor record);
    
    Vhlinfor csrgetVhlinforByorderno(String orderno);
}