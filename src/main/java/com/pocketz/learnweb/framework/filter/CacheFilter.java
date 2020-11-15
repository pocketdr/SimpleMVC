package com.pocketz.learnweb.framework.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/slow/*")
public class CacheFilter implements Filter {

	private Map<String, byte[]> cache = new ConcurrentHashMap<>();
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		//path
		String url = req.getRequestURI();
		//catch
		byte[] data = this.cache.get(url);
		resp.setHeader("X-Cache-Hit", data == null ? "No":"Yes");
		if (data == null) {
			//fake response
			CachedHttpServletResponse wrapper = new CachedHttpServletResponse(resp);
			chain.doFilter(request, wrapper);
			data = wrapper.getContent();
			cache.put(url, data);
		}
		//origin response
		ServletOutputStream output = resp.getOutputStream();
		output.write(data);
		output.flush();
		
	}

}
