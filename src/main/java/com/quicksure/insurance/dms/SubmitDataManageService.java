package com.quicksure.insurance.dms;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.quicksure.insurance.constants.LudiConstants;
import com.quicksure.insurance.dao.AgreementinforMapper;
import com.quicksure.insurance.dao.BaseinforMapper;
import com.quicksure.insurance.dao.CoverageinforMapper;
import com.quicksure.insurance.dao.DeliveryinforMapper;
import com.quicksure.insurance.dao.InsuranceperinforMapper;
import com.quicksure.insurance.dao.TaxinforMapper;
import com.quicksure.insurance.entity.Agreementinfor;
import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.Coverageinfor;
import com.quicksure.insurance.entity.Deliveryinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;
import com.quicksure.insurance.entity.Insuranceperinfor;
import com.quicksure.insurance.entity.Taxinfor;
/**
 * 存提核数据
 * @author lenny.li001
 *
 */
@Repository
public class SubmitDataManageService {

	private static final Logger logger = Logger.getLogger(PremiumDataManageService.class);
	
	@Resource
	private BaseinforMapper baseinforMapper;
	@Resource
	private InsuranceperinforMapper insuranceperinforMapper;
	@Resource
	private DeliveryinforMapper deliveryinforMapper;
	@Resource
	private CoverageinforMapper coverageinforMapper;
	@Resource
	private AgreementinforMapper agreementinforMapper;
	@Resource
	private TaxinforMapper taxinforMapper;
	/**
	 * 提核请求的数据
	 * @param insuranceDetailsVO
	 * @return
	 */
	public InsuranceDetailsVO AddSubmitinfor(InsuranceDetailsVO insuranceDetailsVO) {
		logger.info("开始操作提核请求的数据，投保人："
				+ insuranceDetailsVO.getInsuranceperinfor().getApplicationname());
		int insuranceperResult = 0;
		int deliveryResult = 0;//配送
		Insuranceperinfor insuranceperinfor = insuranceDetailsVO.getInsuranceperinfor();
		Deliveryinfor deliveryinfor = insuranceDetailsVO.getDeliveryinfor();
		if(insuranceperinfor!=null){
			if(insuranceDetailsVO.getUserinfor().getPageFlag()>=5 
					|| insuranceDetailsVO.getInsuranceperinfor().getInsuranceperinforid()>0){
				insuranceperResult = insuranceperinforMapper.updateByPrimaryKeySelective(insuranceperinfor);
				deliveryinfor.setUpdatetime(new Date());
				deliveryResult = deliveryinforMapper.updateByPrimaryKeySelective(deliveryinfor);
				logger.info("处理完毕提核请求的数据，投保人："
						+ insuranceperinfor.getApplicationname()
						+ "更新insuranceperinfor： "+ insuranceperinfor.getInsuranceperinforid() );
			}else{
				if(insuranceperinfor.getInsuranceperinforid()==null||(insuranceperinfor.getInsuranceperinforid()!=null&&insuranceperinfor.getInsuranceperinforid()==0)){
					insuranceperResult = insuranceperinforMapper.insertSelective(insuranceperinfor);
				}				
				deliveryinfor.setCreatedatetime(new Date());
				if(deliveryinfor.getDeliveryid()==null||(deliveryinfor.getDeliveryid()!=null&&deliveryinfor.getDeliveryid()==0)){
					deliveryResult = deliveryinforMapper.insertSelective(deliveryinfor);
				}				
				Baseinfor baseinfor = insuranceDetailsVO.getBaseinfor();
				baseinfor.setInsuranceperinforid(insuranceperinfor.getInsuranceperinforid());
				baseinfor.setDeliveryinforid(deliveryinfor.getDeliveryid());
				int BaseinforID =  baseinforMapper.updateByPrimaryKeySelective(baseinfor);
				logger.info("处理完毕提核请求的数据，投保人："
						+ insuranceperinfor.getApplicationname()
						+ "处理结果为: 增加进insuranceperinfor： "+ insuranceperResult+" 人员信息更新进Base表 ："+ BaseinforID );
			}
			insuranceDetailsVO.getUserinfor().setPageFlag(5);
		}
		
		return insuranceDetailsVO ;
    
	}
	
		
	/**
	 * 提核返回数据处理
	 * @param insuranceDetailsVO
	 * @return
	 */
	public void saveSubmitinfor(Baseinfor baseinfor) {
		logger.info("开始操作提核返回的数据，订单号："
				+ baseinfor.getOrderno());
		int updateBaseinforResult = 0;//返回标志
		int  orderstate = 30;
		//赋值成功状态
//		insuranceDetailsVO.getBaseinfor().setOrderstate(orderstate);
//		Baseinfor baseinfor = insuranceDetailsVO.getBaseinfor();
		
		if (baseinfor != null) {
			baseinfor.setOrderstate(orderstate);
			updateBaseinforResult = baseinforMapper
					.updateByPrimaryKeySelective(baseinfor);
		}
//		if(insuranceperinfor!=null){
//			insuranceperResult = insuranceperinforMapper.insertSelective(insuranceperinfor);
//		}
//		insuranceDetailsVO.setBaseinfor(baseinfor);
		logger.info("处理完毕提核返回的数据，订单号："
				+ baseinfor.getOrderno()
				+ "处理结果为: 更新baseinfor： "+ updateBaseinforResult);
//		return insuranceDetailsVO;

	}
	
	public void insertCoverageBatch(HttpServletRequest request,List<Coverageinfor> coverageinfors,Baseinfor baseinfor,Taxinfor taxinfor,List<Agreementinfor> agreementinfors){
		List<Coverageinfor> listCoverge=coverageinforMapper.getListCoverage(baseinfor.getOrderno());
		List<Agreementinfor> listAgreement=agreementinforMapper.getListAgreement(baseinfor.getOrderno());
		
		if (coverageinfors != null && coverageinfors.size() > 0) {
			for(int i=0;coverageinfors.size()>i;i++){
				if(coverageinfors.get(i).getInsrnccode().equals(LudiConstants.THREEPARTYINSUEANCE)){
					if(coverageinfors.get(i).getSuminsured().equals("306006004")){
						 coverageinfors.get(i).setSuminsured("50000");
					 }else if(coverageinfors.get(i).getSuminsured().equals("306006005")){
						 coverageinfors.get(i).setSuminsured("100000");
					 }else if(coverageinfors.get(i).getSuminsured().equals("306006006")){
						 coverageinfors.get(i).setSuminsured("200000");
					 }else if(coverageinfors.get(i).getSuminsured().equals("306006007")){
						 coverageinfors.get(i).setSuminsured("300000");
					 }else if(coverageinfors.get(i).getSuminsured().equals("306006009")){
						 coverageinfors.get(i).setSuminsured("500000");
					 }else if(coverageinfors.get(i).getSuminsured().equals("306006014")){
						 coverageinfors.get(i).setSuminsured("1000000");
					 }
				}
			}
		}
		if(listCoverge.size()>0 || listAgreement.size()>0){//update操作
			//批量删除再批量插入
			int coverdeletebatch=coverageinforMapper.deleteCoverageBatch(listCoverge);
			int insertcoveragebatch=coverageinforMapper.insertCoverageBatch(coverageinfors);
			logger.info("处理完毕险种批量删除批量新增,订单号:" + baseinfor.getOrderno() + "处理结果为: 删除covergeinfor"+ coverdeletebatch +", 新增covergeinfor "+ insertcoveragebatch);
			int agreedeletebatch=agreementinforMapper.deleteAgreementBatch(listAgreement);
			int insertagreebatch=agreementinforMapper.insertAgreementinforBatch(agreementinfors);
			logger.info("处理完毕特约表批量删除批量新增,订单号:" + baseinfor.getOrderno() + "处理结果为: 删除agreementinfor"+ agreedeletebatch +", 新增agreementinfor "+ insertagreebatch);
			int insertTax=taxinforMapper.insertSelective(taxinfor);
			logger.info("处理完毕新增taxinfor表,订单号：" + baseinfor.getOrderno() + "处理结果为: 新增 ： "+ insertTax);
			baseinfor.setTaxinforid(taxinfor.getTaxinforid());
			int updatebaseinfor=baseinforMapper.updateByPrimaryKey(baseinfor);
			logger.info("处理完毕修改baseinfor表,订单号：" + baseinfor.getOrderno() + "处理结果为: 修改baseinfor ： "+ updatebaseinfor);
		}else{
			int insertAllCoverage = coverageinforMapper.insertCoverageBatch(coverageinfors);
			logger.info("处理完毕新增险种数据，订单号：" + baseinfor.getOrderno() + "处理结果为: 新增covergeinfor ： "+ insertAllCoverage);
			if(insertAllCoverage>0){
				int insertTax=taxinforMapper.insertSelective(taxinfor);
				logger.info("处理完毕新增taxinfor表,订单号：" + baseinfor.getOrderno() + "处理结果为: 新增 ： "+ insertTax);
				baseinfor.setTaxinforid(taxinfor.getTaxinforid());//赋值执行sql返回的taxinforid
				baseinfor.setOrderstate(20);
				int updateorderstate=baseinforMapper.updateByPrimaryKeySelective(baseinfor);
				logger.info("处理完毕修改baseinfor表,订单号：" + baseinfor.getOrderno() + "处理结果为: 修改baseinfor ： "+ updateorderstate);
				if(updateorderstate>0){
					int insertAllAgreement=agreementinforMapper.insertAgreementinforBatch(agreementinfors);
					logger.info("处理完毕新增特约表数据,订单号：" + baseinfor.getOrderno() + "处理结果为 : 新增agreementinfor ： "+ insertAllAgreement);
				}
			}
		}
	}
	
	public Insuranceperinfor csrgetInsuranceByorderNo(String orderno){
		return insuranceperinforMapper.getInsuranceByorderNo(orderno);
	};
	 
	 public Deliveryinfor csrgetDeliveryByorderNo(String orderno){
		 return deliveryinforMapper.getDevlieryByorderNo(orderno);
	 };
}
