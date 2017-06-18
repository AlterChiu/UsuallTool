package usualTool;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String oldFileAdd = "C:\\Users\\alter\\Desktop\\test\\Uncertainty\\QPESUMS\\adjusted\\QPE092611_092811\\";
		String newFileAdd = "C:\\Users\\alter\\Desktop\\test\\Uncertainty\\QPESUMS\\adjusted\\QPEAD\\";
		
		new FileFunction().moveFolderWithOutDel(oldFileAdd, newFileAdd);
		

	}

}
