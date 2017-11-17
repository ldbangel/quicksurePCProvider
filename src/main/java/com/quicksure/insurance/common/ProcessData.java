package com.quicksure.insurance.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.quicksure.insurance.constants.LudiConstants;
import com.quicksure.insurance.entity.Agreementinfor;
import com.quicksure.insurance.entity.Baseinfor;
import com.quicksure.insurance.entity.Coverageinfor;
import com.quicksure.insurance.entity.Deliveryinfor;
import com.quicksure.insurance.entity.Dptinfor;
import com.quicksure.insurance.entity.DropdownListInfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;
import com.quicksure.insurance.entity.Insuranceperinfor;
import com.quicksure.insurance.entity.InterfaceDetails;
import com.quicksure.insurance.entity.InterfaceslogsWithBLOBs;
import com.quicksure.insurance.entity.Paymentinfor;
import com.quicksure.insurance.entity.Taxinfor;
import com.quicksure.insurance.entity.Userinfor;
import com.quicksure.insurance.entity.Vhlinfor;
import com.quicksure.insurance.util.StringUtils;
import com.quicksure.insurance.utils.DateFormatUtils;
import com.quicksure.insurance.utils.SysConstantsConfig;
import com.quicksure.insurance.utils.XmlUtilsByDom4j;
import com.quicksure.insurance.velocity.VelocityTemplate;

public class ProcessData {
	private static final Logger logger = Logger
			.getLogger(ProcessData.class);
	/**
	 * 生成請求信息根據action
	 * 
	 * @param insuranceDetailsVO
	 * @return
	 */
	public static String handleRequest(InsuranceDetailsVO insuranceDetailsVO) {
		String action = insuranceDetailsVO.getUserinfor().getUserAction();
		String templateName = getTemplateName(action);
		insuranceDetailsVO = setInterfacecode(insuranceDetailsVO); 
		return generaceRequest(templateName, insuranceDetailsVO);
	}

	/**
	 * 調用velocity，生成requets
	 * 
	 * @param templateName
	 * @param obj
	 * @return
	 */
	public static String generaceRequest(String templateName, Object obj) {
		String requestXml = "";
		Map<String, Object> map = new HashedMap();
		try {
			InsuranceDetailsVO insuranceDetailsVO = (InsuranceDetailsVO) obj;
			String userAction = insuranceDetailsVO.getUserinfor().getUserAction();
			if (StringUtils.checkStringEmpty(userAction)
					&& (userAction.equalsIgnoreCase("ModelSerachByVin") 
							|| userAction.equalsIgnoreCase("ModelSerachByName") )) {
				String Vehiclename=insuranceDetailsVO.getVhlinfor().getVehiclename();
				if(Vehiclename!=null&&!"".equalsIgnoreCase(Vehiclename)){
					Vehiclename=URLEncoder.encode(Vehiclename, "UTF-8");
					insuranceDetailsVO.getVhlinfor().setVehiclename(Vehiclename);
				}
			}
			String dateTime = DateFormatUtils.getSystemDate();
			String[] dateTimes = StringUtils.split(dateTime, " ");
			String date = dateTimes[0] != null ? dateTimes[0] : "";
			String Times = dateTimes[1] != null ? dateTimes[1] : "";
			map.put("dateTime", dateTime);
			map.put("date", date);
			map.put("times", Times);
			map.put("JYUserName", SysConstantsConfig.JY_CONNECT_USERNAME);
			map.put("JYPassword", SysConstantsConfig.JY_CONNECT_PASSWORD);
			map.put("SinosafeUserName",
					SysConstantsConfig.SINOSAFE_CONNECT_USERNAME);
			map.put("SinosafePassword",
					SysConstantsConfig.SINOSAFE_CONNECT_PASSWOED);
			map.put("SinosafeExtenterCode",
					SysConstantsConfig.SINOSAFE_EXTENTERPCODE);
			map.put("returnURL",SysConstantsConfig.SINOSAFE_RETURN_URL+"?LudiOrderNo="+insuranceDetailsVO.getBaseinfor().getOrderno());
			map.put("dataObj", obj);
			requestXml = VelocityTemplate
					.getVelocityTemplate(templateName, map);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();  
			  e.printStackTrace(new PrintWriter(sw, true));  
			  String str = sw.toString(); 
			  logger.error("调用模板引擎异常"+str);
		}
		return requestXml;

	}

	/**
	 * 
	 * 根据useraction 获取对应action的template 请求模板
	 * 
	 * @param useraction
	 * @return
	 */
	public static String getTemplateName(String useraction) {
		String templateName = "";
		if (StringUtils.checkStringEmpty(useraction)) {
			if (useraction.equals("ModelSerachByVin")) {
				templateName = "template_ModelSerachbyvinfromJY.xml";
			} else if (useraction.equals("ModelSerachByName")) {
				templateName = "template_ModelSerachbyModelfromJY.xml";	
			} else if (useraction.equals("SubmitUnderwriting")) {
				templateName = "template_SubmitUnderwriting.xml";//提交核保
			} else if (useraction.equals("DeliveryOrderInfo")){
				templateName = "template_ModelDeliveryInforToJY.xml";
			} else if (useraction.equals("PaymentApplication")){
				templateName = "template_PaymentApplication.xml";
			} else if(useraction.equals("PremiumCount")){
				templateName = "template_PremiumCount.xml"; //保费试算报文
			} /*else if (useraction.equals("PolicyStatusQuery")){
				templateName = "template_PolicyStatusQuery.xml";
			} */else if(useraction.equals("PolicyCancel")){
				templateName = "template_ModelPolicyCancelFromSinosafe.xml";
			} else if(useraction.equals("PolicyStatusQueryList")){
				templateName = "template_PolicyStatusQueryList.xml";
			}else if(useraction.equals("Renewalinfor")){//续保
				templateName = "template_RenewalinforQuery.xml";
			}else if(useraction.equals("ModelSearchBySinosafe")){ //车辆查询通过华安
				templateName = "template_ModelSearchFromSinosafe.xml";
			}

		}
		return templateName;

	}

	/**
	 * 根據action，生成需要解析的對應節點 如
	 * DataPacket/ProductResponses/VehicleResponse/Vehicle 即解析的xml結構為
	 * <DataPacket
	 * ><ProductResponses><VehicleResponse><Vehicle></VehicleResponse>
	 * </ProductResponses><DataPacket>
	 * 
	 * @param action
	 * @return
	 */
	public static String getAnalysisPathformAction(String action) {
		String path = "";
		if (StringUtils.checkStringEmpty(action)) {
			if (action.equals("ModelSerachByVin")
					|| action.equalsIgnoreCase("ModelSerachByName")) {
				path = "DataPacket/ProductResponses/VehicleResponse/Vehicle";
			}else if (action.equals("DeliveryOrderInfo")) {
				path = "PACKET/HEAD"; 
			}else if(action.equals("PaymentApplication")){
				path = "PACKET/BODY/BASE";
			}else if(action.equals("PolicyStatusQueryList")){
				path = "PACKET/BODY/APP_INFO";
			} else if(action.equals("PolicyCancel")){
				path = "PACKET/BODY/BASE/APP_INFO";
			} else if(action.equals("PremiumCount")){ //保费试算
				path="PACKET/BODY";
			} else if(action.equals("SubmitUnderwriting")){ //提交核保
				path="PACKET/BODY";
			}else if(action.equals("Renewalinfor")){ //续保返回解析
				path="PACKET/BODY";
			}else if(action.equals("ModelSearchBySinosafe")){
				path="PACKET/BODY/VHL_LIST/VHL_DATA";
			}	
		}
		return path;
	}

	/**
	 * 處理interfaces response
	 * 
	 * @param response
	 * @param insuranceDetailsVO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static InsuranceDetailsVO handleResponse(String response,
			InsuranceDetailsVO insuranceDetailsVO) {
		String action = insuranceDetailsVO.getUserinfor().getUserAction();
		Map<String, Object> responseList = getListfromResponse(response,action);
		
		logger.info("-------action------:"+action);
		if (StringUtils.checkStringEmpty(action)) {
			if (action.equalsIgnoreCase("ModelSerachByVin")
					|| action.equalsIgnoreCase("ModelSerachByName")) {
				if(responseList.get("Vehicle")==null){
					List<Map<String, Object>> vhlinforList=new ArrayList<Map<String,Object>>();
					vhlinforList.add(responseList);
					insuranceDetailsVO.setVhlinfoList(vhlinforList);
				}else{
					insuranceDetailsVO.setVhlinfoList((List<Map<String, Object>>) responseList.get("Vehicle"));				
				}								
			}else if(action.equalsIgnoreCase("ModelSearchBySinosafe")){
				if(responseList.get("VHL_DATA")==null){
					List<Map<String, Object>> vhlinforList=new ArrayList<Map<String,Object>>();
					vhlinforList.add(responseList);
					insuranceDetailsVO.setVhlinfoList(vhlinforList);
				}else{
					insuranceDetailsVO.setVhlinfoList((List<Map<String, Object>>) responseList.get("VHL_DATA"));				
				}	
			}else if(action.equalsIgnoreCase("DeliveryOrderInfo")){
				insuranceDetailsVO.setPolCancelList((List<Map<String, String>>) responseList.get(""));
			}else if(action.equalsIgnoreCase("PaymentApplication")){
				insuranceDetailsVO.getPaymentinfor().setPaymentno(responseList.get("PAY_APP_NO").toString());//支付链接：paymentUrl
				insuranceDetailsVO.getPaymentinfor().setPaymenturl(responseList.get("PAYADDRESS").toString());//订单号：paymentNo
			}else if(action.equalsIgnoreCase("PolicyCancel")){
				insuranceDetailsVO.setPolCancelList((List<Map<String, String>>) responseList.get("APP_INFO"));
			}else if(action.equalsIgnoreCase("PolicyStatusQueryList")){
				insuranceDetailsVO.setMultiplePolicyStatusList((List<Map<String, String>>) responseList.get("APP_INFO"));
			}else if(action.equalsIgnoreCase("SubmitUnderwriting")){
				Map<String,String> jqmap =(HashMap<String, String>) responseList.get("JQ_BASE");//获取到JQ节点
				Map<String,String> symap =(HashMap<String, String>) responseList.get("SY_BASE") ;//获取到SY节点
				String syapplicationno = "";//商业险投保单
				String sypremium = "";  //商业险保费
				String sypolicyenddate = ""; //商业险保险止期
				String jqapplicationno = ""; //交强险投保单
				String jqpremium = ""; //交强险保费
				String jqpolicyenddate = ""; //交强险止期
				String createtime = ""; //下单时间
				String applicationname = ""; //投保人
				String insrntname = ""; //被保人
				if(!symap.get("PLY_APP_NO").equals("")){
					 syapplicationno=symap.get("PLY_APP_NO");
					 sypremium=symap.get("PREMIUM");
					 sypolicyenddate=symap.get("INSRNC_END_TM");
					 createtime=symap.get("OPER_DATE");
					 applicationname = symap.get("APP_NME");
					 insrntname = symap.get("INSRNT_CNM");
					 insuranceDetailsVO.getBaseinfor().setSyapplicationno(syapplicationno);
				     insuranceDetailsVO.getBaseinfor().setSypremium(sypremium);
				     insuranceDetailsVO.getBaseinfor().setSypolicyenddate(sypolicyenddate);
				     insuranceDetailsVO.getBaseinfor().setCreatetime(createtime);
					 insuranceDetailsVO.getBaseinfor().setBase_applicationname(applicationname);
					 insuranceDetailsVO.getBaseinfor().setInsrntname(insrntname);
				}
				if(!jqmap.get("PLY_APP_NO").equals("")){
					 jqapplicationno=jqmap.get("PLY_APP_NO");
					 jqpremium=jqmap.get("PREMIUM");
					 jqpolicyenddate=jqmap.get("INSRNC_END_TM");
					 insuranceDetailsVO.getBaseinfor().setJqapplicationno(jqapplicationno);
					 insuranceDetailsVO.getBaseinfor().setJqpremium(jqpremium);
					 insuranceDetailsVO.getBaseinfor().setJqpolicyenddate(jqpolicyenddate);
				}
			}else if(action.equalsIgnoreCase("PremiumCount")){	
				logger.info("-----处理responseList-----");
				Map<String,Object> changedMap = changeKeyForPremium(responseList);//将保费试算返回key转换为对应对象的属性名
				insuranceDetailsVO = setPremiumResponsetoVo(changedMap,insuranceDetailsVO);//将保费计算返回封装为对象
				
				 Map<String,String> sybase =(HashMap<String, String>) responseList.get("SY_BASE") ;//获取到sy节点
				 Map<String,String> jqbase =(HashMap<String, String>) responseList.get("JQ_BASE") ;//获取到jq节点
				 Map<String,String> vhl =(HashMap<String, String>) responseList.get("VHL") ;//获取到jq节点
				 String syquerySequenceNo ="";//商业投保查询码
				 String jqquerySequenceNo ="";//交强投保查询码
				 String setNumber="";//核定载客人数
				 
				 if(!sybase.get("QUERY_SEQUENCE_NO").equals("")&&sybase.get("QUERY_SEQUENCE_NO")!=null){
					 syquerySequenceNo=sybase.get("QUERY_SEQUENCE_NO");
					 insuranceDetailsVO.getBaseinfor().setSyquerySequenceNo(syquerySequenceNo);
				 }
				 if(jqbase!=null){
					 if(!jqbase.get("QUERY_SEQUENCE_NO").equals("")&&jqbase.get("QUERY_SEQUENCE_NO")!=null){
						 jqquerySequenceNo=jqbase.get("QUERY_SEQUENCE_NO");
						 insuranceDetailsVO.getBaseinfor().setJqquerySequenceNo(jqquerySequenceNo);
					 }
				 }
				 if(!vhl.get("SET_NUM").equals("")&&vhl.get("SET_NUM")!=null){
					 setNumber=vhl.get("SET_NUM");
					 insuranceDetailsVO.getVhlinfor().setSetNumber(setNumber);
				 }
								
			}else if(action.equalsIgnoreCase("Renewalinfor")){	//解析续保KEY封装到对象里			
				Map<String,Object> returnforRenewalinforMap = (Map<String, Object>) responseList.get("VHL");
				if(returnforRenewalinforMap!=null&&returnforRenewalinforMap.size()>0){
					Map<String, Object> newresponseList=new HashMap<String, Object>();
					newresponseList.put("VHL_RENEWAL", returnforRenewalinforMap);
					Map<String,String> changedMap=(Map<String, String>) changeKeyForPremium(newresponseList).get("VHL_RENEWAL");//将保费试算返回key转换为对应对象的属性名
					for(String key: changedMap.keySet()){
						setValuetoObj(key,changedMap.get(key),insuranceDetailsVO.getVhlinfor());
					}
				}				
			}
		}
		return insuranceDetailsVO;
	}

	/**
	 * 處理interfaces response
	 * 
	 * @param response
	 * @param action
	 * @return
	 */
	public static Map<String, Object> getListfromResponse(String response,
			String action) {
		Map<String, Object> responseMap = null;
		String encodeing = "UTF-8";
		if (StringUtils.checkStringEmpty(action)
				&& (action.equals("ModelSerachByVin") || action
						.equalsIgnoreCase("ModelSerachByName"))) {
			encodeing = "gbk";
		}
		String analysisPath = getAnalysisPathformAction(action);

		try {
			responseMap = XmlUtilsByDom4j.XmlToMap(response, analysisPath,encodeing);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();  
			e.printStackTrace(new PrintWriter(sw, true));  
			String str = sw.toString(); 
			logger.error("解析返回信息异常"+str);
		}
		return responseMap;
	}

	/**
	 * 获取interface code通过action
	 * @param insuranceDetailsVO
	 * @return
	 */
   public static InsuranceDetailsVO setInterfacecode(InsuranceDetailsVO insuranceDetailsVO){
	   String action=insuranceDetailsVO.getUserinfor().getUserAction();
	   String interfaceCode="";
	   if(StringUtils.checkStringEmpty(action)){
		   if(action.equals("ModelSerachByVin") 
				   || action.equalsIgnoreCase("ModelSerachByName") 
				   || action.equalsIgnoreCase("ModelSearchBySinosafe")){
			   interfaceCode="100001";//车型查询代码
		   }else if(action.equals("SubmitUnderwriting")){
			   interfaceCode="100014";//提核代码
		   }else if(action.equals("DeliveryOrderInfo")){
			   interfaceCode="100016";//配送代码
		   }else if(action.equals("PaymentApplication")){
			   interfaceCode="100010";//支付申请
		   }else if(action.equals("PremiumCount")){
			   interfaceCode="100004";//保费试算
		   }/*else if(action.equals("PolicyStatusQuery")){
			   interfaceCode="100007";//保单状态查询
		   }*/else if(action.equals("PolicyCancel")){
			   interfaceCode="100015";//撤单
		   }else if(action.equals("PolicyStatusQueryList")){
			   interfaceCode="100007";//保单状态查询
		   }else if(action.equals("Renewalinfor")){
			   interfaceCode="100019";//续保
		   }
	   }
	   insuranceDetailsVO.getInterfaceslogsWithBLOBs().setInterfacescode(interfaceCode);
	   return insuranceDetailsVO;
   }
   /**
    * 获取responseCode
    * &&errorMessage
    * @param action
    * @param response
    * @return
    */
   public static  Map<String, String> getResponseCodeandMessage(String action,String response){
	   Map<String, String> map=new HashMap<String,String>();
	   String path="";
	   String encoding="utf-8";
	   if(StringUtils.checkStringEmpty(action)){
		   if(action.equals("ModelSerachByVin") 
				   || action.equalsIgnoreCase("ModelSerachByName")){
			   path = "DataPacket/Responsetor";
			   encoding="gbk";
		   }else{
			   path="PACKET/HEAD";
		   }	   
	   }
	   List<Map<String, String>> resultList = XmlUtilsByDom4j.specifiedXml2list(response, path, encoding);
	   if(resultList!=null && resultList.size()>0){
		   for(String s : resultList.get(0).keySet()){
			   if(LudiConstants.SINOSAFEERRORCODE.equalsIgnoreCase(s)
					   ||LudiConstants.JYERRORCODE.equalsIgnoreCase(s)
					   ||LudiConstants.SINOSAFECANCELERRORCODE.equalsIgnoreCase(s)){			   
				   map.put("errorCode",resultList.get(0).get(s) );
			   }
			   if(LudiConstants.SINOSAFEERRORMESSAGE.equalsIgnoreCase(s)
					   ||LudiConstants.JYERRORMESSAGE.equalsIgnoreCase(s)
					   ||LudiConstants.SINOSAFECANCELERRORMESSAGE.equalsIgnoreCase(s)){
				   map.put("errorMessage", resultList.get(0).get(s));
			   }
			   
		   }
	   }	   
	   return map;
	   
   }
   /**
    * 赋值给大对象
    * 孙小东
    * @param object
    * @param insuranceDetails
    * @return
    */
   public static InsuranceDetailsVO setObjValuetoinsuranceDetails(Object object,InsuranceDetailsVO insuranceDetails){
		Class objclass = object.getClass();
		Class insuranceDetaisClass = insuranceDetails.getClass();
		String methodNames = "get" + objclass.getSimpleName();
		Object object1 = null;
		try {
			Method method = insuranceDetaisClass.getMethod(methodNames, null);
			object1 = method.invoke(insuranceDetails, null);
			Field[] field = objclass.getDeclaredFields();
			for (int i = 0; i < field.length; i++) {
				String fieldName = field[i].getName();
				String methodName = "get" + fieldName;
				Object fieldvalue;
				Method[] methods = objclass.getDeclaredMethods();
				if(StringUtils.checkStringEmpty(fieldName)&&"vhiinforid".equalsIgnoreCase(fieldName)){
					continue;
				 }
				for (int j = 0; j < methods.length; j++) {
					if (methodName.equalsIgnoreCase(methods[j].getName())) {

						fieldvalue = methods[j].invoke(object, null);
						if (fieldvalue != null
								&& !"".equalsIgnoreCase(fieldvalue.toString())) {
							setValuetoObj(fieldName, fieldvalue, object1);
						}

					}
				}
			}
		} catch (Exception e) {
			  StringWriter sw = new StringWriter();  
			  e.printStackTrace(new PrintWriter(sw, true));  
			  String str = sw.toString();
			  logger.error("setObjValuetoinsuranceDetails 方法异常"+str);
		}

		return insuranceDetails;
	}
   /**
    * 赋value到对象
    * 孙小东
    * @param fieldName
    * @param value
    * @param obj
    * @return
    */
   public static Object setValuetoObj(String fieldName,Object value, Object obj){
	   Class classObject=obj.getClass();
	   Method[] methods=classObject.getDeclaredMethods();
	   for(int i=0;i<methods.length;i++){
		   Method method=methods[i];
		   String methodName="set"+fieldName;
		   String currentMethodName=methods[i].getName();
		   Class paramType=null;		   		  	   		    
		   if(methodName.equalsIgnoreCase(currentMethodName)){
			   try {					
				obj= method.invoke(obj, value);
			} catch (IllegalAccessException e) {
				 StringWriter sw = new StringWriter();  
				  e.printStackTrace(new PrintWriter(sw, true));  
				  String str = sw.toString();
				  logger.error("setValuetoObj 方法异常"+str);
			} catch (IllegalArgumentException e) {
				StringWriter sw = new StringWriter();  
				  e.printStackTrace(new PrintWriter(sw, true));  
				  String str = sw.toString();
				  logger.error("setValuetoObj 方法异常"+str);
			} catch (InvocationTargetException e) {
				 StringWriter sw = new StringWriter();  
				  e.printStackTrace(new PrintWriter(sw, true));  
				  String str = sw.toString();
				  logger.error("setValuetoObj 方法异常"+str);
			}			   
		   }
	   }
	   
	   return obj;
   }
   /**
    * 大对象初始化
    * 孙小东
    * @return
    */
   public static InsuranceDetailsVO initInsuranceDetailsVO(){
	   InsuranceDetailsVO insuranceDetailsVO= new InsuranceDetailsVO();
	   
	   Baseinfor baseinfor=new Baseinfor();
	   Deliveryinfor deliveryinfor=new Deliveryinfor();
	   Dptinfor dptinfor=new Dptinfor();
	   DropdownListInfor dropdownListInfor=new DropdownListInfor();
	   Insuranceperinfor insuranceperinfor=new Insuranceperinfor();
	   InterfaceDetails interfaceDetails=new InterfaceDetails();
	   Paymentinfor paymentinfor=new Paymentinfor();
	   Taxinfor taxinfor=new Taxinfor();
	   Userinfor userinfor=new Userinfor();
	   Vhlinfor vhlinfor=new Vhlinfor();
	   InterfaceslogsWithBLOBs interfaceslogsWithBLOBs=new InterfaceslogsWithBLOBs();
	   List<Map<String, Object>> vhlinfoLis=new ArrayList<Map<String,Object>>();
	   List<Coverageinfor> coverageinfors=new ArrayList<Coverageinfor>();
	   
	   insuranceDetailsVO.setBaseinfor(baseinfor);
	   insuranceDetailsVO.setCoverageinfors(coverageinfors);
	   insuranceDetailsVO.setDeliveryinfor(deliveryinfor);
	   insuranceDetailsVO.setDptinfor(dptinfor);
	   insuranceDetailsVO.setDropdownListInfor(dropdownListInfor);
	   insuranceDetailsVO.setInsuranceperinfor(insuranceperinfor);
	   insuranceDetailsVO.setInterfaceDetails(interfaceDetails);
	   insuranceDetailsVO.setInterfaceslogsWithBLOBs(interfaceslogsWithBLOBs);
	   insuranceDetailsVO.setTaxinfor(taxinfor);
	   insuranceDetailsVO.setUserinfor(userinfor);
	   insuranceDetailsVO.setVhlinfoList(vhlinfoLis);
	   insuranceDetailsVO.setPaymentinfor(paymentinfor);
	   insuranceDetailsVO.setVhlinfor(vhlinfor);
	   
	   return insuranceDetailsVO;
   }
/**
 * 转换保费试算返回的key转换为对像的属性名
 * 孙小东
 * @param map
 * @return
 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> changeKeyForPremium(
			Map<String, Object> returnLists) {
		Map<String, Object> changedData = new HashMap<String, Object>();
		ApplicationContext context = new ClassPathXmlApplicationContext("premiumReturnMapping.xml");
		logger.info("--------加载 premiumReturnMapping.xml--------");
		for (String key : returnLists.keySet()) {
			boolean breakflag = false;
			if ( ("CLAIM_LIST").equalsIgnoreCase(key)
					|| ("REPEAT_DATA").equalsIgnoreCase(key)
					|| "REINSURE_MSG".equalsIgnoreCase(key)
					|| "ACCIDENT_LIST".equalsIgnoreCase(key)
					|| "REINSURE_LIST".equalsIgnoreCase(key)) {
				continue;
			}
			Map<String, String> map1 = (Map<String, String>) context
					.getBean(key);
			Map<String, Object> map2 = null;
			String appntList ="";
			if(key.equals("APPNT_LIST")){
				if(returnLists.get(key).equals("")){
				    appntList="{\"APPNT_DATA\":[{\"APPNT_CDE\":\"HA0005N\",\"IS_MODIFY\":\"0\",\"APPNT_NME\":\"贷款车特约\",\"PROD_NO\":\"0380\",\"APPNT_DTL\":\"本保单涉及车辆损失类险种的第一受益人为***。①未经其事先书面同意本保单不得被退保或减保或批改，不影响第一受益人权益的批改除外。②当一次事故的保险赔款金额高于人民币5000元时，被保险人必须提供第一受益人书面同意书（责任保险赔款除外.\",\"IS_NEEDED\":\"1\"},{\"APPNT_CDE\":\"HA0006N\",\"IS_MODIFY\":\"0\",\"APPNT_NME\":\"车型特约\",\"PROD_NO\":\"0380\",\"APPNT_DTL\":\"本保险车辆的实际车辆型号为****。\",\"IS_NEEDED\":\"1\"}]}";
				    JSONObject jsonObject = JSONObject.fromObject(appntList);
				    map2=JSONObject.fromObject(jsonObject);
				}else{
					 map2 = (Map<String, Object>) returnLists
							.get(key);
				}
			}else{
				 map2 = (Map<String, Object>) returnLists
						.get(key);
			}
			Map<String, Object> map3 = new HashMap<String, Object>();
			for (String key1 : map1.keySet()) {
				for (String key2 : map2.keySet()) {
					if ("CVRG_DATA".equalsIgnoreCase(key2)) {
						breakflag = true;
						if(map2.get(key2) instanceof List){
							List<Map<String, String>> list = (List<Map<String, String>>) map2
									.get(key2);
							List<Map<String, String>> coverageList = new ArrayList<Map<String, String>>();
							for (Map<String, String> map : list) {
								Map<String, String> map4 = new HashMap<String, String>();
								for (String key4 : map1.keySet()) {
									for (String key3 : map.keySet()) {
										if (key4.equalsIgnoreCase(key3)) {
											map4.put(map1.get(key4),
													(String) map.get(key3));
											break;
										}
									}
								}
								
								coverageList.add(map4);
							}
							map3.put(key2, coverageList);
						}else{   //只选三者或司机并且不勾选不计免赔的时候报类型转换异常
							Map<String, String> map = (Map<String, String>) map2.get(key2);
							List<Map<String, String>> coverageList = new ArrayList<Map<String, String>>();
							Map<String, String> map4 = new HashMap<String, String>();
							for (String key4 : map1.keySet()) {
								for (String key3 : map.keySet()) {
									if (key4.equalsIgnoreCase(key3)) {
										map4.put(map1.get(key4),
												(String) map.get(key3));
										break;
									}
								}
							}
							
							coverageList.add(map4);
							map3.put(key2, coverageList);
						}

					} 
					else if ("APPNT_DATA".equalsIgnoreCase(key2)) {
						breakflag = true;
						List<Map<String, String>> list = (List<Map<String, String>>) map2
								.get(key2);
						List<Map<String, String>> agreementList = new ArrayList<Map<String, String>>();
						for (Map<String, String> map : list) {
							Map<String, String> map4 = new HashMap<String, String>();
							for (String key4 : map1.keySet()) {
								for (String key3 : map.keySet()) {
									if (key4.equalsIgnoreCase(key3)) {
										map4.put(map1.get(key4),
												(String) map.get(key3));
										break;
									}
								}
							}

							agreementList.add(map4);
						}
						map3.put(key2, agreementList);

					} 
					else if (key1.equalsIgnoreCase(key2)) {
						map3.put(map1.get(key1), (String) map2.get(key2));
						break;
					}

				}
				if (breakflag) {
					break;
				}
			}
			changedData.put(key, map3);
		}

		return changedData;

	}
	
	/**
	 * 转换续保返回的key转换为对像的属性名
	 * 
	 * @param map
	 * @return
	 */
/*		public static Map<String, Object> changeKeyForRenewal(
				Map<String, Object> returnLists) {
			Map<String, Object> changedData = new HashMap<String, Object>();
			ApplicationContext context = new ClassPathXmlApplicationContext("premiumReturnMapping.xml");
			for (String key : returnLists.keySet()) {
				boolean breakflag = false;
				if ( ("CLAIM_LIST").equalsIgnoreCase(key)
						|| ("REPEAT_DATA").equalsIgnoreCase(key)
						|| "REINSURE_LIST".equalsIgnoreCase(key)) {
					continue;
				}
				
			}
			
			
			return null;
		}*/

	/**
	 * 将保费计算返回的数据封装到承保大对象里面 
	 * 孙小东
	 */
	public static InsuranceDetailsVO setPremiumResponsetoVo(Map<String, Object> map,InsuranceDetailsVO insuranceDetailsVO){
		String orderNo=insuranceDetailsVO.getBaseinfor().getOrderno();
		Map<String, String> jQBaseinforMap=(Map<String, String>) map.get("JQ_BASE");
		Map<String, String> SYBaseinforMap=(Map<String, String>) map.get("SY_BASE");
		Map<String,String>  VHLMap=(Map<String, String>) map.get("VHL");
		Map<String, String> adjustMap=(Map<String, String>) map.get("ADJUST");
		Map<String, String> VhTaxMap=(Map<String, String>) map.get("VHLTAX");
		Map<String, List<Map<String, String>>> coveragemap=(Map<String, List<Map<String, String>>>) map.get("CVRG_LIST");
		List<Map<String,String>> coverageDatas=coveragemap.get("CVRG_DATA");
		Map<String, List<Map<String, String>>> agreementmap=(Map<String, List<Map<String, String>>>) map.get("APPNT_LIST");
		List<Map<String,String>> agreementData=agreementmap.get("APPNT_DATA");
		List<Coverageinfor> coverageinfors=new ArrayList<Coverageinfor>(); 
		List<Agreementinfor> agreementinfors=new ArrayList<Agreementinfor>(); 
		if(jQBaseinforMap!=null){
			for(String key:jQBaseinforMap.keySet()){
				setValuetoObj(key,jQBaseinforMap.get(key),insuranceDetailsVO.getBaseinfor());
			}
		}
		for(String key:SYBaseinforMap.keySet()){
			setValuetoObj(key,SYBaseinforMap.get(key),insuranceDetailsVO.getBaseinfor());
		}
		for(String key:VHLMap.keySet()){
			setValuetoObj(key,VHLMap.get(key),insuranceDetailsVO.getVhlinfor());
		}
		for(String key:adjustMap.keySet()){
			setValuetoObj(key,adjustMap.get(key),insuranceDetailsVO.getBaseinfor());
		}
		if(VhTaxMap!=null){
			if(insuranceDetailsVO.getTaxinfor()==null){
				Taxinfor taxinfor = new Taxinfor();
				insuranceDetailsVO.setTaxinfor(taxinfor);
			}
			for(String key:VhTaxMap.keySet()){
				setValuetoObj(key,VhTaxMap.get(key),insuranceDetailsVO.getTaxinfor());
			}
		}else{
			insuranceDetailsVO.setTaxinfor(null);//如果没有投保交强险，设置车船税对象为空
		}
		
		for(Map<String, String> coverageMap:coverageDatas ){
			Coverageinfor coverageinfor=new Coverageinfor();
			coverageinfor.setBaseinfororderno(orderNo);
			for(String key:coverageMap.keySet()){
				setValuetoObj(key,coverageMap.get(key),coverageinfor);				
			}
			coverageinfors.add(coverageinfor);
			
		}
		
		for(Map<String, String> agreementMap:agreementData ){
			Agreementinfor agreementinfor=new Agreementinfor();
			agreementinfor.setOrderno(orderNo);
			for(String key:agreementMap.keySet()){
				setValuetoObj(key,agreementMap.get(key),agreementinfor);				
			}
			agreementinfors.add(agreementinfor);
			
		}

		insuranceDetailsVO.setCoverageinfors(coverageinfors);
		insuranceDetailsVO.setAgreementinfors(agreementinfors);
		return insuranceDetailsVO;
	}
	
	public static Baseinfor setValuetoBaseInfor(Map<String, String> map,Baseinfor baseinfor){
		for(String key:map.keySet()){
			setValuetoObj(key,map.get(key),baseinfor);
		}				
		return baseinfor;
		
	}
	/**
	 * 获取发送短信验证码，返回状态
	 * 孙小东
	 * @param xml
	 * @return
	 */
	public static String getTemplateSMSReturnCode(String xml) {
		String resultCode = "";
		List<Map<String, String>> resultList = XmlUtilsByDom4j
				.specifiedXml2list(xml, "resp", "UTF-8");
		if (resultList != null && resultList.size() > 0) {
			for (String s : resultList.get(0).keySet()) {
				if (LudiConstants.TEMPLATE_SMS_RESPONSE_CODE
						.equalsIgnoreCase(s)) {
					resultCode = resultList.get(0).get(s);
				}

			}
		}
		return resultCode;

	}
	
	public static String brandNameToCode(String brandName){
		String code="";
		ApplicationContext context = new ClassPathXmlApplicationContext("premiumReturnMapping.xml");
		Map<String, String> map1 = (Map<String, String>) context.getBean("BRANDNAME_LIST");
		for (String key1 : map1.keySet()) {
//			if(key1.equals(brandName)){
//				code=map1.get(key1);
//			}
			if(brandName.contains(key1)){
				code=map1.get(key1);
			}
		}
		if(code.equals("")){
			code="carLogo";
		}
		return code;
	}
	
	/**
	 * 三者保额的配置
	 */
	public static List<Coverageinfor> setThreeCoverage(List<Coverageinfor> coverageinfors){
		for(int i=0;coverageinfors.size()>i;i++){
			if(coverageinfors.get(i).getInsrnccode().equals(LudiConstants.THREEPARTYINSUEANCE)){
				if(coverageinfors.get(i).getSuminsured().equals(LudiConstants.THREE_COVERAGE_5W)){
					 coverageinfors.get(i).setSuminsured(LudiConstants.THREE_COVERAGE_MOMEY_5W);
				 }else if(coverageinfors.get(i).getSuminsured().equals(LudiConstants.THREE_COVERAGE_10W)){
					 coverageinfors.get(i).setSuminsured(LudiConstants.THREE_COVERAGE_MOMEY_10W);
				 }else if(coverageinfors.get(i).getSuminsured().equals(LudiConstants.THREE_COVERAGE_20W)){
					 coverageinfors.get(i).setSuminsured(LudiConstants.THREE_COVERAGE_MOMEY_20W);
				 }else if(coverageinfors.get(i).getSuminsured().equals(LudiConstants.THREE_COVERAGE_30W)){
					 coverageinfors.get(i).setSuminsured(LudiConstants.THREE_COVERAGE_MOMEY_30W);
				 }else if(coverageinfors.get(i).getSuminsured().equals(LudiConstants.THREE_COVERAGE_50W)){
					 coverageinfors.get(i).setSuminsured(LudiConstants.THREE_COVERAGE_MOMEY_50W);
				 }else if(coverageinfors.get(i).getSuminsured().equals(LudiConstants.THREE_COVERAGE_100W)){
					 coverageinfors.get(i).setSuminsured(LudiConstants.THREE_COVERAGE_MOMEY_100W);
				 }
			}
		}
		return coverageinfors;
	}
}
