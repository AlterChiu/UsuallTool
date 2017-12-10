package usualTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;

public class AtFileReader {
	private ArrayList<String> fileContain = new ArrayList<String>();

	// <==============>
	// <here is the construtor>
	// <for file address or inputStream and special encoding>
	// <==================================================>
	public AtFileReader(String file_add) throws IOException {
		BufferedReader Br = new BufferedReader(new InputStreamReader(new FileInputStream(file_add)));
		String tempt;
		while ((tempt = Br.readLine()) != null) {
			this.fileContain.add(tempt);
		}
		Br.close();
	}

	public AtFileReader(InputStreamReader input) throws IOException {
		BufferedReader Br = new BufferedReader(input);
		String tempt;

		while ((tempt = Br.readLine()) != null) {
			this.fileContain.add(tempt);
		}
		Br.close();
	}

	public AtFileReader(String fileAdd, String encode) throws IOException {
		FileInputStream fis = new FileInputStream(new File(fileAdd));
		byte[] lineb = new byte[500];

		int readLine = 0;
		StringBuffer sb = new StringBuffer("");
		while (fis.read(lineb) > 0) {
			String utf8 = new String(lineb, encode);
			sb.append(utf8);
		}

		this.fileContain = new ArrayList<String>(Arrays.asList(sb.toString().split("\n")));

		fis.close();
	}

	// <get the file by line -> buufered reader>
	// <_________________________________________________________________>
	public String[] getContain() {
		return this.fileContain.parallelStream().toArray(String[]::new);
	}

	public String[] getContain(int start, int end) {
		ArrayList<String> tempt = (ArrayList<String>) this.fileContain.clone();
		for (int i = 0; i < start; i++) {
			tempt.remove(0);
		}
		for (int i = 0; i < end; i++) {
			tempt.remove(tempt.size() - 1);
		}
		return tempt.parallelStream().toArray(String[]::new);
	}

	// <get csv file>
	// <____________________________________________________________>
	public String[][] getCsv() {
		ArrayList<String[]> tempt = new ArrayList<String[]>();
		this.fileContain.stream().forEach(line -> tempt.add(line.split(",")));
		return tempt.parallelStream().toArray(String[][]::new);
	}

	public String[][] getCsv(int start, int end) {
		ArrayList<String[]> tempt = new ArrayList<String[]>();
		this.fileContain.stream().forEach(line -> tempt.add(line.split(",")));

		for (int i = 0; i < start; i++) {
			tempt.remove(0);
		}
		for (int i = 0; i < end; i++) {
			tempt.remove(tempt.size() - 1);
		}

		return tempt.parallelStream().toArray(String[][]::new);
	}

	// <get split file by using significant text>
	// <__________________________________________________________>
	public String[][] getContent(String split) {
		ArrayList<String[]> tempt = new ArrayList<String[]>();
		this.fileContain.stream().forEach(line -> tempt.add(line.split(split)));
		return tempt.parallelStream().toArray(String[][]::new);
	}

	public String[][] getContent(String split, int start, int end) {
		ArrayList<String[]> tempt = new ArrayList<String[]>();
		this.fileContain.stream().forEach(line -> tempt.add(line.split(split)));
		return tempt.parallelStream().toArray(String[][]::new);
	}

	// <get split file by space>
	// <_______________________________________________>
	public String[][] getStr() {
		ArrayList<String[]> tempt = new ArrayList<String[]>();
		this.fileContain.stream().forEach(line -> tempt.add(line.split(" +")));
		return tempt.parallelStream().toArray(String[][]::new);
	}

	public String[][] getStr(int start, int end) {
		ArrayList<String[]> tempt = new ArrayList<String[]>();
		this.fileContain.stream().forEach(line -> tempt.add(line.split(" +")));

		for (int i = 0; i < start; i++) {
			tempt.remove(0);
		}
		for (int i = 0; i < end; i++) {
			tempt.remove(tempt.size() - 1);
		}

		return tempt.parallelStream().toArray(String[][]::new);
	}

	// <get the file by line with or not the significant text>
	// <______________________________________________________________>
	public String[] getContainWithOut(String text) {
		ArrayList<String> tempt = new ArrayList<String>();
		this.fileContain.stream().forEach(line -> {
			if (!line.contains(text)) {
				tempt.add(line);
			}
		});
		return tempt.parallelStream().toArray(String[]::new);
	}

	public String[] getContainWith(String text) {
		ArrayList<String> tempt = new ArrayList<String>();
		this.fileContain.stream().forEach(line -> {
			if (line.contains(text)) {
				tempt.add(line);
			}
		});
		return tempt.parallelStream().toArray(String[]::new);
	}

}
