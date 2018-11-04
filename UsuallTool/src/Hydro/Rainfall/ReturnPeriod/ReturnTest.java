package Hydro.Rainfall.ReturnPeriod;

public class ReturnTest {
	private double summary = 0;
	private int times = 0;

	public void summary(double value) {
		summary = summary + value;
		times++;
	}

	public double getSummary() {
		return this.summary;
	}

	public int getTimes() {
		return this.times;
	}

	public double getMean() {
		return this.summary/this.times;
	}
	
	public double WTFSummary() {
		return this.summary;
	}
}
