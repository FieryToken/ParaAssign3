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
	
	private synchronized int change(int n, int p) {
		if(n == 0) {
			++a;
		}else if(n == 1) {
			--a;
		}else if(n == 2) {
			a = p;
		}else if(n == 3) {
			return a;
		}
		return 0;
	}
	
}
