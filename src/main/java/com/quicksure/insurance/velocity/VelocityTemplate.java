package com.quicksure.insurance.velocity;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.quicksure.insurance.utils.SysConstantsConfig;

public class VelocityTemplate {
	private static final Logger logger = Logger
			.getLogger(VelocityTemplate.class);
	private static VelocityEngine velocityEngine = new VelocityEngine();
	private static final Properties VLY_INIT_PRO = new Properties();

	static {
		if("dev".equals(SysConstantsConfig.PROVIDE_METHOD)){
			logger.info("----------开发环境模板调用！---------");
			String sysRoot = VelocityTemplate.class.getResource("").getPath();
			logger.info("----sysRoot---:"+sysRoot+SysConstantsConfig.Velocity_Template_ForQuickApp_path);
			VLY_INIT_PRO.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, 
				sysRoot+SysConstantsConfig.Velocity_Template_ForQuickApp_path); //本地启动
		}else if("test".equals(SysConstantsConfig.PROVIDE_METHOD)){
			logger.info("----------测试环境模板调用！---------");
			//从jar包中获取资源
			//设置velocity资源加载方式为jar
			VLY_INIT_PRO.setProperty("resource.loader", "jar");
			//设置velocity资源加载方式为file时的处理类
			VLY_INIT_PRO.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
			//设置jar包所在的位置
			VLY_INIT_PRO.setProperty("jar.resource.loader.path", 
					"jar:file:/home/quicksurePCProject/quicksure_Server_Provider.jar"); //打测试服务器时用
		}else if("prod".equals(SysConstantsConfig.PROVIDE_METHOD)){
			//生产的在这里配置就好了
		}
		
		/*//设置velocity资源加载方式为file
		VLY_INIT_PRO.setProperty("resource.loader", "file");
        //设置velocity资源加载方式为file时的处理类
		VLY_INIT_PRO.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");*/
		
		VLY_INIT_PRO.put("input.encoding", "UTF-8");
		VLY_INIT_PRO.put("output.encoding", "UTF-8");
	}

	static {
		try {
			velocityEngine.init(VLY_INIT_PRO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static VelocityEngine getVelocityEngine() throws Exception {
		return velocityEngine;
	}

	public static String getVelocityTemplate(String basePath,
			Map<String, Object> objAliasMap) throws Exception {
		StringWriter stringWriter = new StringWriter();
		VelocityEngine velocityEngine = getVelocityEngine();

		try {
			VelocityContext velocityContext = new VelocityContext();
			if (objAliasMap != null && !objAliasMap.isEmpty()) {
				Set<String> keySet = objAliasMap.keySet();
				Iterator<String> it = keySet.iterator();
				while (it.hasNext()) {
					String key = it.next();
					velocityContext.put(key, objAliasMap.get(key));
				}
			}
			logger.info("--------模板路径--------:"+basePath);
			Template template = null;
			if("dev".equals(SysConstantsConfig.PROVIDE_METHOD)){
				//本地开发环境
				template = velocityEngine.getTemplate(basePath, "UTF-8");
			}else{
				//测试环境/生产环境
				template = velocityEngine.getTemplate("com/quicksure/insurance/velocity/template/"+basePath, "UTF-8"); 
			}
			template.merge(velocityContext, stringWriter);
		} catch (Exception e) {
			logger.error("Velocity处理模板发生错误。模板路径：" + basePath, e);
			e.printStackTrace();
			throw e;
		}
		return stringWriter.toString();

	}
}
