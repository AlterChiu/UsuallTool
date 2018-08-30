package usualTool;

import java.util.ArrayList;
import java.util.List;

public class AtKmeans {
	private List<Double[]> analysisData;
	private List<Double[]> startPoint = new ArrayList<Double[]>();
	private List<List<Double[]>> classData = new ArrayList<List<Double[]>>();
	private int classNum;
	private double maxX;
	private double minX;
	private double maxY;
	private double minY;

	public AtKmeans(List<Double[]> analysisData, int classNum) {
		this.analysisData = analysisData;
		this.classNum = classNum;

		List<Double> xList = new ArrayList<Double>();
		List<Double> yList = new ArrayList<Double>();
		this.analysisData.forEach(cordinate -> {
			xList.add(cordinate[0]);
			yList.add(cordinate[1]);
		});

		AtCommonMath xListStatics = new AtCommonMath(xList);
		this.maxX = xListStatics.getMax();
		this.minX = xListStatics.getMin();

		AtCommonMath yListStatics = new AtCommonMath(yList);
		this.maxY = yListStatics.getMax();
		this.minY = yListStatics.getMin();

		// recreate the startPoints;
		setStartPoint();
	}

	// get the start point
	public List<Double[]> getStartPoint() {
		return this.startPoint;
	}

	public void setStartPoint() {
		RandomMaker random = new RandomMaker();
		startPoint.clear();
		while (this.startPoint.size() < this.classNum) {
			this.startPoint.add(new Double[] { random.RandomDouble(this.minX, this.maxX),
					random.RandomDouble(this.minY, this.maxY) });
		}
	}

	// get the final result of kmeans
	public List<List<Double[]>> getClassifier() {
		Boolean convergence = true;

		while (convergence) {
			// if there is error while start point is null
			// reset the start point
			try {
				double startPointDisplace = 0;
				List<Double[]> renewStartPoint = classfier();

				// check the displacement of the startpoint
				for (int index = 0; index < this.startPoint.size(); index++) {
					startPointDisplace = startPointDisplace
							+ getDis(this.startPoint.get(index), renewStartPoint.get(index));
				}

				// to setup the precious of the displacement
				if (startPointDisplace < 1) {
					convergence = false;
				} else {
					this.startPoint = renewStartPoint;
				}
			} catch (Exception e) {
				setStartPoint();
			}
		}
		return this.classData;
	}

	private List<Double[]> classfier() {
		// initialize the saveList
		this.classData.clear();
		for (int index = 0; index < this.classNum; index++) {
			this.classData.add(new ArrayList<Double[]>());
		}

		// check the whole value
		for (Double[] value : this.analysisData) {
			double temptDis = getDis(this.startPoint.get(0), value);
			int temptIndex = 0;

			// to check which start point is the closest
			for (int index = 1; index < this.classNum; index++) {
				double dis = getDis(this.startPoint.get(index), value);
				if (dis < temptDis) {
					temptDis = dis;
					temptIndex = index;
				}
			}

			// insert the value to the savePoint
			this.classData.get(temptIndex).add(value);
		}

		// renew the startPoint
		List<Double[]> renewStartPoint = new ArrayList<Double[]>();
		for (int index = 0; index < this.classData.size(); index++) {
			renewStartPoint.add(getClassMeanPosint(this.classData.get(index)));
		}
		return renewStartPoint;
	}

	// get the mean point of the class, for renew the startPoint
	public Double[] getClassMeanPosint(List<Double[]> values) {
		List<Double> xList = new ArrayList<Double>();
		List<Double> yList = new ArrayList<Double>();
		values.forEach(cordinate -> {
			xList.add(cordinate[0]);
			yList.add(cordinate[1]);
		});
		return new Double[] { new AtCommonMath(xList).getMean(), new AtCommonMath(yList).getMean() };
	}

	private double getDis(Double[] point1, Double[] point2) {
		return Math.pow(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2), 0.5);
	}

}
