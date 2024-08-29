package com.smartbank.transactionservice.interceptor;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.smartbank.transactionservice.constant.SysConstant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
		 	MDC.put(SysConstant.SYS_REQ_CORR_ID_HEADER, StringEscapeUtils.escapeHtml4(request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER)));
			return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable Exception ex) throws Exception {
		MDC.remove(SysConstant.SYS_REQ_CORR_ID_HEADER);
	} 
}
