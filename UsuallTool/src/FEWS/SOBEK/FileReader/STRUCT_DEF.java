package FEWS.SOBEK.FileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class STRUCT_DEF {
	private List<String> content = new ArrayList<>();
	private List<STDS> stdsList = new ArrayList<>();
	private static String[] ty6 = new String[] { "id", "nm", "ty", "cl", "cw", "ce", "sc", "rt" };
	private static String[] ty7 = new String[] { "id", "nm", "ty", "cl", "cw", "gh", "mu", "sc", "rt", "mp", "mn" };
	private static String[] ty9 = new String[] { "id", "nm", "ty", "dn", "rt cr", "ct lt", "TBLE" };
	private static String[] ty10 = new String[] { "id", "nm", "ty", "tc", "ll", "rl", "dl", "si", "li", "lo", "ov",
			"tv", "rt" };
	private static String[] ty12 = new String[] { "id", "nm", "ty", "tb", "si", "pw", "vf", "li", "lo", "dl", "rl",
			"rt" };

	// <=====================================>
	// <======== CONTRUCTOR ===================>
	// <=====================================>
	public STRUCT_DEF(String fileAdd, String encode) throws IOException {
		if (new File(fileAdd).exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileAdd), encode));
			this.content = SOBEKREADER.readFile(br);
			initialize();
		} else {
			new Exception("No such file " + fileAdd);
		}
	}

	public STRUCT_DEF(String fileAdd) throws IOException {
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
		List<List<String>> stdContentList = SOBEKREADER.getTAGConent(this.content, "STDS");
		for (List<String> stdContent : stdContentList) {
			this.stdsList.add(new STDS(stdContent));
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

	public List<STDS> getSTDSs() {
		return this.stdsList;
	}

	@Override
	public String toString() {
		return String.join("\r\n",
				this.stdsList.parallelStream().map(std -> std.toString()).collect(Collectors.toList()));
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
	public class STDS {

		// contains one value
		private String id = null;
		private String nm = null;
		private String ty = null;
		private TABLE table = null;

		private Map<String, String> attrMap = new TreeMap<>();

		// ty element

		public STDS(List<String> content) {

			// skip the first items, it must be "STDS"
			int index = 1;
			while (!content.get(index).equals("stds")) {
				switch (content.get(index)) {

				case "mp": // contains two values
					this.attrMap.put("mp", content.get(index + 1) + " " + content.get(index + 2));
					index = index + 3;
					break;

				case "mn": // contains two values
					this.attrMap.put("mn", content.get(index + 1) + " " + content.get(index + 2));
					index = index + 3;
					break;

				case "cr": // contains tree values
					this.attrMap.put("cr",
							content.get(index + 1) + " " + content.get(index + 2) + " " + content.get(index + 3));
					index = index + 4;
					break;

				case "TBLE": // another nest construct
					List<List<String>> tbleContents = SOBEKREADER.getTAGConent(content, "TBLE");
					this.table = new TABLE(tbleContents.get(0));
					this.attrMap.put("TBLE", this.table.toString());

					// switch index to "tble"
					while (!content.get(index).equals("tble")) {
						index++;
					}
					index++;
					break;

				case "rt":
					if (content.get(index + 1).equals("cr")) { // three values
						this.attrMap.put("rt cr",
								content.get(index + 2) + " " + content.get(index + 3) + " " + content.get(index + 4));
						index = index + 5;
					} else {
						this.attrMap.put(content.get(index), content.get(index + 1));
						index = index + 2;
					}
					break;

				case "ct":
					if (content.get(index + 1).equals("lt")) {
						this.attrMap.put("ct lt", content.get(index + 2));
						index = index + 3;
					} else {
						this.attrMap.put(content.get(index), content.get(index + 1));
						index = index + 2;
					}
					break;

				default:
					this.attrMap.put(content.get(index), content.get(index + 1));
					index = index + 2;
				}

			}

			// initial
			if (this.attrMap.containsKey("id")) {
				this.id = this.attrMap.get("id");
			} else {
				new Exception("missing nessary tag \"id\"");
			}

			if (this.attrMap.containsKey("nm")) {
				this.id = this.attrMap.get("nm");
			} else {
				new Exception("missing nessary tag \"nm\"");
			}

			if (this.attrMap.containsKey("ty")) {
				this.id = this.attrMap.get("ty");
			} else {
				new Exception("missing nessary tag \"ty\"");
			}
		}

		public void setID(String id) {
			this.id = id;
		}

		public void setNM(String nm) {
			this.nm = nm;
		}

		public void setTY(String ty) {
			this.ty = ty;
		}

		public String getID() {
			return this.id;
		}

		public String getNM() {
			return this.nm;
		}

		public String getTY() {
			return this.ty;
		}

		public void setMap(Map<String, String> attrMap) {
			this.attrMap = attrMap;
		}

		public void setTABLE(TABLE table) {
			this.table = table;
		}

		public TABLE getTable() {
			return this.table;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("STDS");
			switch (Integer.parseInt(this.ty)) {
			case 6:
				for (String key : STRUCT_DEF.ty6) {
					sb.append(" " + key + " " + this.attrMap.get(key));
				}
				break;

			case 7:
				for (String key : STRUCT_DEF.ty7) {
					sb.append(" " + key + " " + this.attrMap.get(key));
				}
				break;

			case 9:
				for (String key : STRUCT_DEF.ty9) {
					if (key.equals("TBLE")) {
						sb.delete(sb.length() - 1, sb.length());
						sb.append("\r\n" + this.attrMap.get(key));
					} else {
						sb.append(" " + key + " " + this.attrMap.get(key));
					}
				}
				break;

			case 10:
				for (String key : STRUCT_DEF.ty10) {
					sb.append(" " + key + " " + this.attrMap.get(key));
				}
				break;

			case 12:
				for (String key : STRUCT_DEF.ty12) {
					sb.append(" " + key + " " + this.attrMap.get(key));
				}
				break;

			default:
				new Exception("no such \"ty\" tag in this version, you should update");
			}

			sb.append(" stds");
			return sb.toString();
		}
	}

	public class TABLE {
		private String values = null;

		public TABLE(List<String> content) {
			content.remove(0);
			content.remove(content.size() - 1);
			this.values = String.join(" ", content);
		}

		public void setValues(String values) {
			this.values = values;
		}

		public String getValues() {
			return this.values;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("TBLE\r\n");
			sb.append(this.getValues());
			sb.append("\r\ntble");
			return sb.toString();
		}
	}

// <=================================================>
}
