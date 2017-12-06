package usualTool.Xml;

import java.util.List;

import org.dom4j.Node;

public class XmlCollections {
	private List<Node> nodes;
	
	public XmlCollections(List<Node> nodes){
		this.nodes = nodes;
	}
	
	public XmlCollection getXmlCollection(int order){
		return new XmlCollection(this.nodes.get(order));
	}
	public int getSize(){
		return this.nodes.size();
	}

}
