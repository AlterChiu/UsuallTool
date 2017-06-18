package Hibernate;

import java.io.FileWriter;
import java.io.IOException;

public class ClassCreater {

	public ClassCreater(FileWriter fw, String className, String[] property) throws IOException {
		
		

		fw.write("public class " + className + " {  \n");
		for (int i = 0; i < property.length; i++) {
			fw.write("private String " + property[i] + "; \n");
		}
		for (int i = 0; i < property.length; i++) {
			System.out.println(property[i]);
			String tempt = property[i].substring(0,1).toUpperCase() + property[i].substring(1, property[i].length());
			fw.write("public void set" + tempt + "(String " + property[i] + "){\n");
			fw.write("this." + property[i] + " = " + property[i] + ";\n");
			fw.write("} \n");

			fw.write("public String get" + tempt + "(){\n");
			fw.write("return this." + property[i] + ";\n");
			fw.write("} \n");
		}
		fw.close();
	}
}
