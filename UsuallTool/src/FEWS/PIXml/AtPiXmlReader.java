package FEWS.PIXml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

import nl.wldelft.util.timeseries.TimeSeriesArray;
import nl.wldelft.util.timeseries.TimeSeriesArrays;
import tw.ntut.ce.util.pi.XMLReader;

public class AtPiXmlReader extends XMLReader{

	public ArrayList<TimeSeriesArray> getTimeSeriesArrays(File file) throws OperationNotSupportedException, IOException{
		TimeSeriesArrays timeSeriesArrays = this.readPiTimeSeries(file);
		ArrayList<TimeSeriesArray> timeSeriesArraysList = new ArrayList<TimeSeriesArray>();
		for(int order=0;order<timeSeriesArrays.size();order++){
			timeSeriesArraysList.add(timeSeriesArrays.get(order));
		}
		return timeSeriesArraysList;
	}

	
	public ArrayList<TimeSeriesArray> getTimeSeriesArrays(String pathName) throws OperationNotSupportedException, IOException{
		TimeSeriesArrays timeSeriesArrays = this.readPiTimeSeries(new File(pathName));
		ArrayList<TimeSeriesArray> timeSeriesArraysList = new ArrayList<TimeSeriesArray>();
		for(int order=0;order<timeSeriesArrays.size();order++){
			timeSeriesArraysList.add(timeSeriesArrays.get(order));
		}
		return timeSeriesArraysList;
	}
	
	
	@Override
	public void run(String[] arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
