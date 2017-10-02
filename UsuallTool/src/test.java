import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import tw.ntut.ce.util.gzip.GZipUtils;
import usualTool.AtExcelReader;

public class test {

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		String zipFilePath = "C:\\Users\\alter\\Downloads\\commons-collections4-4.1-bin.tar.gz";
        
        String destDir = "C:\\Users\\alter\\Downloads\\zipTest";
        
       
		GZipUtils.ungzipFile(zipFilePath,destDir);
        
	}

}
