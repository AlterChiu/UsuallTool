package testFolder;

import java.io.IOException;

import Hydro.Rainfall.ReturnPeriod.ReturnTest;


public class testAtCommon {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method
		ReturnTest classTest = new ReturnTest();

		for (int index = 1; index <= 10; index++) {
			classTest.summary(index);
		}

		System.out.println(classTest.wtfSummary());
		System.out.println(classTest.getTimes());
		System.out.println(classTest.getMean());
	}
}