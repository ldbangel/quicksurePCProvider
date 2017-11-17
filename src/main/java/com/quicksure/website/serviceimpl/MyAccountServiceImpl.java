package com.quicksure.website.serviceimpl;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.quicksure.insurance.common.ProcessData;
import com.quicksure.insurance.dao.BaseinforMapper;
import com.quicksure.insurance.dao.CoverageinforMapper;
import com.quicksure.insurance.dao.MyAccountMapper;
import com.quicksure.insurance.dao.OTPGenerationMapper;
import com.quicksure.insurance.dao.UserinforMapper;
import com.quicksure.insurance.dao.WechatBindMapper;
import com.quicksure.insurance.dms.LoginManageService;
import com.quicksure.insurance.dms.PaymentInforDataManageService;
import com.quicksure.insurance.dms.PolicyCancelManageService;
import com.quicksure.insurance.entity.AgentCode;
import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.Coverageinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;
import com.quicksure.insurance.entity.InterfaceslogsWithBLOBs;
import com.quicksure.insurance.entity.OTPGeneration;
import com.quicksure.insurance.entity.Userinfor;
import com.quicksure.insurance.entity.WechatBind;
import com.quicksure.insurance.service.MyAccountService;
import com.quicksure.insurance.sinosafeFactory.Sinosafeinterface;
import com.quicksure.insurance.utils.DateFormatUtils;
import com.quicksure.insurance.utils.DesUtil;

/**
 * The Class MyAccountServiceImpl.
 *
 * @Description: 我的订单功能服务实现类
 * @author sunxiaodong
 * @date 2017-2-22 10:54:58
 */
@Component("myAccountService")
@Service(version="1.0.0")
public class MyAccountServiceImpl implements MyAccountService,Serializable {
	
	private static final Logger logger = Logger.getLogger(MyAccountServiceImpl.class);
	
	@Autowired
	private MyAccountMapper myAccountMapper;
	@Resource
	private BaseinforMapper baseinforMapper;
	@Resource
	private UserinforMapper userinforMapper;
	@Resource
	private LoginManageService loginManageService;
	@Resource 
	private OTPGenerationMapper otpGenerationMapper;
	@Resource
	private WechatBindMapper wechatBindMapper;
	@Resource
	private Sinosafeinterface sinosafeinterface;
	@Autowired
	private PaymentInforDataManageService dbDataManageService;
	@Resource 
	private PolicyCancelManageService policyCancelManageService;
	@Resource
	private CoverageinforMapper coverageinforMapper;
	
	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#userExists(com.quicksure.insurance.entity.Userinfor, javax.servlet.http.HttpServletRequest)
	 */
	public String userExists(Userinfor userinfor, HttpServletRequest request)
			throws Exception {
		HttpSession session=request.getSession();
		String msg="";
		Integer type=userinfor.getUsertype();//1：动态密码登录；2：会员密码登录
		if(type!=3){
			userinfor.setUsertype(1);//用户登录统一设置type为1
		}
		if(userinfor.getPassword()!=null&&!userinfor.getPassword().equals("")){
			userinfor.setPassword(DesUtil.encrypt(userinfor.getPassword()));
		}
		Userinfor user = userinforMapper.selectByName(userinfor);
		AgentCode agent = new AgentCode();
		if(type==1){//动态密码登录先验证用户是否存在，不存在的话添加用户 
			if(user==null){
				logger.info("首次验证码登录,用户为空  添加用户");
				userinfor.setPassword(DesUtil.encrypt("ludi123"));//首次登录默认密码
				userinfor.setPhoneno(userinfor.getUsername());
				userinfor.setUsertype(1);
				msg=loginManageService.registUser(userinfor,agent);//添加用户
			}else{
				logger.info("用户登录查询结果用户名为:" + user.getUsername());
				userinfor=user;
			}
			session.setAttribute("loginUser", userinfor);
		}
		if(type==2||type==3){
			if(user==null){
				msg="3";//"用户名不存在或密码输入错误";
			}else{
				logger.info("帐号密码登录或者微信登录查询用户名为：" + user.getUsername());
				session.setAttribute("loginUser", user);
			}
		}		
		return msg;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#resetPassword(java.lang.String, java.lang.String)
	 */
	public String resetPassword(String phoneno, String password)
			throws Exception {
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

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#userFindPassword(com.quicksure.insurance.entity.Userinfor, javax.servlet.http.HttpServletRequest)
	 */
	public String userFindPassword(Userinfor userinfor,
			HttpServletRequest request) {
		String msg=loginManageService.userFindPassword(userinfor, request);
		return msg;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#registUser(java.lang.String, java.lang.String, int, javax.servlet.http.HttpServletRequest)
	 */
	public String registUser(String username, String password, int type,
			HttpServletRequest request) {
		String agentCode = request.getParameter("agentCode")==null?"0":request.getParameter("agentCode");
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

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#validateCode(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public OTPGeneration validateCode(String phoneNo, HttpServletRequest request) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#phoneCodeExist(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public String phoneCodeExist(String phoneNo, String checkCode,
			HttpServletRequest request) {
		Boolean exist=false;
		HashMap<String, Object> params=new HashMap<String,Object>();
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

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#userEverRegist(java.lang.String)
	 */
	public String userEverRegist(String username) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#validateAgentCode(java.lang.String)
	 */
	public AgentCode validateAgentCode(String agentCode) {
		return loginManageService.selectAgent(agentCode);
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#getMyOrdersInfor(javax.servlet.http.HttpServletRequest)
	 */
	public Map<String, Object> getMyOrdersInfor(Map<String,Object> map1) {
		int thatpage = (Integer) map1.get("thatpage");
		int tabIndex = (Integer) map1.get("tabIndex");
		String startTime = (String) map1.get("startTime");
		String endTime = (String) map1.get("endTime");
		String content = (String) map1.get("content");
		Userinfor user = (Userinfor) map1.get("user");
		
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String,Object> result = new HashMap<String, Object>();
		int bindUserId = 0 ;
		//开始获取用户绑定用户的数据
		if(user.getUsertype()==1||user.getUsertype()==2){//移动端获取我的订单数据
			WechatBind wechatBind=null;
			int phoneUserId=user.getUserid();
			wechatBind = wechatBindMapper.selectByphoneUserId(phoneUserId);
			if(wechatBind!=null&&wechatBind.getWechatuserid()!=null){
				 bindUserId = wechatBind.getWechatuserid();
			}
		}
		map.put("bindUserId", bindUserId);
		map.put("userid", user.getUserid());
		map.put("currentNum", (thatpage-1)*5); //要查询的起始数据条数
		map.put("pageSize", 5); //每页展示的最多数据条数
		map.put("content", content);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		int count0 = myAccountMapper.getMyOrdersCount0(map);
		int count1 = myAccountMapper.getMyOrdersCount1(map);
		int count2 = myAccountMapper.getMyOrdersCount2(map);
		int count3 = myAccountMapper.getMyOrdersCount3(map);
		int count4 = myAccountMapper.getMyOrdersCount4(map);
		result.put("count0", count0);
		result.put("count1", count1);
		result.put("count2", count2);
		result.put("count3", count3);
		result.put("count4", count4);
		map.put("index", tabIndex); //选择的tab:0表示待支付、1表示已支付、2表示暂存、3表示已撤销
		List<InsuranceDetailsVO> insuranceDetailsVOs = myAccountMapper.getMyOrders(map);
		for(InsuranceDetailsVO insuranceDetailsVO:insuranceDetailsVOs){
			if(insuranceDetailsVO!=null){
				insuranceDetailsVO.getVhlinfor().setCodeName(ProcessData.brandNameToCode(insuranceDetailsVO.getVhlinfor().getBrandName()==null?"":insuranceDetailsVO.getVhlinfor().getBrandName()));
				insuranceDetailsVO.getVhlinfor().setDrvowner(insuranceDetailsVO.getVhlinfor().getDrvowner()==null?"":insuranceDetailsVO.getVhlinfor().getDrvowner());
				String systartdate=insuranceDetailsVO.getBaseinfor().getSypolicystartdate();
				String syenddate=insuranceDetailsVO.getBaseinfor().getSypolicyenddate();
				String jqstartdate=insuranceDetailsVO.getBaseinfor().getJqpolicystartdate();
				String jqenddate=insuranceDetailsVO.getBaseinfor().getJqpolicyenddate();
				
				insuranceDetailsVO.getBaseinfor().setSypolicystartdate(systartdate==null?"":systartdate.substring(0, 10));
				insuranceDetailsVO.getBaseinfor().setSypolicyenddate(syenddate==null?"":syenddate.substring(0, 10));
				insuranceDetailsVO.getBaseinfor().setJqpolicystartdate(jqstartdate==null?"":jqstartdate.substring(0, 10));
				insuranceDetailsVO.getBaseinfor().setJqpolicyenddate(jqenddate==null?"":jqenddate.substring(0, 10));
			}
			if(insuranceDetailsVO.getBaseinfor().getTotalPremium() == null){
				insuranceDetailsVO.getBaseinfor().setTotalPremium("0");
			}
		}
		result.put("insuranceDetailsVOs", insuranceDetailsVOs);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#cancelOrder(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public String cancelOrder(String orderNo) {
		String msg = "";
		Baseinfor baseinfor = baseinforMapper.selectByPrimaryKey(orderNo);//根据orderNo查询出订单
		int orderstate = baseinfor.getOrderstate();
		if(orderstate==10 || orderstate==20){ //暂存状态的订单撤销只需要更新我们数据库就OK
			baseinfor.setOrderstate(80);//状态为关闭
			int result=baseinforMapper.updateOrderState(baseinfor);
			if(result==1){
				msg="success";
				logger.info("取消订单修改状态成功 订单号为 :" +orderNo);
			}
		}else if(orderstate == 30 || orderstate == 40){ //待支付状态的订单需要经过华安的接口才能撤销
			InsuranceDetailsVO insuranceDetailsVo=ProcessData.initInsuranceDetailsVO();
			insuranceDetailsVo.setBaseinfor(baseinfor);
			insuranceDetailsVo.getUserinfor().setUserAction("PolicyCancel");
			insuranceDetailsVo = sinosafeinterface.performSinosafeOprations(insuranceDetailsVo);
	        if("10".equalsIgnoreCase(insuranceDetailsVo.getInterfaceslogsWithBLOBs().getInterfacesstatu())){
	        	msg = policyCancelManageService.updateBaseinforOrderState(insuranceDetailsVo);//baseinfor须orderNo有值
	        }else{
	        	logger.info("订单号 :"+baseinfor.getOrderno()+"取消订单调用撤单接口失败");
	        	msg=insuranceDetailsVo.getUserinfor().getErrorMessage();
	        }
		}
		return msg;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#continuePay(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	/**
	 * 继续支付 
	 * continue pay
	 */
	public String continuePay(String orderNo) {
		InsuranceDetailsVO insuranceDetailsVO = myAccountMapper.getInsuranceByOrderNo(orderNo);
		InterfaceslogsWithBLOBs interfaceslogsWithBLOBs = new InterfaceslogsWithBLOBs();    //存报文信息
		insuranceDetailsVO.setInterfaceslogsWithBLOBs(interfaceslogsWithBLOBs);
		String url = insuranceDetailsVO.getPaymentinfor().getPaymenturl();
		Baseinfor baseinfor = insuranceDetailsVO.getBaseinfor();
		if(baseinfor.getOrderstate()==30&&(url==null||url.equals(""))){//状态等于30,未生成url,调用支付接口生成URL
			Userinfor userinfor = new Userinfor();
			insuranceDetailsVO.setUserinfor(userinfor);
			insuranceDetailsVO.getUserinfor().setUserAction("PaymentApplication");//设置action name
			sinosafeinterface.performSinosafeOprations(insuranceDetailsVO);
			
			Float taxpremium =Float.parseFloat((insuranceDetailsVO.getBaseinfor().getTaxpremium()==null?0:insuranceDetailsVO.getBaseinfor().getTaxpremium()).toString()); 
			Float jqpremium = Float.parseFloat((insuranceDetailsVO.getBaseinfor().getJqpremium()==null?0:insuranceDetailsVO.getBaseinfor().getJqpremium()).toString());
			Float sypremium = Float.parseFloat((insuranceDetailsVO.getBaseinfor().getSypremium()==null?0:insuranceDetailsVO.getBaseinfor().getSypremium()).toString());
			Float paymentpremium = taxpremium + jqpremium + sypremium; //得到支付的总金额
			insuranceDetailsVO.getPaymentinfor().setPaymentpremium(paymentpremium.toString());
			insuranceDetailsVO.getPaymentinfor().setPaymenttimes(DateFormatUtils.getSystemDate());
			
			if("10".equals(insuranceDetailsVO.getInterfaceslogsWithBLOBs().getInterfacesstatu())){
				dbDataManageService.operatePaymentInfo(insuranceDetailsVO);
			}
			url=insuranceDetailsVO.getPaymentinfor().getPaymenturl();
		}
		logger.info("URL:"+url);
		return url;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#continueInsure(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public Map<String,Object> continueInsure(String orderNo) {
		Map<String,Object> map = new HashMap<String,Object>();
		String path="";
		InsuranceDetailsVO insuranceDetailsVO = myAccountMapper.getInsuranceByOrderNo(orderNo);
		List<Coverageinfor> listCoverage=coverageinforMapper.getListCoverage(orderNo);
		insuranceDetailsVO.setCoverageinfors(listCoverage);
		InterfaceslogsWithBLOBs interfaceslogsWithBLOBs = new InterfaceslogsWithBLOBs();
		insuranceDetailsVO.setInterfaceslogsWithBLOBs(interfaceslogsWithBLOBs);
     	Baseinfor baseinfor = insuranceDetailsVO.getBaseinfor();
		if(baseinfor.getOrderstate()==10 //状态为10，并且没有身份证号，判断此单最后操作为首页提交表单成功了
				&&(insuranceDetailsVO.getVhlinfor().getCertificateno()==null
				||insuranceDetailsVO.getVhlinfor().getCertificateno()=="")){
			insuranceDetailsVO.getUserinfor().setPageFlag(2);
		}else if(baseinfor.getOrderstate()==10  //状态为10，并且有身份证号码，就判断为此单最后操作为车辆信息页面提交表单成功了
				&&insuranceDetailsVO.getVhlinfor().getCertificateno()!=null
				&&insuranceDetailsVO.getVhlinfor().getCertificateno()!=""){
			insuranceDetailsVO.getUserinfor().setPageFlag(3);
		}else if(baseinfor.getOrderstate()==20){ //判断此单最后操作为保险页面确认报价成功了
			insuranceDetailsVO.getUserinfor().setPageFlag(4);
		}
		path = "/vehicleInfor/backToVehicleScreen.do?orderNo="+orderNo;
		map.put("path", path);
		map.put("insuranceDetailsVO", insuranceDetailsVO);
		return map;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#showOrderDetail(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public InsuranceDetailsVO showOrderDetail(String orderNo) {
		InsuranceDetailsVO insuranceDetailsVO = myAccountMapper.getInsuranceByOrderNo(orderNo);
		List<Coverageinfor> listCoverage=coverageinforMapper.getListCoverage(orderNo);
		DecimalFormat decimalFormat=new DecimalFormat(".00");
		for(Coverageinfor cover:listCoverage){
			if(cover.getSuminsured()!=null&&!cover.getSuminsured().equals("")&&!cover.getSuminsured().equals("0")
		        	   &&!cover.getSuminsured().equals("0.00")){
		        		cover.setSuminsured(decimalFormat.format(Float.parseFloat(cover.getSuminsured())));
		        	}
		        	if(cover.getPremium()!=null&&!cover.getPremium().equals("")&&!cover.getPremium().equals("0")&&!cover.getPremium().equals("0.00")){
		        		cover.setPremium(decimalFormat.format(Float.parseFloat(cover.getPremium())));
		        	}
		        	if(cover.getNyl12()!=null&&!cover.getNyl12().equals("")&&!cover.getNyl12().equals("0")&&!cover.getNyl12().equals("0.00")){
		        		cover.setNyl12(decimalFormat.format(Float.parseFloat(cover.getNyl12())));
		        	}
		}
		insuranceDetailsVO.setCoverageinfors(listCoverage);
		return insuranceDetailsVO;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#getMyAccountInitInfor(javax.servlet.http.HttpServletRequest)
	 */
	public Map<String, Object> getMyAccountInitInfor(Userinfor user,String startTime,String endTime) {
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String,Object> result = new HashMap<String, Object>();
		int userType = user.getUsertype();
		int bindUserId = 0 ;
		if(userType==1||userType==2){//移动端获取我的订单数据
			WechatBind wechatBind=null;
			int phoneUserId=user.getUserid();
			wechatBind = wechatBindMapper.selectByphoneUserId(phoneUserId);
			if(wechatBind!=null&&wechatBind.getWechatuserid()!=null){
				 bindUserId = wechatBind.getWechatuserid();
			}
		}
		map.put("userid", user.getUserid());
		map.put("bindUserId", bindUserId);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		int count0 = myAccountMapper.getMyOrdersCount0(map);
		int count1 = myAccountMapper.getMyOrdersCount1(map);
		int count2 = myAccountMapper.getMyOrdersCount2(map);
		int count3 = myAccountMapper.getMyOrdersCount3(map);
		int count4 = myAccountMapper.getMyOrdersCount4(map);
		result.put("count0", count0);
		result.put("count1", count1);
		result.put("count2", count2);
		result.put("count3", count3);
		result.put("count4", count4);
		List<InsuranceDetailsVO> insuranceDetailsVOs0 = myAccountMapper.getMyOrdersTopTen0(map);
		List<InsuranceDetailsVO> insuranceDetailsVOs1 = myAccountMapper.getMyOrdersTopTen1(map);
		List<InsuranceDetailsVO> insuranceDetailsVOs2 = myAccountMapper.getMyOrdersTopTen2(map);
		List<InsuranceDetailsVO> insuranceDetailsVOs3 = myAccountMapper.getMyOrdersTopTen3(map);
		List<InsuranceDetailsVO> insuranceDetailsVOs4 = myAccountMapper.getMyOrdersTopTen4(map);
		List<List<InsuranceDetailsVO>> insuranceDetailsVOsList = new ArrayList<List<InsuranceDetailsVO>>();
		insuranceDetailsVOsList.add(insuranceDetailsVOs0);
		insuranceDetailsVOsList.add(insuranceDetailsVOs1);
		insuranceDetailsVOsList.add(insuranceDetailsVOs2);
		insuranceDetailsVOsList.add(insuranceDetailsVOs3);
		insuranceDetailsVOsList.add(insuranceDetailsVOs4);
		for (List<InsuranceDetailsVO> insuranceDetailsVOs:insuranceDetailsVOsList) {
			for(InsuranceDetailsVO insuranceDetailsVO:insuranceDetailsVOs){
				if(insuranceDetailsVO!=null){
					insuranceDetailsVO.getVhlinfor().setCodeName(ProcessData.brandNameToCode(insuranceDetailsVO.getVhlinfor().getBrandName()==null?"":insuranceDetailsVO.getVhlinfor().getBrandName()));
					insuranceDetailsVO.getVhlinfor().setDrvowner(insuranceDetailsVO.getVhlinfor().getDrvowner()==null?"":insuranceDetailsVO.getVhlinfor().getDrvowner());
					String systartdate=insuranceDetailsVO.getBaseinfor().getSypolicystartdate();
					String syenddate=insuranceDetailsVO.getBaseinfor().getSypolicyenddate();
					String jqstartdate=insuranceDetailsVO.getBaseinfor().getJqpolicystartdate();
					String jqenddate=insuranceDetailsVO.getBaseinfor().getJqpolicyenddate();
					
					insuranceDetailsVO.getBaseinfor().setSypolicystartdate(systartdate==null?"":systartdate.substring(0, 10));
					insuranceDetailsVO.getBaseinfor().setSypolicyenddate(syenddate==null?"":syenddate.substring(0, 10));
					insuranceDetailsVO.getBaseinfor().setJqpolicystartdate(jqstartdate==null?"":jqstartdate.substring(0, 10));
					insuranceDetailsVO.getBaseinfor().setJqpolicyenddate(jqenddate==null?"":jqenddate.substring(0, 10));
				}
				if(insuranceDetailsVO.getBaseinfor().getTotalPremium() == null){
					insuranceDetailsVO.getBaseinfor().setTotalPremium("0");
				}
			}
		}
		result.put("insuranceDetailsVOs0", insuranceDetailsVOs0);
		result.put("insuranceDetailsVOs1", insuranceDetailsVOs1);
		result.put("insuranceDetailsVOs2", insuranceDetailsVOs2);
		result.put("insuranceDetailsVOs3", insuranceDetailsVOs3);
		result.put("insuranceDetailsVOs4", insuranceDetailsVOs4);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#getListCoverage(java.lang.String)
	 */
	public List<Coverageinfor> getListCoverage(String orderNo) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#getOrderinformation(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	public List<InsuranceDetailsVO> getOrderinformation(
			Map<String, Object> map, HttpServletRequest request) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#cancelOrderNo(com.quicksure.insurance.entity.Baseinfor, javax.servlet.http.HttpServletRequest)
	 */
	public String cancelOrderNo(Baseinfor baseinfor, HttpServletRequest request) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#continuePayment(com.quicksure.insurance.entity.InsuranceDetailsVO, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public InsuranceDetailsVO continuePayment(
			InsuranceDetailsVO insuranceDetails, HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.quicksure.insurance.service.MyAccountService#getAllBaseinfor()
	 */
	public List<Baseinfor> getAllBaseinfor() {
		return null;
	}

}
