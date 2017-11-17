package com.quicksure.insurance.dmsUtils;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.googlecode.ehcache.annotations.Cacheable;
import com.quicksure.insurance.dao.DptinforMapper;
import com.quicksure.insurance.dao.InterfaceDetailsMapper;
import com.quicksure.insurance.dao.InterfaceslogsMapper;
import com.quicksure.insurance.entity.Dptinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;
import com.quicksure.insurance.entity.InterfaceDetails;
import com.quicksure.insurance.entity.InterfaceslogsWithBLOBs;
import com.quicksure.insurance.utils.DateFormatUtils;
@Repository
public class DMSUtility {
	private static final Logger logger = Logger
			.getLogger(DMSUtility.class);
	@Resource
	private  InterfaceslogsMapper interfaceslogsMapper;
	@Resource
	private DptinforMapper dptinforMapper;
	@Resource
	private InterfaceDetailsMapper interfaceDetailsMapper;
	/**
	 * 存储logs to interfacelogs table
	 * @param insuranceDetailsVO
	 * @return
	 */
	public InsuranceDetailsVO Saveinterfaceslogs(InsuranceDetailsVO insuranceDetailsVO){
		int result=-1;
		InterfaceslogsWithBLOBs interfaceslogsWithBLOBs=null;
	    if(insuranceDetailsVO!=null){
	    	interfaceslogsWithBLOBs=insuranceDetailsVO.getInterfaceslogsWithBLOBs();
	    	if(interfaceslogsWithBLOBs!=null){
	    		interfaceslogsWithBLOBs.setBaseinfororderno(insuranceDetailsVO.getBaseinfor().getOrderno());
	    		result=interfaceslogsMapper.insertSelective(interfaceslogsWithBLOBs);
	    	}
	    	if(result==1){
	    		logger.info("请求数据成功增加进ludimb_interfaceslogs表"+result);
	    		insuranceDetailsVO.setInterfaceslogsWithBLOBs(interfaceslogsWithBLOBs);
	    	}
	    	
	    } 
		return insuranceDetailsVO;
		
	}
	/**
	 * 更新interface logs
	 * @return
	 */
	
	public InsuranceDetailsVO updateInterfaceLogs(InsuranceDetailsVO insuranceDetailsVO){
		int result=-1;
		InterfaceslogsWithBLOBs interfaceslogsWithBLOBs=null;
	    if(insuranceDetailsVO!=null){
	    	interfaceslogsWithBLOBs=insuranceDetailsVO.getInterfaceslogsWithBLOBs();
	    	if(interfaceslogsWithBLOBs!=null){
	    		String responseTime=DateFormatUtils.getSystemDate();
	    		interfaceslogsWithBLOBs.setResposnetime(responseTime);
	    		interfaceslogsWithBLOBs.setUpdatedatatime(responseTime);
	    		result=interfaceslogsMapper.updateByPrimaryKeyWithBLOBs(interfaceslogsWithBLOBs);
	    	}
	    	if(result==1){
	    		logger.info("返回数据成功更新进ludimb_interfaceslogs表"+result);
	    		insuranceDetailsVO.setInterfaceslogsWithBLOBs(interfaceslogsWithBLOBs);
	    	}
	    	
	    }
	    insuranceDetailsVO.getInterfaceslogsWithBLOBs().setResponsexml(null);
		return insuranceDetailsVO;
					
	}	
	/**
	 * 获取所有的行政机构
	 * 孙小东
	 */
	@Cacheable(cacheName="baseCache")
	public List<Dptinfor> getAlldeptinfor(){
		List<Dptinfor> dptinfors=null;
		dptinfors=dptinforMapper.selectALL();
		return dptinfors;
		
	}

	/**
	 * 获取对接系统的连接信息 孙小东
	 * 
	 * @return
	 */
	@Cacheable(cacheName="baseCache")
	public List<InterfaceDetails> getConnectinfor() {		  	
		List<InterfaceDetails> interfaceDetails = interfaceDetailsMapper.selectAll();		
		return interfaceDetails;
	}
	
}
