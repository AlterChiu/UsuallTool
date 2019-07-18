package usualTool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileFunction {

	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void delete(String path) {
		File file = new File(path);
		try {
			for (File childFile : file.listFiles()) {
				if (childFile.isDirectory()) {
					delete(childFile.getAbsolutePath());
				} else {
					childFile.delete();
				}
			}
		} catch (Exception e) {
		}
		try {
			file.delete();
		} catch (Exception e) {
		}
	}

	public static void copyFile(String oldPath, String newPath) {
		try {
			Files.copy(new File(oldPath).toPath(), new File(newPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void copyFolder(String oldPath, String newPath) {
		newFolder(newPath);
		oldPath = oldPath + "\\";
		newPath = newPath + "\\";
		for (String file : new File(oldPath).list()) {
			try {
				Files.copy(new File(oldPath + file).toPath(), new File(newPath + file).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delete(oldPath);
	}

	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delete(oldPath);
	}

	public static void reNameFile(String oldPath, String newPath) {
		File oldFile = new File(oldPath);
		oldFile.renameTo(new File(newPath));
	}
}
