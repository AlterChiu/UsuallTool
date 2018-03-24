package usualTool;

import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class AtFileWriter {
	private String[][] temptDoubleArray = null;
	private String[] temptArray = null;
	private FileWriter fw;
	private String fileAdd;
	private Document doc;

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

	public AtFileWriter(Document doc, String fileAdd) throws IOException {
		this.fileAdd = fileAdd;
		this.doc = doc;
	}

	public void csvWriter() throws IOException {
		this.fw = new FileWriter(fileAdd);
		wirteFIle(",");
	}

	public void textWriter(String split) throws IOException {
		this.fw = new FileWriter(fileAdd);
		wirteFIle(split);
	}

	public void tabWriter() throws IOException {
		this.fw = new FileWriter(fileAdd);
		wirteFIle("\t");
	}

	private void wirteFIle(String split) throws IOException {
		if (temptDoubleArray != null) {
			for (int i = 0; i < this.temptDoubleArray.length; i++) {
				fw.write(temptDoubleArray[i][0]);
				for (int j = 1; j < this.temptDoubleArray[i].length; j++) {
					fw.write(split + temptDoubleArray[i][j]);
				}
				fw.write("\r\n");
			}
		} else if (temptArray != null) {
			for (int i = 0; i < this.temptArray.length; i++) {
				fw.write(temptArray[i] + "\r\n");
			}
		}
		this.fw.close();
	}

	public void writeXml() throws IOException {
		this.fw = new FileWriter(fileAdd);
		OutputFormat of = new OutputFormat();
		of.setIndentSize(4);
		of.setNewlines(true);
		XMLWriter xw = new XMLWriter(fw, of);
		xw.write(doc);
		xw.close();
	}
	

}
