package usualTool;

import java.text.ParseException;

public class test {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		/**
		 * 	String oldFileAdd = "C:\\Users\\alter\\Desktop\\tempSectionTotal\\waterShed\\watershed_NO_Name_01_忠孝成都貴陽.cpg";
		 *String newFileAdd = "C:\\Users\\alter\\Desktop\\tempSectionTotal\\waterShed\\忠孝成都貴陽.cpg";
		*
		 * new FileFunction().reNameFile(oldFileAdd, newFileAdd);
		 */
		TimeTranslate tt = new TimeTranslate();
		String start = "201706010100";
	
		
		start = tt.milliToDate(tt.StringToLong(start, "yyyyMMddHHmm") + 600000, "yyyyMMddHHmm");
		
		System.out.println(start);
	}

}
