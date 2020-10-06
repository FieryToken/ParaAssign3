package stuff;

public class Main {
	
	public static void main(String[]args) {
		
		/*String data;
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Trage deine Daten ein: ");
		data = scan.nextLine();
		
		System.out.println("Deine Eingabe lautet: "+data);*/
		
		NodeAbstract a = NodeFactory.getInstance("a",true);
		NodeAbstract b = NodeFactory.getInstance("b",false);
		NodeAbstract c = NodeFactory.getInstance("c",false);
		NodeAbstract d = NodeFactory.getInstance("d",false);
		
		a.setupNeighbours(b);
		b.setupNeighbours(c,d);
		c.setupNeighbours(b);
		d.setupNeighbours(b);
		
		a.start();
		b.start();
		c.start();
		d.start();
		
//		NodeAbstract a = NodeFactory.getInstance("a",false);
//		NodeAbstract b = NodeFactory.getInstance("b",false);
//		NodeAbstract c = NodeFactory.getInstance("c",false);
//		NodeAbstract d = NodeFactory.getInstance("d",false);
//		NodeAbstract e = NodeFactory.getInstance("e",true);
//		NodeAbstract f = NodeFactory.getInstance("f",false);
//		NodeAbstract g = NodeFactory.getInstance("g",false);
//		NodeAbstract h = NodeFactory.getInstance("h",false);
//		NodeAbstract i = NodeFactory.getInstance("i",false);
//		
//		a.setupNeighbours(b, d, f);
//		b.setupNeighbours(c);
//		c.setupNeighbours(a, c);
//		d.setupNeighbours(c, d);
//		f.setupNeighbours(g, h, i);
//		g.setupNeighbours(h, b);
//		h.setupNeighbours(i);
//		i.setupNeighbours(c, d);
//		e.setupNeighbours(a, b, c, d,h);
//
//		a.start();
//		b.start();
//		c.start();
//		d.start();
//		f.start();
//		g.start();
//		h.start();
//		i.start();
//		e.start();
		
//		NodeAbstract a = NodeFactory.getInstance("a",false);
//		NodeAbstract b = NodeFactory.getInstance("b",false);
//		NodeAbstract c = NodeFactory.getInstance("c",false);
//		NodeAbstract d = NodeFactory.getInstance("d",false);
//		NodeAbstract e = NodeFactory.getInstance("e",true);
//		
//		a.setupNeighbours(b, d);
//		b.setupNeighbours(c);
//		c.setupNeighbours(a, c);
//		d.setupNeighbours(c, d);
//		e.setupNeighbours(a, b, c, d);
//		
//		a.start();
//		b.start();
//		c.start();
//		d.start();
//		e.start();
		
		
		
	}
}