package stuff;

import java.util.ArrayList;
import java.util.List;

public class Main {
	
	static int nodeCount = 10;
	static int maxNeighbours = 5;
	static List<NodeAbstract> nodes;
	static boolean election = true;
	
	public static void main(String[] args) {
		
		
//		startThis();
		/*String data;
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Trage deine Daten ein: ");
		data = scan.nextLine();
		
		System.out.println("Deine Eingabe lautet: "+data);*/
		
//		NodeAbstract a = NodeFactory.getInstance("a",true);
//		NodeAbstract b = NodeFactory.getInstance("b",false);
//		NodeAbstract c = NodeFactory.getInstance("c",false);
//		NodeAbstract d = NodeFactory.getInstance("d",false);
//		NodeAbstract e= NodeFactory.getInstance("e",false);
//		NodeAbstract f = NodeFactory.getInstance("f",false);
//		
//		a.setupNeighbours(b);
//		b.setupNeighbours(c);
//		c.setupNeighbours(d);
//		d.setupNeighbours(e);
//		e.setupNeighbours(f);
//		f.setupNeighbours(a);
//		
//		a.setupValues();
//		b.setupValues();
//		c.setupValues();
//		d.setupValues();
//		e.setupValues();
//		f.setupValues();
//		
//		a.start();
//		b.start();
//		c.start();
//		d.start();
//		e.start();
//		f.start();
//		
//		NodeAbstract a = NodeFactory.getInstance("a",true);
//		NodeAbstract b = NodeFactory.getInstance("b",false);
//		NodeAbstract c = NodeFactory.getInstance("c",false);
//		NodeAbstract d = NodeFactory.getInstance("d",false);
//		
//		a.setupNeighbours(b);
//		b.setupNeighbours(c,d);
//		c.setupNeighbours(b);
//		d.setupNeighbours(b);
//		
//		a.start();
//		b.start();
//		c.start();
//		d.start();
		
		
		
//		NodeAbstract a = NodeFactory.getInstance("a",false);
//		NodeAbstract b = NodeFactory.getInstance("b",false);
//		NodeAbstract c = NodeFactory.getInstance("c",false);
//		NodeAbstract d = NodeFactory.getInstance("d",false);
//		NodeAbstract e = NodeFactory.getInstance("e",false);
//		NodeAbstract f = NodeFactory.getInstance("f",false);
//		NodeAbstract g = NodeFactory.getInstance("g",true);
//		NodeAbstract h = NodeFactory.getInstance("h",false);
//		NodeAbstract i = NodeFactory.getInstance("i",true);
//		
//		a.setupNeighbours(b);
//		b.setupNeighbours(c);
//		c.setupNeighbours(b);
//		d.setupNeighbours(f);
//		f.setupNeighbours(c);
//		g.setupNeighbours(h);
//		h.setupNeighbours(c);
//		i.setupNeighbours(e);
//		e.setupNeighbours(c);
//		
//		a.setupValues();
//		b.setupValues();
//		c.setupValues();
//		d.setupValues();
//		e.setupValues();
//		f.setupValues();
//		g.setupValues();
//		h.setupValues();
//		i.setupValues();
//		e.setupValues();
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
//		
		
		NodeAbstract a = NodeFactory.getInstance("a",false);
		NodeAbstract b = NodeFactory.getInstance("b",false);
		NodeAbstract c = NodeFactory.getInstance("c",true);
		NodeAbstract d = NodeFactory.getInstance("d",false);
		NodeAbstract e = NodeFactory.getInstance("e",true);
		NodeAbstract f = NodeFactory.getInstance("f",false);
		NodeAbstract g = NodeFactory.getInstance("g",true);
		NodeAbstract h = NodeFactory.getInstance("h",false);
		NodeAbstract i = NodeFactory.getInstance("i",true);
//		
		a.setupNeighbours(b, d, f);
		b.setupNeighbours(c);
		c.setupNeighbours(a, c);
		d.setupNeighbours(c, d);
		f.setupNeighbours(g, h, i);
		g.setupNeighbours(h, b);
		h.setupNeighbours(i);
		i.setupNeighbours(c, d);
		e.setupNeighbours(a, b, c, d,h);
		
		a.setupValues();
		b.setupValues();
		c.setupValues();
		d.setupValues();
		e.setupValues();
		f.setupValues();
		g.setupValues();
		h.setupValues();
		i.setupValues();
		e.setupValues();

		a.start();
		b.start();
		c.start();
		d.start();
		f.start();
		g.start();
		h.start();
		i.start();
		e.start();
		
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
		private static void startThis() {
			createNodes();
			setupNodeNeighbours();
			setupNodeValues();
			startNodes();
		}

		private static void startNodes() {
			for(NodeAbstract node : nodes) {
				node.start();
			}
			
		}
		
		private static void setupNodeValues() {
			for (NodeAbstract node : nodes ) {
				node.setupValues();
			}
		}
		
		private static void setupNodeNeighbours() {
			for (NodeAbstract node : nodes) {
				int neighboursSize = (int) ((Math.random() * (maxNeighbours - 1)) + 1);
				Node[] neighbours = new Node[neighboursSize];
				for (int n = 0; n < neighboursSize; ++n) {
					int randomNeighbour = (int) ((Math.random() * (nodeCount - 0)) + 0);
					neighbours[n] = nodes.get(randomNeighbour);
				}
				node.setupNeighbours(neighbours);
			}

		}

		private static void createNodes() {
			nodes = new ArrayList<NodeAbstract>();

			if (!election) {
				for (int i = 0; i < nodeCount; ++i) {
					String name = RandomString.createString(10);
					if (i == nodeCount - 1) {
						nodes.add(NodeFactory.getInstance(name, true));
					} else {
						nodes.add(NodeFactory.getInstance(name, false));
					}
				}
			} else {
				boolean leader = false;
				for (int i = 0; i < nodeCount; ++i) {
					leader = Math.random() < 0.5;
					String name = RandomString.createString(10);
					nodes.add(NodeFactory.getInstance(name, leader));
				}
			}
		}
}
		
	
