package com.quicksure.insurance.dao;

import java.util.List;

import com.quicksure.insurance.entity.Agreementinfor;

public interface AgreementinforMapper {
    int deleteByPrimaryKey(Integer agreementid);

    int insert(Agreementinfor record);

    int insertSelective(Agreementinfor record);

    Agreementinfor selectByPrimaryKey(Integer agreementid);

    int updateByPrimaryKeySelective(Agreementinfor record);

    int updateByPrimaryKey(Agreementinfor record);
    
    int insertAgreementinforBatch(List<Agreementinfor> agreementinfors );
    
    int updateAgreementinforBatch(List<Agreementinfor> agreementinfors);
    
    int deleteByPrimary(String orderNo);
    
    List<Agreementinfor> getListAgreement(String orderNo);
    
    int deleteAgreementBatch(List<Agreementinfor> agreementinfors);
}