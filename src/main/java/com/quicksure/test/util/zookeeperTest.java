package com.quicksure.test.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class zookeeperTest {
	private static ApplicationContext ctx_producer = null;

    public final static String ApplicationContextRoot = "";
    public final static String ApplicationContextPath = ApplicationContextRoot + "spring.xml";
    

    public static void init() {
    	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
    			new String[]{"spring.xml","spring-mybatis.xml"});
        context.start();
        System.out.println("Provider Server Started ！");
    }

    public static ApplicationContext getContext() {
        init();
        return ctx_producer;
    }
}
