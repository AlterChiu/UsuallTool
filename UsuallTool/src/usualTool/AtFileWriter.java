
package usualTool;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class AtFileWriter {
	private String[][] temptDoubleArray = null;
	private String[] temptArray = null;
	private String fileAdd;

	private String encode = "UTF-8";
	public static String ANSI = "Cp1252";
	public static String Unicode = "Unicode";
	public static String UTF8 = "UTF-8";
	public static String BIG5 = "big5";
	public static String ASCII = "ASCII";

	public AtFileWriter(String[][] content, String fileAdd) throws IOException {
		this.fileAdd = fileAdd;
		this.temptDoubleArray = content;

	}

	public AtFileWriter(String[] content, String fileAdd) throws IOException {
		this.fileAdd = fileAdd;
		this.temptArray = content;
	}

	public AtFileWriter(String content, String fileAdd) throws IOException {
		this.fileAdd = fileAdd;
		this.temptArray = new String[] { content, "" };
	}

	public AtFileWriter(JsonObject json, String fileAdd) throws IOException {
		this.fileAdd = fileAdd;
		Gson jsonWriter = new GsonBuilder().setPrettyPrinting().create();
		this.temptArray = new String[] { jsonWriter.toJson(json), "" };
	}

	public AtFileWriter(JsonObject json, String fileAdd, Boolean prettyPrint) throws IOException {
		this.fileAdd = fileAdd;
		if (prettyPrint) {
			Gson jsonWriter = new GsonBuilder().setPrettyPrinting().create();
			this.temptArray = new String[] { jsonWriter.toJson(json), "" };
		} else {
			Gson jsonWriter = new GsonBuilder().create();
			this.temptArray = new String[] { jsonWriter.toJson(json), "" };
		}
	}

	public void csvWriter() throws IOException {
		wirteFIle(",");
	}

	public void textWriter(String split) throws IOException {
		wirteFIle(split);
	}

	public void tabWriter() throws IOException {
		wirteFIle("\t");
	}

	private void wirteFIle(String split) throws IOException {
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.fileAdd), this.encode));

		if (temptDoubleArray != null) {
			for (int line = 0; line < this.temptDoubleArray.length; line++) {
				List<String> temptLine = new ArrayList<>();
				for (int column = 0; column < this.temptDoubleArray[line].length; column++) {
					temptLine.add(this.temptDoubleArray[line][column]);
				}
				writer.write(String.join(split, temptLine) + "\r\n");
			}

		} else if (temptArray != null) {
			for (int i = 0; i < this.temptArray.length; i++) {
				writer.write(temptArray[i] + "\r\n");
			}
		}
		writer.close();
	}

	public AtFileWriter setEncoding(String encode) {
		this.encode = encode;
		return this;
	}

}
