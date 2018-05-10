package com.chyu.www.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

public class XMLProperties {
	private static final Log log = LogFactory.getLog(XMLProperties.class);
	private File file;
	private Document doc;

	public XMLProperties(String file) throws Exception {
		this.file = new File(file);
		try {
			SAXBuilder builder = new SAXBuilder();

			DataUnformatFilter format = new DataUnformatFilter();
			builder.setXMLFilter(format);
			this.doc = builder.build(new File(file));
		} catch (Exception e) {
			throw e;
		}
	}
	
	public XMLProperties(InputSource is) throws Exception {
		try {
			SAXBuilder builder = new SAXBuilder();
			DataUnformatFilter format = new DataUnformatFilter();
			builder.setXMLFilter(format);
			this.doc = builder.build(is);
		} catch (Exception e) {
			throw e;
		}
	}
	
	

	public String getProperty(String name) {
		String[] propName = parsePropertyName(name);
		Element element = this.doc.getRootElement();
		for (int i = 0; i < propName.length; ++i) {
			//如果有前缀，则通过前缀获取命名空间
			if(propName[i].contains(":")){
				String[] ss = propName[i].split(":");
				Namespace np = element.getNamespace(ss[0]);
				element = element.getChild(ss[1],np);
			}else{
				Namespace np = element.getNamespace("");
				element = element.getChild(propName[i],np);
				//如果没有前缀，看是否具有默认的命名空间
//				Element elementTmp = element.getChild(propName[i]);
//				if(elementTmp==null){
//					Namespace np = element.getNamespace("");
//					element = element.getChild(propName[i],np);
//				}else{
//					element = element.getChild(propName[i]);
//				}
			}
			if (element == null) {
				return null;
			}
		}
		
		String value = element.getText();
		if ("".equals(value)) {
			return null;
		}

		value = value.trim();

		return value;
	}

	public String[][] getChildrenPropertiesByName(String parent, String child,
			String name) {
		String[] propName = parsePropertyName(parent);

		Element element = this.doc.getRootElement();
		for (int i = 0; i < propName.length; ++i) {
			element = element.getChild(propName[i]);
			if (element == null) {
				return new String[0][];
			}
		}

		List children = element.getChildren();
		int childCount = children.size();
		Element ee = null;
		for (int i = 0; i < childCount; ++i) {
			Element e = (Element) children.get(i);
			if (e.getAttributeValue("name").equalsIgnoreCase(name)) {
				ee = e;
				break;
			}
		}
		if (ee == null) {
			return new String[0][];
		}
		List cc = ee.getChildren(child);
		childCount = cc.size();
		String[][] childrenNames = new String[childCount][];
		for (int i = 0; i < childCount; ++i) {
			Element e = (Element) cc.get(i);
			int childSize = e.getChildren().size();
			childrenNames[i] = new String[childSize];
			for (int j = 0; j < childSize; ++j) {
				Element e1 = (Element) e.getChildren().get(j);
				childrenNames[i][j] = e1.getText();
			}
		}
		return childrenNames;
	}

	public String[][] getChildrenProperties(String parent) {
		String[] propName = parsePropertyName(parent);

		Element element = this.doc.getRootElement();
		for (int i = 0; i < propName.length; ++i) {
			element = element.getChild(propName[i]);
			if (element == null) {
				return new String[0][];
			}
		}

		List children = element.getChildren();
		int childCount = children.size();

		String[][] childrenNames = new String[childCount][];
		for (int i = 0; i < childCount; ++i) {
			Element e = (Element) children.get(i);
			int childSize = e.getChildren().size();
			childrenNames[i] = new String[childSize];
			for (int j = 0; j < childSize; ++j) {
				Element e1 = (Element) e.getChildren().get(j);
				childrenNames[i][j] = e1.getText();
			}
		}
		return childrenNames;
	}

	private String[] parsePropertyName(String name) {
		int size = 1;
		for (int i = 0; i < name.length(); ++i) {
			if (name.charAt(i) == '.') {
				++size;
			}
		}
		String[] propName = new String[size];

		StringTokenizer tokenizer = new StringTokenizer(name, ".");
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			propName[i] = tokenizer.nextToken();
			++i;
		}
		return propName;
	}

	public void setProperty(String name, String value) {
		String[] propName = parsePropertyName(name);

		Element element = this.doc.getRootElement();
		for (int i = 0; i < propName.length; ++i) {
			if (element.getChild(propName[i]) == null) {
				element.addContent(new Element(propName[i]));
			}
			element = element.getChild(propName[i]);
		}

		element.setText(value);
	}

	public void deleteProperty(String name) {
		String[] propName = parsePropertyName(name);

		Element element = this.doc.getRootElement();
		for (int i = 0; i < propName.length - 1; ++i) {
			element = element.getChild(propName[i]);

			if (element == null) {
				return;
			}
		}

		element.removeChild(propName[(propName.length - 1)]);
	}

	public synchronized void saveProperties() {
		OutputStream out = null;
		boolean error = false;

		File tempFile = null;
		try {
			tempFile = new File(this.file.getParentFile(), this.file.getName()
					+ ".tmp");

			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			out = new BufferedOutputStream(new FileOutputStream(tempFile));
			outputter.output(this.doc, out);
		} catch (Exception e) {
			log.fatal(e);

			error = true;
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				log.fatal(e);
				error = true;
			}
		}

		if (error)
			return;
		this.file.delete();

		tempFile.renameTo(this.file);
	}
}