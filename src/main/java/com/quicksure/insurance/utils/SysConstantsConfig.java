package com.quicksure.insurance.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SysConstantsConfig {
	private static Logger logger = Logger.getLogger(SysConstantsConfig.class);
	/**
	 * 系统中各种模板、文件路径等配置
	 */
	public static Properties Pro_Sys_FilePath_Config = new Properties();
	// ---------------------LUDI 通用配置------------------
	public static String Velocity_Template_ForQuickApp_path;//velocity 读取模板的路径
	public static String JY_CONNECT_USERNAME; // 精友服务用户名
	public static String JY_CONNECT_PASSWORD; // 精友服务密码
    public static String SINOSAFE_EXTENTERPCODE;//ludi渠道号
    public static String SINOSAFE_CONNECT_USERNAME;//华安服务用户名
    public static String SINOSAFE_CONNECT_PASSWOED;//华安服务密码
    public static String SINOSAFE_RETURN_URL;//华安支付系统成功之后返回我们系统的地址
    public static String OTP_VALIDATION_URL;//OTP短信验证请求地址
    public static String OTP_APPID;//
    public static String OTP_ACCOUNTSID;//
    public static String OTP_AUTHTOKEN;//
    public static String OTP_SOFTVERSION;
    public static String SMS_TEMPLATE_ID;
    public static String WECHAT_URL;
    public static String WECHAT_APPID;
    public static String WECHAT_APPSECRET;
    public static String OCR_URL_ADDRESS;
    public static String  OCR_PATH;
    public static String OCR_APPCODE;
    
    public static String PROVIDE_METHOD; //启动方式（本地/测试/）
    
	static {
		loadConfigFiles();
	}

	/**
	 * 方法reloadConfigs的详细说明 <br>
	 * 
	 * <pre>
	 * 重新加载配置信息 <br>
	 * 编写者：孙小东
	 * 创建时间：2014-9-29 上午9:58:15
	 * </pre>
	 */
	public static void reloadConfigs() {
		loadConfigFiles();
	}

	private static void loadConfigFiles() {
		Class clazz = SysConstantsConfig.class;
		String fileName = "";
		try {
			fileName = "/ludiconfigurationinfor.properties";
			Pro_Sys_FilePath_Config.load(clazz.getResourceAsStream(fileName));
		} catch (IOException e) {
			logger.error("加载配置文件失败。文件名是：" + fileName, e);
		}
		initConfig();
	}

	private static void initConfig() {
		JY_CONNECT_USERNAME = Pro_Sys_FilePath_Config.getProperty("JY_CONNECT_USERNAME");
		JY_CONNECT_PASSWORD=Pro_Sys_FilePath_Config.getProperty("JY_CONNECT_PASSWORD");
		SINOSAFE_EXTENTERPCODE=Pro_Sys_FilePath_Config.getProperty("SINOSAFE_EXTENTERPCODE");
		SINOSAFE_CONNECT_USERNAME=Pro_Sys_FilePath_Config.getProperty("SINOSAFE_CONNECT_USERNAME");
		SINOSAFE_CONNECT_PASSWOED=Pro_Sys_FilePath_Config.getProperty("SINOSAFE_CONNECT_PASSWOED");
		Velocity_Template_ForQuickApp_path=Pro_Sys_FilePath_Config.getProperty("Velocity_Template_ForQuickApp_path");
		SINOSAFE_RETURN_URL=Pro_Sys_FilePath_Config.getProperty("SINOSAFE_RETURN_URL");
		OTP_VALIDATION_URL=Pro_Sys_FilePath_Config.getProperty("OTP_VALIDATION_URL");
		OTP_APPID=Pro_Sys_FilePath_Config.getProperty("OTP_APPID");
		OTP_ACCOUNTSID=Pro_Sys_FilePath_Config.getProperty("OTP_ACCOUNTSID");
		OTP_AUTHTOKEN=Pro_Sys_FilePath_Config.getProperty("OTP_AUTHTOKEN");
		OTP_SOFTVERSION=Pro_Sys_FilePath_Config.getProperty("OTP_SOFTVERSION");
		SMS_TEMPLATE_ID=Pro_Sys_FilePath_Config.getProperty("SMS_TEMPLATE_ID");
		WECHAT_URL=Pro_Sys_FilePath_Config.getProperty("WECHAT_URL");
		WECHAT_APPID=Pro_Sys_FilePath_Config.getProperty("WECHAT_APPID");
		WECHAT_APPSECRET=Pro_Sys_FilePath_Config.getProperty("WECHAT_APPSECRET");
		OCR_URL_ADDRESS=Pro_Sys_FilePath_Config.getProperty("OCR_URL_ADDRESS");
		OCR_PATH=Pro_Sys_FilePath_Config.getProperty("OCR_PATH");
		OCR_APPCODE=Pro_Sys_FilePath_Config.getProperty("OCR_APPCODE");
		PROVIDE_METHOD=Pro_Sys_FilePath_Config.getProperty("PROVIDE_METHOD");
	}
}
