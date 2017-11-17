package com.quicksure.test.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {
	private static Log logger = LogFactory.getLog(Launcher.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
      getLocalip();
      logger.info("---****Provider 启动****-----");
      ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
    	  //本地开发环境    
	  logger.info("本地服务启动！");
	  System.out.println("Provider Server Started!");
      try{
          System.in.read();
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  /**
   * 暴露IP
   */
  private static void getLocalip() {
      try {
          System.out.println("暴露IP: "
                  + java.net.InetAddress.getLocalHost().getHostAddress());
      } catch (Exception e) {
          System.out.println(e.getMessage());
      }
  }
}
