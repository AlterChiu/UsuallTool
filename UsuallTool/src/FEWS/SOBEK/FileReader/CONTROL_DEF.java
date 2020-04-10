package FEWS.SOBEK.FileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import usualTool.TimeTranslate;

public class CONTROL_DEF {
	private List<String> content = new ArrayList<>();
	private List<CNTL> cntlList = new ArrayList<>();

	// <=====================================>
	// <======== CONTRUCTOR ===================>
	// <=====================================>
	public CONTROL_DEF(String fileAdd, String encode) throws IOException {
		if (new File(fileAdd).exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileAdd), encode));
			this.content = SOBEKREADER.readFile(br);
			initialize();
		} else {
			new Exception("No such file " + fileAdd);
		}
	}

	public CONTROL_DEF(String fileAdd) throws IOException {
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
		List<List<String>> cntlContentList = SOBEKREADER.getTAGConent(this.content, "CNTL");
		cntlContentList.forEach(e -> this.cntlList.add(new CNTL(e)));
	}

	// <=================================================>
	// <=================================================>
	/*
	 * 
	 * 
	 * 
	 */
	// <=====================================>
	// <======== USER FUNCTIONS =================>
	// <=====================================>

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
	public class CNTL {
		private Map<String, String> attrMap = new HashMap<>();
		private TBLE table = null;

		public CNTL(List<String> content) {

		}

	}

	public class TBLE {
		private String timeFormat = "yyyy/MM/dd;HH:mm:ss";
		private Map<String, String> valuesMap = new TreeMap<>();

		public TBLE(List<String> content) {
			// skip TBLE tble tag
			for (int index = 1; index < content.size() - 1; index = index + 3) {
				String time = content.get(index).replace("\'", ""); 
				String value = content.get(index + 1);
				this.valuesMap.put(time, value);
			}
		}

		public void addValue(String time, String value) {
			try {
				TimeTranslate.getDateLong(time, timeFormat);
				Double.parseDouble(value);
				this.valuesMap.put(time, value);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void removeValue(String time) {
			if (this.valuesMap.containsKey(time)) {
				this.valuesMap.remove(time);
			}
		}

		public Map<String, String> getValuesMap() {
			return this.valuesMap;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("TBLE");
			for (String key : this.valuesMap.keySet()) {
				sb.append(" \'" + key + "\' ");
				sb.append(this.valuesMap.get(key));
				sb.append(" <\r\n");
			}
			sb.append("tble");
			return sb.toString();
		}
	}
}