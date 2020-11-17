package com.pocketz.learnweb.framework.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {
	//init  WebApp,e.g,database-connection
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("WebApp initialized."+sce.getServletContext());
	}
	
	//destroy WebApp,e.g.,database-connection
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		System.out.println("WebApp destroyed.");
	}
}
