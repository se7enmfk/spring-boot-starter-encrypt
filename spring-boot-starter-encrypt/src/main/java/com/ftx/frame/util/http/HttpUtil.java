package com.ftx.frame.util.http;

import com.ftx.frame.util.BaseConstant;
import com.ftx.frame.util.string.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class HttpUtil {
	
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
	
    public static String getGzipString(String jsonResult) {

//        Map<String, Object> map = JsonUtil.parseObject(jsonResult, Map.class);
//        if (map != null && BaseConstant.CODE_200.equals(map.get("code"))) {
//            return ZipUtils.unGzip((String) map.get("gzip_string"));
//        } else {
            return null;
//        }

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
}
