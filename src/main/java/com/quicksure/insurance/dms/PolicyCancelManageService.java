package com.quicksure.insurance.dms;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.quicksure.insurance.dao.BaseinforMapper;
import com.quicksure.insurance.entity.InsuranceDetailsVO;

@Repository
public class PolicyCancelManageService {
	private static final Logger logger = Logger.getLogger(PolicyCancelManageService.class);
	@Resource
	private BaseinforMapper baseinfroMapper;

	@Transactional
	public String updateBaseinforOrderState(InsuranceDetailsVO insurancedetail) {
		// 成功返回并且交易明细状态为成功(目前只根据头部返回消息处理),更改订单状态
		String msg = null;
		insurancedetail.getBaseinfor().setOrderstate(80);// 已撤销状态
		int result = baseinfroMapper.updateOrderState(insurancedetail.getBaseinfor());
		if (result == 1) {
			msg="success";
			logger.info("请求撤销保单成功并修改订单状态为已撤销成功  单号为"+insurancedetail.getBaseinfor().getOrderno());
		} else {
			msg="fail";
			logger.info("请求撤销保单成功并修改订单状态为已撤销失败  单号为"+insurancedetail.getBaseinfor().getOrderno());
		}
		return msg;
	}

}
