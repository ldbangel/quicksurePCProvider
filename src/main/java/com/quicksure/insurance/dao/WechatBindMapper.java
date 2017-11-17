package com.quicksure.insurance.dao;

import com.quicksure.insurance.entity.WechatBind;

public interface WechatBindMapper {
    int deleteByPrimaryKey(Integer binduserid);

    int insert(WechatBind record);

    int insertSelective(WechatBind record);

	WechatBind selectByPrimaryKey(Integer binduserid);

	WechatBind selectByOpenId(String openId);

	WechatBind selectByphoneUserId(Integer phoneUserId);

	WechatBind selectByWechatUserId(Integer wechatUserId);
    
    int updateByPrimaryKeySelective(WechatBind record);

    int updateByPrimaryKey(WechatBind record);
}