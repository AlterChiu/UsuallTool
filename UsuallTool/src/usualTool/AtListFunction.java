package usualTool;

import java.util.ArrayList;
import java.util.Collections;

public class AtListFunction<type> {
	private ArrayList<type> temptList = new ArrayList<type>();

	public AtListFunction(ArrayList<type> temptList) {
		this.temptList = temptList;
	}

	public ArrayList<type> getMaxReapt() {
		ArrayList<type> repeatList = new ArrayList<type>();
		
		int times = -1;

		for (type item : this.temptList) {
			int frequence = Collections.frequency(this.temptList, item);
			
			// if there are one more items be max repeat times
			if(frequence == times) {
				repeatList.add(item);
			}
			
			
			if (frequence > times) {
				repeatList.clear();
				repeatList.add(item);
				times = frequence;
			}
		};
		return repeatList;
	}
	
	
	public int getRepeatTimes(type item) {
		return Collections.frequency(this.temptList , item);
	}

}
