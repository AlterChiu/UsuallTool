package usualTool;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class AtArrayFunction<type> {

	public double[] getDoubleArray(ArrayList<Double> temptList) {
		return temptList.stream().mapToDouble(Double::doubleValue).toArray();
	}

	public double[][] getDoubleMatrix(ArrayList<Double[]> temptList) {
		double outArray[][] = new double[temptList.size()][];

		for (int index = 0; index < temptList.size(); index++) {
			outArray[index] = Arrays.asList(temptList.get(index)).stream().mapToDouble(Double::doubleValue).toArray();
		}
		return outArray;
	}

	public String[] getStringArray(ArrayList<String> temptList) {
		return temptList.parallelStream().toArray(String[]::new);
	}

	public String[][] getStringMatrix(ArrayList<String[]> temptList) {
		return temptList.parallelStream().toArray(String[][]::new);
	}

	public type getMostReapetTimesValue(ArrayList<type> temptList) {
		ArrayList<type> noDuplicate = temptList;
		noDuplicate = new ArrayList<type>(noDuplicate.stream().distinct().collect(Collectors.toList()));
		
		int maxReapet = 0;
		type maxReapetValue  = null;
		for(type temptValue : noDuplicate) {
			int times = Collections.frequency(temptList, temptValue);
			if(times > maxReapet) {
				maxReapet = times;
				maxReapetValue = temptValue;
			}
		}
		return maxReapetValue;
	}
	
	public int  getMostReapetTimes(ArrayList<type> temptList) {
		ArrayList<type> noDuplicate = temptList;
		noDuplicate = new ArrayList<type>(noDuplicate.stream().distinct().collect(Collectors.toList()));
		
		int maxReapet = 0;
		for(type temptValue : noDuplicate) {
			int times = Collections.frequency(temptList, temptValue);
			if(times > maxReapet) {
				maxReapet = times;
			}
		}
		return maxReapet;
	}
	public int  getMostReapetTimes(ArrayList<type> temptList , int minTimes) {
		ArrayList<type> noDuplicate = temptList;
		noDuplicate = new ArrayList<type>(noDuplicate.stream().distinct().collect(Collectors.toList()));
		
		int maxReapet = 0;
		for(type temptValue : noDuplicate) {
			int times = Collections.frequency(temptList, temptValue);
			if(times > maxReapet && times>= minTimes) {
				maxReapet = times;
			}
		}
		return maxReapet;
	}
	
	public type getLeastReapetTimesValue(ArrayList<type> temptList) {
		ArrayList<type> noDuplicate = temptList;
		noDuplicate = new ArrayList<type>(noDuplicate.stream().distinct().collect(Collectors.toList()));
		
		int leastReapet = 9999999;
		type leastReapetValue  = null;
		for(type temptValue : noDuplicate) {
			int times = Collections.frequency(temptList, temptValue);
			if(times < leastReapet) {
				leastReapet = times;
				leastReapetValue = temptValue;
			}
		}
		return leastReapetValue;
	}
	
	public int  getLeastReapetTimes(ArrayList<type> temptList) {
		ArrayList<type> noDuplicate = temptList;
		noDuplicate = new ArrayList<type>(noDuplicate.stream().distinct().collect(Collectors.toList()));
		
		int leastReapet = 0;
		for(type temptValue : noDuplicate) {
			int times = Collections.frequency(temptList, temptValue);
			if(times < leastReapet) {
				leastReapet = times;
			}
		}
		return leastReapet;
	}
	
	public int  getLeastReapetTimes(ArrayList<type> temptList , int maxTimes) {
		ArrayList<type> noDuplicate = temptList;
		noDuplicate = new ArrayList<type>(noDuplicate.stream().distinct().collect(Collectors.toList()));
		
		int leastReapet = 0;
		for(type temptValue : noDuplicate) {
			int times = Collections.frequency(temptList, temptValue);
			if(times < leastReapet && times<=maxTimes) {
				leastReapet = times;
			}
		}
		return leastReapet;
	}
	
	public ArrayList<type> getSortedArrayList(ArrayList<type> temptList){
		return new ArrayList<type>(temptList.stream().sorted().collect(Collectors.toList()));
	}
	
	public ArrayList<type> getSortedArrayList(type[] temptList){
		return new ArrayList<type>(Arrays.asList(temptList).stream().sorted().collect(Collectors.toList()));
	}
	
	public ArrayList<type> getReverseSortedArrayList(ArrayList<type> temptList){
		Collections.sort(temptList,Collections.reverseOrder());
		return temptList;
	}
	
	public ArrayList<type> getReverseSortedArrayList(type[] temptList){
		ArrayList<type>  temptSave = new ArrayList<type>(Arrays.asList(temptList));
		return getReverseSortedArrayList(temptSave);
	}
	
	
	public String[] getMostReapetArrayValue(ArrayList<type[]> temptList){
		AtArrayFunction<String>arrayFunction = new AtArrayFunction<String>();
		ArrayList<String> compareList = new ArrayList<String>();
		temptList.stream().forEach(array -> {
			String temptValue = String.valueOf(array[0]);
			temptValue = temptValue + ","+array[1];
			compareList.add(temptValue);
		});
		return Arrays.asList(arrayFunction.getMostReapetTimesValue(compareList).split(",")).parallelStream().toArray(String[]::new);
	}

	public int getMostReapetArrayTimes(ArrayList<type[]> temptList){
		AtArrayFunction<String>arrayFunction = new AtArrayFunction<String>();
		ArrayList<String> compareList = new ArrayList<String>();
		temptList.stream().forEach(array -> {
			String temptValue = String.valueOf(array[0]);
			temptValue = temptValue + ","+array[1];
			compareList.add(temptValue);
		});
		return arrayFunction.getMostReapetTimes(compareList);
	}
	
	public int getMostReapetArrayTimes(ArrayList<type[]> temptList , int minTImes){
		AtArrayFunction<String>arrayFunction = new AtArrayFunction<String>();
		ArrayList<String> compareList = new ArrayList<String>();
		temptList.stream().forEach(array -> {
			String temptValue = String.valueOf(array[0]);
			temptValue = temptValue + ","+array[1];
			compareList.add(temptValue);
		});
		return arrayFunction.getMostReapetTimes(compareList , minTImes);
	}
	
	public String[] getLeastReapetArrayValue(ArrayList<type[]> temptList){
		AtArrayFunction<String>arrayFunction = new AtArrayFunction<String>();
		ArrayList<String> compareList = new ArrayList<String>();
		temptList.stream().forEach(array -> {
			String temptValue = String.valueOf(array[0]);
			temptValue = temptValue + ","+array[1];
			compareList.add(temptValue);
		});
		return Arrays.asList(arrayFunction.getLeastReapetTimesValue(compareList).split(",")).parallelStream().toArray(String[]::new);
	}

	public int getLeastReapetArrayTimes(ArrayList<type[]> temptList , int maxTimes){
		AtArrayFunction<String>arrayFunction = new AtArrayFunction<String>();
		ArrayList<String> compareList = new ArrayList<String>();
		temptList.stream().forEach(array -> {
			String temptValue = String.valueOf(array[0]);
			temptValue = temptValue + ","+array[1];
			compareList.add(temptValue);
		});
		return arrayFunction.getLeastReapetTimes(compareList,maxTimes);
	}

}
