package com.pocketz.learnweb.framework.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/upload/*")
public class ValidataUploadFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// get signature-method & signature
		String digest = req.getHeader("Signature-Method");
		String signature = req.getHeader("Signature");
		if (digest == null || digest.isEmpty() || signature == null || signature.isEmpty()) {
			sendErrorPage(resp, "Missing signature");
			return;
		}
		// read request body
		MessageDigest md = getMessageDigest(digest);
		InputStream input = new DigestInputStream(request.getInputStream(), md);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (;;) {
			int len = input.read(buffer);//has the same effect as:
										//read(b, 0, b.length) 
			if (len == -1) {
				break;
			}
			output.write(buffer,0,len);
		}
		String  actual = toHexString(md.digest());
		if (!signature.equals(actual)) {
			sendErrorPage(resp, "Invalid signature");
			return;
		}
		
		chain.doFilter(new ReReadableHttpServletRequest(req,output.toByteArray()), response);

	}

	private String toHexString(byte[] digest) {
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	private MessageDigest getMessageDigest(String name) throws ServletException {
		try {
			return MessageDigest.getInstance(name);
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException(e);
		}
	}

	private void sendErrorPage(HttpServletResponse resp, String errorMessage) throws IOException {
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		PrintWriter pw = resp.getWriter();
		pw.write("<html><body><h1>");
		pw.write(errorMessage);
		pw.write("</h1></body></html>");
		pw.flush();
	}

}
