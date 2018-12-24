package com.cxytiandi.encrypt.advice;

import com.cxytiandi.encrypt.auto.EncryptProperties;
import com.cxytiandi.encrypt.util.AesEncryptUtils;
import com.cxytiandi.encrypt.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DecryptHttpInputMessage implements HttpInputMessage {
    private Logger logger = LoggerFactory.getLogger(EncryptRequestBodyAdvice.class);
    private HttpHeaders headers;
    private InputStream body;

    public DecryptHttpInputMessage(HttpInputMessage inputMessage, EncryptProperties encryptProperties) throws Exception {
        this.headers = inputMessage.getHeaders();
        String content = IOUtils.toString(inputMessage.getBody(), encryptProperties.getCharset());
        long startTime = System.currentTimeMillis();
        String decryptBody = "";

        // 对应的markMsg进行解密
        if (content.contains(encryptProperties.getMarkMsg())) {
            HashMap hashMap = JsonUtils.toBean(HashMap.class, content);
            decryptBody = AesEncryptUtils.aesDecrypt(hashMap.get(encryptProperties.getMarkMsg()).toString(), encryptProperties.getKey());
        }
        long endTime = System.currentTimeMillis();
        logger.debug("Decrypt Time:" + (endTime - startTime));
        this.body = IOUtils.toInputStream(decryptBody, encryptProperties.getCharset());
    }

    @Override
    public InputStream getBody() throws IOException {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}