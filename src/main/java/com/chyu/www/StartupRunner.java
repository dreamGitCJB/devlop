package com.chyu.www;

import java.net.URLDecoder;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.chyu.www.util.Properties;


/**
 * @author chenjinbao
 * @version 创建时间：2017年11月14日 上午9:32:44 类说明
 *          springboot启动后会去遍历CommandLineRunner接口，然后执行run方法 可以用@Order指定执行顺序
 */
@Component
@Order(1)
public class StartupRunner implements CommandLineRunner {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void run(String... arg0) throws Exception {
		ShowLoadedBean();
		loadConfig();
	}

	/**
	 * 显示一下系统加载了哪些Bean
	 */
	private void ShowLoadedBean() {
		logger.info("系统加载了以下Bean--------------------");
		String[] beanNames = Application.ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			logger.info(beanName);
		}
		logger.info("-------------------------------------------");
	}

	/**
	 * 加载config文件配置
	 * 
	 * @param configPath
	 */
	private void loadConfig() {
		logger.info("系统加载Config.xml--------------------");
		// 加载配置文件
		String xmlPath = this.getClass().getResource("/") + "config.xml";
		xmlPath = xmlPath.replaceAll("%20", " ");
		xmlPath = xmlPath.replaceAll("file:", "");
		try {
			xmlPath = URLDecoder.decode(xmlPath, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Properties p = new Properties(xmlPath);
		// 上传图片存放路径
		Application.UPLOAD_DIR = p.getStringProp("upload.path", "");
		// 访问频率限制
		Application.FREQUENCY_ENABLE = p.getBooleanProp("accessfrequency.enable", false);
		Application.EXCLUDE = p.getStringProp("accessfrequency.exclude", "");
		Application.LOCKSECONDS = p.getIntProp("accessfrequency.lockseconds", 5);

		logger.info("UPLOAD_DIR:" + Application.UPLOAD_DIR);
		logger.info("FREQUENCY_ENABLE:" + Application.FREQUENCY_ENABLE);
		logger.info("FREQUENCY_COUNT:" + Application.FREQUENCY_COUNT);
		logger.info("FREQUENCY_TIME:" + Application.FREQUENCY_TIME);
		logger.info("EXCLUDE:" + Application.EXCLUDE);
		logger.info("LOCKSECOND:" + Application.LOCKSECONDS);
		logger.info("-------------------------------------------");
	}

}
