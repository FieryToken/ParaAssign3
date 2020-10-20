package utils;

public class customBool {

	private volatile boolean b;
	
	public customBool(boolean i){
		b = i;
	}
	
	public customBool(){
		;
	}
	
	public synchronized void set(boolean i) {
		change(0,i);
	}
	
	public synchronized boolean get() {
		return change(1,b);
	}
	
	public synchronized void invert() {
		change(2,b);
	}
	
	private synchronized boolean change(int n, boolean p) {
		if(n == 0) {
			b = p;
		}else if(n == 1) {
			return b;
		}else if(n == 2) {
			b = !b;
		}
		return false;
	}
}
