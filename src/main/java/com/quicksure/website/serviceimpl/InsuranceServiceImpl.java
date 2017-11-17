package com.quicksure.website.serviceimpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Service;
import com.quicksure.insurance.common.ProcessData;
import com.quicksure.insurance.constants.LudiConstants;
import com.quicksure.insurance.dao.OTPGenerationMapper;
import com.quicksure.insurance.dao.UserinforMapper;
import com.quicksure.insurance.dms.DeliveryInforManageService;
import com.quicksure.insurance.dms.LoginManageService;
import com.quicksure.insurance.dms.OTPdataManageService;
import com.quicksure.insurance.dms.PaymentInforDataManageService;
import com.quicksure.insurance.dms.PremiumDataManageService;
import com.quicksure.insurance.dms.SubmitDataManageService;
import com.quicksure.insurance.dms.VHLDataManageService;
import com.quicksure.insurance.dmsUtils.DMSUtility;
import com.quicksure.insurance.entity.AgentCode;
import com.quicksure.insurance.entity.Agreementinfor;
import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.Coverageinfor;
import com.quicksure.insurance.entity.Dptinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;
import com.quicksure.insurance.entity.InterfaceslogsWithBLOBs;
import com.quicksure.insurance.entity.OTPGeneration;
import com.quicksure.insurance.entity.Paymentinfor;
import com.quicksure.insurance.entity.Taxinfor;
import com.quicksure.insurance.entity.Userinfor;
import com.quicksure.insurance.entity.Vhlinfor;
import com.quicksure.insurance.service.InsuranceService;
import com.quicksure.insurance.sinosafeFactory.Sinosafeinterface;
import com.quicksure.insurance.util.StringUtils;
import com.quicksure.insurance.utils.DateFormatUtils;
import com.quicksure.insurance.utils.DesUtil;
import com.quicksure.insurance.utils.EncryptUtil;
import com.quicksure.insurance.utils.SysConstantsConfig;
/**
 * The Class InsuranceServiceImpl.
 *
 * @Description: (这里用一句话描述这个类的作用)
 * @author sunxiaodong
 * @date 2017-2-22 10:51:11
 */
/**
 * @author haidong
 *
 */
@Component("insuranceService")
@Service(version="1.0.0")
public class InsuranceServiceImpl implements InsuranceService,Serializable{
	
	private static final Logger logger = Logger.getLogger(InsuranceServiceImpl.class);
	
	@Resource(name="redisTemplate")
	private ValueOperations<String, Object> vops;
	@Resource(name="redisTemplate")
	private RedisTemplate<String, Object> redisTemplate; 
	@Resource
	private InsuranceService insuranceService;
	@Resource
	private Sinosafeinterface sinosafeinterface;
	@Resource 
	private VHLDataManageService dataManageService;
	@Resource
	private PremiumDataManageService premiumDataManageService;
	@Resource
	private SubmitDataManageService submitDataManageService;
	@Autowired
	private PaymentInforDataManageService dbDataManageService;
	@Resource
	private DeliveryInforManageService deliveryInforManageService;	
	@Resource
	private DMSUtility dmsUtility;
	@Resource
	private  UserinforMapper userinforMapper;
	@Resource
	private LoginManageService loginManageService;
	@Resource
	private OTPdataManageService otpdataManageService;
	@Resource 
	private OTPGenerationMapper otpGenerationMapper;
	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#modleSerachByVin(com.quicksure.insurance.entity.Vhlinfor, javax.servlet.http.HttpServletRequest)
	 */
	public InsuranceDetailsVO modleSerachByVin(Vhlinfor vhlinfor,
			InsuranceDetailsVO insuranceDetails){
		insuranceDetails = ProcessData.setObjValuetoinsuranceDetails(vhlinfor,
				insuranceDetails);
		// insuranceDetails.setVhlinfor(vhlinfor);
		insuranceDetails.getUserinfor().setUserAction("ModelSerachByVin");// 设置action
		sinosafeinterface.performSinosafeOprations(insuranceDetails);																// name
		String lcnNo = insuranceDetails.getVhlinfor().getLcnno();
		try {
			if (lcnNo != null && !"".equalsIgnoreCase(lcnNo)) {
				insuranceDetails.getVhlinfor().setLcnno(
						URLDecoder.decode(lcnNo, "UTF-8"));
			}
			;
		} catch (UnsupportedEncodingException e) {
			StringWriter sw = new StringWriter();  
			 e.printStackTrace(new PrintWriter(sw, true));  
			 String str = sw.toString();
			logger.error("modleSerachByVin 字符编码异常： "+str);
		}
		//session.setAttribute(insuranceDetails.getBaseinfor().getOrderno()+"insuranceDetailsVO", insuranceDetails);
		logger.info("车架号车型查询结束，订单号："+insuranceDetails.getBaseinfor().getOrderno());
		return insuranceDetails;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#modleSerachByName(com.quicksure.insurance.entity.Vhlinfor, javax.servlet.http.HttpServletRequest)
	 */
	public InsuranceDetailsVO modleSerachByName(Vhlinfor vhlinfor,
			InsuranceDetailsVO insuranceDetails){
		insuranceDetails=ProcessData.setObjValuetoinsuranceDetails(vhlinfor,insuranceDetails);
		insuranceDetails.getUserinfor().setUserAction("ModelSerachByName");
		sinosafeinterface.performSinosafeOprations(insuranceDetails);
		String lcnNo=insuranceDetails.getVhlinfor().getLcnno();
		String vehicle=insuranceDetails.getVhlinfor().getVehiclename();
		try {
			if(lcnNo!=null&&!"".equalsIgnoreCase(lcnNo)){
			insuranceDetails.getVhlinfor().setLcnno(URLDecoder.decode(lcnNo, "UTF-8"));
			}
			if(vehicle!=null&&!"".equalsIgnoreCase(vehicle)){
				insuranceDetails.getVhlinfor().setVehiclename(URLDecoder.decode(vehicle, "UTF-8"));
				}
		} catch (UnsupportedEncodingException e) {
			 StringWriter sw = new StringWriter();  
			 e.printStackTrace(new PrintWriter(sw, true));  
			 String str = sw.toString();
			logger.error("modleSerachByName 字符编码异常： "+str);
		}
		//session.setAttribute(insuranceDetails.getBaseinfor().getOrderno()+"insuranceDetailsVO", insuranceDetails);
		logger.info("车型名称车型查询结束，订单号："+insuranceDetails.getBaseinfor().getOrderno());
		return insuranceDetails;
	}
	
	/**
	 * 调华安车辆查询
	 */
	public InsuranceDetailsVO modleSerachFromSinosafe(Vhlinfor vhlinfor,
			InsuranceDetailsVO insuranceDetails) {
		insuranceDetails=ProcessData.setObjValuetoinsuranceDetails(vhlinfor,insuranceDetails);
		insuranceDetails.getUserinfor().setUserAction("ModelSearchBySinosafe");
		sinosafeinterface.performSinosafeOprations(insuranceDetails);
		logger.info("车型名称车型查询结束，订单号："+insuranceDetails.getBaseinfor().getOrderno());
		return insuranceDetails;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#saveVehicleDate(com.quicksure.insurance.entity.Vhlinfor, com.quicksure.insurance.entity.Baseinfor, javax.servlet.http.HttpServletRequest)
	 */
	public InsuranceDetailsVO saveVehicleDate(Vhlinfor vhlinfor,
			Baseinfor baseinfor, InsuranceDetailsVO insuranceDetails) {
		
		logger.info("================进入提交车辆信息服务层===============");
		/*String orderNo=httprequest.getParameter("orderNo");				
		logger.info("提交车辆信息开始，订单号："+orderNo);
		HttpSession session=httprequest.getSession();
		InsuranceDetailsVO insuranceDetails=null;
		String Chgownerflag = "";
		if(vhlinfor!=null){
			if(vhlinfor.getChgownerflag()!=null){
				
			}else{
				vhlinfor.setChgownerflag("0");
				Chgownerflag = vhlinfor.getChgownerflag();
			}
			
		}
		
		if (StringUtils.checkStringEmpty(orderNo)
				&& session.getAttribute(orderNo + "insuranceDetailsVO") != null) {
			insuranceDetails = (InsuranceDetailsVO) session
					.getAttribute(orderNo + "insuranceDetailsVO");
			if(Chgownerflag.endsWith("0")){
				insuranceDetails.getVhlinfor().setChgownerflag(Chgownerflag);
				insuranceDetails.getVhlinfor().setTransferdate(null);
			}
		} else if (insuranceDetails == null) {
			InsuranceDetailsVO insuranceDetailsVo = ProcessData
					.initInsuranceDetailsVO();
			insuranceDetails = insuranceDetailsVo;
		}
		if(insuranceDetails.getUserinfor().getPageFlag()<3){
			insuranceDetails.getUserinfor().setPageFlag(3); //如果首次进入到车辆信息(vehicleinfor)页面,将pageFlag状态改变为3
		}*/
		insuranceDetails = ProcessData.setObjValuetoinsuranceDetails(baseinfor,insuranceDetails);
		insuranceDetails = ProcessData.setObjValuetoinsuranceDetails(vhlinfor,insuranceDetails);
		insuranceDetails=dataManageService.updateVehicleData(insuranceDetails);
		logger.info("提交车辆信息结束，订单号："+insuranceDetails.getBaseinfor().getOrderno());
		return insuranceDetails;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#goToVehicleinfor(javax.servlet.http.HttpServletRequest)
	 */
	public InsuranceDetailsVO goToVehicleinfor(InsuranceDetailsVO insuranceDetails){
		logger.info("----------进入到服务层了!-------------");
		/*页面刷新url的时候回调到index页面，还是登陆状态pageFlag还是存在的，
			不过这时orderNo没有了，所以条件判断判断一下orderNo*/
		if("".equals(insuranceDetails.getBaseinfor().getOrderno()) ||
				insuranceDetails.getUserinfor().getPageFlag()<2){ 
			logger.info("---------开始111-----------");
			insuranceDetails = dataManageService
					.savefirstScreenData(insuranceDetails);//获取订单号
			insuranceDetails.getUserinfor().setPageFlag(2); //首次进入车辆信息(vehicleinfor)页面,将pageFlag更改为2
			//第一次跳转根据车牌号查出续保的单子
			if(insuranceDetails.getVhlinfor().getLcnno()!=null && 
					!("*-*").equals(insuranceDetails.getVhlinfor().getLcnno())){
				//封装数据调用华安接口
				insuranceDetails.getUserinfor().setUserAction("Renewalinfor");
				logger.info("----------开始调取华安接口-----------");
				insuranceDetails = sinosafeinterface.performSinosafeOprations(insuranceDetails);
				if("10".equals(insuranceDetails.getInterfaceslogsWithBLOBs().getInterfacesstatu())){
					insuranceDetails.getBaseinfor().setOrigFlg("1");//
				}else{
					insuranceDetails.getBaseinfor().setOrigFlg("0");
				}
			}else{
				insuranceDetails.getBaseinfor().setOrigFlg("0");
			}
			
		}else{
			logger.info("----------update first screen----------");
			insuranceDetails = dataManageService.updatefirstScreenData(insuranceDetails);
		}	
		logger.info("跳转到车辆信息页面结束，订单号："+insuranceDetails.getBaseinfor().getOrderno());
		return insuranceDetails;
	}

	/**
	 * 获取行驶区域代码 孙小东
	 */
	public List<Dptinfor> getAlldeptinfor() {
		List<Dptinfor> dptinfors=dmsUtility.getAlldeptinfor();
		return dptinfors;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#getVehicleinforByOCR(org.springframework.web.multipart.MultipartFile, java.lang.String)
	 */
	public String getVehicleinforByOCR(MultipartFile myfile, String imgdata) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#setPageFlag(java.lang.String, com.quicksure.insurance.entity.InsuranceDetailsVO)
	 */
	public InsuranceDetailsVO setPageFlag(String orderNo,
			InsuranceDetailsVO insuranceDetailsVO) {
		return null;
	}

	public InsuranceDetailsVO PremiumCount(Vhlinfor vhlinfor,
			Baseinfor baseinfor, List<Coverageinfor> coverageinfors,
			InsuranceDetailsVO insuranceDetailsVO) {
		String sypolicystartdate =null;//商业起保时间
		String jqpolicystartdate =null;//交强起保时间
		String drvowner="";//车主姓名
		InsuranceDetailsVO insuranceDetails = insuranceDetailsVO;
		//玻璃类型
		String glassType = "";
		//给予各个险种配置险种常量代码
		if(coverageinfors.size()>0){
			for(int i=0;coverageinfors.size()>i;i++){
				if(coverageinfors.get(i).getInsrnccode()!=null){
					if(coverageinfors.get(i).getInsrnccode().equals("102")){
						coverageinfors.get(i).setInsrnccode(LudiConstants.THREEPARTYINSUEANCE);
					}else if (coverageinfors.get(i).getInsrnccode().equals("101")){
						coverageinfors.get(i).setInsrnccode(LudiConstants.LOSSINSUEANCE);
					}else if(coverageinfors.get(i).getInsrnccode().equals("357")){
						coverageinfors.get(i).setInsrnccode(LudiConstants.COMPULSORYINSURANCE);
					}else if(coverageinfors.get(i).getInsrnccode().equals("104")){
						coverageinfors.get(i).setInsrnccode(LudiConstants.DRIVERINSUEANCE);
					}else if(coverageinfors.get(i).getInsrnccode().equals("105")){
						coverageinfors.get(i).setInsrnccode(LudiConstants.PASSENGERINSUEANCE);
					}else if(coverageinfors.get(i).getInsrnccode().equals("103")){
						coverageinfors.get(i).setInsrnccode(LudiConstants.ROBBERYINSUEANCE);
					}else if(coverageinfors.get(i).getInsrnccode().equals("108")){
						coverageinfors.get(i).setInsrnccode(LudiConstants.SPONTANEOUSCOMBUSTIONINSUEANCE);
					}else if(coverageinfors.get(i).getInsrnccode().equals("107")){
						glassType=coverageinfors.get(i).getSuminsured();
						coverageinfors.get(i).setSuminsured("1");//设置保额为1
						coverageinfors.get(i).setBulletGlass("0");//设置默认值不是防爆玻璃
						coverageinfors.get(i).setInsrnccode(LudiConstants.GLASSINSUEANCE);
					}else if(coverageinfors.get(i).getInsrnccode().equals("115")){
						coverageinfors.get(i).setInsrnccode(LudiConstants.NOTFOUNDINSUEANCE);
					}else if(coverageinfors.get(i).getInsrnccode().equals("116")){
						coverageinfors.get(i).setInsrnccode(LudiConstants.APPOINTINSUEANCE); 
					}
				}else{
					sypolicystartdate=coverageinfors.get(i).getSypolicystartdate();
					jqpolicystartdate=coverageinfors.get(i).getJqpolicystartdate();
				}
			}		 
		}
		
		if (insuranceDetails == null) {			
			InsuranceDetailsVO insuranceDetailsVo=ProcessData.initInsuranceDetailsVO();
			insuranceDetails=insuranceDetailsVo;
		}
		
		if(insuranceDetails.getBaseinfor()!=null){
			 insuranceDetails.getBaseinfor().setJqpremium(null);
			 insuranceDetails.getBaseinfor().setSypolicyenddate(null);
			 insuranceDetails.getBaseinfor().setJqpolicyenddate(null);
			 insuranceDetails.getBaseinfor().setSypremium(null);
			 insuranceDetails.getBaseinfor().setJqpolicystartdate(null);
		}			 
		if(insuranceDetails.getTaxinfor()!=null){
			 insuranceDetails.getTaxinfor().setSumuptax(null);
			 insuranceDetails.getTaxinfor().setCurrenttax(null);
		}
		if(insuranceDetails.getVhlinfor()!=null){
			if(insuranceDetails.getVhlinfor().getChgownerflag()!=null){
				if(insuranceDetails.getVhlinfor().getChgownerflag().equals("0")){
					insuranceDetails.getVhlinfor().setTransferdate(null);
				}
			}
		}
		
		//给予起保时间加入时分秒,后续前端控件有就删除     给予请求报文的时间都为T+1默认时间
	    Date date = new Date();
	    Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
		if(!"".equals(sypolicystartdate) && sypolicystartdate!=null && sypolicystartdate.length()<12 ){
			String lcn = insuranceDetails.getVhlinfor().getLcnno();
		    if("*-*".equals(lcn)){
		    	sypolicystartdate=sypolicystartdate+" 00:00:00";
				insuranceDetails.getBaseinfor().setSypolicystartdate(sypolicystartdate);
		    }else{
		    	
		    }
		}
		if(!"".equals(jqpolicystartdate) && jqpolicystartdate!=null && jqpolicystartdate.length()<12){
			String lcn = insuranceDetails.getVhlinfor().getLcnno();
			if("*-*".equals(lcn)){
		    	jqpolicystartdate=jqpolicystartdate+" 00:00:00";
				insuranceDetails.getBaseinfor().setJqpolicystartdate(jqpolicystartdate);
		    }else{
		    	jqpolicystartdate=dateString+" 00:00:00";
		    	insuranceDetails.getBaseinfor().setJqpolicystartdate(jqpolicystartdate);//默认去当前时间+1天的话，提前续保的单出不了。 孙小东
		    }
		}
		if(insuranceDetails.getBaseinfor()!=null&&insuranceDetails.getBaseinfor().getSypolicystartdate()!=null&&insuranceDetails.getBaseinfor().getSypolicystartdate().length()>19){
        	insuranceDetails.getBaseinfor().setSypolicystartdate(insuranceDetails.getBaseinfor().getSypolicystartdate().substring(0, insuranceDetails.getBaseinfor().getSypolicystartdate().length()-2)); 
        }
		if(insuranceDetails.getVhlinfor()!=null){
			if(insuranceDetails.getVhlinfor().getBrandcode()!=null){
				insuranceDetails.getVhlinfor().setGlasstype(glassType);
			}else{
				insuranceDetails.setVhlinfor(vhlinfor);
				insuranceDetails.getVhlinfor().setGlasstype(glassType);
			}
		}
		 
		 //第一个元素的起保日期是否为空，不为空就移除
		 if(coverageinfors.get(0).getSypolicystartdate()!=null || "".equals(coverageinfors.get(0).getSypolicystartdate())){
			 coverageinfors.remove(0);	 
		 }
		 if(baseinfor.getJqpolicystartdate()!=null || baseinfor.getJqpolicystartdate()!=""){
			 baseinfor.setJqpolicystartdate("");
			 baseinfor.setJqpolicyenddate("");
		 }
		 if(baseinfor.getSypolicystartdate()!=null || baseinfor.getSypolicystartdate()!=""){
			 baseinfor.setSypolicystartdate("");
			 baseinfor.setSypolicyenddate("");
		 }
		 insuranceDetails.setCoverageinfors(coverageinfors);
		 insuranceDetails=ProcessData.setObjValuetoinsuranceDetails(baseinfor,insuranceDetails);
         insuranceDetails.getUserinfor().setUserAction("PremiumCount");
         
         if(insuranceDetails.getVhlinfor().getDrvowner()!=null){
        	 logger.info("试算获取前台的数据车主名称为："+insuranceDetails.getVhlinfor().getDrvowner());
        	 drvowner = insuranceDetails.getVhlinfor().getDrvowner();
        	 insuranceDetails.getBaseinfor().setOwnersname(drvowner);
        	 //把车主姓名更新进base表
        	 premiumDataManageService.updatePremium(insuranceDetails);
         }
         
        
        //封装数据调用华安接口
        sinosafeinterface.performSinosafeOprations(insuranceDetails);
        //费用保留两位小数,不足不0
        DecimalFormat decimalFormat=new DecimalFormat(".00");
        logger.info("-------险种数量-------:"+insuranceDetails.getCoverageinfors().size());
        for(Coverageinfor cover:insuranceDetails.getCoverageinfors()){
        	if(cover.getSuminsured()!=null&&!cover.getSuminsured().equals("")&&!cover.getSuminsured().equals("0")
        	   &&!cover.getSuminsured().equals("0.00")&&!cover.getSuminsured().equals(LudiConstants.THREE_COVERAGE_5W)
        	   &&!cover.getSuminsured().equals(LudiConstants.THREE_COVERAGE_10W)&&!cover.getSuminsured().equals(LudiConstants.THREE_COVERAGE_20W)
        	   &&!cover.getSuminsured().equals(LudiConstants.THREE_COVERAGE_30W)&&!cover.getSuminsured().equals(LudiConstants.THREE_COVERAGE_50W)
        	   &&!cover.getSuminsured().equals(LudiConstants.THREE_COVERAGE_100W)){
        		cover.setSuminsured(decimalFormat.format(Float.parseFloat(cover.getSuminsured())));
        	}
        	if(cover.getPremium()!=null&&!cover.getPremium().equals("")&&!cover.getPremium().equals("0")&&!cover.getPremium().equals("0.00")){
        		cover.setPremium(decimalFormat.format(Float.parseFloat(cover.getPremium())));
        	}
        	if(cover.getBasepremium()!=null&&!cover.getBasepremium().equals("")&&!cover.getBasepremium().equals("0")&&!cover.getBasepremium().equals("0.00")){
        		cover.setBasepremium(decimalFormat.format(Float.parseFloat(cover.getBasepremium())));
        	}
        	if(cover.getBeforepremium()!=null&&!cover.getBeforepremium().equals("")&&!cover.getBeforepremium().equals("0")&&!cover.getBeforepremium().equals("0.00")){
        		cover.setBeforepremium(decimalFormat.format(Float.parseFloat(cover.getBeforepremium())));
        	}
        	if(cover.getCyl15()!=null&&!cover.getCyl15().equals("")&&!cover.getCyl15().equals("0")&&!cover.getCyl15().equals("0.00")){
        		cover.setCyl15(decimalFormat.format(Float.parseFloat(cover.getCyl15())));
        	}
        	if(cover.getNyl12()!=null&&!cover.getNyl12().equals("")&&!cover.getNyl12().equals("0")&&!cover.getNyl12().equals("0.00")){
        		cover.setNyl12(decimalFormat.format(Float.parseFloat(cover.getNyl12())));
        	}
        	if(cover.getRiskpremium()!=null&&!cover.getRiskpremium().equals("")&&!cover.getRiskpremium().equals("0")&&!cover.getRiskpremium().equals("0.00")){
        		cover.setRiskpremium(decimalFormat.format(Float.parseFloat(cover.getRiskpremium())));
        	}
        }
        if(insuranceDetails.getBaseinfor()!=null){
        	Baseinfor bb = insuranceDetails.getBaseinfor();
        	if(bb.getTotalPremium()!=null&&!bb.getTotalPremium().equals("")&&!bb.getTotalPremium().equals("0")&&!bb.getTotalPremium().equals("0.00")){
        		bb.setTotalPremium(decimalFormat.format(Float.parseFloat(bb.getTotalPremium())));
        	}
        	if(bb.getJqpremium()!=null&&!bb.getJqpremium().equals("")&&!bb.getJqpremium().equals("0")&&!bb.getJqpremium().equals("0.00")){
        		bb.setJqpremium(decimalFormat.format(Float.parseFloat(bb.getJqpremium())));
        	}
        	insuranceDetails.setBaseinfor(bb);
        }
        if(insuranceDetails.getTaxinfor()!=null){
        	if(insuranceDetails.getTaxinfor().getSumuptax()!=null&&!insuranceDetails.getTaxinfor().getSumuptax().equals("")){
        		insuranceDetails.getTaxinfor().setSumuptax(decimalFormat.format(Float.parseFloat(insuranceDetails.getTaxinfor().getSumuptax())));
        	}
        }
        
		/*session.setAttribute(insuranceDetails.getBaseinfor().getOrderno()+"insuranceDetailsVO", insuranceDetails);
		session.setAttribute("errorCode", insuranceDetails.getUserinfor().getErrorCode());
		session.setAttribute("errorMessage", insuranceDetails.getUserinfor().getErrorMessage());*/
        //更新车辆信息表，添加核定载客人数
       /* insuranceDetails=dataManageService.updateVehicleData(insuranceDetails);*/
		return insuranceDetails;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#savePremiumInfor(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	public InsuranceDetailsVO savePremiumInfor(InsuranceDetailsVO insuranceDetailsVO) {
		
		if(insuranceDetailsVO.getUserinfor().getPageFlag()<4){//返回保费计算重新提交的数据
			insuranceDetailsVO = premiumDataManageService.savePremiuminfor(insuranceDetailsVO);
			insuranceDetailsVO.getUserinfor().setPageFlag(4); //首次进入人员信息(personinfor)页面,将pageFlag状态改为4
		}else{
			insuranceDetailsVO = premiumDataManageService.updatePremiuminfor(insuranceDetailsVO);
		}
		for(Coverageinfor coverage:insuranceDetailsVO.getCoverageinfors()){
			if("030107".equals(coverage.getInsrnccode())){
				if("0".equals(coverage.getSuminsured())){
					coverage.setSuminsured("");
				}
			}
			if("0.00".equals(coverage.getNyl12())){
				coverage.setNyl12("");
			}
		}
		/*session.setAttribute(insuranceDetailsVO.getBaseinfor().getOrderno()+"insuranceDetailsVO", insuranceDetails);
		session.removeAttribute("appCoypCheckbox");
		session.removeAttribute("insureCopyCheckbox");
		session.removeAttribute("deliveryCopyCheckbox");*/
		return insuranceDetailsVO;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#submitUnderwriting(org.springframework.ui.ModelMap, com.quicksure.insurance.entity.Deliveryinfor, com.quicksure.insurance.entity.Insuranceperinfor, javax.servlet.http.HttpServletRequest)
	 */
	public InsuranceDetailsVO submitUnderwriting(InsuranceDetailsVO insuranceDetails) {
		/*String orderNo=httprequest.getParameter("orderNo");
		HttpSession session=httprequest.getSession();
		InsuranceDetailsVO insuranceDetails=null;
		if (StringUtils.checkStringEmpty(orderNo)
				&& session.getAttribute(orderNo + "insuranceDetailsVO") != null) {
		 insuranceDetails = (InsuranceDetailsVO)session.getAttribute(orderNo+"insuranceDetailsVO");
		}
		if (insuranceDetails == null) {
			InsuranceDetailsVO insuranceDetailsVo=ProcessData.initInsuranceDetailsVO();
			insuranceDetails=insuranceDetailsVo;
		}
		int insuranceperinforid=0;
		if(insuranceDetails!=null&&insuranceDetails.getInsuranceperinfor()!=null&&insuranceDetails.getInsuranceperinfor().getInsuranceperinforid()!=null){
			insuranceperinforid = insuranceDetails.getInsuranceperinfor().getInsuranceperinforid();
		}
		int deliveryinforid=0;
		if(insuranceDetails!=null&&insuranceDetails.getDeliveryinfor()!=null&&insuranceDetails.getDeliveryinfor().getDeliveryid()!=null){
			deliveryinforid=insuranceDetails.getDeliveryinfor().getDeliveryid();
		}
		if(insuranceperinforid!=0){
			insuranceperinfor.setInsuranceperinforid(insuranceperinforid);
		}
		if(deliveryinforid!=0){
			deliveryinfor.setDeliveryid(deliveryinforid);
		}
		if(insuranceDetails.getBaseinfor()!=null){
			if(insuranceDetails.getBaseinfor().getQuoteno()!=null){
				
			}else{
				String quotano =httprequest.getParameter("quotano");
				insuranceDetails.getBaseinfor().setQuoteno(quotano);
			}
		}
		
		insuranceDetails.setDeliveryinfor(deliveryinfor);
		insuranceDetails.setInsuranceperinfor(insuranceperinfor);*/
		
		/*if(insuranceDetails.getUserinfor().getPageFlag()<5){
			insuranceDetails.getUserinfor().setPageFlag(5); //首次进入baseinfor页面,将pageFlag状态改为5
		}*/
	
		insuranceDetails.getUserinfor().setUserAction("SubmitUnderwriting");
		//提核错误方便页面数据填充
		//去除提核xml地址属性值中的特殊符号
		String insuraddress="";
		String applicaddress="";
		if(insuranceDetails.getInsuranceperinfor()!=null){
			insuraddress=insuranceDetails.getInsuranceperinfor().getInsureaddress();
			applicaddress=insuranceDetails.getInsuranceperinfor().getApplicationaddress();
			insuranceDetails.getInsuranceperinfor().setApplicationaddress((applicaddress+insuranceDetails.getInsuranceperinfor().getApplicationdetailaddress()).replace("-", "").replace(",", ""));
			insuranceDetails.getInsuranceperinfor().setInsureaddress((insuraddress+insuranceDetails.getInsuranceperinfor().getInsuredetailaddress()).replace("-", "").replace(",", ""));
			
		}
		
		//请求过来的数据插入	insuranceperinfor表，
		sinosafeinterface.performSinosafeOprations(insuranceDetails);
		if(insuranceDetails.getInsuranceperinfor()!=null){
			insuranceDetails.getInsuranceperinfor().setApplicationaddress(applicaddress);
			insuranceDetails.getInsuranceperinfor().setInsureaddress(insuraddress);
		}
		/*
		session.setAttribute(insuranceDetails.getBaseinfor().getOrderno()+"insuranceDetailsVO", insuranceDetails);
		session.setAttribute("errorCode", insuranceDetails.getUserinfor().getErrorCode());
		session.setAttribute("errorMessage", insuranceDetails.getUserinfor().getErrorMessage());
		session.setAttribute("appCoypCheckbox", insuranceperinfor.getAppCoypCheckbox());
		session.setAttribute("insureCopyCheckbox", insuranceperinfor.getInsureCopyCheckbox());
		session.setAttribute("deliveryCopyCheckbox", insuranceperinfor.getDeliveryCopyCheckbox());*/
		return insuranceDetails;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#saveSubmitInfor(com.quicksure.insurance.entity.Baseinfor)
	 */
	public void saveSubmitInfor(Baseinfor baseinfor) {
		submitDataManageService.saveSubmitinfor(baseinfor);

	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#AddSubmitInfor(com.quicksure.insurance.entity.Deliveryinfor, com.quicksure.insurance.entity.Insuranceperinfor, javax.servlet.http.HttpServletRequest)
	 */
	public InsuranceDetailsVO AddSubmitInfor(InsuranceDetailsVO insuranceDetails) {
	/*	String orderNo=httprequest.getParameter("orderNo");
		String applicationProvinceName = httprequest.getParameter("applicationProvinceName");
		String applicationCityName = httprequest.getParameter("applicationCityName");
		String applicationCountyName = httprequest.getParameter("applicationCountyName");
		String insuredProvinceName = httprequest.getParameter("insuredProvinceName");
		String insuredCityName = httprequest.getParameter("insuredCityName");
		String insuredCountyName = httprequest.getParameter("insuredCountyName");
		String deliveryProvinceName = httprequest.getParameter("deliveryProvinceName");
		String deliveryCityName = httprequest.getParameter("deliveryCityName");
		String deliveryCountyName = httprequest.getParameter("deliveryCountyName");
		HttpSession session=httprequest.getSession();
		InsuranceDetailsVO insuranceDetails=null;
		if (StringUtils.checkStringEmpty(orderNo)
				&& session.getAttribute(orderNo + "insuranceDetailsVO") != null) {
		 insuranceDetails = (InsuranceDetailsVO)session.getAttribute(orderNo+"insuranceDetailsVO");
		}
		if (insuranceDetails == null) {
			InsuranceDetailsVO insuranceDetailsVo=ProcessData.initInsuranceDetailsVO();
			insuranceDetails=insuranceDetailsVo;
		}
		insuranceDetails.getBaseinfor().setApplicationProvinceName(applicationProvinceName);
		insuranceDetails.getBaseinfor().setApplicationCityName(applicationCityName);
		insuranceDetails.getBaseinfor().setApplicationCountyName(applicationCountyName);
		insuranceDetails.getBaseinfor().setInsuredProvinceName(insuredProvinceName);
		insuranceDetails.getBaseinfor().setInsuredCityName(insuredCityName);
		insuranceDetails.getBaseinfor().setInsuredCountyName(insuredCountyName);
		insuranceDetails.getBaseinfor().setDeliveryProvinceName(deliveryProvinceName);
		insuranceDetails.getBaseinfor().setDeliveryCityName(deliveryCityName);
		insuranceDetails.getBaseinfor().setDeliveryCountyName(deliveryCountyName);
		int insuranceperinforid = insuranceDetails.getInsuranceperinfor().getInsuranceperinforid();
		int deliveryinforid = insuranceDetails.getDeliveryinfor().getDeliveryid()==null?0:insuranceDetails.getDeliveryinfor().getDeliveryid();
		if(insuranceperinforid!=0){
			insuranceperinfor.setInsuranceperinforid(insuranceperinforid);
		}
		if(deliveryinforid!=0){
			deliveryinfor.setDeliveryid(deliveryinforid);
		}
		insuranceDetails.setDeliveryinfor(deliveryinfor);
		insuranceDetails.setInsuranceperinfor(insuranceperinfor);*/
		insuranceDetails =submitDataManageService.AddSubmitinfor(insuranceDetails);
		DecimalFormat decimalFormat=new DecimalFormat(".00");
		if(insuranceDetails.getBaseinfor()!=null){
        	Baseinfor bb=insuranceDetails.getBaseinfor();
        	if(bb.getTotalPremium()!=null&&!bb.getTotalPremium().equals("")&&!bb.getTotalPremium().equals("0")&&!bb.getTotalPremium().equals("0.00")){
        		bb.setTotalPremium(decimalFormat.format(Float.parseFloat(bb.getTotalPremium())));
        	}
        	if(bb.getJqpremium()!=null&&!bb.getJqpremium().equals("")&&!bb.getJqpremium().equals("0")&&!bb.getJqpremium().equals("0.00")){
        		bb.setJqpremium(decimalFormat.format(Float.parseFloat(bb.getJqpremium())));
        	}
        	insuranceDetails.setBaseinfor(bb);
        }
		
		/*session.setAttribute(insuranceDetails.getBaseinfor().getOrderno()+"insuranceDetailsVO", insuranceDetails);
		session.setAttribute("insuranceDetailsVO", insuranceDetails);*/
		return insuranceDetails;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#insertCoverageBatch(java.util.List, com.quicksure.insurance.entity.Baseinfor, com.quicksure.insurance.entity.Taxinfor, java.util.List, javax.servlet.http.HttpServletRequest)
	 */
	public void insertCoverageBatch(List<Coverageinfor> coverageinfors,
			Baseinfor baseinfor, Taxinfor taxinfor,
			List<Agreementinfor> agreementinfors, HttpServletRequest request) {

	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#paymentApplication(javax.servlet.http.HttpServletRequest)
	 */
	public InsuranceDetailsVO paymentApplication(InsuranceDetailsVO insuranceDetails) {
		/*HttpSession  session = request.getSession();
		 System.out.println(session.getId());
		String orderNo=request.getParameter("orderNo");
		InsuranceDetailsVO insuranceDetails=null;
		if (StringUtils.checkStringEmpty(orderNo)&& session.getAttribute(orderNo + "insuranceDetailsVO") != null){
			 insuranceDetails = (InsuranceDetailsVO)session.getAttribute(orderNo +"insuranceDetailsVO");
			}
		if (insuranceDetails == null) {			
			InsuranceDetailsVO insuranceDetailsVo=ProcessData.initInsuranceDetailsVO();
			insuranceDetails=insuranceDetailsVo;
		}*/
		
		insuranceDetails.getUserinfor().setUserAction("PaymentApplication");//设置action name
		sinosafeinterface.performSinosafeOprations(insuranceDetails);
		
		Float taxpremium =Float.parseFloat((insuranceDetails.getBaseinfor().getTaxpremium()==null?0:insuranceDetails.getBaseinfor().getTaxpremium()).toString()); 
		Float jqpremium = Float.parseFloat((insuranceDetails.getBaseinfor().getJqpremium()==null?0:insuranceDetails.getBaseinfor().getJqpremium()).toString());
		Float sypremium = Float.parseFloat((insuranceDetails.getBaseinfor().getSypremium()==null?0:insuranceDetails.getBaseinfor().getSypremium()).toString());
		Float paymentpremium = taxpremium + jqpremium + sypremium; //得到支付的总金额
		insuranceDetails.getPaymentinfor().setPaymentpremium(paymentpremium.toString());
		insuranceDetails.getPaymentinfor().setPaymenttimes(DateFormatUtils.getSystemDate());
		insuranceDetails.getPaymentinfor().setPaymentstate(10);
		
		if("10".equals(insuranceDetails.getInterfaceslogsWithBLOBs().getInterfacesstatu())){
			dbDataManageService.operatePaymentInfo(insuranceDetails);
		}
/*		session.setAttribute(insuranceDetails.getBaseinfor().getOrderno()+"insuranceDetailsVO", insuranceDetails);*/
		return insuranceDetails;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#updatePaymentInfor(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public boolean updatePaymentInfor(Baseinfor baseinfor) {
       boolean isSuccess = false;	
		
		logger.info("保单生成----更新保单号和投保单号---开始！");
		int result = dbDataManageService.udpatePaymentCompleteData(baseinfor);
		logger.info("END");
		if(result > 0){
			isSuccess = true;
		}
		
		return isSuccess;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.InsureanceService#processPaymentSuccessData(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public InsuranceDetailsVO processPaymentSuccessData(String orderNo) {
		InsuranceDetailsVO insuranceDetailsVO = null;
		Paymentinfor paymentinfor = null;
	    Baseinfor baseinfor=dbDataManageService.getBaseInfor(orderNo);
	    paymentinfor=dbDataManageService.getPaymentinfor(baseinfor);
	    insuranceDetailsVO =ProcessData.initInsuranceDetailsVO();
	    insuranceDetailsVO.setBaseinfor(baseinfor);
	    insuranceDetailsVO.setPaymentinfor(paymentinfor);
		if (insuranceDetailsVO != null
				&& insuranceDetailsVO.getPaymentinfor() != null) {
			paymentinfor = insuranceDetailsVO.getPaymentinfor();
			if (insuranceDetailsVO.getPaymentinfor().getPaymentno() != null) {
				//手机页面支付完成 没有订单信息展示  		
				paymentinfor.setPaymentstate(20);
				dbDataManageService
						.updatePaymentAndBaseinfor(insuranceDetailsVO);

			}
		}
		return insuranceDetailsVO;

	}

	/**
	 * 调用华安接口执行配送
	 */
	public InsuranceDetailsVO deliveryToCustomer(
			InsuranceDetailsVO insuranceDetails) {
		int result=deliveryInforManageService.updateOrdersdeliver(insuranceDetails);//调用配送接口前修改订单状态的方法
		if(result==1){
			Userinfor userinfor =new Userinfor();
			InterfaceslogsWithBLOBs interfaceslogsWithBLOBs = new InterfaceslogsWithBLOBs();
			userinfor.setUserAction("DeliveryOrderInfo");
			insuranceDetails.setUserinfor(userinfor);
			insuranceDetails.setInterfaceslogsWithBLOBs(interfaceslogsWithBLOBs);
			insuranceDetails = sinosafeinterface.performSinosafeOprations(insuranceDetails);
			if ("10".equalsIgnoreCase(insuranceDetails.getInterfaceslogsWithBLOBs().getInterfacesstatu())) {
				logger.info("调用配送接口返回成功.");
				deliveryInforManageService.insertDeliveryData(insuranceDetails);
			}else{
				logger.info("调用配送接口返回失败.");
			}
		}		
		/*session.setAttribute(insuranceDetails.getBaseinfor().getOrderno()+"insuranceDetailsVO", insuranceDetails);*/
		return null;
	}
	/**
	 * 查询配送接口请求报文所需要的字段
	 */
	public InsuranceDetailsVO selectDistributionInfors(String orderno){
		/*String jqapplicationno = request.getParameter("jq_app_ply_no"); //交强险投保单号
		String syapplicationno = request.getParameter("sy_app_ply_no"); //商业险投保单号
		String jqpolicyno = request.getParameter("jq_ply_no"); //交强险保单号
		String sypolicyno = request.getParameter("sy_ply_no"); //商业险保单号
		String orderno=request.getParameter("LudiOrderNo");*/
		Baseinfor baseinfor = new Baseinfor();
		/*baseinfor.setSyapplicationno(syapplicationno);
		baseinfor.setJqapplicationno(jqapplicationno);
		baseinfor.setSypolicyno(sypolicyno);
		baseinfor.setJqpolicyno(jqpolicyno);*/
		baseinfor.setOrderno(orderno);
		InsuranceDetailsVO ins=deliveryInforManageService.selectDistributionInfor(baseinfor);
		if(ins!=null&&!"".equals(ins)){
			String str=ins.getDeliveryinfor().getDeliveryaddress();		
			String[]  strs=str.split("-");
			ins.getDeliveryinfor().setDeliveryCity(strs[1]);
			ins.getDeliveryinfor().setDeliveryCounty(strs[2]);
			ins.getDeliveryinfor().setDeliveryProvince(strs[0]);
		}	
		return ins;
		
	}
	

	public String test(final InsuranceDetailsVO insuranceDetailsVO) {
		logger.info("---------InsuranceDetailsVO---------:");
//		vops.set("test", "111111");
		/*String message = (String) vops.get("password");
		logger.info("-----password------:"+message);*/
		insuranceDetailsVO.getBaseinfor().setOrderno("123456789");
//		vops.set("object", insuranceDetailsVO);
		redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				byte[] key = serializer.serialize("insurance3");
				byte[] value = serialize(insuranceDetailsVO);
				connection.set(key, value);
				return true;
			}
		});
		logger.info("-----插入对象成功-----");
		
		InsuranceDetailsVO result= redisTemplate.execute(new RedisCallback<InsuranceDetailsVO>() {
			public InsuranceDetailsVO doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				byte[] key = serializer.serialize("insurance3");
				if(connection.exists(key)){
					byte[] valuebytes = connection.get(key);
					InsuranceDetailsVO insurance2 = (InsuranceDetailsVO) unserialize(valuebytes);
					return insurance2;
				}
				return null;
			}
		});
		
		
//		InsuranceDetailsVO insuranceDetails = (InsuranceDetailsVO) vops.get("insurance2");
		logger.info("-----orderNo-----:"+result.getBaseinfor().getOrderno());
		
		System.out.println("--------------1111111111111------------------:");
		return "success";
	}
	
	

	public String test2(final Userinfor userinfor) {
		logger.info("--------测试redis储存userinfor 开始---------");
		redisTemplate.execute(new RedisCallback<Boolean>() {
		public Boolean doInRedis(RedisConnection connection)
				throws DataAccessException {
			RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
			byte[] key = serializer.serialize("userinfor");
			byte[] value = serialize(userinfor);
			connection.set(key, value);
			return true;
			}
		});
		logger.info("储存成功！");
			
		Userinfor result= redisTemplate.execute(new RedisCallback<Userinfor>() {
			public Userinfor doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				byte[] key = serializer.serialize("userinfor");
				if(connection.exists(key)){
					byte[] valuebytes = connection.get(key);
					Userinfor userinfor = (Userinfor) unserialize(valuebytes);
					return userinfor;
				}
				return null;
				}
		});
		
		logger.info("------获取数据-------:"+result.getUsername());
		return "success";
	}
	
	
	
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	
	public Userinfor userExists(Userinfor userinfor,int loginType) throws Exception{
		String msg="";
		//1：动态密码登录；   2：会员密码登录
		
		if(userinfor.getPassword()!=null&&!userinfor.getPassword().equals("")){
			userinfor.setPassword(DesUtil.encrypt(userinfor.getPassword()));
		}
		Userinfor user = userinforMapper.selectByName(userinfor);
		AgentCode agent = new AgentCode();
		if(loginType==1){ //动态密码登录先验证用户是否存在，不存在的话添加用户 
			if(user==null){
				logger.info("首次验证码登录,用户为空  添加用户");
				userinfor.setPassword(DesUtil.encrypt("ludi123"));//首次登录默认密码
				userinfor.setPhoneno(userinfor.getUsername());
				userinfor.setUsertype(1);
				msg=loginManageService.registUser(userinfor,agent);//添加用户
				userinfor.setMsg(msg);
			}else{
				logger.info("用户登录查询结果用户名为:" + user.getUsername());
				userinfor=user;
			}
		}else if(loginType==2){ //账户密码登录
			if(user==null){
				msg="3"; //"用户名不存在或密码输入错误";
				userinfor.setMsg(msg);
			}else{
				logger.info("帐号密码登录或者微信登录查询用户名为：" + user.getUsername());
				userinfor=user;
			}
		}		
		return userinfor;
	}
   
	//重置密码
	public String resetPassword(String phoneno,String password) throws Exception {
		String msg="";
		Userinfor user=new Userinfor();
		user.setUsername(phoneno);
		Userinfor uu=loginManageService.selectByName(user);
		if(uu==null){
			msg="2";//手机号不存在
		}else{
			Userinfor userinfor=new Userinfor();
			userinfor.setPassword(DesUtil.encrypt(password));
			userinfor.setUsername(phoneno);
			msg=loginManageService.resetPassword(userinfor);
		}
		
		return msg;
	}

	public String userFindPassword(Userinfor userinfor,HttpServletRequest request) {
		String msg=loginManageService.userFindPassword(userinfor, request);
		return msg;
	}
	
	//账户注册
	public String registUser(String username,String password,int type, String agentCode) {
		int isAgent = Integer.parseInt(agentCode);
		//用户名和密码唯一,注册须判断
		String msg = "";
			try {
//				userinfor.setUsertype(2);// 用户类型(会员)
				// userinfor.setPassword(Md5Utils.string2MD5(userinfor.getPassword()));
				Userinfor user=new Userinfor();
				user.setUsername(username);
				Userinfor uu=loginManageService.selectByName(user);
				if(uu!=null){
					msg="2";//手机号已被使用（手机已注册）
				}else{
					Userinfor userinfor=new Userinfor();
					AgentCode agent = new AgentCode();
					userinfor.setUsername(username);
					userinfor.setAgentFlag(isAgent);
					//前台页面已经做了判断，如果勾选了代理人则必须要输入正确的识别码才能提交表单，所以这里直接判断agentCode如果为空则是没勾选代理人
//					if("1".equals(agentCode)){
//						userinfor.setAgentFlag(1);
////						agent.setAgentcode(agentCode);
//					}
//					if("0".equals(agentCode)){
//						userinfor.setAgentFlag(1);
////						agent.setAgentcode(agentCode);
//					}
					if(type!=3){
						userinfor.setPassword(DesUtil.encrypt(password));
						userinfor.setPhoneno(username);//手机就是用户名						
					}
					userinfor.setUsertype(type);
					msg=loginManageService.registUser(userinfor,agent);
				}
				
			} catch (Exception e) {
				StringWriter sw = new StringWriter();  
				 e.printStackTrace(new PrintWriter(sw, true));  
				 String str = sw.toString();
				logger.info("用户注册异常: "+str);
			}
		return msg;
	}
	
	//注册判断用户是否注册过
	public String userEverRegist(String username) {
		String msg="";
		try {
			Userinfor user=new Userinfor();
			user.setUsername(username);
			Userinfor uu=loginManageService.selectByName(user);
			if(uu!=null){
				msg="1";
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();  
			 e.printStackTrace(new PrintWriter(sw, true));  
			 String str = sw.toString();
			logger.info("用户查询异常: "+str);
		}
		return msg;
	}
	
	//发送验证码到用户手机
	public OTPGeneration validateCode(String phoneNo) {
		OTPGeneration otpGeneration=new OTPGeneration();
		insuranceService.templateSMS(phoneNo, otpGeneration);
		return otpGeneration;
	}
	
	public String phoneCodeExist(String phoneNo,String checkCode) {
		Boolean exist=false;
		Map params=new HashMap();
		params.put("phoneno", phoneNo);
		params.put("dateTime", new Date());
		//查询该用户所有未过期的并且有效的验证码
		List<OTPGeneration> listOTP=otpGenerationMapper.listOTPGeneration(params);
		for(OTPGeneration otp:listOTP){
			if(checkCode.equals(otp.getValidationno())){
				exist=true;
				otp.setStatus("20");//验证成功,设置验证码状态为已用
				otpGenerationMapper.updateByPrimaryKey(otp);
			}
		}
		return exist.toString();
	}
	
	/**
	 * 代理人识别码校验
	 */
	public AgentCode validateAgentCode(String agentCode){
		return loginManageService.selectAgent(agentCode);
	}

	
	/**
	 * 发送短信
	 */
	public OTPGeneration templateSMS(String phoneNo,OTPGeneration otpGeneration) {
		logger.info("发送短信验证码开始手机号： "+phoneNo);
		String result = "";
		DefaultHttpClient httpclient=new DefaultHttpClient();
		String param="";
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			param=getValidateNo(true,6);
			logger.info("手机短信验证码为： "+param);
			//先将验证码存入数据库
			otpGeneration.setPhoneno(phoneNo);
			otpGeneration.setValidationno(param);
			otpGeneration.setExprietimes(getExprieTime());
			otpGeneration.setStatus("10");//设置验证码的状态为未使用状态
			otpGeneration=otpdataManageService.saveOTPinfor(otpGeneration);
			
			
			String timestamp = DateFormatUtils.getSystemDateByYYYYMMDD();// 时间戳
			String signature =getSignature(encryptUtil,timestamp);
			String url = new StringBuffer(SysConstantsConfig.OTP_VALIDATION_URL).append("/").append(SysConstantsConfig.OTP_SOFTVERSION)
					.append("/Accounts/").append(SysConstantsConfig.OTP_ACCOUNTSID)
					.append("/Messages/templateSMS")
					.append(".xml?sig=").append(signature).toString();
			String body = (new StringBuilder("<?xml version='1.0' encoding='utf-8'?><templateSMS>")
            .append("<appId>").append(SysConstantsConfig.OTP_APPID).append("</appId>")
            .append("<templateId>").append(SysConstantsConfig.SMS_TEMPLATE_ID).append("</templateId>")
            .append("<to>").append(phoneNo).append("</to>")
            .append("<param>").append(param).append("</param>")
            .append("</templateSMS>")).toString();
			logger.info("组装报文的Boy： "+body);
			HttpResponse response=post("application/xml",SysConstantsConfig.OTP_ACCOUNTSID, SysConstantsConfig.OTP_AUTHTOKEN, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}			
			EntityUtils.consume(entity);
		} catch (Exception e) {
			//删除已插入的验证码
			otpdataManageService.deleteOTPinfor(otpGeneration);
			StringWriter sw = new StringWriter();  
			 e.printStackTrace(new PrintWriter(sw, true));  
			 String str = sw.toString();
			logger.error("发送短信验证码异常，异常消息为： "+str);
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		if(StringUtils.checkStringEmpty(result)){
			String responseCode=ProcessData.getTemplateSMSReturnCode(result);
			if(StringUtils.checkStringEmpty(responseCode)&&!LudiConstants.TEMPLATE_SMS_SUCCESS_CODE.equalsIgnoreCase(responseCode)){//发送验证码失败
//				otpGeneration.setPhoneno(phoneNo);
//				otpGeneration.setValidationno(param);
//				otpGeneration.setExprietimes(getExprieTime());
//				otpGeneration.setStatus("10");//设置验证码的状态为未使用状态
//				otpGeneration=otpdataManageService.saveOTPinfor(otpGeneration);
				//删除已插入的验证码
				otpdataManageService.deleteOTPinfor(otpGeneration);
			}
			
		}
		logger.info("发送短信验证码结束，返回结果为： "+result);
		return otpGeneration;
	}		
	
	/**
	 * 获取Signature
	 * 孙小东
	 * @return
	 */
	public String getSignature(EncryptUtil encryptUtil,String timestamp) {
		String signature = "";		
		try {			
			String sig = SysConstantsConfig.OTP_ACCOUNTSID
					+ SysConstantsConfig.OTP_AUTHTOKEN + timestamp;
			signature = encryptUtil.md5Digest(sig);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();  
			 e.printStackTrace(new PrintWriter(sw, true));  
			 String str = sw.toString();
			logger.error("发送短信验证码异常，异常消息为： "+str);
		}
		return signature;

	}

	public HttpResponse post(String cType, String accountSid, String authToken,
			String timestamp, String url, DefaultHttpClient httpclient,
			EncryptUtil encryptUtil, String body) throws Exception {
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Accept", cType);
		httppost.setHeader("Content-Type", cType + ";charset=utf-8");
		String src = accountSid + ":" + timestamp;
		String auth = encryptUtil.base64Encoder(src);
		httppost.setHeader("Authorization", auth);
		BasicHttpEntity requestBody = new BasicHttpEntity();
		requestBody
				.setContent(new ByteArrayInputStream(body.getBytes("UTF-8")));
		requestBody.setContentLength(body.getBytes("UTF-8").length);
		httppost.setEntity(requestBody);
		// 执行客户端请求
		HttpResponse response = httpclient.execute(httppost);
		return response;
	}

	/**
	 * 创建指定数量的随机字符串
	 * 
	 * @param numberFlag
	 *            是否是数字
	 * @param length
	 * @return
	 */
	public static String getValidateNo(boolean numberFlag, int length){
      String retStr = "";
      String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
      int len = strTable.length();
      boolean bDone = true;
    do {
         retStr = "";
        int count = 0;
        for (int i = 0; i < length; i++) {
        double dblR = Math.random() * len;
       int intR = (int) Math.floor(dblR);
       char c = strTable.charAt(intR);
        if (('0' <= c) && (c <= '9')) {
        count++;
         }
      retStr += strTable.charAt(intR);
      }
    if (count >= 2) {
    bDone = false;
     }
    } while (bDone);
     return retStr;
	}
	
	/**
	 * 获取验证码的有效时间
	 * 手机验证码的有效时间为20分钟
	 * @return
	 */
 public String getExprieTime(){;
	 Calendar calendar=Calendar.getInstance();//创建实例 默认是当前时刻
	 calendar.setTime(new Date());
	 calendar.add(Calendar.MINUTE, 20);
	 String exprieTime= DateFormatUtils.getSystemDate(calendar.getTime());
	 return exprieTime;
	 
 }

}
