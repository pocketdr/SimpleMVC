package com.pocketz.learnweb.framework;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class ViewEngine {

	private final PebbleEngine engine;

	public ViewEngine(ServletContext servletContext) {
		ServletLoader loader = new ServletLoader(servletContext);
		loader.setCharset("UTF-8");
		loader.setPrefix("/WEB-INF/templates");
		loader.setSuffix("");
		this.engine = new PebbleEngine.Builder()
				.autoEscaping(true) // 默认打开HTML字符转义，防止XSS攻击
				.cacheActive(false) // 禁用缓存使得每次修改模板可以立刻看到效果
				.loader(loader).build();
	}
	
	public void render(ModelAndView mv,Writer writer)throws IOException{
		PebbleTemplate template = this.engine.getTemplate(mv.view.substring(1));
		template.evaluate(writer,mv.model);
	}
}
