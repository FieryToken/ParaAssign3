package utils;

public class customInt {
	
	private volatile int a;
	
	public customInt(int i){
		a = i;
	}
	
	public customInt(){
		;
	}
	
	public synchronized void increment() {
		change(0,0);
	}
	public synchronized void decrement() {
		change(1,0);
	}
	
	public synchronized void set(int i) {
		change(2,i);
	}
	
	public synchronized int get() {
		return change(3,0);
	}
	
	private synchronized int change(int operation, int p) {
		if(operation == 0) {
			++a;
		}else if(operation == 1) {
			--a;
		}else if(operation == 2) {
			a = p;
		}else if(operation == 3) {
			return a;
		}
		return a;
	}
	
}
