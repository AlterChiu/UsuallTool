package Drawing.JFreeChart;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;



public class LabelXYDataset extends AbstractXYDataset {

	private TreeMap<Integer,TreeMap<String,List>> tree = new TreeMap<Integer,TreeMap<String,List>>();
	private ArrayList<Number> x = new ArrayList<Number>();
	private ArrayList<Number> y = new ArrayList<Number>();
	private ArrayList<String> label = new ArrayList<String>();

	public void add( double x, double y, String label) {
		this.x.add(x);
		this.y.add(y);
		this.label.add(label);
	}

	public String getLabel(int series, int item) {
		return (String)this.tree.get(series).get("Label").get(item);
	}
	
	public void endSeries(int series){
		TreeMap<String,List> tempTree = new TreeMap<String,List>();
		tempTree.put("X",(List<Number>) this.x.clone());
		tempTree.put("Y", (List<Number>)this.y.clone());
		tempTree.put("Label", (List<String>)this.label.clone());
		this.tree.put(series, tempTree);
		
		this.x.clear();
		this.y.clear();
		this.label.clear();
	}

	@Override
	public int getSeriesCount() {
		return this.tree.size();
	}

	@Override
	// setting the name of line here
	public Comparable getSeriesKey(int series) {
		String[] name = {"Observation","Forecast","timeLine"};
		return name[series];
	}

	@Override
	public int getItemCount(int series) {
		return this.tree.get(series).get("Label").size();
	}

	@Override
	public Number getX(int series, int item) {
		return (Number)this.tree.get(series).get("X").get(item);
	}

	@Override
	public Number getY(int series, int item) {
		return  (Number)this.tree.get(series).get("Y").get(item);
	}
}


