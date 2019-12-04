/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import com.ftx.frame.util.BaseConstant;
import com.ftx.frame.util.string.StringUtil;

public class ResponseUtil {

	public static final String HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_NO_CACHE = "no-cache";
	public static final String HEADER_MUST_REVALIDATE = "must-revalidate";
	public static final String HEADER_ACCESS = "*";
	public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	public final static String RESPONSE_TYPE_A = "A"; // 没权限
	public final static String RESPONSE_TYPE_E = "E"; // exception
	public final static String RESPONSE_TYPE_L = "L"; // 登录超时

	public static void setResponseHeader(HttpServletResponse response) {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 设置ContentType
		response.setCharacterEncoding(BaseConstant.UTF8); // 避免乱码
		response.setHeader(HEADER_CACHE_CONTROL, StringUtil.concat(
				HEADER_NO_CACHE, BaseConstant.COMMA, HEADER_MUST_REVALIDATE));
		response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, HEADER_ACCESS);
	}

	public static void setResponseWriter(HttpServletResponse response,
			String type, String msg) {

		try {
			response.getWriter().write(
					"{\"success\":\"false\",\"type\":\"" + type
							+ "\",\"msg\":\"" + msg + "\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}