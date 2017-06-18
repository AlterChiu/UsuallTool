package Hibernate;

import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class HibernateDocumentCreater {
	private static String className = "WaterLevel";

	private static String property[] = { "sn", "dataTime", "dataValue" , "quality"};

	private static String colum[] = { "SN", "DataTime", "DataValue" , "Quality"};

	private static String classLocate = "ntut.Hibernate.DataBase.Table." + className;

	private static String table = "cwb_his.StageList";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TreeMap<String, String> tr = new TreeMap<String, String>();
		tr.put("table", table);
		tr.put("locate", classLocate);

		String fileAdd = "C:\\Users\\alter\\Desktop\\test\\LIST\\";
		FileWriter fw = new FileWriter(fileAdd + "class");

		new ClassCreater(fw, className, property);
		fw = new FileWriter(fileAdd + "xml");
		new XmlCreater(fw, className, property, colum, tr);

	}

}
