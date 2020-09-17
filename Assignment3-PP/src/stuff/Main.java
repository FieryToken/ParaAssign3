package stuff;

public class Main {
	
	public static void main(String[]args) {
		
		/*String data;
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Trage deine Daten ein: ");
		data = scan.nextLine();
		
		System.out.println("Deine Eingabe lautet: "+data);*/
		
		NodeAbstract a = NodeFactory.getInstance("a",false);
		NodeAbstract b = NodeFactory.getInstance("b",false);
		NodeAbstract c = NodeFactory.getInstance("c",false);
		NodeAbstract d = NodeFactory.getInstance("d",false);
		NodeAbstract e = NodeFactory.getInstance("e",true);
		
		a.setupNeighbours(b, d);
		b.setupNeighbours(c);
		c.setupNeighbours(a, c);
		d.setupNeighbours(c, d);
		e.setupNeighbours(a, b, c, d);
		
		a.start();
		b.start();
		c.start();
		d.start();
		e.start();
		
	}
}