package com.quicksure.insurance.dms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.quicksure.insurance.dao.BaseinforMapper;
import com.quicksure.insurance.dao.VhlinforMapper;
import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;
import com.quicksure.insurance.entity.Vhlinfor;
import com.quicksure.insurance.util.StringUtils;
import com.quicksure.insurance.utils.DateFormatUtils;

@Repository
public class VHLDataManageService {
	private static final Logger logger = Logger
			.getLogger(VHLDataManageService.class);

	@Resource
	private BaseinforMapper baseinforMapper;
	@Resource
	private VhlinforMapper vhlinforMapper;
	
	
	/**
	 * 存储首页的信息
	 * 孙小东
	 * @param insuranceDetailsVO
	 * @return
	 */
	public InsuranceDetailsVO savefirstScreenData(
			InsuranceDetailsVO insuranceDetailsVO) {
		logger.info(" Start save first Screen Data  ");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Baseinfor baseinfor = null;
		Vhlinfor vhlinfor = null;
		int vhlinforresult = 0;
		int baseinforresult = 0;
		String orderNo = "";
		int vhlinforId = 0;
		String dataTime = DateFormatUtils.getSystemDate();
		if (insuranceDetailsVO != null) {
			baseinfor = insuranceDetailsVO.getBaseinfor();
			vhlinfor = insuranceDetailsVO.getVhlinfor();
		}
		resultMap.put("orderNamePre", "LD");
		resultMap.put("number", 8);
		resultMap.put("orderNo", "@orderNo");
		logger.info("Starts to generate orderNo ");
		orderNo = baseinforMapper.getOrderNo(resultMap)+StringUtils.getValidateNo(true,4);;// 生成订单号
		logger.info("Ends to generate orderNo the orderNo is " + orderNo);
		if (StringUtils.checkStringEmpty(orderNo)) {
			logger.info(" Starts save vehicle information first  ");
			vhlinforresult = vhlinforMapper.insertSelective(vhlinfor);
			logger.info(" Ends vehicle information result is  "
					+ vhlinforresult);
			if (vhlinforresult == 1) {
				vhlinforId = vhlinfor.getVhiinforid();
			}
			if (baseinfor != null) {
				baseinfor.setOrderno(orderNo);
				baseinfor.setVhlinforid(vhlinforId);
			}
			logger.info(" Starts save base  information the vhlinforId is "
					+ vhlinforId);
			baseinfor.setOrderstate(10);//设置订单状态
			baseinforresult = baseinforMapper.insertSelective(baseinfor);
			logger.info(" Ends save vehicle base result is  " + baseinforresult);

		}		
		logger.info(" End save first Screen Data  ");
		return insuranceDetailsVO;
	}
	
	/**
	 * 更新首页的信息
	 * 刘东波
	 * @param insuranceDetailsVO
	 * @return
	 */
	public InsuranceDetailsVO updatefirstScreenData(InsuranceDetailsVO insuranceDetailsVO) {
		logger.info("start update first screen data!");
		int baseinforresult = 0;
		int vhlinforresult = 0;
		Baseinfor baseinfor = insuranceDetailsVO.getBaseinfor();
		Vhlinfor vhlinfor = insuranceDetailsVO.getVhlinfor();
		
		baseinforresult = baseinforMapper.updateByPrimaryKeySelective(baseinfor);
		vhlinforresult = vhlinforMapper.updateByPrimaryKeySelective(vhlinfor);		
		logger.info("end update first screen data!");
		return insuranceDetailsVO;
	}
	
	
	/**
	 * 更新车辆信息
	 * 孙小东
	 * @param insuranceDetailsVO
	 * @return
	 */
	public InsuranceDetailsVO updateVehicleData(InsuranceDetailsVO insuranceDetailsVO){
		 String responseTime=DateFormatUtils.getSystemDate();
		 insuranceDetailsVO.getVhlinfor().setUpdatetime(responseTime);
		 int result=vhlinforMapper.updateByPrimaryKey(insuranceDetailsVO.getVhlinfor());
		 int updatebaseinfor=baseinforMapper.updateByPrimaryKeySelective(insuranceDetailsVO.getBaseinfor());
		 return insuranceDetailsVO;
		
	}
	/**
	 * 更新基本信息
	 */
	public InsuranceDetailsVO updateBaseData(InsuranceDetailsVO insuranceDetailsVO){
		 String responseTime=DateFormatUtils.getSystemDate();
		 insuranceDetailsVO.getBaseinfor().setUpdatetime(responseTime);
		 int result=baseinforMapper.updateByPrimaryKey(insuranceDetailsVO.getBaseinfor());	
		 return insuranceDetailsVO;
	}
	
	
	/**
	 * 存储CSR车辆信息并生成订单号
	 * 李长立
	 * @param insuranceDetailsVO
	 * @return
	 */
	public InsuranceDetailsVO saveVhlData(
			InsuranceDetailsVO insuranceDetailsVO) {
		logger.info("");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Baseinfor baseinfor = new Baseinfor();
		Vhlinfor vhlinfor = null;
		int vhlinforresult = 0;
		int baseinforresult = 0;
		String orderNo = "";
		int vhlinforId = 0;
		String dataTime = DateFormatUtils.getSystemDate();
		if (insuranceDetailsVO != null) {
			vhlinfor = insuranceDetailsVO.getVhlinfor();
		}
		resultMap.put("orderNamePre", "DL");
		resultMap.put("number", 8);
		resultMap.put("orderNo", "@orderNo");
		logger.info("Starts to generate orderNo ");
		orderNo = baseinforMapper.getOrderNo(resultMap)+StringUtils.getValidateNo(true,4);// 生成订单号
		logger.info("Ends to generate orderNo the orderNo is " + orderNo);
		try {
			
		
		if (StringUtils.checkStringEmpty(orderNo)) {
			logger.info(" Starts save vehicle information first  ");
			vhlinforresult = vhlinforMapper.insertSelective(vhlinfor);
			logger.info(" Ends vehicle information result is  "
					+ vhlinforresult);
			if (vhlinforresult == 1) {
				vhlinforId = vhlinfor.getVhiinforid();
			}
			if (baseinfor != null) {
				baseinfor.setOrderno(orderNo);
				baseinfor.setVhlinforid(vhlinforId);
			}
			logger.info(" Starts save base  information the vhlinforId is "
					+ vhlinforId);
			baseinfor.setOrderstate(10);//设置订单状态
			baseinforresult = baseinforMapper.insertSelective(baseinfor);
			logger.info(" Ends save vehicle base result is  " + baseinforresult);
			insuranceDetailsVO.setBaseinfor(baseinfor);
		}		
		} catch (Exception e) {
			StringWriter sw = new StringWriter();  
			  e.printStackTrace(new PrintWriter(sw, true));  
			  String str = sw.toString();
			logger.info("提交车辆信息失败: "+str);
		}
		logger.info(" End save first Screen Data  ");
		return insuranceDetailsVO;
	}
	
	public Vhlinfor csrgetVhlinforByorderno(String orderno){
		
		return vhlinforMapper.csrgetVhlinforByorderno(orderno);
	}
}
