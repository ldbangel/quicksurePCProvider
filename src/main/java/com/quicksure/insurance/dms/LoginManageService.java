package com.quicksure.insurance.dms;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.quicksure.insurance.dao.AgentCodeMapper;
import com.quicksure.insurance.dao.UserinforMapper;
import com.quicksure.insurance.dao.WechatBindMapper;
import com.quicksure.insurance.entity.AgentCode;
import com.quicksure.insurance.entity.Userinfor;
import com.quicksure.insurance.entity.WechatBind;
import com.quicksure.insurance.util.StringUtils;
import com.quicksure.insurance.utils.DesUtil;

@Repository
public class LoginManageService {
	private HttpSession session;
	private static final Logger logger = Logger.getLogger(LoginManageService.class);
	@Resource
	private  UserinforMapper userinforMapper;
	@Resource
	private AgentCodeMapper agentCodeMapper;
	 @Resource
	 private  WechatBindMapper wechatBindMapper;
	@Transactional
	public Userinfor selectByName(Userinfor userinfor) throws Exception{
		if(userinfor.getPassword()!=null&&!userinfor.getPassword().equals("")){
			userinfor.setPassword(DesUtil.encrypt(userinfor.getPassword()));// 加密密码
		}
		Userinfor uinfo = userinforMapper.selectByName(userinfor);
		return uinfo;
	}
	@Transactional
	public String resetPassword(Userinfor userinfor){
		int result=userinforMapper.updatePasswordByName(userinfor);
		String msg = "";
		if (result != 1) {
			msg = "1";//"密码重置失败";
		}
		return msg;
	}
	@Transactional
	public String userFindPassword(Userinfor userinfor,HttpServletRequest request){
		session = request.getSession();
		Userinfor uinfo = userinforMapper.selectByName(userinfor);
		String msg = "";
		if (uinfo != null) {
			try {
				session.setAttribute("loginUser", uinfo);
				String password = DesUtil.decrypt(uinfo.getPassword());//解密password
				// 调用短信接口将密码通过短信方式发送给用户
				
			} catch (Exception e) {
				StringWriter sw = new StringWriter();  
				  e.printStackTrace(new PrintWriter(sw, true));  
				  String str = sw.toString();
				logger.info("用户找回密码异常: "+str);
			}
		} else {
			msg = "手机与用户名不匹配";
		}

		return msg;
	}
	@Transactional
	public String registUser(Userinfor userinfor,AgentCode agent){
		
		//用户名和密码唯一,注册须判断
				String msg = "";
					try {
//						userinfor.setPassword(DesUtil.encrypt(userinfor.getPassword()));
						int result = userinforMapper.insertSelective(userinfor);
						if (result != 1) {
							msg = "1";//"用户注册失败";
						}else if(agent.getAgentcode()!=null && agent.getAgentcode()!=""){//注册时是代理人的话更新agentcode表
							//agentCode绑定userId
							agent.setUserid(userinfor.getUserid());
							//代理人注册成功后，把agentcode表里面的状态改为20
							agent.setCodestatus(20);
							agentCodeMapper.updateByagentCodeSelective(agent);
						}
					} catch (Exception e) {
						StringWriter sw = new StringWriter();  
						  e.printStackTrace(new PrintWriter(sw, true));  
						  String str = sw.toString();
						logger.info("用户登录添加异常: "+str);
					}
				return msg;
		
	}
	/**
	 * 微信账号绑定手机号，校验该微信账号是否已经存在
	 * 孙小东
	 * @param openId
	 * @return
	 */
	public boolean existFlag(String openId){
		boolean flag=false;
		if(StringUtils.checkStringEmpty(openId)){
		WechatBind  wechatBind=wechatBindMapper.selectByOpenId(openId);
		if(wechatBind==null){
			flag=true;
		}
		}
		return flag;		
	}
	
	/**
	 * 微信账号绑定手机号
	 * 孙小东
	 * @param openId
	 * @return
	 */
	public int saveBindData( WechatBind wechatBind){	
		return wechatBindMapper.insert(wechatBind);		
	}	
	

	/**
	 * 更新用户数据
	 * 孙小东
	 */
	public int updateUserInfor(Userinfor userinfor){
		return userinforMapper.updateByPrimaryKey(userinfor);
	}

	/**
	 * 代理人注册账号，代理人识别码校验
	 */
	public AgentCode selectAgent(String agentCode){
		return agentCodeMapper.selectByAgentCode(agentCode);
	}

	
	
}
