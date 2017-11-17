package com.quicksure.insurance.sinosafeFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;

import javax.annotation.Resource;

import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.quicksure.insurance.dmsUtils.DMSUtility;
import com.quicksure.insurance.entity.InterfaceDetails;
import com.quicksure.insurance.utils.DateFormatUtils;
@Component
public class JYConnect {
	private static final Logger logger = Logger
			.getLogger(JYConnect.class);
	@Resource
	private DMSUtility dmsUtility;

	/**
	 * JY connecttion
	 * 
	 * @param strRequestxml
	 * @return
	 * @throws Exception
	 */
	public  String SinosafeconnectionJingyou(String strRequestxml)
			throws Exception {
		logger.info("请求精友开始"
				+ DateFormatUtils.getSystemDate()
				+ "请求数据为 \r\n"
				+ strRequestxml);
		String outputString = "";
		List<InterfaceDetails> interfaceDetails=null;
		Element element=null;
		String connectUrl="";
		URL url;
		logger.error(" 请求connectUrl地址");
		try {
			/*CacheManager cacheManager=CacheManager.newInstance();
        	Cache cache=cacheManager.getCache("baseCache");
        	if(cache!=null){
        		element=cache.get("interfacesdetails");
        		if(element==null){        			
                	interfaceDetails=dmsUtility.getConnectinfor();
                	element=new Element("interfacesdetails", (Serializable) interfaceDetails);
                	cache.put(element);
                }else{
                	interfaceDetails=(List<InterfaceDetails>) element.getValue();
                }
        	}  */
        	/*if(interfaceDetails!=null&&interfaceDetails.size()>0){
        		for(InterfaceDetails interfaceDetail :interfaceDetails){
        			if(interfaceDetail.getInterfaceid()==2){
        			connectUrl=interfaceDetail.getInterfaceurl();
        			logger.error(" 请求connectUrl地址"+connectUrl);
        			break;
        			}
        		}
        	}*/

			url = new URL("http://demo.genilex.com/DataPreFillServer/DataPreFillService");
			logger.error("http 请求地址"+url);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buffer = new byte[strRequestxml.toString().length()];
			buffer = strRequestxml.toString().getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			String policyRef = "";
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			// Set the appropriate HTTP parameters.
			httpConn.setRequestProperty("Cache-Control", "no-cache");
			httpConn.setRequestProperty("Content-Length",
					String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type",
					"application/xml; charset=UTF-8");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();

			out.write(b);
			out.close();
			String responseString = "";
			InputStreamReader isr = new InputStreamReader(
					httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}

		} catch (Exception e) {
			 StringWriter sw = new StringWriter();  
			 e.printStackTrace(new PrintWriter(sw, true));  
			 String str = sw.toString();
			logger.error("精友请求异常，异常消息为"+str);
		}

		outputString = URLDecoder.decode(outputString, "UTF-8");
		logger.info("请求精友结束，结束时间为 "
				+ DateFormatUtils.getSystemDate() + "返回数据为 \r\n"
				+ outputString);
		return outputString;
	}

}
