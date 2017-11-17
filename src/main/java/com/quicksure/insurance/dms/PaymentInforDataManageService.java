package com.quicksure.insurance.dms;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.quicksure.insurance.dao.BaseinforMapper;
import com.quicksure.insurance.dao.PaymentinforMapper;
import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;
import com.quicksure.insurance.entity.Paymentinfor;
import com.quicksure.insurance.utils.DateFormatUtils;

@Repository
public class PaymentInforDataManageService {
	private static final Logger logger = Logger.getLogger(PaymentInforDataManageService.class);
	
	@Resource
	private BaseinforMapper baseinforMapper;
	@Resource
	private PaymentinforMapper paymentinforMapper;
	
	public int udpatePaymentCompleteData(Baseinfor baseinfor){
		logger.info("保单生成---根据投保单号查询订单号！--开始");
		Baseinfor base = baseinforMapper.selectByApplicationNo(baseinfor); //根据投保单号查询订单号
		logger.info("保单生成---订单号："+base.getOrderno());
		String updatetimes=DateFormatUtils.getSystemDate();
		baseinfor.setUpdatetime(updatetimes);//生成保单号之后，添加更新时间
		baseinfor.setOrderstate(60);
		baseinfor.setOrderno(base.getOrderno()); 
		logger.info("保单生成---订单状态："+baseinfor.getOrderstate());
		int result = baseinforMapper.updateByPrimaryKeySelective(baseinfor); //更新支付后baseinfor表的信息
		logger.info("保单生成---更新数据库结果："+result);
		return result;
	}
	
	/**
	 * 支付成功之后，更新payment和baseinfor表数据
	 * @param insuranceDetailsVO
	 * @return
	 */
	public InsuranceDetailsVO updatePaymentAndBaseinfor(InsuranceDetailsVO insuranceDetailsVO){
		logger.info("开始支付成功之后，更新payment和baseinfor表数据 ");
		if(insuranceDetailsVO!=null&&insuranceDetailsVO.getBaseinfor()!=null&&insuranceDetailsVO.getPaymentinfor()!=null){
			String orderNo=insuranceDetailsVO.getBaseinfor().getOrderno();
			logger.info("订单号： "+orderNo);
			String updatetimes=DateFormatUtils.getSystemDate();
			insuranceDetailsVO.getBaseinfor().setUpdatetime(updatetimes);
			logger.info("开始更新订单状态前,订单状态为： "+insuranceDetailsVO.getBaseinfor().getOrderstate());
			if(insuranceDetailsVO.getBaseinfor().getOrderstate()!=60){
				insuranceDetailsVO.getBaseinfor().setOrderstate(50);
			}			
			insuranceDetailsVO.getPaymentinfor().setUpdatetimes(updatetimes);
			logger.info("开始更新paymentinfor表");
			int updatePaymentInfor=paymentinforMapper.updateByPrimaryKeySelective(insuranceDetailsVO.getPaymentinfor());
			logger.info("结束更新paymentinfor表，结果为："+updatePaymentInfor);
			logger.info("开始更新Baseinfor表");
			int updateBaseinfor=baseinforMapper.updateByPrimaryKeySelective(insuranceDetailsVO.getBaseinfor());
			logger.info("结束更新Baseinfor表，结果为："+updateBaseinfor);
		}
		logger.info("结束支付成功之后，更新payment和baseinfor表数据 ");
		return insuranceDetailsVO;
	}

	public Baseinfor getBaseInfor(String orderNo) {

		return baseinforMapper.selectByPrimaryKey(orderNo);
	}

	public Paymentinfor getPaymentinfor(Baseinfor baseinfor) {
		int paymentinforId = baseinfor.getPaymentinforid();
		return paymentinforMapper.selectByPrimaryKey(paymentinforId);
	}
	public void operatePaymentInfo(InsuranceDetailsVO insuranceDetails){
		int paymentInforId = -1;
		
		int isSuccess = paymentinforMapper.insertSelective(insuranceDetails.getPaymentinfor());//插入支付信息
		if(isSuccess>0){
			paymentInforId = insuranceDetails.getPaymentinfor().getPaymentinforid();
			logger.info("支付ID---Payment ID is :"+paymentInforId);
		}
		
		//保单已生成
		if(paymentInforId>-1){
			insuranceDetails.getBaseinfor().setPaymentinforid(paymentInforId);
			insuranceDetails.getBaseinfor().setOrderstate(40); //华安返回支付单号，说明华安已生成保单了，所以保单状态改为--40：调用支付接口成功
			baseinforMapper.updateByPrimaryKeySelective(insuranceDetails.getBaseinfor()); //将paymentinforId更新到baseinfor表
		}
	}
}
