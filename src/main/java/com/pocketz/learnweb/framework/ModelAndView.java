package com.pocketz.learnweb.framework;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
	Map<String, Object> model;
	String view;
	public ModelAndView(String view) {
		this.view = view;
		this.model = Collections.emptyMap();//Java9:Map.of();
	}
	public ModelAndView(String view, String name, Object value) {
		// TODO Auto-generated constructor stub
		this.view = view;
		this.model = new HashMap<>();
		this.model.put(name,value);
	}

//	public ModelAndView(String view, Map<String, Object> model) {
//		this.view = view;
//		this.model = new HashMap<>(model);
//	}
}
