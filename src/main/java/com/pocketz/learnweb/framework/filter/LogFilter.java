package com.pocketz.learnweb.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter("/*")
public class LogFilter implements Filter {
	private final Logger logger =LoggerFactory.getLogger(getClass());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//System.out.println("LogFilter:process"+((HttpServletRequest)request).getRequestURI());
		logger.info("a {} request for {}", ((HttpServletRequest)request).getMethod(),((HttpServletRequest)request).getRequestURI());
		chain.doFilter(request, response);

	}

}
