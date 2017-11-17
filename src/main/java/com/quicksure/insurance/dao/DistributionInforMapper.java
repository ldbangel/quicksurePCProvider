package com.quicksure.insurance.dao;


import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;

public interface DistributionInforMapper {
	
	InsuranceDetailsVO selectDistributionInfor(Baseinfor baseinfor);
}