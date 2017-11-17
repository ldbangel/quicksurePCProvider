package com.quicksure.insurance.dao;

import com.quicksure.insurance.entity.AgentCode;

public interface AgentCodeMapper {
    int deleteByPrimaryKey(Integer agentcodeid);

    int insert(AgentCode record);

    int insertSelective(AgentCode record);

    AgentCode selectByPrimaryKey(Integer agentcodeid);
    
    AgentCode selectByAgentCode(String agentCode);

    int updateByPrimaryKeySelective(AgentCode record);

    int updateByPrimaryKey(AgentCode record);
    
    int updateByagentCodeSelective(AgentCode record);
}