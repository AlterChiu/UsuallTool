package usualTool;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class AtXmlReader{
	private String nameSpace = "//";
	private Document document;

	public AtXmlReader(String fileAdd) throws DocumentException {
		SAXReader reader = new SAXReader();

		Document document = reader.read(new File(fileAdd));

		Element root = document.getRootElement();
		Map<String, String> nameSpace = new HashMap<String, String>();
		nameSpace.put("np", root.getNamespaceURI());
		reader.getDocumentFactory().setXPathNamespaceURIs(nameSpace);
		this.nameSpace = this.nameSpace + "np:";

		this.document = reader.read(new File(fileAdd));
	}

	public List<Node> getNodes(String node) {
		return this.document.selectNodes(this.nameSpace + node);
	}

	public Element getRoot() {
		return this.document.getRootElement();
	}

}


