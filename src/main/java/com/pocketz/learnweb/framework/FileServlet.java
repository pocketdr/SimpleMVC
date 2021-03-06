package com.pocketz.learnweb.framework;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = { "/favicon.ico", "/static/*" })
public class FileServlet extends HttpServlet {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServletContext ctx = req.getServletContext();
		
		String urlPath = req.getRequestURI().substring(ctx.getContextPath().length());
		
		String filepath = ctx.getRealPath(urlPath);
		if (filepath == null) {
			logger.warn("Path null");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		Path path = Paths.get(filepath);
		if (!path.toFile().isFile()) {
			logger.warn("This path is not file");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String mime = Files.probeContentType(path);
		if (mime == null) {
			logger.warn("Mime is null");
			mime = "application/octet-stream";
		}
		resp.setContentType(mime);
		
		OutputStream output = resp.getOutputStream();
		try (InputStream input = new BufferedInputStream(new FileInputStream(filepath))) {
			//TODO :version9
			input.transferTo(output);
		}
		output.flush();
	}
}
