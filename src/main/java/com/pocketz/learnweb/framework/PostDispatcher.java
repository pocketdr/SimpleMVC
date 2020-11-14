package com.pocketz.learnweb.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PostDispatcher extends AbstractDispatcher {
	final Object instance;
	final Method method;
	final Class<?>[] parameterClasses;
	final ObjectMapper objectMapper;

	public PostDispatcher(Object instance, Method method, Class<?>[] parameterClasses, ObjectMapper objectMapper) {
		this.instance = instance;
		this.method = method;
		this.parameterClasses = parameterClasses;
		this.objectMapper = objectMapper;
		// TODO Auto-generated constructor stub
	}

	public ModelAndView invoke(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ReflectiveOperationException {
		Object[] arguments = new Object[parameterClasses.length];
		for (int i = 0; i < parameterClasses.length; i++) {
			Class<?> parameterClass = parameterClasses[i];
			if (parameterClass == HttpServletRequest.class) {
				arguments[i] = request;
			} else if (parameterClass == HttpServletResponse.class) {
				arguments[i] = response;
			} else if (parameterClass == HttpSession.class) {
				arguments[i] = request.getSession();
			} else {
				// 读取JSON并解析为JavaBean:
				BufferedReader reader = request.getReader();
				arguments[i] = this.objectMapper.readValue(reader, parameterClass);
			}
		}
		return (ModelAndView) this.method.invoke(instance, arguments);
	}

}
