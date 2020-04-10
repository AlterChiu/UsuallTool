package FEWS.SOBEK.FileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SACRMNTO_3B {
	private List<String> content = new ArrayList<>();
	private List<SCS> scsList = new ArrayList<>();
	private static String[] attrKey1 = new String[] { "id", "ar", "slp", "le", "cn", "ms", "uh", "amc" };
	private static String[] attrKey2 = new String[] { "id", "nm", "ar", "slp", "le", "cn", "uh", "tl", "ms", "aaf",
			"amc" };

	// <=====================================>
	// <======== CONTRUCTOR ===================>
	// <=====================================>
	public SACRMNTO_3B(String fileAdd, String encode) throws IOException {
		if (new File(fileAdd).exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileAdd), encode));
			this.content = SOBEKREADER.readFile(br);
			initialize();
		} else {
			new Exception("No such file " + fileAdd);
		}
	}

	public SACRMNTO_3B(String fileAdd) throws IOException {
		if (new File(fileAdd).exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileAdd)));
			this.content = SOBEKREADER.readFile(br);
			initialize();
		} else {
			new Exception("No such file " + fileAdd);
		}
	}

	// make stuct.date to several "STDS" class
	private void initialize() {
		List<List<String>> scsContentList = SOBEKREADER.getTAGConent(this.content, "SCS");
		for (List<String> scsContent : scsContentList) {
			this.scsList.add(new SCS(scsContent));
		}
	}

	// <=================================================>
	/*
	 * 
	 * 
	 * 
	 */
	// <=====================================>
	// <======== USER FUNCTIONS =================>
	// <=====================================>

	public List<SCS> getSCSs() {
		return this.scsList;
	}

	@Override
	public String toString() {
		return String.join("\r\n",
				this.scsList.parallelStream().map(scs -> scs.toString()).collect(Collectors.toList()));
	}

	// <=================================================>
	/*
	 * 
	 * 
	 * 
	 * 
	 */
	// <=====================================>
	// <======== NEST STRUCTUR ==================>
	// <=====================================>

	public class SCS {
		private String id = null;
		private String rainfallStationID = null;
		private Map<String, String> attrMap = new TreeMap<>();

		public SCS(List<String> scsContent) {
			for (int index = 1; index < scsContent.size() - 1; index = index + 2) {
				attrMap.put(scsContent.get(index), scsContent.get(index + 1));
			}
		}

		public void setScsID(String scsID) {
			this.id = scsID;
			this.id = this.id.replace("'", "");
			this.id = "'" + this.id + "'";
			this.attrMap.put("id", this.id);
		}

		public void setRainfallStationID(String rainfallStationID) {
			this.rainfallStationID = rainfallStationID;
			this.rainfallStationID = this.rainfallStationID.replace("'", "");
			this.rainfallStationID = "'" + this.rainfallStationID + "'";
			this.attrMap.put("ms", this.rainfallStationID);
		}

		public String getScsID() {
			return this.attrMap.get("id").replace("'", "");
		}

		public String getRainfallStationID() {
			return this.attrMap.get("ms").replace("'", "");
		}

		@Override
		public String toString() {
			List<String> outString = new ArrayList<>();
			outString.add("SCS");

			if (this.attrMap.containsKey("nm")) {
				Arrays.asList(attrKey2).forEach(key -> {
					outString.add(key);
					outString.add(this.attrMap.get(key));
				});
			} else {
				Arrays.asList(attrKey1).forEach(key -> {
					outString.add(key);
					outString.add(this.attrMap.get(key));
				});
			}
			outString.add("scs");
			return String.join(" ", outString);
		}
	}
}
