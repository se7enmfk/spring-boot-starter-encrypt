package com.ftx.frame.util.http;

import com.ftx.frame.util.BaseConstant;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private HttpClientUtil() {
    }

    /**
     * do get
     *
     * @param url
     * @return
     */
    public static String doGet(String url) {
        String uuid = UUID.randomUUID().toString();
        String result = null;
        HttpResponse response;
        try {
            logger.info("【调用接口】" + uuid + ":\r\n\t" + url);

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

            CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

            HttpGet httpGet = new HttpGet(url);

            response = closeableHttpClient.execute(httpGet);

            result = getResponseString(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * do post
     *
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, List<NameValuePair> params) {
        String uuid = UUID.randomUUID().toString();
        String result = null;
        HttpResponse response;
        HttpEntity formEntity;
        try {
            logger.info("【调用接口】" + uuid + ":\r\n\t" + url);
            logger.info("【传入参数】" + uuid + ":\r\n\t" + params);

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

            CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

            HttpPost httppost = new HttpPost(url);

            formEntity = new UrlEncodedFormEntity(params, "UTF-8");

            httppost.setHeader(BaseConstant.HEADER_FTX_INTERFACE_KEY, BaseConstant.HEADER_FTX_INTERFACE_VALUE);

            httppost.setEntity(formEntity);

            response = closeableHttpClient.execute(httppost);

            result = getResponseString(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String getResponseString(HttpResponse response) {

        String result = null;
        try {
            if (response != null) {
                HttpEntity entity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    //获取返回值
                    result = EntityUtils.toString(entity, "UTF-8");
                    result = URLDecoder.decode(result, "UTF-8");

                    Map<String, Object> map = JsonUtil.parseObject(result, Map.class);
                    if (map != null && BaseConstant.CODE_200.equals(map.get("code"))) {
                        result = map.get("entity").toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Object getString(String jsonResult) {

        Map<String, Object> map = JsonUtil.parseObject(jsonResult, Map.class);
        if (map != null && BaseConstant.CODE_200.equals(map.get("code"))) {
            return map.get("entity");
        } else {
            return null;
        }

    }

    /**
     * 获取IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {// 如果获取失败,则获取代理Ip
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");

        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {// 多次反向代理后会有多个Ip第一个为真实IP
            int index = ip.indexOf("\\,");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }

    }

    public static String getGzipString(String jsonResult) {

//        Map<String, Object> map = JsonUtil.parseObject(jsonResult, Map.class);
//        if (map != null && BaseConstant.CODE_200.equals(map.get("code"))) {
//            return ZipUtils.unGzip((String) map.get("gzip_string"));
//        } else {
            return null;
//        }

    }

    public static void main(String[] args) {
/*

        String s = doGet("http://localhost:9080/FdcServer/findProHisPfmc1?user_code=12312");
        logger.info(1);
        logger.info(s);
*/


        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("dataBeginDt", "2002-01-01"));
        params.add(new BasicNameValuePair("dataEndDt", "2002-06-01"));
        params.add(new BasicNameValuePair("proCodes", "000001.FT"));
        String s = doPost("http://localhost:9080/FdcServer/findProHisPfmc", params);
        logger.info(s);
    }
}
