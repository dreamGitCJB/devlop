package com.chyu.www.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 蒋侃 E-mail:jiangkan@163.com
 * @version 创建时间：2017年5月25日 下午9:15:37 类说明 Cookie帮助类
 */
public class CookieUtil {

	/**
	 * 添加cookie
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 */
	public static void addCookie(HttpServletResponse response, String name, String value,int maxAge) {
		Cookie cookie = new Cookie(name.trim(), value.trim());
		if(maxAge>0)
			cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 根据名字获取cookie值
	 * @param request
	 * @param name
	 *            cookie的名字
	 * @return
	 */
	public static Cookie  getCookieByName(HttpServletRequest request, String name) {
		Map<String,Cookie> cookieMap = ReadCookieMap(request);
	    if(cookieMap.containsKey(name)){
	        Cookie cookie = (Cookie)cookieMap.get(name);
	        return cookie;
	    }else{
	        return null;
	    } 
	}

	public static void delCookeByName(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().contains(name)) {
					cookie.setValue(null);
					cookie.setMaxAge(0);// 立即销毁
					cookie.setPath("/");
					response.addCookie(cookie);
				}
			}
		}
	}
	
	
	/**
	 * 将cookie封装到Map里面
	 * @param request
	 * @return
	 */
	private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){  
	    Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
	    Cookie[] cookies = request.getCookies();
	    if(null!=cookies){
	        for(Cookie cookie : cookies){
	            cookieMap.put(cookie.getName(), cookie);
	        }
	    }
	    return cookieMap;
	}

}
