package com.chyu.www.annotation;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;

import com.chyu.www.Application;

/**
 * @author 蒋侃 E-mail:jiangkan@163.com
 * @version 创建时间：2017年12月7日 下午10:28:08
 * 类说明
 * 切面类，实现方法级访问限制
 */
@Aspect
@Component
public class RequestLimitAspect {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	// 黑名单IP的key
	public static String KEY_BLACK_IP = "black_ip_%s";
	// 记录每个IP在1秒内的访问某一个URL的次数
	public static String KEY_IP_TIMES = "times_ip_%s_%s";
	
	//前置通知
	@Before("within(@org.springframework.stereotype.Controller *) && @annotation(limit)")
	public void requestLimit(final JoinPoint joinPoint, RequestLimit limit) throws Exception {
	      try {
	            Object[] args = joinPoint.getArgs();
	            HttpServletRequest request = null;
	            for (int i = 0; i < args.length; i++) {
	                if (args[i] instanceof HttpServletRequest) {
	                    request = (HttpServletRequest) args[i];
	                    break;
	                }
	            }
	            if (request == null) {
	                throw new Exception("方法中缺失HttpServletRequest参数");
	            }
	           
	            // 如果没有开启限制，直接返回
	    		if (!Application.FREQUENCY_ENABLE) {
	    			return;
	    		}
	    		// 排除的IP地址直接返回
	    		if (Application.EXCLUDE.contains(request.getRemoteHost())) {
	    			return;
	    		}
	    		// ajax请求也直接返回true
	    		String requestType = request.getHeader("X-Requested-With");
	    		if (requestType != null) {
	    			return;
	    		}
	    		
	    		// 获取访问者IP地址
	    		String ip = request.getRemoteHost();
	    		String url = request.getRequestURL().toString();
	    		String method =request.getRequestURI()+"-"+ServletRequestUtils.getStringParameter(request, "method", "");
	    		// 如果该IP还在锁定黑名单中，则返回false
	    		if (stringRedisTemplate.opsForValue().get(String.format(KEY_BLACK_IP, ip)) != null) {
	    			 logger.info("用户IP[" + ip + "]访问超过了限定的次数[" + limit.count() + "]，目前处于暂时锁定状态");
		             throw new Exception();
	    		}
	    		// 获取time秒内已经访问该url的次数
	    		String strTimes = stringRedisTemplate.opsForValue().get(String.format(KEY_IP_TIMES, ip,method));
	    		int times = Integer.parseInt(strTimes == null ? "0" : strTimes);
	    		// 访问加一次
	    		stringRedisTemplate.opsForValue().increment(String.format(KEY_IP_TIMES, ip,method), 1);
	    		// 如果没有设置过期时间,需要设置一个
	    		if (stringRedisTemplate.getExpire(String.format(KEY_IP_TIMES, ip,method)) == -1) {
	    			// 设置一个过期时间
	    			stringRedisTemplate.expire(String.format(KEY_IP_TIMES, ip,method), limit.time(), TimeUnit.SECONDS);
	    		}
	    		// 达到次数限制，加入黑名单，过指定时间以后解锁
	    		if (times > limit.count()) {
	    			stringRedisTemplate.opsForValue().append(String.format(KEY_BLACK_IP, ip), ip);
	    			// 设置N秒后解锁
	    			stringRedisTemplate.expire(String.format(KEY_BLACK_IP, ip), Application.LOCKSECONDS, TimeUnit.SECONDS);
	    			logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
		            throw new Exception();
	    		}
	        }  catch (Exception e) {
	            logger.error("发生异常: ", e);
	        }
	    }
}
