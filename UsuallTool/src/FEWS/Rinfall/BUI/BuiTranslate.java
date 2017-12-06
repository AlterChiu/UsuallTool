package FEWS.Rinfall.BUI;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.dom4j.DocumentException;

public class BuiTranslate implements IDMapping{
	private File PiXmlFile;
	private TreeMap<String,String>idMapping = null;
	
	public BuiTranslate(String fileAdd){
		this.PiXmlFile = new File(fileAdd);
	}
	
	public BuiTranslate(File file){
		this.PiXmlFile = file;
	}
	
	public void setIDMapping(String fileAdd) throws OperationNotSupportedException, IOException, DocumentException{
		this.idMapping = IDMapping.getIDMapping(fileAdd);
	}
	
	

}
