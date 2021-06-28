package Microsoft.Office.Word.Wrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextSegment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class WordWrapper {

	private String preFix = "%";
	private String postFix = "%";

	private XWPFDocument doc;

	public WordWrapper(String fileAdd) throws FileNotFoundException, IOException {
		this.doc = new XWPFDocument(new FileInputStream(fileAdd));
	}
	
	public void Save(String saveAdd) throws FileNotFoundException, IOException {
		doc.write(new FileOutputStream(saveAdd));
	}
	
	
	public void replace(String target, String replaceString) {
		this.replaceFromParagraph(target, replaceString);
		this.replaceFromTable(target, replaceString);
	}

	public void replaceFromParagraph(String target, String replaceString) {
		for (XWPFParagraph xwpfParagraph : doc.getParagraphs()) {
			wrapTarget(xwpfParagraph, target, replaceString);
		}
	}

	public void replaceFromTable(String target, String replaceString) {
		for (XWPFTable tbl : doc.getTables()) {
			for (XWPFTableRow row : tbl.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					for (XWPFParagraph xwpfParagraph : cell.getParagraphs()) {
						wrapTarget(xwpfParagraph, target, replaceString);
					}
				}
			}
		}
	}

	private void wrapTarget(XWPFParagraph xwpfParagraph, String target, String replaceString) {

		String content = xwpfParagraph.getText();

		StringBuilder targetString = new StringBuilder();
		targetString.append(this.preFix);
		targetString.append(target.replace(this.preFix, "").replace(this.postFix, ""));
		targetString.append(this.postFix);

		// find the target amount in paragraph
		int targetNum = content.split(targetString.toString()).length - 1;
		if (targetNum > 0) {
			for (int index = 0; index < targetNum; index++) {
				paragraphWrapper(xwpfParagraph, targetString.toString(), replaceString);
			}
		}
	}

	private void paragraphWrapper(XWPFParagraph xwpfParagraph, String target, String replaceString) {
		PositionInParagraph position = new PositionInParagraph();
		TextSegment segement = xwpfParagraph.searchText(target, position);

		// get the index of runs in paragraph
		int beginIndex = segement.getBeginRun();
		int endIndex = segement.getEndRun();

		// wrap
		List<XWPFRun> runs = xwpfParagraph.getRuns();

		// get original content
		StringBuilder temptContent = new StringBuilder();
		for (int index = beginIndex; index <= endIndex; index++) {
			temptContent.append(runs.get(index).getText(0));
		}

		// wrap the first run
		runs.get(beginIndex).setText(temptContent.toString().replace(target, replaceString), 0);

		// clear text in other runs
		for (int index = beginIndex + 1; index <= endIndex; index++) {
			runs.get(index).setText("", 0);
		}
	}
}
