package usualTool;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class tempt{

	private String nameSpeace = "//np:";
	private Document document;
	
	public tempt(String fileAdd) throws Exception {
		SAXReader reader = new SAXReader();
		this.document = reader.read(new File(fileAdd));
		String nameSpace = document.getRootElement().getNamespaceURI();
		
		Map<String,String> nameMap = new HashMap<String,String>();
		nameMap.put("np", nameSpace);
		reader.getDocumentFactory().setXPathNamespaceURIs(nameMap);
		
		this.document = reader.read(new File(fileAdd));
	}
	
	public List<Node> getNodes(String nodeName) {
		return this.document.selectNodes(this.nameSpeace + nodeName);	
	}
	
	public Node getNode(String nodeName) {
		return  this.document.selectSingleNode(this.nameSpeace + nodeName);	
	}
	
	public Element getRoot() {
		return this.document.getRootElement();
	}
}