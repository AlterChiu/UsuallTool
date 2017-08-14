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

		String time = "2016-07-12  10:15";
		TimeTranslate timetranse = new TimeTranslate();	
		 String YMDdash_HM = "yyyy-MM-dd  HH:mm";
		
		long text =  timetranse.StringToLong(time, TimeTranslate.YMDdash_HM);
		System.out.println(text+"");
		
		

	}

}
