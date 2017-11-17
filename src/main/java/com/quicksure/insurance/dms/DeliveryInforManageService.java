package com.quicksure.insurance.dms;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.quicksure.insurance.dao.BaseinforMapper;
import com.quicksure.insurance.dao.DeliveryinforMapper;
import com.quicksure.insurance.dao.DistributionInforMapper;
import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;

@Repository
public class DeliveryInforManageService {
	private static final Logger logger = Logger.getLogger(DeliveryInforManageService.class);
	@Resource
	private  DeliveryinforMapper deliveryinforMapper;
	@Resource
	private BaseinforMapper baseinfroMapper;
	
	@Resource
	private DistributionInforMapper distributionInforMapper;
	
	
	@Transactional
	public void insertDeliveryData(InsuranceDetailsVO insurancedetail){
		/*//数据插入配送表
		int result=deliveryinforMapper.insert(insurancedetail.getDeliveryinfor());//将对象添加到数据库
		if(result==1){*/
			Baseinfor baseinfor=new Baseinfor();
			baseinfor.setOrderno(insurancedetail.getBaseinfor().getOrderno());
			/*baseinfor.setDeliveryinforid(deliveryinforMapper.getDevlieryIdByCreateTime());*/
			baseinfor.setOrderstate(90);
			/*logger.info("ludimb_deliveryinfor : 配送数据插入成功");*/
			int result1=baseinfroMapper.updateOrderState(baseinfor);//根据订单来修改订单状态
			if(result1==1){
				logger.info("修改baseinfor表状态为配送成功.");
			}else{
				logger.info("修改baseinfor表状态为配送异常.");
			}
		/*}else{
			logger.info("ludimb_deliveryinfor : 配送数据插入失败");
		}*/
	}
	/**
	 * 调用配送接口前修改订单状态为70
	 */
	public int updateOrdersdeliver(InsuranceDetailsVO ins){
		Baseinfor baseinfor=new Baseinfor();
		baseinfor.setOrderno(ins.getBaseinfor().getOrderno());
		baseinfor.setOrderstate(70);
		int result1=baseinfroMapper.updateOrderState(baseinfor);
		if(result1==1){
			logger.info("调用配送接口前，将订单状态修改为70成功.");
		}else{
			logger.info("调用配送接口前，将订单状态修改为70失败.");
		}
		
		return result1;		
	}
		
	
	/**
	 * 
	* @Title: selectDistributionInfor 
	* @Description: 获取配送信息
	* @param @param baseinfor
	* @param @return    设定文件 
	* @return InsuranceDetailsVO    返回类型 
	* @throws
	 */
	public InsuranceDetailsVO selectDistributionInfor(Baseinfor baseinfor){
		InsuranceDetailsVO ins=distributionInforMapper.selectDistributionInfor(baseinfor);
		
		return ins;
		
	}
	
}
