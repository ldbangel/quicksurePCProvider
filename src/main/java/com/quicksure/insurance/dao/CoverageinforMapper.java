package com.quicksure.insurance.dao;

import java.util.List;

import com.quicksure.insurance.entity.Coverageinfor;

public interface CoverageinforMapper {
    int deleteByPrimaryKey(Integer coverageid);

    int insert(Coverageinfor record);

    int insertSelective(Coverageinfor record);

    Coverageinfor selectByPrimaryKey(Integer coverageid);

    int updateByPrimaryKeySelective(Coverageinfor record);

    int updateByPrimaryKey(Coverageinfor record);
    
    int insertCoverageBatch(List<Coverageinfor> coverageinfors );
    
    int updateCoverageBatch(List<Coverageinfor> coverageinfors);
    
    int updateListCoverageBatch(List<Coverageinfor> coverageinfors);
    
    List<Coverageinfor> getListCoverage(String baseinforOrderNo);
    
    int deleteCoverageBatch(List<Coverageinfor> coverageinfors);
}