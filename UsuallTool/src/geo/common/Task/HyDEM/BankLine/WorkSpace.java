package geo.common.Task.HyDEM.BankLine;

public class WorkSpace {
	// workSpace
	public static String workSpace = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\溢堤線更新\\港尾溝溪\\";
	public static String sobekObjectWorkSpace = workSpace + "SOBEK物件\\shp-file\\";
	public static String hydemObjectWorkSpace = workSpace + "港尾溝溪溢堤線\\";
	public static String testingWorkSpace = workSpace + "";

	// creating fileName
	public static String pairseBankLine_Error = "SOBEK_BankLinepairesError.shp";
	public static String pairseBankLine = "SOBEK_BankLinepaires.shp";
	public static String pariseBankPointsError = "SOBEK_BankPointspairesError.shp";
	public static String pariseBankPoints = "SOBEK_BankPointspaires.shp";
	public static String reachNodesShp = "SOBEK_ReachNode.shp";
	public static String splitLinePairseBankPoints = "SOBEK_BankPointsLine.shp";

	public static String splitHydemPolygons = "HyDEM_SplitPolygons.shp"; // HyDEM polygons, which split by splitLine
	public static String splitHydemLines = "HyDEM_SplitLine.shp"; // splitLine, which split polygons from HyDEM mergedBankLine to 
	public static String mergedHydemPolygons = "HyDEM_MergedBankLine.shp";
	public static String centerLineHydemPolygons = "HyDEM_CenterLine.shp";
	public static String bankLineHydem = "HyDEM_BankLine.shp";
	public static String userDefineSplitLine = "userDefine_SplitLine.shp";
	
}
