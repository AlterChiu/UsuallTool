package FEWS.Rinfall.BUI;

import java.io.IOException;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.dom4j.DocumentException;

import FEWS.PIXml.AtPiXmlReader;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import nl.wldelft.util.timeseries.TimeSeriesArrays;
import usualTool.Xml.AtXmlReader;
import usualTool.Xml.XmlCollection;
import usualTool.Xml.XmlCollections;

public interface IDMapping {
	
	public static TreeMap<String,String> getIDMapping(String idMapFile) throws OperationNotSupportedException, IOException, DocumentException{
		TreeMap<String , String> temptTree = new TreeMap<String,String>();
		AtXmlReader xmlReader = new AtXmlReader(idMapFile);
		xmlReader.setPath("idMap");
		xmlReader.setPath("parameter");
	
		XmlCollections xmlCollections = xmlReader.getCollections();
		for(int order=0;order<xmlCollections.getSize();order++){
			XmlCollection temptCollection = xmlCollections.getXmlCollection(order);
			temptTree.put(temptCollection.getAttribute("internal"), temptCollection.getAttribute("external"));
		}
		
		return temptTree;
	}

}
