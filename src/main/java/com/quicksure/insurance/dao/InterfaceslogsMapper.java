package com.quicksure.insurance.dao;

import com.quicksure.insurance.entity.Interfaceslogs;
import com.quicksure.insurance.entity.InterfaceslogsWithBLOBs;

public interface InterfaceslogsMapper {
    int deleteByPrimaryKey(Integer interfaceslogsid);

    int insert(InterfaceslogsWithBLOBs record);

    int insertSelective(InterfaceslogsWithBLOBs record);

    InterfaceslogsWithBLOBs selectByPrimaryKey(Integer interfaceslogsid);

    int updateByPrimaryKeySelective(InterfaceslogsWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(InterfaceslogsWithBLOBs record);

    int updateByPrimaryKey(Interfaceslogs record);
}