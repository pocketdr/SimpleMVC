package com.pocketz.learnweb.framework.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

class CachedHttpServletResponse extends HttpServletResponseWrapper {
	private boolean open = false;
	private ByteArrayOutputStream output = new ByteArrayOutputStream();

	public CachedHttpServletResponse(HttpServletResponse response) {
		super(response);
	}

	// get writer
	public PrintWriter getWriter() throws IOException {
		if (open) {
			throw new IllegalStateException("Cannot re-open writer!");
		}
		open = true;
		//TODO version10
		return new PrintWriter(output, false, StandardCharsets.UTF_8);
	}

	// get outputStream
	public ServletOutputStream getOutputStream() throws IOException {
		if (open) {
			throw new IllegalStateException("Cannot re-open output stream!");
		}
		open = true;
		return new ServletOutputStream() {

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setWriteListener(WriteListener listener) {

			}

			@Override
			public void write(int b) throws IOException {
				output.write(b);
			}

		};
	}

	public byte[] getContent() {
		return output.toByteArray();
	}
}