package com.pocketz.learnweb.framework.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

class ReReadableHttpServletRequest extends HttpServletRequestWrapper {
	private byte[] body;
	private boolean open = false;
	private String illegalMessage = "Cannot re-open input stream!";

	public ReReadableHttpServletRequest(HttpServletRequest request, byte[] body) {
		super(request);
		this.body = body;
	}

	// retuen inputstream
	public ServletInputStream getInputStream() throws IOException {
		if (open) {
			throw new IllegalStateException(illegalMessage);
		}
		open = true;
		return new ServletInputStream() {//匿名内部类
			private int offset = 0;

			public boolean isFinished() {
				return offset >= body.length;
			}

			public boolean isReady() {
				return true;
			}

			public void setReadListener(ReadListener listener) {
			}

			public int read() throws IOException {
				if (offset >= body.length) {
					return -1;
				}
				int n = body[offset] & 0xff;
				offset++;
				return n;
			}
		};
	}
	
	public BufferedReader getReader() throws IOException{
		if(open) {
			throw new IllegalStateException(illegalMessage);
		}
		open= true;
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body), "UTF-8"));
	}

}
