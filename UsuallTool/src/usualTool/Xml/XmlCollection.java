package usualTool.Xml;

import org.dom4j.Node;

public class XmlCollection {
	private Node node;

	public XmlCollection(Node node){
		this.node = node;
	}
	
	public String getAttribute(String attribute){
		return this.node.valueOf("@" + attribute);
	}
	
	public String getValue(){
		return this.node.getText();
	}
	
}
