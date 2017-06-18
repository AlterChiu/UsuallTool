package usualTool;

import java.io.FileWriter;
import java.io.IOException;

public class AtFileWriter<T> {
	private String[][] temptDoubleArray = null;
	private String[] temptArray = null;
	private  FileWriter fw;

	public AtFileWriter(String[][] content, String fileAdd) throws IOException {
		this.fw = new FileWriter(fileAdd);
		this.temptDoubleArray = content;

	}

	public AtFileWriter(String[] content, String fileAdd) throws IOException {
		fw = new FileWriter(fileAdd);
		this.temptArray = content;
	}

	public void csvWriter() throws IOException {
		wirteFIle(",");
	}
	
	
	public void testWriter(String split) throws IOException {
		wirteFIle(split);
	}
	
	public void tabWriter() throws IOException {
		wirteFIle("\t");
	}
	
	
	private void wirteFIle(String split) throws IOException{
		if (temptDoubleArray != null) {
			for (int i = 0; i < this.temptDoubleArray.length; i++) {
				fw.write(temptDoubleArray[i][0]);
				for (int j = 1; j < this.temptDoubleArray[i].length; j++) {
					fw.write(split + temptDoubleArray[i][j]);
				}
				fw.write("\r\n");
			}
		} else if (temptArray != null) {
			fw.write(temptArray[0]);
			for (int i = 1; i < this.temptArray.length; i++) {
				fw.write(split+ temptArray[i]);
			}
		}
		this.fw.close();
	}

}
