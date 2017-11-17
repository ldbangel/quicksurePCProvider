package com.quicksure.insurance.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public class JsonUtility {
 /**
  * 解析JSON 对象为对应的Bean
  * 孙小东
  * @param object
  * @param json
  * @return
  */
	public static Object getObjFromJson(Object object, String json) {
		JSONObject jsonObject = JSONObject.fromObject(json);
		Object Beanobject = jsonObject.toBean(jsonObject, object.getClass());
		return Beanobject;
	}
	/**
	 * 解析获取json里面对应key的value
	 * 孙小东
	 * @param key
	 * @param json
	 * @return
	 */
	public static String getVauleFromJson(String key, String json) {
		String value="";
		JSONObject jsonObject = JSONObject.fromObject(json);
		if(jsonObject.containsKey(key)){
			value=jsonObject.getString(key);
		}		 
		return value;
	}
	
	/**
	 * 解析获取json,返回json对象
	 * 孙小东
	 * @param key
	 * @param json
	 * @return
	 */
	public static Object getVauleFromJson(String key, String json,String path) {
		Object value="";
		List<Map<String, Object>> jsonList=new ArrayList<Map<String,Object>>();
		JSONObject jsonObject = JSONObject.fromObject(json);		
		if(jsonObject.containsKey(key)){
			jsonList=(List<Map<String, Object>>) jsonObject.get(key);
		}
		if(jsonList!=null&&jsonList.size()>0){
			for(int i=0;i<jsonList.size();i++){
				Map<String,Object> map=new HashMap<String, Object>();
				map=jsonList.get(i);
				if(map.containsKey(path)){
					if("coverageinfors".equalsIgnoreCase(path)){
						List<Map<String, String>> list=(List<Map<String, String>>) map.get(path);
						value=list;
					}else if("agreementinfors".equalsIgnoreCase(path)){
						List<Map<String, String>> list=(List<Map<String, String>>) map.get(path);
						value=list;
					}
					else{
					value=map.get(path).toString();
					break;
					}
				}
			}
		}
		return value;
	}

	
}
