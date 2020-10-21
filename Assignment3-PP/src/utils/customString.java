package utils;

public class customString {
	
	private volatile String a;
	
	public customString() {
		a = "";
	}
	
	public customString(String a) {
		this.a = a;
	}
	
	public synchronized String get() {
		return toString();
	}
	
	@Override
	public synchronized String toString() {
		return a;	
	}
	
	public synchronized void set(String toSet) {
		a = toSet;
	}
	
	public synchronized void set(customString toSet) {
		a = toSet.get();
	}
	
	public synchronized void append (String toAppend) {
		a = a + toAppend;
	}
	
	public synchronized String substring (int startIndex) {
		return a.substring(startIndex);
	}
	
	public synchronized String substring (int startIndex, int endIndex) {
		return a.substring(startIndex,endIndex);
	}
	
	public synchronized int compareTo (String toCompare) {
		return a.compareTo(toCompare);
	}
	
	public synchronized int compareTo (customString toCompare) {
		return a.compareTo(toCompare.get());
	}
}
