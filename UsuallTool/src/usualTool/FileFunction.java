package usualTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;

public class FileFunction {

	public FileFunction() {
	}

	/**
	 * �s�إؿ�
	 * 
	 * @param folderPath
	 *            String �pc:/fqf
	 * @return boolean
	 */
	public void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("�s�إؿ��ާ@�X��");
			e.printStackTrace();
		}
	}

	/**
	 * �s����
	 * 
	 * @param filePathAndNameString
	 *            �ɸ��|�ΦW�� �pc:/fqf.txt
	 * @param fileContent
	 *            String�ɤ��e
	 * @return boolean
	 */
	public void newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();

		} catch (Exception e) {
			System.out.println("�s�إؿ��ާ@�X��");
			e.printStackTrace();

		}

	}

	/**
	 * �R����
	 * 
	 * @param filePathAndNameString
	 *            �ɸ��|�ΦW�� �pc:/fqf.txt
	 * @param fileContentString
	 * @return boolean
	 */
	public void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("�R���ɾާ@�X��");
			e.printStackTrace();

		}

	}

	/**
	 * �R����Ƨ�
	 * 
	 * @param filePathAndNameString
	 *            ��Ƨ����|�ΦW�� �pc:/fqf
	 * @param fileContentString
	 * @return boolean
	 */
	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // �R�����̭��Ҧ����e
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // �R���Ÿ�Ƨ�

		} catch (Exception e) {
			System.out.println("�R����Ƨ��ާ@�X��");
			e.printStackTrace();

		}

	}

	/**
	 * �R����Ƨ��̭����Ҧ���
	 * 
	 * @param path
	 *            String��Ƨ����| �p c:/fqf
	 */
	public void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ���R����Ƨ��̭�����
				delFolder(path + "/" + tempList[i]);// �A�R���Ÿ�Ƨ�
			}
		}
	}

	/**
	 * �ƻs�����
	 * 
	 * @param oldPath
	 *            String���ɸ��| �p�Gc:/fqf.txt
	 * @param newPath
	 *            String�ƻs����| �p�Gf:/fqf.txt
	 * @return boolean
	 */
	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // �ɦs�b��
				InputStream inStream = new FileInputStream(oldPath);// Ū�J����
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // �줸�ռ� �ɮפj�p
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("�ƻs����ɾާ@�X��");
			e.printStackTrace();

		}

	}

	/**
	 * �ƻs��Ӹ�Ƨ����e
	 * 
	 * @param oldPath
	 *            String���ɸ��| �p�Gc:/fqf
	 * @param newPath
	 *            String�ƻs����| �p�Gf:/fqf/ff
	 * @return boolean
	 */
	public void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // �p�G��Ƨ����s�b�h�إ߷s��Ƨ�
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// �p�G�O�l��Ƨ�
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("�ƻs��Ӹ�Ƨ����e�ާ@�X��");
			e.printStackTrace();

		}

	}

	/**
	 * �����ɨ���w�ؿ�
	 * 
	 * @param oldPath
	 *            String�p�Gc:/fqf.txt
	 * @param newPath
	 *            String�p�Gd:/fqf.txt
	 */
	public void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);
	}
	public void moveFileWithOutDel(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
	}

	/**
	 * �����ɨ���w�ؿ�
	 * 
	 * @param oldPath
	 *            String�p�Gc:/fqf.txt
	 * @param newPath
	 *            String�p�Gd:/fqf.txt
	 */
	public void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}
	public void moveFolderWithOutDel(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
	}
	
	public void reNameFile(String oldPath , String newPath){
		File oldFile = new File(oldPath);
		oldFile.renameTo(new File(newPath));
	}
}


