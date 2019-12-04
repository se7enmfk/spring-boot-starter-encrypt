/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.common.component;

import com.ftx.frame.util.date.DateUtil;
import com.ftx.frame.util.email.EmailEntity;
import com.ftx.frame.util.email.EmailUtil;
import com.ftx.frame.util.response.ResponseUtil;
import com.ftx.frame.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.UUID;

@Component
public class FtExceptionResolver implements HandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(FtExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object obj, Exception ex) {
        ModelAndView mv = new ModelAndView();
        UUID uuid = UUID.randomUUID();

        // response返回
        response.setStatus(HttpStatus.ACCEPTED.value());
        ResponseUtil.setResponseHeader(response);
        ResponseUtil.setResponseWriter(response, ResponseUtil.RESPONSE_TYPE_E,
                request.getRequestURL() + "===" + uuid.toString());

        // 数据库记录
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw, true));

        try {

            EmailEntity emailEntity = new EmailEntity()
                    .setAddress(StringUtil.stringToArray(SystemConfig.MANAGE_EMAIL_ACCOUNT))
                    .setSubject(SystemConfig.SYSTEM_NAME + " error msg:" + DateUtil.getCurrDateTimeStr())
                    .setContent("Request URL:" + request.getRequestURL() + "=============" +
                            "exception uuid:" + uuid + "=============" +
                            ex.getMessage() + "=============" + Arrays.toString(ex.getStackTrace()));

            EmailUtil.wrapAndSendEmail(emailEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // System.out.println(sw.toString());
        // logger记录
        logger.error("exception uuid==============" + uuid + "=============" + ex.getMessage(), ex);
        return mv;
    }

}
