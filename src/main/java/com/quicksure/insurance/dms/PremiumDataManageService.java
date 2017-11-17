package com.quicksure.insurance.dms;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.quicksure.insurance.common.ProcessData;
import com.quicksure.insurance.dao.AgreementinforMapper;
import com.quicksure.insurance.dao.BaseinforMapper;
import com.quicksure.insurance.dao.CoverageinforMapper;
import com.quicksure.insurance.dao.TaxinforMapper;
import com.quicksure.insurance.dao.VhlinforMapper;
import com.quicksure.insurance.entity.Agreementinfor;
import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.Coverageinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;
import com.quicksure.insurance.entity.Taxinfor;
import com.quicksure.insurance.entity.Vhlinfor;
import com.quicksure.insurance.utils.DateFormatUtils;
/**
 * 存储保费计算数据
 * @author 孙小东
 *
 */
@Repository
public class PremiumDataManageService {
	private static final Logger logger = Logger.getLogger(PremiumDataManageService.class);
	@Resource
	private VhlinforMapper vhlinforMapper;
	@Resource
	private BaseinforMapper baseinforMapper;
	@Resource 
	private TaxinforMapper taxinforMapper;
	@Resource
	private CoverageinforMapper coverageinforMapper;
	@Resource
	private AgreementinforMapper agreementinforMapper;
	
	/**
	 * 保费请求之前更新车主姓名到base表
	 */
	public void updatePremium(InsuranceDetailsVO insuranceDetailsVO){
		Baseinfor base = insuranceDetailsVO.getBaseinfor();
		  if(base!=null){
			  baseinforMapper.updateByPrimaryKeySelective(base);
		  }
		
	}
	
	/**
	 * 保费试算返回数据操作
	 * 
	 * @param insuranceDetailsVO
	 * @return
	 */
	public InsuranceDetailsVO savePremiuminfor(
			InsuranceDetailsVO insuranceDetailsVO) {
		logger.info("开始操作保费试算返回的数据，订单号："
				+ insuranceDetailsVO.getBaseinfor().getOrderno());
		int updateBaseinforResult = 0;
		int insertAllCoverage = 0;
		int updateVhlResult = 0;
		int insertTaxinfor = 0;
		int insertAllAgreement=0;
		String taxPremium =null; //车船税总保费
		Float tax=0f;
		Float jqPremium = 0f;//交强总保费
		Float syPremium=0f;//商业总保费
		Float totalPremium=0f;//订单合计保费
		Baseinfor baseinfor = insuranceDetailsVO.getBaseinfor();
		Vhlinfor vhlinfor = insuranceDetailsVO.getVhlinfor();
		Taxinfor taxinfor = insuranceDetailsVO.getTaxinfor();
		List<Coverageinfor> coverageinfors = insuranceDetailsVO	    
				.getCoverageinfors();
		List<Agreementinfor> agreementinfors =insuranceDetailsVO.getAgreementinfors();
		String currentTime=DateFormatUtils.getSystemDate();
		if (vhlinfor != null) {
			vhlinfor.setUpdatetime(currentTime);
			updateVhlResult = vhlinforMapper
					.updateByPrimaryKeySelective(vhlinfor);
		}
		if (taxinfor != null) {
			taxPremium = taxinfor.getSumuptax();
			insertTaxinfor = taxinforMapper.insertSelective(taxinfor);
			baseinfor.setTaxinforid(taxinfor.getTaxinforid());
		}
		if (coverageinfors != null && coverageinfors.size() > 0) {
			coverageinfors =ProcessData.setThreeCoverage(coverageinfors);
			insertAllCoverage = coverageinforMapper
					.insertCoverageBatch(coverageinfors);
		}
		if(agreementinfors!=null && agreementinfors.size()>0){
			insertAllAgreement = agreementinforMapper.insertAgreementinforBatch(agreementinfors);
		}
		if (baseinfor != null) {
			baseinfor.setOrderstate(20);
			baseinfor.setUpdatetime(currentTime);
			baseinfor.setTaxpremium(taxPremium);//车船税总保费
			if(baseinfor.getJqpremium()!=null){
				jqPremium = Float.parseFloat((baseinfor.getJqpremium()==null?0:baseinfor.getJqpremium()).toString());
				tax = Float.parseFloat((baseinfor.getTaxpremium()==null?0:baseinfor.getTaxpremium()).toString());
			}else{
				jqPremium=Float.parseFloat("0");
				tax = Float.parseFloat("0");
			}
			syPremium = Float.parseFloat((baseinfor.getSypremium()==null?0:baseinfor.getSypremium()).toString());
			totalPremium = jqPremium+syPremium+tax;
			baseinfor.setTotalPremium(totalPremium.toString());
			updateBaseinforResult = baseinforMapper
					.updateByPrimaryKeySelective(baseinfor);
		}
		insuranceDetailsVO.setBaseinfor(baseinfor);
		insuranceDetailsVO.setVhlinfor(vhlinfor);
		insuranceDetailsVO.setTaxinfor(taxinfor);
		insuranceDetailsVO.setCoverageinfors(coverageinfors);
		insuranceDetailsVO.setAgreementinfors(agreementinfors);
		logger.info("开始操作保费试算返回的数据，订单号："
				+ insuranceDetailsVO.getBaseinfor().getOrderno()
				+ "处理结果为: 更新baseinfor： " + updateBaseinforResult + "批量插入险别信息： "
				+ insertAllCoverage + "插入车船税信息： " + insertTaxinfor + "更新车辆信息： " 
				+ updateVhlResult   + "插入特约信息: " + insertAllAgreement);
		return insuranceDetailsVO;

	}
	
	public InsuranceDetailsVO updatePremiuminfor(InsuranceDetailsVO insuranceDetailsVO){
		logger.info("开始操作从提核页面返回后重新提交的数据，订单号：" + insuranceDetailsVO.getBaseinfor().getOrderno());
		int updateBaseinforResult = 0;
		int addCoverage = 0;
		int updateCoverage = 0;
		int updateVhlResult = 0;
		int updateTaxinfor = 0;
		int deleteBatchCount=0;
		int updateAllAgreement =0;
		int DeleteAllAggreement=0;
		String taxPremium =null; //车船税总保费
		Float tax=0f;
		Float jqPremium = 0f;//交强总保费
		Float syPremium=0f;//商业总保费
		Float totalPremium=0f;//订单合计保费
		Baseinfor baseinfor = insuranceDetailsVO.getBaseinfor();
		Vhlinfor vhlinfor = insuranceDetailsVO.getVhlinfor();
		Taxinfor taxinfor = insuranceDetailsVO.getTaxinfor();
		List<Coverageinfor> coverageinfors = insuranceDetailsVO.getCoverageinfors();
		List<Agreementinfor> agreementinfors = insuranceDetailsVO.getAgreementinfors();
		String currentTime=DateFormatUtils.getSystemDate();
		if (vhlinfor != null) {
			vhlinfor.setUpdatetime(currentTime);
			updateVhlResult = vhlinforMapper.updateByPrimaryKeySelective(vhlinfor);
		}
		if (taxinfor != null) {
			taxPremium = taxinfor.getSumuptax();
			//这里需改为批量更新(执行insert会填充对象)
//			insertTaxinfor = taxinforMapper.insertSelective(taxinfor);
			updateTaxinfor = taxinforMapper.updateByPrimaryKeySelective(taxinfor);
			baseinfor.setTaxinforid(taxinfor.getTaxinforid());
		}
		if (agreementinfors != null && agreementinfors.size() > 0) {
			String orderNo = agreementinfors.get(0).getOrderno();//获取出订单号
			DeleteAllAggreement = agreementinforMapper.deleteByPrimary(orderNo);//根据订单号删除所有特约
			if(DeleteAllAggreement>0){
				updateAllAgreement = agreementinforMapper.insertAgreementinforBatch(agreementinfors);
			}
		}
		if (coverageinfors != null && coverageinfors.size() > 0) {
			List<Coverageinfor> exist=new ArrayList<Coverageinfor>();//需要remove掉的对象
			List<Coverageinfor> unexistCoverageinfors=new ArrayList<Coverageinfor>();//需要被更新的对象
			List<Coverageinfor> unexist=new ArrayList<Coverageinfor>();//需要新增的对象
			coverageinfors =ProcessData.setThreeCoverage(coverageinfors);//封装三者保额
			for(Coverageinfor cov:coverageinfors){//直接赋值指向地址,后面赋值会造成数据丢失
				unexist.add(cov);
			}
			List<Coverageinfor> listByOrder=coverageinforMapper.getListCoverage(insuranceDetailsVO.getBaseinfor().getOrderno());
			if(listByOrder!=null&&listByOrder.size()>0){
				for(Coverageinfor cov:listByOrder){//直接赋值指向地址,后面赋值会造成数据丢失
					exist.add(cov);
				}
			}
			for(Coverageinfor coverTable:coverageinfors){
				for(Coverageinfor coverOrder:listByOrder){
					if(coverTable.getInsrnccode().equals(coverOrder.getInsrnccode())){
						//exist.add(coverOrder);//页面返回当前订单号存在的险种
						String updatedatatime="";
						try {
							updatedatatime = DateFormatUtils.getSystemDateByYYYYMMDD();
						} catch (ParseException e) {
							e.printStackTrace();
						}
						coverTable.setCoverageid(coverOrder.getCoverageid());//给新的已存在的险种赋ID，然后进行根据ID进行更新 孙小东
						coverTable.setUpdatedatatime(updatedatatime);
						unexistCoverageinfors.add(coverTable);//这些对象需要被更新
						unexist.remove(coverTable);
					}
				}
			}
			for(Coverageinfor coverOrder:listByOrder){
				for(Coverageinfor coverTable:coverageinfors){
					if(coverTable.getInsrnccode().equals(coverOrder.getInsrnccode())){						
						exist.remove(coverOrder);//将查询出来的订单险别，和最新保费计算更新的进行比较，存在的remove掉，不存在的需要删除。			
						}
				}
			}
			
			/*for(Coverageinfor cover:exist){
//				unexist.remove(cover);//当前订单号不存在的险种(这里删除不成功)
				listByOrder.remove(cover);//从之前订单号所属险种中去除这次勾选的险种
			}
			for(Coverageinfor cover:unexistCoverageinfors){
				unexist.remove(cover);
			}*/
			//这里需改为批量更新(执行insert会填充对象)ID不存在就insert,存在就update
//			insertAllCoverage = coverageinforMapper.insertCoverageBatch(coverageinfors);
			if(unexist!=null&&unexist.size()>0){//unexist会报错
				addCoverage=coverageinforMapper.insertCoverageBatch(unexist);//新增第一次未勾选第二次勾选的险种
			}
			if(unexistCoverageinfors!=null&&unexistCoverageinfors.size()>0){
				updateCoverage=coverageinforMapper.updateListCoverageBatch(unexistCoverageinfors);//更新两次都勾选的险种
			}
			if(exist!=null&&exist.size()>0){
				deleteBatchCount=coverageinforMapper.deleteCoverageBatch(exist);//删除第一次勾选第二次没勾选的险别
			}
			
		}
		
		if (baseinfor != null) {
			baseinfor.setOrderstate(20);
			baseinfor.setUpdatetime(currentTime);			
			if(taxinfor== null){
				baseinfor.setTaxinforid(null);//如果车船税的对象为空，清空baseinfor对象里面所对应的tax id 
			}else{
				baseinfor.setTaxpremium(taxPremium);//车船税总保费
			}
			if(baseinfor.getJqpremium()!=null){
				jqPremium = Float.parseFloat((baseinfor.getJqpremium()==null?0:baseinfor.getJqpremium()).toString());
				tax = Float.parseFloat((baseinfor.getTaxpremium()==null?0:baseinfor.getTaxpremium()).toString());
			}else{
				jqPremium=Float.parseFloat("0");
				tax = Float.parseFloat("0");
				baseinfor.setJqpremium("0");
				baseinfor.setTaxpremium("0");
			}
			syPremium = Float.parseFloat((baseinfor.getSypremium()==null?0:baseinfor.getSypremium()).toString());
			
			totalPremium = jqPremium+syPremium+tax;
			String total= String.valueOf(totalPremium);
			baseinfor.setTotalPremium(total);
			updateBaseinforResult = baseinforMapper.updateByPrimaryKey(baseinfor);
		}
		insuranceDetailsVO.setBaseinfor(baseinfor);
		insuranceDetailsVO.setVhlinfor(vhlinfor);
		insuranceDetailsVO.setTaxinfor(taxinfor);
		insuranceDetailsVO.setCoverageinfors(coverageinfors);
		
		logger.info("开始操作提核页面返回保费试算重新提交的数据，订单号："
				+ insuranceDetailsVO.getBaseinfor().getOrderno()
				+ "处理结果为: 更新baseinfor： " + updateBaseinforResult + "批量新增险别信息： "
				+ addCoverage + "批量修改险别信息： " + updateCoverage + "修改车船税信息： "
				+ updateTaxinfor + "更新车辆信息： "+ updateVhlResult + "删除未选中险别: "
				+ deleteBatchCount + "更新特约信息: " + updateAllAgreement);
		return insuranceDetailsVO;
	}
	
}
