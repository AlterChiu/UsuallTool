package usualTool;

import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class AtXmlReader {
	private String nameSpace = "//";
	private Document document;

	// <+++++++++++++++++++++++++++++++++++++++>
	// <+++++++++++++ Constructor +++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++>
	public AtXmlReader(File xmlFile) throws DocumentException {
		SAXReader reader = new SAXReader();
		this.document = reader.read(xmlFile);

		Element root = this.document.getRootElement();
		Map<String, String> nameSpace = new HashMap<String, String>();
		nameSpace.put("np", root.getNamespaceURI());
		reader.getDocumentFactory().setXPathNamespaceURIs(nameSpace);
		this.nameSpace = this.nameSpace + "np:";

		this.document = reader.read(xmlFile);
	}

	public AtXmlReader(String[] stringContent) throws DocumentException {
		StringBuilder sb = new StringBuilder();
		for (String temptLine : stringContent) {
			sb.append(temptLine);
		}
		stringConvertToDocument(sb.toString());
	}

	public AtXmlReader(String stringContent) throws DocumentException {
		stringConvertToDocument(stringContent);
	}

	private void stringConvertToDocument(String stringContent) throws DocumentException {
		this.document = DocumentHelper.parseText(stringContent);
		SAXReader reader = new SAXReader();

		Element root = this.document.getRootElement();
		Map<String, String> nameSpace = new HashMap<String, String>();
		nameSpace.put("np", root.getNamespaceURI());
		reader.getDocumentFactory().setXPathNamespaceURIs(nameSpace);
		this.nameSpace = this.nameSpace + "np:";

		this.document = reader.read(new StringReader(stringContent));
	}
	// <++++++++++++++++++++++++++++++++++++++++++++++>

	public List<Node> getNodes(String node) {
		return this.document.selectNodes(this.nameSpace + node);
	}

	public Element getRoot() {
		return this.document.getRootElement();
	}

}
