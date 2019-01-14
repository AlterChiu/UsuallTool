package testFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usualTool.FileFunction;

public class testAtCommon {
	private static int threadNum = 4;
	public static String fileAdd = "E:\\QpesumsAnalysis\\RainfallData\\catchment\\";
	public static FileFunction ff = new FileFunction();
	public static String[] fileList = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int fileCount = 0;

		// the fileList here is the name of catchments
		List<String> catchmentsList = new ArrayList<>(Arrays.asList(fileList = new File(fileAdd).list()));
		List<String> lostData = loseData();
		int fileLength = catchmentsList.size();

		/*
		 * initial thread
		 */
		Map<Integer, Thread> threadList = new HashMap<>();
		for (int index = 0; index < threadNum; index++) {
			if (lostData.size() > 0) {
				threadList.put(index, initialThread(catchmentsList.get(Integer.parseInt(lostData.get(0))),
						Integer.parseInt(lostData.get(0))));
				threadList.get(index).start();
				lostData.remove(0);

			} else {
				threadList.put(index, initialThread(catchmentsList.get(fileCount), fileCount));
				fileCount++;
				threadList.get(index).start();
			}
		}

		/*
		 * run thread
		 */
		runThread: while (fileCount < fileLength) {
			for (int index = 0; index < threadNum; index++) {
				if (!threadList.get(index).isAlive()) {
					System.out.println(threadList.get(index).getName() + " end");
					fileCount++;

					if (fileCount == fileLength) {
						break runThread;
					} else {
						threadList.remove(index);
						threadList.put(index, initialThread(catchmentsList.get(fileCount), fileCount));
						threadList.get(index).start();
					}
				}
			}
		}
	}

	private static List<String> loseData() {
		String[] timeList = new String[] {};
		return new ArrayList<String>(Arrays.asList(timeList));
	}

	private static Thread initialThread(String targetCatchment, int fileCount) {
		Thread temptThread = new Thread(new threadClass(fileAdd + targetCatchment + "//day//"));
		temptThread.setName(fileCount + "");
		return temptThread;
	}

	public static class threadClass extends Thread {
		private String targetFolder = "";
	
		public threadClass(String targetFolder) {
			this.targetFolder = targetFolder ;
		}

		public void run() {
			for(String fileName : new File(this.targetFolder).list()) {
				ff.delete(this.targetFolder + fileName);
			}
		
		}
	
}

}