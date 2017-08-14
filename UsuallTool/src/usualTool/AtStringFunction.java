package usualTool;

public class AtStringFunction {
	private String text;

	public AtStringFunction(String text) {
		this.text = text;
	}

	public String Clip(String target) {
		String[] temp = text.split(target);
		String out = "";
		for (String d : temp) {
			out = out + d;
		}
		return out;
	}
	public String FillBack(String fill , int length){
		while( this.text.length()<length){
			this.text = this.text + fill;
		}
		return this.text;
	}
	
	public String FillFront(String fill , int length){
		while( this.text.length()<length){
			this.text = fill +  this.text;
		}
		return this.text;
	}
}