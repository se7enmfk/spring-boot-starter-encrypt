package com.ftx.frame.util.http;

import com.ftx.frame.util.BaseConstant;
import com.ftx.frame.util.string.StringUtil;
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
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

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

                    Map<String, Object> map = JsonUtil.fromJson(result, Map.class);
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

        Map<String, Object> map = JsonUtil.fromJson(jsonResult, Map.class);
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
    /**
     * get JSON from URL
     *
     * @param urlPath
     * @param type  post or get
     * @param inputJson when post then is not null
     * @return
     */
    public static String getJsonResult(String urlPath, String type, String inputJson) {
        type = StringUtil.isEmpty(type) ? "GET" : type;

        Scanner scanner = null;
        String response;
        String jsonStr = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(type);
            conn.setRequestProperty("Content-Type", "application/json");
            //分特专用接口header
            conn.setRequestProperty(BaseConstant.HEADER_FTX_INTERFACE_KEY, BaseConstant.HEADER_FTX_INTERFACE_VALUE);
            if(StringUtil.isNotEmpty(inputJson)){
                OutputStream os = conn.getOutputStream();
                os.write(gbEncoding(inputJson).getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != 200) {
                if(conn.getErrorStream() != null)
                    scanner = new Scanner(conn.getErrorStream(), "UTF-8");
                if(conn.getInputStream() != null)
                    scanner = new Scanner(conn.getInputStream(), "UTF-8");
                response = "Error From Server \n\n";
            } else {
                scanner = new Scanner(conn.getInputStream(), "UTF-8");
                response = "Response From Server \n\n";
            }
            scanner.useDelimiter("\\Z");
            jsonStr = scanner.next();
            System.out.println(response + jsonStr);
            scanner.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    public static String getServletPath(HttpServletRequest request) {
        return request.getServletPath();
    }


    private static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            int a = (char) utfBytes[byteIndex];
            if (a > 255) {
                String hexB = Integer.toHexString(utfBytes[byteIndex]);
                if (hexB.length() <= 2) {
                    hexB = "00" + hexB;
                }
                unicodeBytes = unicodeBytes + "\\u" + hexB;
            } else {
                unicodeBytes = unicodeBytes + utfBytes[byteIndex];
            }
        }
        System.out.println("unicodeBytes is: " + unicodeBytes);
        return unicodeBytes;
    }

    //axis get json
	/*
	 * public static String getJsonResult(String urlPath, String Namespace,
			String mothodName, String param, String param_value) {
		Service service = new Service();
		Call call;
		String res = "";
		try {
			call = (Call) service.createCall();
			call.setTargetEndpointAddress(urlPath);
			call.setOperationName(new QName(Namespace, mothodName));
			call.addParameter(param, XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnType(XMLType.XSD_STRING);
			Object[] obj = { param_value };
			res = (String) call.invoke(obj);

		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}*/
    public static void main(String[] args) {

        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("dataBeginDt", "2002-01-01"));
        params.add(new BasicNameValuePair("dataEndDt", "2002-06-01"));
        params.add(new BasicNameValuePair("proCodes", "000001.FT"));
        String s = doPost("http://localhost:9080/FdcServer/findProHisPfmc", params);
        logger.info(s);
    }
}
