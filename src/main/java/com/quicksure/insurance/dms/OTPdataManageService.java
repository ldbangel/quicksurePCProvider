package com.quicksure.insurance.dms;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.quicksure.insurance.dao.OTPGenerationMapper;
import com.quicksure.insurance.entity.OTPGeneration;

@Repository
public class OTPdataManageService {
@Resource
private OTPGenerationMapper otpGenerationMapper;
private static final Logger logger = Logger
.getLogger(OTPGenerationMapper.class);
	/**
	 * 存储短信验证码等信息
	 * 
	 * @param otpGeneration
	 */
	public OTPGeneration saveOTPinfor(OTPGeneration otpGeneration) {
		if (otpGeneration != null) {
			logger.info("开始存储短信验证信息，电话号码为: "+otpGeneration.getPhoneno());
			int result=otpGenerationMapper.insertSelective(otpGeneration);
			logger.info("结束存储短信验证信息，返回结果为: "+result);
		}
		return otpGeneration;

	}
	
	/**
	 * 删除短信验证码
	 * 
	 * @param otpGeneration
	 */
	public int deleteOTPinfor(OTPGeneration otpGeneration) {
		int result=0;
		if (otpGeneration != null) {
			logger.info("开始删除短信验证信息，电话号码为: "+otpGeneration.getPhoneno());
//			int result=otpGenerationMapper.insertSelective(otpGeneration);
			result=otpGenerationMapper.deleteByPrimaryKey(otpGeneration.getOtpid());
			logger.info("结束删除短信验证信息，处理结果为: "+result);
		}
		return result;

	}

}
