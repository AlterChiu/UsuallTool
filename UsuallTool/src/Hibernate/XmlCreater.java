package Hibernate;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.TreeMap;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlCreater {
	private FileWriter fw  ;

	public XmlCreater(FileWriter fw, String className, String property[], String column[], TreeMap<String, String> tr)
			throws Exception {
		this.fw = fw;
		Document doc = DocumentHelper.createDocument();
		// ------------------------------------------------------
		// �e�m�ʧ@�A�̻ݨD�i�ٲ��G
		/*
		 * doc.addDocType(<�ڤ���>,<���U//��´//��������//�w�q�y��>,<���������w�q��m>); �򥻤W <�ڤ���> �W�٭n�P
		 * doc.addElement() �ۦP�A�{���X�� 23 ��
		 */
		doc.addDocType("hibernate-mapping ", // �����o�����|�ŧi HTML
				"-//Hibernate/Hibernate Mapping DTD 3.0//EN",
				"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");
		// ------------------------------------------------------
		// �إ߮ڤ���(������)�G
		Element root = doc.addElement("hibernate-mapping"); // �إ� <�ۭqXML>
		// ------------------------------------------------------
		// ���e(�l����)�G
		Element classElement = root.addElement("class")
																		.addAttribute("name", tr.get("locate"))
																		.addAttribute("table", tr.get("table")); // �إ�<�Τ�>���ݩʡG
		
																// �W�l="ī�G"
		Element idElement = root.addElement("id")
																	.addAttribute("name", property[0])
																	.addAttribute("column", column[0]);
		
		Element generatorElement = idElement.addElement("generator")
																								   .addAttribute("class", "native");
		
		
		Element propertyElement[] = new Element[property.length - 1];
		for (int i = 1; i < property.length; i++) {
			propertyElement[i - 1] = root.addElement("property")
																				.addAttribute("name", property[i])
																				.addAttribute("column", column[i]);
		}
		prettyPrint(doc);
	}

	public  void  prettyPrint(Document xml) throws Exception {
	
		 OutputFormat format = OutputFormat.createPrettyPrint();
		 XMLWriter   writer = new XMLWriter( fw, format );
	        writer.write(xml);
	        writer.close();
	        fw.close();
	}

}
