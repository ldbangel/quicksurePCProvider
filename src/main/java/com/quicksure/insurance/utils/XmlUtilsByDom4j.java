package com.quicksure.insurance.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
public class XmlUtilsByDom4j {
public final static String Encoding="UTF-8";
	
	/**
	 * 将xml格式字符串，转为org.dom4j.Document对像,Document对像默认编码ISO-8859-1
	 * @param paramString xml格式字符串
	 * @return
	 * @throws DocumentException 
	 * @throws UnsupportedEncodingException 
	 * @throws Exception 
	 */
	public static Document parseDocFromXml(String paramString) throws Exception{
		return parseDocFromXml(paramString, Encoding);
	}
	 
	/**
	 * 将xml格式字符串，根据指定编码转为org.w3c.dom.Document对像,如果传入编码为null或"",则默认编码为UTF-8
	 * 创建时间：Mar 12, 2013 7:31:19 PM </pre>
	 * @param paramString xml格式字符串
	 * @param encoding  编码
	 * @throws 异常类型 说明  
	 * @return
	 * @throws DocumentException 
	 * @throws UnsupportedEncodingException 
	 */
	public static Document parseDocFromXml(String paramString,String encoding) throws Exception{
		if("".equalsIgnoreCase(FileUitls.trim(encoding))){
			encoding = Encoding;
		}
		Document document = null;
		SAXReader saxReader = null;
		try {
			saxReader = new SAXReader();
			document = saxReader.read(new ByteArrayInputStream(paramString.getBytes(encoding)));
		} catch (Exception e) {
			throw e;
		}
		return document;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String,String>> specifiedXml2list(String xml,String spefNode,String encoding){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		Document document = null;
		List<Element> list = null;
		List<Element> elemlilst = null;
		try {
			document = parseDocFromXml(xml,encoding);
			list = document.selectNodes("//" + spefNode);
			for(Element elem : list){
				elemlilst = elem.elements();
				map = new HashMap<String, String>();
				for(Element subelem : elemlilst){
					map.put(subelem.getName(), subelem.getText());
				}
				result.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		
	}
	
	/**
	 * 方法XmlToMap的简要说明 <br>
	 * 将xml中指定路径(nodePath)下的内容，转换为map
	 * <pre>
	 * 创建时间：Apr 24, 2014 3:55:00 PM 
	 * </pre>
	 * @param xml
	 * @param nodePath
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> XmlToMap(String xml,String nodePath,String encoding) throws Exception{
		Document doc = parseDocFromXml(xml,encoding); 
		Map<String, Object> rMap = null;
		if("".equalsIgnoreCase(FileUitls.trim(nodePath))){
			rMap = new HashMap<String, Object>();
			Element root = doc.getRootElement(); 
			rMap.put(root.getName(), DomToMap(doc));
		}else{
			List list = doc.selectNodes(nodePath);
			if(null!=list && list.size()>0){
				Element e = (Element)list.get(0);
				if(list.size()>1){
					rMap = DomToMap(e.getParent());
				}else{
					rMap = DomToMap(e);
				}
			}
		}
		return rMap;
	}
	
	/**
	 * 方法XmlToMap的简要说明 <br>
	 * 将整个xml转为map
	 * <pre>
	 * 创建时间：Apr 24, 2014 3:55:50 PM 
	 * </pre>
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> XmlToMap(String xml) throws Exception{
		return DomToMap(parseDocFromXml(xml));
	}
	
	/**
	 * 方法DomToMap的简要说明 <br>
	 * 将org.dom4j.Document对像，转为map
	 * <pre>
	 * 创建时间：Apr 24, 2014 3:56:11 PM 
	 * </pre>
	 * @param doc
	 * @return
	 */
	public static Map<String, Object> DomToMap(Document doc){  
        if(doc == null)  
            return null;  
        Element root = doc.getRootElement();  
        return DomToMap(root);
    }  
	/**
	 * 给xml添加节点和属性
	 * sunxiaodong
	 * @param xml
	 * @param path
	 * @param list
	 * @return
	 * @throws Exception 
	 */
	public static String AddNodeToList (String xml,String path,List<Map<String,String>> list) throws Exception{
		Document document=parseDocFromXml(xml);//创建dom 对像
		Element root = document.getRootElement();
		Element pEle = root.addElement(path);
		for(Map<String,String> map:list){
			Element sEle = pEle.addElement("row");
			setNodeAttrs(sEle, map);
		}
		return transDocToString(document);
		
	}
	/**
	 * 将指定xml中的数据转化为list<Map<sting,Object>> 主要针对指定路径下面包含子节点
	 * sunxiadong
	 * @param xml
	 * @param path
	 * @param childNodepath
	 * @return
	 */
	public static List<Map<String,Object>> XmltolListHasChildNode(String xml,String path,String encoding){
		List<Map<String,Object>> list=new LinkedList<Map<String,Object>>();
		try {
			 list=parserXmlNodeAttributesTochildNode(xml,path,encoding);//取节点里面的属性
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
		
	}
	/**
	 * 方法prossMap的简要说明 <br>
	 * 处理在ＸＭＬ中同一父节点下有Ｎ个相同节点名的子节点
	 * <pre>
	 * 创建时间：Jan 22, 2014 4:49:00 PM 
	 * </pre>
	 * @param map
	 * @param key
	 * @param obj
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void prossMap(Map<String, Object> map,String key,Object obj){
		List<Object> mapList = null;
		//如果在封装的map的key中有当前节点名,如果有，那么说明在当前节点的父节点下，
    	//有Ｎ个相同名称的子节点，这时需要使用list保存相同名称的子节点
		if(map.get(key) != null){
			//取出map中已存的值
			Object temp = map.get(key); 
			//判断其值的类型不为java.util.ArrayList，那么创建一个新的ArrayList对像，
            //将上一个相同名字的节点值与当前值，保存到list中，
            if(!obj.getClass().getName().equals("java.util.ArrayList")&&!temp.getClass().getName().equals("java.util.ArrayList")){  
                mapList = new ArrayList<Object>();  
                mapList.add(temp);
                mapList.add(obj);
            	
            }  
            //如果其值的类型为java.util.ArrayList
            if(obj.getClass().getName().equals("java.util.ArrayList")||temp.getClass().getName().equals("java.util.ArrayList")){  
                mapList = (List) temp;  
                mapList.add(obj);  
            }  
            //再次保存到map中
            map.put(key, mapList);
		}else{//如果map的key中，还不存iter.getName()
			map.put(key, obj);
		}
	}
  
	/**
	 * 方法Dom2Map的简要说明 <br>
	 * 子节点遍历方法
	 * <pre>
	 * 创建时间：Jan 22, 2014 4:50:07 PM 
	 * </pre>
	 * @param e
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> DomToMap(Element e){  
        Map<String, Object> map = new HashMap<String, Object>();  
        List list = e.elements(); 
        //当前节点还有子节点
        if(list.size() > 0){  
            for (int i = 0;i < list.size(); i++) {  
                Element iter = (Element) list.get(i);  
                //如果当前节点还有子节点，则先处理其子节点
                Object obj = null;
                if(iter.elements().size() > 0){  
                	obj = DomToMap(iter);  
                }else{
                	obj = iter.getText();
                }  
                prossMap(map, iter.getName(), obj);
            }  
        }else{//当前节点无子节点
        	map.put(e.getName(), e.getText());
        }  
        return map;  
    }
	
	/**
	 * 方法tranMap的详细说明  <br><pre>
	 * 将map中的key替换为CorrespondenceMap中value,其中未替换前时，
	 *  map中的key与CorrespondenceMap中key是相同的,当CorrespondenceMap
	 *  为null时，不进行替换<br>
	 * 创建时间：2014-9-23 上午11:16:53 </pre>
	 * @param map
	 * @param CorrespondenceMap
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> tranMap(Map<String, Object> map,Map<String, Object> CorrespondenceMap) throws Exception{
		if(map==null || map.isEmpty()){
			return null;
		}
		//CorrespondenceMap为空，返回参数map
		if(CorrespondenceMap==null || CorrespondenceMap.isEmpty()){
			return map;
		}
		Map<String, Object> rMap = new HashMap<String, Object>();
		for(Iterator<String> iter =map.keySet().iterator();iter.hasNext();){
			String oldKey = FileUitls.trim(iter.next());
			String newKey = FileUitls.trim(CorrespondenceMap.get(oldKey));
			Object obj = map.get(oldKey);
			rMap.put("".equalsIgnoreCase(newKey)?oldKey:newKey, obj);
		}
		return rMap;
	}
	
	/**
	 * 解析XML中指定节点的所有属性，并保存到Map中<br>
	 * 创建时间：Mar 12, 2013 7:09:44 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return void 说明
	 * @throws 异常类型 说明  
	 * @param xmlString xml格式字符串
	 * @param pathString
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	public static List<Map<String, String>> parserXmlNodeAttributes(String xmlString,String pathString) throws Exception{
		Document document = null;
		List<Map<String, String>> rList = null;
		try {
			rList = new ArrayList<Map<String,String>>();
			document = parseDocFromXml(xmlString);//创建dom 对像
			List<Node> list = document.selectNodes(pathString);//取结点
			for(int i=0;i<list.size();i++){
				Element ele = (Element)list.get(i);
				rList.add(parserXmlNodeAttributes(ele));
			}
		} catch (Exception e){
			throw e;
		}finally{
			return rList;
		}
	}
	
	/**
	 * 解析XML中指定节点的所有属性，并保存到Map中<br>节点里面同时包含了子节点和属性
	 * 创建时间：Mar 12, 2013 7:09:44 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return void 说明
	 * @throws 异常类型 说明  
	 * @param xmlString xml格式字符串
	 * @param pathString
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	public static List<Map<String, Object>> parserXmlNodeAttributesTochildNode(String xmlString,String pathString,String encoding) throws Exception{
		Document document = null;
		List<Map<String, Object>> rList = null;
//		 String[]childNodes=childNode.split("/");
//		 String childNodepath=childNodes[childNodes.length-1];
		try {
			rList = new LinkedList<Map<String,Object>>();
			document = parseDocFromXml(xmlString,encoding);//创建dom 对像
			List<Node> list = document.selectNodes(pathString);//取结点
			for(int i=0;i<list.size();i++){
				Element ele = (Element)list.get(i);
				rList.add(DomToMap(ele));
			}
		} catch (Exception e){
			throw e;
		}finally{
			return rList;
		}
	}
	
	/**
	 * 方法parserXmlNodeAttributes的简要说明 <br>
	 * 解释节点属性集合
	 * <pre>
	 * 创建时间：Jan 22, 2014 5:33:47 PM 
	 * </pre>
	 * @param ele
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parserXmlNodeAttributes(Element ele){
		Map<String, String> map = null;
		if(ele==null){
			return map;
		}
		List<Attribute> aList = ele.attributes();//结点属性
		if(aList!=null && aList.size()>0){//遍历属性集
			map = new HashMap<String, String>();
			for(int j=0;j<aList.size();j++){
				Attribute attr = aList.get(j);
				map.put(attr.getName(), attr.getValue());
			}
		}
		return map;
	} 
	
	/**
	 * 方法parserXmlNode的简要说明 <br>
	 * 将xml指定路径下的内容转为List->map保存,但不包含节点的属性
	 * <pre>
	 * 创建时间：Jan 15, 2014 2:55:15 PM 
	 * </pre>
	 * @param xmlString
	 * @param pathString
	 * @return
	 */
	@SuppressWarnings({ "finally", "unchecked" })
	public static List<Map<String, Object>> parserXmlNode(String xmlString,String pathString) throws Exception{
		Document document = null;
		List<Map<String, Object>> rList = null;
		try {
			rList = new ArrayList<Map<String,Object>>();
			document = parseDocFromXml(xmlString);//创建dom 对像
			List<Node> list = document.selectNodes(pathString);//取结点
			for(int i=0;i<list.size();i++){
				Element ele = (Element)list.get(i);
				List<Element> elist = ele.elements();
				if(elist!=null && elist.size()>0){//遍历属性集
					Map<String, Object> map = new HashMap<String, Object>();
					for(Element temp : elist){
						String value = temp.getText();
						map.put(temp.getName(), value);
					}
					rList.add(map);
				}
			}
		} catch (Exception e){
			throw e;
		}finally{
			return rList;
		}
	}
	
	/**
	 * 将文档对像转换为String<br>
	 * 创建时间：Jun 25, 2012 2:34:16 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return String 说明
	 * @throws 异常类型 说明  
	 * @param paramDocument
	 * @return   
	 * @throws Exception
	 */
	public static String transDocToString(Document paramDocument) throws Exception{
		return paramDocument.asXML();
	}
	
	/**
	 * 将xml字符串附加到另一个xml字符串中<br>
	 * 创建时间：Jun 26, 2012 9:10:24 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return String 说明
	 * @throws 异常类型 说明  
	 * @param xml 主xml
	 * @param path 需要将子xml字符串附加到的节点路径
	 * @param sXml 子xml
	 * @return
	 * @throws Exception
	 */
	public static String addNodeToXml(String pXml, String pPath, String sXml,String sPath) throws Exception{
		Document pDocument = parseDocFromXml(pXml);
		Document sDocument = parseDocFromXml(sXml);
		Element ele = "".equalsIgnoreCase(FileUitls.trim(sPath))?sDocument.getRootElement():(Element)sDocument.selectSingleNode(sPath);
		Element pEle = (Element)pDocument.selectSingleNode(pPath);
		pEle.add((Element)ele.clone());
		return transDocToString(pDocument);
	}
	
	/**
	 * 将xml字符串附加到另一个xml字符串中<br>
	 * 创建时间：Jun 26, 2012 9:10:24 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return String 说明
	 * @throws 异常类型 说明  
	 * @param xml 主xml
	 * @param path 需要将子xml字符串附加到的节点路径
	 * @param sXml 子xml
	 * @return
	 * @throws Exception
	 */
	public static String addNodeToXml(String xml, String path, String sXml) throws Exception{
		Document pDocument = parseDocFromXml(xml);
		Document sDocument = parseDocFromXml(sXml);
		Element ele = sDocument.getRootElement();
		Element pEle = (Element)pDocument.selectSingleNode(path);
		if(ele!=null){
			Element sEle = pEle.addElement(ele.getName());
			copyNode(pDocument, sEle, ele);
		}
		return transDocToString(pDocument);
	}
	
	/**
	 * 将sourceNode节点中的内容，复制到doc中的baseNode节点下<br>
	 * 创建时间：Jun 26, 2012 8:27:35 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return void 说明
	 * @throws 异常类型 说明  
	 * @param doc 目标文档
	 * @param baseNode 目标文档下的节点
	 * @param sourceNode 需复制内容的节点
	 */
	@SuppressWarnings("unchecked")
	private static void copyNode(Document doc, Element baseEle, Element sourceEle){
		//如果文档，文档中的节点及内容节点中有一个为空，则不进行操作
		if(doc!=null && baseEle!=null && sourceEle!=null){
			setNodeAttrs(baseEle, sourceEle.attributes());//复制节点属性
			baseEle.setText(sourceEle.getText());
			//取得内容节点下的所有子节点
			List<Element> list = sourceEle.elements();
			for(int i=0;i<list.size();i++){
				Element node = list.get(i);
				//在目标文档中创建一个新的节点
				Element ele = null;
				ele = baseEle.addElement(node.getName());
				if(ele!=null){
					//如果内容节点中有属性值
					setNodeAttrs(ele, node.attributes());
					//如果当前节点有值
					if(node.getText()!=null && !"".equalsIgnoreCase(node.getText().trim())){
						ele.setText(node.getText());
					}
					//如果当前节点还有子节点
					if(node.elements().size()>0){
						copyNode(doc, ele, node);
					}
				}
			}
		}
	}
	
	/**
	 * 设置节点属性 <br>
	 * 创建时间：Jun 26, 2012 3:14:35 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return void 说明
	 * @throws 异常类型 说明  
	 * @param ele
	 * @param attrs
	 */
	public static void setNodeAttrs(Element ele, List<Attribute> list){
		if(list!=null && list.size()>0){
			for(int j=0;j<list.size();j++){
				Attribute t = list.get(j);
				ele.addAttribute(t.getName(), t.getValue());
			}
		}
	}
	
	/**
	 * 添加节点属性，如果属性已经存在，则修改属性值
	 * @param xmlStirng
	 * @param nodePath
	 * @param attrs
	 * @param index 指定修改第index个节点中属性值，如果index==-1，则表示修改所有nodePath节点的属性
	 * @return
	 * @throws Exception
	 */
	public static String setXmlNodeAttrs(String xmlStirng,String nodePath,Map<String, String> attrs,int index)throws Exception{
		Document paramDocument = parseDocFromXml(xmlStirng);
		return setDocNodeAttrs(paramDocument, nodePath, attrs,index);
	}
	
	/**
	 * 方法setXmlNodeValue的详细说明  <br><pre>
	 * 设置单个节点的值,当通过nodePath可以得到多个节点时，只修改第一个节点的值 <br>
	 * 创建时间：2014-10-9 上午9:44:34 </pre>
	 * @param xmlString
	 * @param nodePath
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String setXmlNodeValue(String xmlString,String nodePath,String value) throws Exception{
		Document paramDocument = parseDocFromXml(xmlString);
		return setXmlNodeValue(paramDocument, nodePath, value);
	}
	
	/**
	 * 方法setXmlNodeValue的详细说明  <br><pre>
	 * 设置单个节点的值,当通过nodePath可以得到多个节点时，只修改第一个节点的值 <br>
	 * 创建时间：2014-10-9 上午9:49:42 </pre>
	 * @param paramDocument
	 * @param nodePath
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String setXmlNodeValue(Document paramDocument,String nodePath,String value) throws Exception{
		Node node = paramDocument.selectSingleNode(nodePath);
		node.setText(value);
		return transDocToString(paramDocument);
	}
	/**
	 * 添加节点属性，如果属性已经存在，则修改属性值
	 * @param paramDocument
	 * @param nodePath
	 * @param attrs
	 * @param index 指定修改第index个节点中属性值，如果index==-1，则表示修改所有nodePath节点的属性
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String setDocNodeAttrs(Document paramDocument,String nodePath,Map<String, String> attrs,int index)throws Exception{
		List<Node> list = paramDocument.selectNodes(nodePath);//取结点
		if(list!=null && list.size()>0){
			if(index>0){
				Element ele = (Element)list.get(index-1);
				setNodeAttrs(ele, attrs);
			}else{
				for(int i=0;i<list.size();i++){
					Element ele = (Element)list.get(i);
					setNodeAttrs(ele, attrs);
				}
			}
		}
		return transDocToString(paramDocument);
	}
	
	/**
	 * 设置节点属性 <br>
	 * 创建时间：Jun 26, 2012 3:14:35 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return void 说明
	 * @throws 异常类型 说明  
	 * @param ele
	 * @param attrs
	 */
	public static void setNodeAttrs(Element ele, Map<String, String> attrs){
		if(attrs!=null && !attrs.isEmpty()){
			for(Iterator<String> iter = attrs.keySet().iterator();iter.hasNext();){
				String attrName = iter.next();
				ele.addAttribute(attrName, attrs.get(attrName));
			}
		}
	}
	
	/**添加节点，若该节点已经存在则替换
	 * 2015-3-11
	 *
	 */
	public static String setNode(String xml,String nodePath,Map<String, String> attr,int index) throws Exception{
		Document paramDocument = parseDocFromXml(xml);
		return  setNode(paramDocument, nodePath, attr,index);
	}
	
	
	/**添加节点，若该节点已经存在则替换
	 * 2015-3-11
	 *
	 */
	public static String setNode(Document paramDocument,String nodePath,Map<String, String> attr,int index)throws Exception{
		List<Node> list = paramDocument.selectNodes(nodePath);//取结点
		if(list!=null && list.size()>0){
			if(index>0){
				Element ele = (Element)list.get(index-1);
				setNode(ele,attr);
			}
		}
		return transDocToString(paramDocument);
	}
	/**添加节点，若该节点已经存在则替换
	 * 孙晓东
	 * 2015-3-11
	 *
	 */
	public static void setNode(Element em,Map<String, String> map){
		if(map!=null&&!map.isEmpty()){
			for(Iterator<String> iter = map.keySet().iterator();iter.hasNext();){
				String attrName = iter.next();
				String attrvalue=(String) map.get(attrName);
			    Element element=em.element(attrName);
			    if(element!=null){
			    	em.remove(element);
			    }
			    	Element addElement=em.addElement(attrName);
			    	addElement.setText(attrvalue);
			}
		}		
	}
	
	
	/**
	 * 删除节点属性，如果属性已经存在，则修改属性值
	 * @param xml
	 * @param nodePath
	 * @param attrName 属性名
	 * @param index 指定修改第index个节点中属性值，如果index==-1，则表示修改所有nodePath节点的属性
	 * @return
	 * @throws Exception
	 */
	public static String removeNodeAttr(String xml,String nodePath,String attrName,int index) throws Exception{
		Document paramDocument = parseDocFromXml(xml);
		return removeNodeAttr(paramDocument, nodePath, attrName,index);
	}
	
	/**
	 * 删除节点属性，如果属性已经存在，则修改属性值
	 * @param nodePath
	 * @param attrName 属性名
	 * @param index 指定修改第index个节点中属性值，如果index==-1，则表示修改所有nodePath节点的属性
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String removeNodeAttr(Document paramDocument,String nodePath,String attrName,int index)throws Exception{
		List<Node> list = paramDocument.selectNodes(nodePath);//取结点
		if(list!=null && list.size()>0){
			if(index>0){
				Element ele = (Element)list.get(index-1);
				ele.remove(ele.attribute(attrName));
			}else{
				for(int i=0;i<list.size();i++){
					Element ele = (Element)list.get(i);
					ele.remove(ele.attribute(attrName));
				}
			}
		}
		return transDocToString(paramDocument);
	}
	
	/**
	 * 删除节点<br>
	 * 创建时间：Mar 12, 2013 9:40:00 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return String 说明
	 * @throws 异常类型 说明  
	 * @param xml
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String removeNode(String xml,String path) throws Exception {
		Document paramDocument = parseDocFromXml(xml);
		return removeNode(paramDocument, path);
	}
	
	/**
	 * 删除节点 <br>
	 * 创建时间：Mar 12, 2013 9:40:26 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return String 说明
	 * @throws 异常类型 说明  
	 * @param paramDocument
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String removeNode(Document paramDocument,String path) throws Exception{
		Element ele = (Element) paramDocument.selectSingleNode(path);
		Element pEle = ele.getParent();
		pEle.remove(ele);
		return transDocToString(paramDocument);
	}
	
	/**
	 * 方法XmlToMapWithAttr的详细说明  <br><pre>
	 * 解析xml字符串，每个节点将解析为一个map，其中attrMap中保存该节点的所有属性
	 * 集合（也使用map进行保存），valueMap中保存所有子节
	 * 点的name与value（使用Map进行保存）<br>
	 * 创建时间：2014-10-9 上午9:21:45 </pre>
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Map<String, Object>> XmlToMapWithAttr(String xml) throws Exception{
		Document paramDocument = parseDocFromXml(xml);
		return XmlToMapWithAttr(paramDocument);
	}
	
	/**
	 * 方法XmlToMapWithAttr的详细说明  <br><pre>
	 * 解析xml的Document对像，每个节点将解析为一个map，其中attrMap中保存该节点的所有属性
	 * 集合（也使用map进行保存），valueMap中保存所有子节
	 * 点的name与value（使用Map进行保存） <br>
	 * 创建时间：2014-10-9 上午9:25:10 </pre>
	 * @param paramDocument
	 * @return
	 */
	public static Map<String, Map<String, Object>> XmlToMapWithAttr(Document paramDocument){
		Element root = paramDocument.getRootElement();
        return DomToMapWithAttr(root);
	}
	
	/**
	 * 方法DomToMapWithAttr的详细说明  <br><pre>
	 * 解析xml的Document对像的单个Element,节点将解析为一个map，其中attrMap中保
	 * 存该节点的所有属性集合（也使用map进行保存），valueMap中保存所有子节点的name
	 * 与value（使用Map进行保存）;如果当前节点的子节点不为叶子节点，即子节点下仍有子
	 * 节点时，递归调用<br>
	 * 创建时间：2014-10-9 上午9:25:46 </pre>
	 * @param e
	 * @return
	 */
	public static Map<String, Map<String, Object>> DomToMapWithAttr(Element e){
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>(); 
		Map<String, Object> eMap = new HashMap<String, Object>();
		Map<String, Object> valueMap = new HashMap<String, Object>();
		Map<String, String> attrMap = new HashMap<String, String>();
		List list = e.elements(); 
        //当前节点还有子节点
        if(list.size() > 0){  
            for (int i = 0;i < list.size(); i++) {  
                Element iter = (Element) list.get(i);  
                //如果当前节点还有子节点，则先处理其子节点
                Object obj = null;
                
                if(iter.elements().size() > 0){  
                	obj = DomToMapWithAttr(iter);
                	valueMap = ( Map<String, Object>)obj;
                }else{
                	obj = iter.getText();
                	prossMap(valueMap, iter.getName(), obj);
                }  
            }
        }else{//当前节点无子节点
        	prossMap(valueMap, e.getName(), e.getText());
        }
        attrMap = parserXmlNodeAttributes(e);
        if(null!=attrMap && !attrMap.isEmpty()){
        	eMap.put("attrMap", attrMap);
        }
        eMap.put("valueMap", valueMap);
        map.put(e.getName(), eMap);
        return map;
	}
   public static String GetNodefFromXml(String xml,String path){
		
		SAXReader read = new SAXReader();
        Document doc;
        String text = null;
		try {
			doc = read.read(IOUtils.toInputStream(xml,"utf-8"));
			Element el = doc.getRootElement();
			el = (Element) el.selectSingleNode("//"+path);
			text = el.getText();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        
		return  text; 
	}
   
   public static String GetEleFromXml(String xml,String path,String attr,String encoding) throws IOException{
		
		SAXReader read = new SAXReader();
       Document doc;
       String result = null;
       Element el = null;
		try {
			doc = read.read(IOUtils.toInputStream(xml, encoding));
			el = doc.getRootElement();
			el = (Element) el.selectSingleNode("//"+path);
			Attribute attri = el.attribute(attr);
			result = attri.getText();
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
		return  result; 
	}
  
   public static String generateXML(List<Map<String, String>> list){
	   Document document = DocumentHelper.createDocument();
	   Element packetElement = document.addElement("Packet");
	   packetElement.addAttribute("type", "RESPONSE");
	   packetElement.addAttribute("version", "1.0");
	   Element headElement = packetElement.addElement("Head");
	   headElement.addElement("RequestType");
	   headElement.addElement("ResponseCode");
	   headElement.addElement("ErrorCode");
	   headElement.addElement("ErrorMessage");
	   Element bodyElement = packetElement.addElement("Body");
	   for(Map<String, String> map : list){
		   Element inspectElement = bodyElement.addElement("InspectResult");
		   for(String key : map.keySet()){
			   inspectElement.addElement(key).addText(map.get(key));
		   }
	   }
	   return document.asXML();
   }
  
}
