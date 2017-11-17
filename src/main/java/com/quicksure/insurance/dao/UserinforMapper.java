package com.quicksure.insurance.dao;

import com.quicksure.insurance.entity.Userinfor;

public interface UserinforMapper {
    int deleteByPrimaryKey(Integer userid);

    int insert(Userinfor record);

    int insertSelective(Userinfor record);

    Userinfor selectByPrimaryKey(Integer userid);

    int updateByPrimaryKeySelective(Userinfor record);

    int updateByPrimaryKey(Userinfor record);
    
    Userinfor selectByName(Userinfor record);
    
    int updatePasswordByName(Userinfor record);
}