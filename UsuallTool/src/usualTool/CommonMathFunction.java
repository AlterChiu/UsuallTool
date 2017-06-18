package usualTool;

public class CommonMathFunction {
	
	
	public double BinomialCoefficient(int top , int base){
		/**
		C     take    n    form   k
		
		       top:
		C
			    base
		 */
		
		int topF = Factorial(top);
		int baseF = Factorial(base);
		int disF = Factorial(top-base);
		
		return (topF/baseF/disF);
	}
	
	public int Factorial(int n){
		/**
		 *                      n!
		 */
		int K = 1;
		for(int i=n;i>0;i--){
			K = K*i;
		}
		return K;
	}

}
