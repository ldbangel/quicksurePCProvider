package com.quicksure.insurance.dao;

import java.util.List;

import com.quicksure.insurance.entity.Dptinfor;

public interface DptinforMapper {
    int deleteByPrimaryKey(Integer deptinforid);

    int insert(Dptinfor record);

    int insertSelective(Dptinfor record);

    Dptinfor selectByPrimaryKey(Integer deptinforid);

    int updateByPrimaryKeySelective(Dptinfor record);

    int updateByPrimaryKey(Dptinfor record);
    
     List<Dptinfor> selectALL();
}