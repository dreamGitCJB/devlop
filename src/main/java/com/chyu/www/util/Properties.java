package com.chyu.www.util;

import java.io.StringReader;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

/**
 * 读写XML配置文件
 * 
 * @author jiangkan
 * 
 */
public class Properties {
	private static Logger logger = Logger.getLogger(Properties.class);

	private XMLProperties properties = null;

//	public static void main(String[] args) {
//		Properties p = new Properties("config.xml");
//		System.out.println(p.getStringProp("SMS.CorpID", "0"));
//	}

	public Properties(String filepath) {
		try {
			this.properties = new XMLProperties(filepath);
			logger.info("load " + filepath + " ok!");
		} catch (Exception e) {
			logger.fatal("Error reading properties file " + filepath, e);
		}
	}
	
	public Properties(){
	}
	
	public void SetXmlDoc(String xmlDoc){
		StringReader read = new StringReader(xmlDoc);
		//创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(read);
		try {
			this.properties = new XMLProperties(source);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void saveProperties() {
		this.properties.saveProperties();
	}

	public void setStringProp(String name, String value) {
		this.properties.setProperty(name, value);
	}

	public void setIntProp(String name, int value) {
		this.properties.setProperty(name, String.valueOf(value));
	}

	public void setBooleanProp(String name, boolean value) {
		this.properties.setProperty(name, (value) ? "yes" : "no");
	}

	public void setDoubleProp(String name, double value) {
		this.properties.setProperty(name, String.valueOf(value));
	}

	/**
	 * 获取字符串值
	 * 
	 * @param name
	 * @param def
	 * @return
	 */
	public String getStringProp(String name, String def) {
		try {
			String ret = this.properties.getProperty(name);
			if ((ret == null) || ("".equals(ret))) {
				return def;
			}
			return ret;
		} catch (Exception e) {
		}
		return def;
	}

	/**
	 * 获取布尔值
	 * 
	 * @param name
	 * @param def
	 * @return
	 */
	public boolean getBooleanProp(String name, boolean def) {
		try {
			String value = this.properties.getProperty(name).toLowerCase();
			if ((value.equalsIgnoreCase("true"))
					|| (value.equalsIgnoreCase("on"))
					|| (value.equalsIgnoreCase("yes"))) {
				return true;
			}
			if ((value.equalsIgnoreCase("false"))
					|| (value.equalsIgnoreCase("off"))
					|| (value.equalsIgnoreCase("no"))) {
				return false;
			}
			return def;
		} catch (Exception e) {
		}
		return def;
	}

	/**
	 * 获取INT值
	 * 
	 * @param name
	 * @param def
	 * @return
	 */
	public int getIntProp(String name, int def) {
		try {
			return Integer.parseInt(this.properties.getProperty(name));
		} catch (Exception e) {
		}
		return def;
	}

	/**
	 * 获取Double值
	 * 
	 * @param name
	 * @param def
	 * @return
	 */
	public double getDoubleProp(String name, double def) {
		try {
			return Double.parseDouble(this.properties.getProperty(name));
		} catch (Exception e) {
		}
		return def;
	}

	public String[][] getProp(String name, String[][] def) {
		try {
			return this.properties.getChildrenProperties(name);
		} catch (Exception e) {
		}
		return def;
	}

}