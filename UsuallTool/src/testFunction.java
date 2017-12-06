import java.io.File;
import java.io.IOException;

import javax.naming.OperationNotSupportedException;

import nl.wldelft.fews.pi.PiTimeSeriesModifierParser;
import nl.wldelft.fews.pi.PiTimeSeriesParser;
import nl.wldelft.fews.pi.PiTimeSeriesReader;
import nl.wldelft.util.timeseries.TimeSeriesArrays;
import tw.ntut.ce.util.pi.XMLReader;


@SuppressWarnings("deprecation")
public  class testFunction extends XMLReader{
	
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
