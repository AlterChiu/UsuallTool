package testFolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Hydro.Rainfall.ReturnPeriod.RetrunPeriod;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_PT3;
import asciiFunction.AsciiBasicControl;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;

public class test {

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		AsciiBasicControl originalAscii = new AsciiBasicControl(
				"E:\\mapReduce\\報告用-別刪\\20181201_Pintung_FEWS_12_3_plis1.5\\OriginalDEM\\lowResolutionDem.asc");

		double topY =2505914.5;
		double botY = 2504314.5;
		double differ = topY - botY;

		double temptTopY = topY;
		int count = 0;
		while (temptTopY >= originalAscii.getBoundary().get("minY")) {
			Map<String, Double> temptClip = originalAscii.getBoundary();
			double temptBot = temptTopY - differ;
			temptClip.put("maxY", temptTopY);
			temptClip.put("minY", temptBot);
			new AtFileWriter(originalAscii.getClipAsciiFile(temptClip).getAsciiFile(),
					"E:\\mapReduce\\報告用-別刪\\20181201_Pintung_FEWS_12_3_plis1.5\\test\\" + count + ".asc")
							.textWriter(" ");

			temptTopY = temptBot;
			count++;
		}

	}

}
