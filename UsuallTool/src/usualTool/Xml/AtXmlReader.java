package usualTool.Xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

public class AtXmlReader {
	private File xmlFile;
	private String finderCommand;
	private Map uris;
	private Document xmlDocument;
	
	
	
//	<================>
//	<this is the construction>
//	<================>
	public AtXmlReader(String fileAdd) throws IOException, DocumentException {
		this.xmlFile = new File(fileAdd);
		settingSchemaLocation();
		settingXmlDocumrnt();
	}

	public AtXmlReader(File file) throws IOException, DocumentException {
		this.xmlFile = file;
		settingSchemaLocation();
		settingXmlDocumrnt();
	}
	
	
	public XmlCollections getCollections(){
		XPath xpath = this.xmlDocument.createXPath(this.finderCommand);
		xpath.setNamespaceURIs(this.uris);
		return new XmlCollections(xpath.selectNodes(this.xmlDocument));
	}
	
	
	
	
	
//	<setting the path of target by user>
//	<____________________________________________________>
	public AtXmlReader setPath(String tag) {
		this.finderCommand = this.finderCommand + "\tempt:" + tag;
		return this;
	}

	public AtXmlReader clearPath() {
		this.finderCommand = "";
		return this;
	}

	
	
	
	
	
	
	
	
	
	
//	<basic setting of any XMLFile>
//	<_________________________________________________________>
	private void settingSchemaLocation() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(this.xmlFile));

		String temptLine;
		String sehemaLocation=null;
		while ((temptLine = br.readLine()) != null) {
			if (temptLine.contains("xsi:schemaLocation")) {
				sehemaLocation = temptLine.substring(temptLine.indexOf("xsi:schemaLocation"), temptLine.length());
				break;
			}
		}
		br.close();
		String[] temptArray = sehemaLocation.trim().split("\"");
		this.uris.put("tempt" , temptArray[1].split(" ")[0]); 
	}
	
	private void settingXmlDocumrnt() throws DocumentException{
		this.xmlDocument = new SAXReader().read(this.xmlFile);
	}

}
