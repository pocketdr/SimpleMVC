package com.pocketz.learnweb.framework;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract class AbstractDispatcher {

	public abstract ModelAndView invoke(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ReflectiveOperationException;
}
