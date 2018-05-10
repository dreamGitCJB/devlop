package com.chyu.www.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author 蒋侃 E-mail:jiangkan@163.com
 * @version 创建时间：2017年10月11日 下午3:36:35
 * 类说明
 * HTTP请求工具类
 */
public class HttpUtil {

	/**
     * POST
     * @param url    不带?号和/杠的连接地址
     * @param params 参数  名值对
     * @param body   用于post提交的，不跟在url后面的内容
     * @return 字符串
     */
    public static String Post(String url, HashMap<String, String> params, String body) throws Exception {
        url = genParamsStr(url, params);
        URL restUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) restUrl.openConnection();
        connection.setConnectTimeout(3000);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        if (body != null) {
            DataOutputStream out = new DataOutputStream(connection
                    .getOutputStream());
            out.write(body.getBytes("UTF-8"));
            out.flush();
            out.close();
        }
        int code = connection.getResponseCode();
        if (code == 200) {
            return getResponse(connection.getInputStream());
        }
        return "";
    }
    
    /**
     * Get
     * @param url 不带?号和/杠的连接地址
     * @param params 参数  名值对
     * @return 字符串
     * @throws Exception
     */
    public static String Get(String url,HashMap<String, String> params) throws Exception {
    	url = genParamsStr(url, params);
        URL restUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) restUrl.openConnection();
        connection.setConnectTimeout(3000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoInput(true);
        int code = connection.getResponseCode();
        if (code == 200) {
            return getResponse(connection.getInputStream());
        }
        return "";
    }
    
    /**
     * 获取响应内容
     * @param inputstream
     * @return
     */
    private static String getResponse(InputStream inputstream) {
        String jsonString = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = 0;
        byte[] data = new byte[1024];
        try {
            while ((len = inputstream.read(data)) != -1) {
                outputStream.write(data, 0, len);
            }
            jsonString = new String(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }
    
    /**
     * 组装参数
     * @param params
     * @return
     */
    private static String genParamsStr(String url, HashMap<String, String> params) {
        try {
            if (params != null) {
                //读取参数params，用?拼接到url后面
                StringBuffer sb = new StringBuffer();
                Iterator iter = params.keySet().iterator();
                int i = 0;
                while (iter.hasNext()) {
                    String key = iter.next().toString();
                    String value = URLEncoder.encode(params.get(key), "utf-8");
                    if (i == 0) {
                        sb.append(key).append("=").append(value);
                    } else {
                        sb.append("&").append(key).append("=").append(value);
                    }
                    i++;
                }
                return url + "?" + sb.toString();
            } else {
                return url;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
}
