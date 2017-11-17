package com.quicksure.insurance.dms;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.quicksure.insurance.dao.BaseinforMapper;
import com.quicksure.insurance.entity.Baseinfor;

@Repository
public class PolicyStatusDataManageService {
	
	@Autowired
	private BaseinforMapper baseinforMapper;
	
	public int updateMultiplePolicyStatus(List<Baseinfor> baseinforList){
		return baseinforMapper.updateMultiplePolicyStatus(baseinforList);
	}
	
	public Baseinfor selectByApplicationNo(Baseinfor baseinfor){
		return baseinforMapper.selectByApplicationNo(baseinfor);
	}
	
	public List<Baseinfor> selectByTimes(Map<String, String> map){
		return baseinforMapper.selectByTimes(map);
	}
}
