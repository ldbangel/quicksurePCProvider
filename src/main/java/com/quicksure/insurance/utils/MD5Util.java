package com.quicksure.insurance.utils;

import com.quicksure.insurance.common.ProcessData;
import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;


public class MD5Util {

	public static void testInsuranceVo(){
		
		InsuranceDetailsVO insuranceDetails=new InsuranceDetailsVO();
		Baseinfor baseinfor=new Baseinfor();
		baseinfor.setOrderno("123");
		insuranceDetails.setBaseinfor(baseinfor);
		Baseinfor binfor=new Baseinfor();
		binfor.setOrderno("ldb");
		
		insuranceDetails=ProcessData.setObjValuetoinsuranceDetails(binfor, insuranceDetails);
		System.out.println(insuranceDetails+"===================");
	}
	
	public static void main(String[] args) {
		testInsuranceVo();
	}
	
}
