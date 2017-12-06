package FEWS.PIXml;

import java.io.File;
import java.io.IOException;

import javax.naming.OperationNotSupportedException;

import nl.wldelft.util.timeseries.TimeSeriesArrays;
import tw.ntut.ce.util.pi.XMLReader;

public class AtPiXmlReader extends XMLReader{

	public TimeSeriesArrays getTimeSeriesArrays(File file) throws OperationNotSupportedException, IOException{
		return this.readPiTimeSeries(file);
	}

	public TimeSeriesArrays getTimeSeriesArrays(String pathName) throws OperationNotSupportedException, IOException{
		return this.readPiTimeSeries(new File(pathName));
	}
	
	
	@Override
	public void run(String[] arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
