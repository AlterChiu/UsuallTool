package testFolder;

public class ThreadTest implements Runnable{
	private Boolean complete = false;
	
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		complete = true;
	}
	
	
	public Boolean completed() {
		return complete;
	}

}
