package com.quicksure.insurance.constants;

public class LudiConstants {
	public static final String SINOSAFECANCELERRORCODE="FLAG";//撤单返回状态
	public static final String SINOSAFECANCELERRORMESSAGE="FAIL_MESSAGE";
	public static final String SINOSAFEERRORCODE="RESPONSECODE";
	public static final String SINOSAFEERRORMESSAGE="ERRORMESSAGE";
	public static final String JYERRORCODE="ResultCode";
	public static final String JYERRORMESSAGE="ResultDesc";
	public static final String SINOSAFECANCELINTERFACESUCCESS="0";//撤单返回成功状态
	public static final String SINOSAFEINTERFACESUCCESS="C00000000";
	public static final String JYSUCCESS="0000";
	public static final String SINOSAFECANCELINTERFACEFAIL="1";//撤单返回失败状态
	public static final String SINOSAFEINTERFACEFAIL="C99999999";
	public static final String TEMPLATE_SMS_RESPONSE_CODE="respCode";
	public static final String TEMPLATE_SMS_SUCCESS_CODE="000000";
	//每个险种对应的编码
	public static final String  COMPULSORYINSURANCE="0357";//交强险 357
	public static final String  LOSSINSUEANCE="030101";//车损险 101
	public static final String  THREEPARTYINSUEANCE="030102";//三者险 102
	public static final String  DRIVERINSUEANCE="030104"; //司机责任险 104
	public static final String  PASSENGERINSUEANCE="030105";//乘客责任险 105
	public static final String  ROBBERYINSUEANCE="030103";//盗抢险 103
	//附加险
	public static final String  SPONTANEOUSCOMBUSTIONINSUEANCE="030108";//自燃险 108
	public static final String  GLASSINSUEANCE="030107";//玻璃险 107
	public static final String  NOTFOUNDINSUEANCE="030115";//无法找到第三方特约险115
	public static final String  APPOINTINSUEANCE="030116";//指定修理厂险116
	//三者保额
	public static final String  THREE_COVERAGE_5W="306006004";
	public static final String  THREE_COVERAGE_10W="306006005";
	public static final String  THREE_COVERAGE_20W="306006006";
	public static final String  THREE_COVERAGE_30W="306006007";
	public static final String  THREE_COVERAGE_50W="306006009";
	public static final String  THREE_COVERAGE_100W="306006014";
	//转换的三者保额金额
	public static final String  THREE_COVERAGE_MOMEY_5W="50000.00";
	public static final String  THREE_COVERAGE_MOMEY_10W="100000.00";
	public static final String  THREE_COVERAGE_MOMEY_20W="200000.00";
	public static final String  THREE_COVERAGE_MOMEY_30W="300000.00";
	public static final String  THREE_COVERAGE_MOMEY_50W="500000.00";
	public static final String  THREE_COVERAGE_MOMEY_100W="1000000.00";
	
	//订单状态
	public static final int  UNDETERMINED = 10; //待定
	public static final int  QUOTED = 20; //已报价
	public static final int  UNDERORDER = 30; //已下单
	public static final int  PAID = 40; //已支付
	public static final int  EFFECTED = 50; //已生效
	public static final int  DELIVERED = 60; //已配送
	public static final int  REVOKED = 70; //已撤销
	
	//页面跳转
	public static final String INSURESUCCESS = "/jsp/insureSuccess"; //支付成功 页面
	public static final String PERSONINFORS = "/jsp/personInfors"; //人员信息
	public static final String BASEINFOR = "/jsp/baseinfor";
	public static final String PAYMENT = "/jsp/paymentInfor"; //支付页面
	public static final String COVEIFNOR = "/jsp/coveinfor"; //保费试算
	public static final String VEHICLEINFOR= "/jsp/vehicleInfor"; //车辆信息页面
	public static final String INDEX = "/jsp/quicksurehome"; //首页
	public static final String MYACCOUNT="/jsp/myAccount";//我的订单页面
	public static final String ERRORSCREEN="/jsp/error";//错误页面
	public static final String BINDUSER="/jsp/registUser";//绑定手机号页面
	public static final String BATCHSEARCH="/jsp/batch_search";//车辆批量查询页面
	public static final String LOGINUSER = "/jsp/LoginUser"; //登录页面
	public static final String ORDERDETAILS = "/jsp/orderDetails"; //订单详情页面
	
	
	//CSR页面跳转
	public static final String CSRORDERDETAILS = "CSR/jsp/CSROrderDetails"; //CSR订单详情页面
	public static final String USERMATION = "CSR/jsp/user_mation"; //CSR继续投保
}
