package asciiFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import GlobalProperty.GlobalProperty;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiGridChange;
import usualTool.AtFileWriter;
import usualTool.AtListFunction;

public class RoughDemMaker {
	private String delicateDem = GlobalProperty.originalDelicate;
	private String delicateKnDem = GlobalProperty.originalDelicateKn;
	private String roughDem = GlobalProperty.originalRough;
	private String roughKnDem = GlobalProperty.originalRoughKn;
	private String roughNullDem = GlobalProperty.originalRoughNull;
	

	// double grid size to the delicate one
	public RoughDemMaker() throws IOException {

		// ====================ChangeGridDem============================
		new AtFileWriter(new AsciiGridChange(this.delicateDem).getChangedContent(2), this.roughDem).textWriter("    ");
		
		
		// =============GetRoughDem     &&       GetDelicateKnDem================
		AsciiBasicControl delicateKnAscii = new AsciiBasicControl(this.delicateKnDem).cutFirstColumn();
		AsciiBasicControl roughAscii = new AsciiBasicControl(this.roughDem).cutFirstColumn();
		
		TreeMap<String,String> delicateProperty = delicateKnAscii.getProperty();
		TreeMap<String,String> roughProperty = roughAscii.getProperty();
		
		double roughStartX = Double.parseDouble(roughProperty.get("bottomX"));
		double roughStartY = Double.parseDouble(roughProperty.get("topY"));
		
		double roughCellSize = Double.parseDouble(roughProperty.get("cellSize"));
		double delicateCellSize = Double.parseDouble(delicateProperty.get("cellSize"));
		
		String roughNoData = roughProperty.get("noData");
		String delicateNoData = delicateProperty.get("noData");
		
		String roughAsciiGird[][] = roughAscii.getAsciiGrid();
		
		
		//=====================temptSave of the output Kn dem    &&    the   null  File================
		ArrayList<String[]> temptOutKn = new ArrayList<String[]>();
		ArrayList<String[]> temptOutNull = new ArrayList<String[]>();
		
		//===================== add the rough dem property==================
		Arrays.asList(roughAscii.getPropertyText()).forEach(line -> temptOutKn.add(line));
		
	
		
//		====================================================
//		===============read the rough dem file=====================
//		====================================================
		for (int row = 0; row < roughAsciiGird.length; row++) {
			double temptRoughY  = roughStartY -row*roughCellSize;
			ArrayList<String> temptKnArray = new ArrayList<String>();
			ArrayList<String> temptNullArray = new ArrayList<String>();
			
			for (int column = 0; column < roughAsciiGird[row].length; column++) {
				double temptRoughX = roughStartX + column*roughCellSize;
				
				
//				===================== get the kn value==================
				ArrayList<String> knList = new ArrayList<String>();
				for(int differY = -1 ; differY <=1 ; differY = differY+2) {
					for(int differX = -1 ; differX <=1 ; differX = differX +2) {
						String temptValue = delicateKnAscii.getValue(temptRoughX + differX*0.5*delicateCellSize, temptRoughY + differY*0.5*delicateCellSize);
						temptKnArray.add(roughNoData);
						if(!temptValue.equals(delicateNoData) && !temptValue.equals("")) {
							knList.add(temptValue);
						}
					}
				}
				
//				============get the kn value while repeat in most times===============
				try {
					String knValue = new AtListFunction<String>(knList).getMaxReapt().get(0);
					temptKnArray.add(knValue);
				}catch(Exception e) {
					temptKnArray.add(roughNoData);
				}
				
			}
			temptOutKn.add(temptKnArray.parallelStream().toArray(String[]::new));
			temptOutNull.add(temptKnArray.parallelStream().toArray(String[]::new));
		}
		
		
//		==========================Out put the kn value grid    &&  the null File =====================
		new AtFileWriter(temptOutKn.parallelStream().toArray(String[][]::new) , this.roughKnDem).textWriter("    ");
		new AtFileWriter(temptOutNull.parallelStream().toArray(String[][]::new) , this.roughNullDem).textWriter("    ");
	}

}
