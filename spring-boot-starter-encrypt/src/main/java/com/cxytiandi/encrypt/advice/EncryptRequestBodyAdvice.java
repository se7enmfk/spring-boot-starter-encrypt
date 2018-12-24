package com.cxytiandi.encrypt.advice;

import com.cxytiandi.encrypt.anno.Decrypt;
import com.cxytiandi.encrypt.auto.EncryptProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 请求数据接收处理类<br>
 * <p>
 * 对加了@Decrypt的方法的数据进行解密操作<br>
 * <p>
 * 只对@RequestBody参数有效
 *
 * @author yinjihuan
 * @about http://cxytiandi.com/about
 */
@ControllerAdvice
public class EncryptRequestBodyAdvice implements RequestBodyAdvice {

    private Logger logger = LoggerFactory.getLogger(EncryptRequestBodyAdvice.class);

    @Autowired
    private EncryptProperties encryptProperties;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                  Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {

        //region 有decrypt且value=false才不解密
        Decrypt methodAnnotation = parameter.getMethodAnnotation(Decrypt.class);
        boolean valid = !encryptProperties.isDebug() && (methodAnnotation == null || methodAnnotation.value());

        if (valid) {
            try {
                return new DecryptHttpInputMessage(inputMessage, encryptProperties);
            } catch (Exception e) {
                logger.error("数据解密失败", e);
            }
        }
        //endregion
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
