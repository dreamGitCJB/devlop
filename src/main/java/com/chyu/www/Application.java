package com.chyu.www;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author chenjinbao
 * @version 创建时间：2017年4月19日 上午10:42:35
 * 类说明
 * 如果要使用外部tomcat容器，需要：1、实现SpringBootServletInitializer 的configure方法，2、pom.xml中
 * <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<!-- 排除内置容器，排除内置容器导出成war包可以让外部容器运行spring-boot项目-->
			<exclusions>
				<exclusion>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-tomcat</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		总结：
		外部容器运行spring-boot项目，只需要在原项目上做两件事
		1、在pom.xml中排除org.springframework.boot的内置tomcat容器
		2、spring-boot入口实现SpringBootServletInitializer接口
		补充：SpringBootServletInitializer接口依赖javax.servlet包，需要在pom.xml中引入该包
 */
@SpringBootApplication
@EnableScheduling
public class Application extends SpringBootServletInitializer implements ApplicationContextAware   {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static ApplicationContext ctx;
	
	//session 变量名称
	public final static String SESSION_NAME = "admin_session";
	//cookie - 用户名
	public final static String COOKIE_USER = "cookie_user";
	//企业微信cookie-用户名
	public final static String COOKIE_WXUSER = "cookie_wxuser";
	
	
	//默认每页大小
	public final static int PAGESIZE=10;
	
	//session过期时间
	public final static int SESSION_TIMEOUT = 5 * 60 * 60; //5小时
	public final static int COOKIE_TIMEOUT  = 60 * 24 * 60 * 60; //60天
	
	//超级管理员
	public final static int ADMIN_TYPE_ADMINSTRATOR=0;
	//学校超级管理员
	public final static int ADMIN_TYPE_SCHOOL_ADMINSTRATOR=1;
	
	//2为视频考勤设备账号
	public final static int ADMIN_TYPE_VIDEO_ADMINSTRATOR=2;
	
	//上传保存路径
	public static String UPLOAD_DIR  ;
	
	//访问频率限制
	public static final int FREQUENCY_COUNT=5;
	public static final int FREQUENCY_TIME=1;
	public static boolean FREQUENCY_ENABLE = false;
	public static String EXCLUDE="";
	public static int LOCKSECONDS = 10;
	
	//设备报修状态
	public final static String EQUIPMENT_REPAIRE_REPAIRING="报修中";
	public final static String EQUIPMENT_REPAIRE_AUDITING="报价待审批";
	public final static String EQUIPMENT_REPAIRE_AUDITED_SUCC="审批通过";
	public final static String EQUIPMENT_REPAIRE_AUDITED_FAIL="审批不通过";
	public final static String EQUIPMENT_REPAIRE_REPAIR_SUCC="维修完成";
	
	//教师任课自设锁定
	public final static String LOCKED_TEACH_COURSE="教师任课自设锁定"; 

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
	/**
	 * 实现SpringBootServletInitializer可以让spring-boot项目在web容器中运行
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		builder.sources(this.getClass());
		return super.configure(builder);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
		logger.info("获取SpringBoot上下文："+ctx);
	}
	
	
//	//设置监听websocket-redis-messaage通道
//	 @Bean
//	 RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
//	            MessageListener listener) {
//	        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//	        container.setConnectionFactory(connectionFactory);
//	        container.addMessageListener(listener, new PatternTopic("websocket-redis-messaage"));
//	        return container;
//	 }
//	
	@Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
   
}
