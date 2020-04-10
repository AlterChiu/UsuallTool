package FEWS.SOBEK.FileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class STRUCT_DAT {
	private List<String> content = new ArrayList<>();
	private List<STRU> struList = new ArrayList<>();
	private static String[] attrKey = new String[] { "id", "nm", "dd" };

	// <=====================================>
	// <======== CONTRUCTOR ===================>
	// <=====================================>
	public STRUCT_DAT(String fileAdd, String encode) throws IOException {
		if (new File(fileAdd).exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileAdd), encode));
			this.content = SOBEKREADER.readFile(br);
			initialize();
		} else {
			new Exception("No such file " + fileAdd);
		}
	}

	public STRUCT_DAT(String fileAdd) throws IOException {
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
		List<List<String>> struContentList = SOBEKREADER.getTAGConent(this.content, "STRU");
		struContentList.forEach(e -> this.struList.add(new STRU(e)));
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

	public List<STRU> getSTRUs() {
		return this.struList;
	}

	public String toString() {
		return String.join("\r\n",
				this.struList.parallelStream().map(stru -> stru.toString()).collect(Collectors.toList()));
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

	public class STRU {
		private Map<String, String> attrMap = new TreeMap<>();

		public STRU(List<String> content) {
			int index = 1;
			while (!content.get(index).equals("stru")) {
				attrMap.put(content.get(index), content.get(index + 1));
				index = index + 2;
			}
		}

		public void setID(String id) {
			this.attrMap.put("id", id);
		}

		public String getID() {
			return this.attrMap.get("id");
		}

		public void setNM(String nm) {
			this.attrMap.put("nm", nm);
		}

		public String getNM() {
			return this.attrMap.get("nm");
		}

		public void setDD(String dd) {
			this.attrMap.put("dd", dd);
		}

		public String getDD() {
			return this.getDD();
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("STRU");
			for (String key : STRUCT_DAT.attrKey) {
				sb.append(" " + key + " " + this.attrMap.get(key));
			}
			sb.append(" stru");
			return sb.toString();
		}
	}

}