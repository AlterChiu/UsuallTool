
package usualTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class AtFileReader {
	private List<String> fileContain = new ArrayList<String>();
	private String fileAdd = null;
	private BufferedReader br;
	public static String ANSI = "Cp1252";
	public static String Unicode = "Unicode";
	public static String UTF8 = "UTF-8";
	public static String BIG5 = "big5";
	public static String ASCII = "ASCII";

	// <==============>
	// <here is the construtor>
	// <for file address or inputStream and special encoding>
	// <==================================================>
	public AtFileReader(String file_add) throws IOException {
		this.br = new BufferedReader(new InputStreamReader(new FileInputStream(file_add)));
		this.fileAdd = file_add;
		String tempt;
		while ((tempt = br.readLine()) != null) {
			this.fileContain.add(tempt);
		}
		br.close();
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
		this.br = new BufferedReader(new InputStreamReader(new FileInputStream(fileAdd), encode));
		this.fileAdd = fileAdd;
		String tempt;
		while ((tempt = br.readLine()) != null) {
			this.fileContain.add(tempt);
		}
		br.close();
	}

	public AtFileReader(String fileAdd, Charset charset) throws IOException {
		this.br = new BufferedReader(new InputStreamReader(new FileInputStream(fileAdd), charset));
		this.fileAdd = fileAdd;
		String tempt;
		while ((tempt = br.readLine()) != null) {
			this.fileContain.add(tempt);
		}
		br.close();
	}

	public AtFileReader skipEmptyLine() {
		List<String> temptList = new ArrayList<>();
		this.fileContain.forEach(e -> {
			if (!e.isEmpty()) {
				temptList.add(e);
			}
		});
		this.fileContain = temptList;
		return this;
	}

	// <get the file by line -> buufered reader>
	// <_________________________________________________________________>
	public String[] getContain() {
		return this.fileContain.parallelStream().toArray(String[]::new);
	}

	public String[] getContain(int start, int end) {
		ArrayList<String> tempt = new ArrayList<String>(this.fileContain);
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
		return this.fileContain.stream().map(e -> e.split(",")).collect(Collectors.toList()).parallelStream()
				.toArray(String[][]::new);
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
		return this.fileContain.stream().map(e -> e.split(split)).collect(Collectors.toList()).parallelStream()
				.toArray(String[][]::new);
	}

	public String[][] getContent(String split, int start, int end) {
		ArrayList<String[]> tempt = new ArrayList<String[]>();

		for (int index = start; index < this.fileContain.size() - end; index++) {
			tempt.add(this.fileContain.get(index).split(split));
		}
		return tempt.parallelStream().toArray(String[][]::new);
	}

	// <get split file by space>
	// <_______________________________________________>
	public String[][] getStr() {
		return this.fileContain.stream().map(e -> e.split(" +")).collect(Collectors.toList()).parallelStream()
				.toArray(String[][]::new);
	}

	public String[][] getStr(int start, int end) {
		ArrayList<String[]> tempt = new ArrayList<String[]>();
		for (int index = start; index < this.fileContain.size() - end; index++) {
			tempt.add(this.fileContain.get(index).split(" +"));
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

	public JsonElement getJson() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		for (String line : this.fileContain) {
			sb.append(line);
		}

		JsonElement jsonElement = new JsonParser().parse(sb.toString());
		return jsonElement;
	}


	// <get image file>
	// <______________________________________________________________>
	public static String getImageAs64Encode(String fileAdd) throws IOException {
		byte[] fileContent = FileUtils.readFileToByteArray(new File(fileAdd));
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		return encodedString;
	}

	final void clear() {
		this.fileContain.clear();
	}

}
