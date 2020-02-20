package FEWS.PIXml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import nl.wldelft.fews.pi.PiTimeSeriesReader;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import nl.wldelft.util.timeseries.TimeSeriesArrays;

public class AtPiXmlReader {

	public List<TimeSeriesArray> getTimeSeriesArrays(File file) throws OperationNotSupportedException, IOException {
		TimeSeriesArrays timeSeriesArrays = new PiTimeSeriesReader(file).read();
		List<TimeSeriesArray> timeSeriesArraysList = new ArrayList<TimeSeriesArray>();
		for (int order = 0; order < timeSeriesArrays.size(); order++) {
			timeSeriesArraysList.add(timeSeriesArrays.get(order));
		}
		return timeSeriesArraysList;
	}

	public List<TimeSeriesArray> getTimeSeriesArrays(String pathName)
			throws OperationNotSupportedException, IOException {
		return getTimeSeriesArrays(new File(pathName));
	}

}
