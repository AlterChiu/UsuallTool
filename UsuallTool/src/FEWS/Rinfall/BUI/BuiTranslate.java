package FEWS.Rinfall.BUI;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import FEWS.PIXml.AtPiXmlReader;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import usualTool.AtFileReader;
import usualTool.TimeInterface;

public class BuiTranslate implements TimeInterface {
	private String fileAdd;
	private TreeMap<String, String> idMapping = null;
	private ArrayList<TimeSeriesArray> timeSeriesArrays;
	private long startDate;
	private int timeStepMultiplier;
	private long endDate;
	private double valueTimes = 1;

	// <===============>
	// <this is the constructor >
	// <===============>
	public BuiTranslate(String fileAdd) throws OperationNotSupportedException, IOException {
		this.fileAdd = fileAdd;
	}

	public BuiTranslate setValueTimes(double times) {
		this.valueTimes = times;
		return this;
	}

	public String[] getBuiRainfall() throws OperationNotSupportedException, IOException {
		this.timeSeriesArrays = new AtPiXmlReader().getTimeSeriesArrays(new File(this.fileAdd));
		// <basic outPut setting>
		// <___________________________________________________________________>
		ArrayList<String> outArray = new ArrayList<String>();

		// <Starting setting BUI>
		// <____________________________________________________>
		outArray.add("*");
		outArray.add("*");
		outArray.add("*Enige algemene wenken:");
		outArray.add("*Gebruik de default dataset (1) of de volledige reeks (0) voor overige invoer");
		outArray.add("1");
		outArray.add("*Aantal stations");
		outArray.add(timeSeriesArrays.size() + "");

		// <checking idMapping is null or not>
		// <_____________________________________________________________________>
		outArray.add("*Namen van stations");
		if (this.idMapping == null) {
			this.timeSeriesArrays.stream().forEach(
					timeSeriesArray -> outArray.add("\'" + timeSeriesArray.getHeader().getLocationId() + "\'"));
		} else {
			this.timeSeriesArrays.stream().forEach(timeSeriesArray -> outArray
					.add("\'" + idMapping.get(timeSeriesArray.getHeader().getLocationId()) + "\'"));
		}

		// <translate to the bui format>
		// <____________________________________________________________________________>
		outArray.add("*Aantal gebeurtenissen (omdat het 1 bui betreft is dit altijd 1)");
		outArray.add("*en het aantal seconden per waarnemingstijdstap");
		TimeSeriesArray firstTimeSeriesArray = this.timeSeriesArrays.get(0);
		outArray.add(" 1 " + firstTimeSeriesArray.getTimeStep().getMaximumStepMillis() / 1000);

		outArray.add("*Elke commentaarregel wordt begonnen met een * (asteriks).");
		outArray.add("*Eerste record bevat startdatum en -tijd, lengte van de gebeurtenis in dd hh mm ss");
		outArray.add("*Het format is: yyyymmdd:hhmmss:ddhhmmss");
		outArray.add("*Daarna voor elk station de neerslag in mm per tijdstap.");

		outArray.add(
				TimeInterface.milliToDate(firstTimeSeriesArray.getStartTime(), " yyyy MM dd HH mm ss") + TimeInterface
						.milliToTime(firstTimeSeriesArray.getTimeStep().getStepMillis() * firstTimeSeriesArray.size(),
								" dd HH mm ss"));

		for (int event = 0; event < firstTimeSeriesArray.size(); event++) {
			ArrayList<String> temptValue = new ArrayList<String>();
			for (TimeSeriesArray timeSeries : this.timeSeriesArrays) {
				if ("NaN".equals(timeSeries.getValue(event) + "")) {
					temptValue.add("0.00");
				} else {
					temptValue.add(new BigDecimal(timeSeries.getValue(event) * this.valueTimes)
							.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				}
			}
			;
			outArray.add(String.join(" ", temptValue));
		}
		return outArray.parallelStream().toArray(String[]::new);
	}

	public String[] getBuiRainfall_Fill(String fill, int times) throws OperationNotSupportedException, IOException {
		this.timeSeriesArrays = new AtPiXmlReader().getTimeSeriesArrays(new File(this.fileAdd));
		// <basic outPut setting>
		// <___________________________________________________________________>
		ArrayList<String> outArray = new ArrayList<String>();

		// <Starting setting BUI>
		// <____________________________________________________>
		outArray.add("*");
		outArray.add("*");
		outArray.add("*Enige algemene wenken:");
		outArray.add("*Gebruik de default dataset (1) of de volledige reeks (0) voor overige invoer");
		outArray.add("1");
		outArray.add("*Aantal stations");
		outArray.add(timeSeriesArrays.size() + "");

		// <checking idMapping is null or not>
		// <_____________________________________________________________________>
		outArray.add("*Namen van stations");
		if (this.idMapping == null) {
			this.timeSeriesArrays.stream().forEach(
					timeSeriesArray -> outArray.add("\'" + timeSeriesArray.getHeader().getLocationId() + "\'"));
		} else {
			this.timeSeriesArrays.stream().forEach(timeSeriesArray -> outArray
					.add("\'" + idMapping.get(timeSeriesArray.getHeader().getLocationId()) + "\'"));
		}

		// <translate to the bui format>
		// <____________________________________________________________________________>
		outArray.add("*Aantal gebeurtenissen (omdat het 1 bui betreft is dit altijd 1)");
		outArray.add("*en het aantal seconden per waarnemingstijdstap");
		TimeSeriesArray firstTimeSeriesArray = this.timeSeriesArrays.get(0);
		outArray.add(" 1 " + firstTimeSeriesArray.getTimeStep().getMaximumStepMillis() / 1000);

		outArray.add("*Elke commentaarregel wordt begonnen met een * (asteriks).");
		outArray.add("*Eerste record bevat startdatum en -tijd, lengte van de gebeurtenis in dd hh mm ss");
		outArray.add("*Het format is: yyyymmdd:hhmmss:ddhhmmss");
		outArray.add("*Daarna voor elk station de neerslag in mm per tijdstap.");

		double previousTime = (firstTimeSeriesArray.getTimeStep().getStepMillis()) * times;

		outArray.add(TimeInterface.milliToDate(firstTimeSeriesArray.getStartTime() - (long) previousTime,
				" yyyy MM dd HH mm ss")
				+ TimeInterface
						.milliToTime(firstTimeSeriesArray.getTimeStep().getStepMillis() * firstTimeSeriesArray.size()
								+ (long) previousTime, " dd HH mm ss"));

		for (int event = 0; event < firstTimeSeriesArray.size() + times; event++) {
			ArrayList<String> temptValue = new ArrayList<String>();
			for (TimeSeriesArray timeSeries : this.timeSeriesArrays) {
				try {
					if ("NaN".equals(timeSeries.getValue(event - times) + "")) {
						temptValue.add("0.00");
					} else {
						temptValue.add(timeSeries.getValue(event - times) * this.valueTimes + "");
					}
				} catch (Exception e) {
					temptValue.add(0, fill);
				}
			}

			outArray.add(String.join(" ", temptValue));
		}

		return outArray.parallelStream().toArray(String[]::new);
	}

	public String getPiXMLRainfall() throws IOException, ParseException {
		AtFileReader buiFIle = new AtFileReader(this.fileAdd);
		String[] content = buiFIle.getContainWithOut("*");
		int stationSize = Integer.parseInt(content[1].trim());

		// station ID
		ArrayList<String> stationId = new ArrayList<String>();
		Arrays.asList(buiFIle.getContainWith("'")).stream()
				.forEach(name -> stationId.add(name.trim().substring(1, name.length() - 1)));

		// timeStep
		String[] timeStepSetting = content[2 + stationSize].trim().split(" +");
		this.timeStepMultiplier = Integer.parseInt(timeStepSetting[1]) / Integer.parseInt(timeStepSetting[0]);

		// start time
		String[] startTimeSetting = content[3 + stationSize].trim().split(" +");
		this.startDate = TimeInterface
				.StringToLong(
						startTimeSetting[0] + " " + startTimeSetting[1] + " " + startTimeSetting[2] + " "
								+ startTimeSetting[3] + " " + startTimeSetting[4] + " " + startTimeSetting[5],
						"yyyy MM dd HH mm ss");

		// rainfallValue
		ArrayList<String[]> rainfallValue = new ArrayList<String[]>();
		for (int line = 4 + stationSize; line < content.length; line++) {
			rainfallValue.add(content[line].trim().split(" +"));
		}

		// end time
		this.endDate = this.startDate + this.timeStepMultiplier * 1000 * rainfallValue.size();

		// Starting Writing
		// <______________________________________________________>
		Document doc = DocumentHelper.createDocument();
		Element timeSeries = doc.addElement("TimeSeries");
		timeSeries.addAttribute("xmlns", "http://www.wldelft.nl/fews/PI");
		timeSeries.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		timeSeries.addAttribute("xsi:schemaLocation",
				"http://www.wldelft.nl/fews/PI http://fews.wldelft.nl/schemas/version1.0/pi-schemas/pi_timeseries.xsd");
		timeSeries.addAttribute("version", "1.23");
		timeSeries.addAttribute("xmlns:fs", "http://www.wldelft.nl/fews/fs");
		timeSeries.addElement("timeZone").addText("8.0");

		for (int order = 0; order < stationId.size(); order++) {
			Element series = timeSeries.addElement("series");
			Element header = series.addElement("header");
			header.addElement("type").addText("accumulative");
			header.addElement("locationId").addText(stationId.get(order));
			header.addElement("parameterId").addText("P.obs");
			header.addElement("timeStep").addAttribute("unit", "second").addAttribute("multiplier",
					this.timeStepMultiplier + "");
			header.addElement("startDate").addAttribute("date", TimeInterface.milliToDate(this.startDate, "yyyy-MM-dd"))
					.addAttribute("time", TimeInterface.milliToDate(this.startDate, "HH:mm:ss"));
			header.addElement("endDate").addAttribute("date", TimeInterface.milliToDate(this.endDate, "yyyy-MM-dd"))
					.addAttribute("time", TimeInterface.milliToDate(this.endDate, "HH:mm:ss"));
			header.addElement("missVal").addText("-999.99");
			header.addElement("stationName").addText(stationId.get(order));
			header.addElement("units").addText("mm");
			header.addElement("creationDate").addText("2017-01-01");
			header.addElement("creationTime").addText("00:00:00");

			for (int time = 0; time < rainfallValue.size(); time++) {
				series.addElement("event")
						.addAttribute("date",
								TimeInterface.milliToDate(this.startDate + time * this.timeStepMultiplier * 1000,
										"yyyy-MM-dd"))
						.addAttribute("time",
								TimeInterface.milliToDate(this.startDate + time * this.timeStepMultiplier * 1000,
										"HH:mm:ss"))
						.addAttribute("value", rainfallValue.get(time)[order]).addAttribute("flag", "2");

			}
		}

		return doc.asXML();
	}
}
