package usualTool;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String oldFileAdd = "C:\\Users\\alter\\Desktop\\tempSectionTotal\\waterShed\\watershed_NO_Name_01_忠孝成都貴陽.cpg";
		String newFileAdd = "C:\\Users\\alter\\Desktop\\tempSectionTotal\\waterShed\\忠孝成都貴陽.cpg";
		
		new FileFunction().reNameFile(oldFileAdd, newFileAdd);
		
		

	}

}
