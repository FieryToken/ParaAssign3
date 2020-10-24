package stuff;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

	static int nodeCount;
	static int maxNeighbours;
	static List<NodeAbstract> nodes;
	static boolean election = false;
	static String graphtyp;
	static int third_type;
	public static void main(String[] args) {
//		System.out.println(
//				"First choose the graph type. You can use Ring, Tree, "
//				+ "Complete or Random. Second, choose the number of nodes. "
//				+ "The third thing you have to do is specify the branches, only for the Tree or the maximum "
//				+ "number of neighbors, only for Random. If you do not enter anything as the third argument, "
//				+ "the value 0 is entered automatically.");
		for (int i = 0; i < args.length; i++) {
			if(args[0] != "" || args[1] != "") {
				try {
					if(i == 0) {
						graphtyp = args[i];
					}
					if(i == 1) {
						nodeCount = Integer.parseInt(args[i]);	
					}
					if((graphtyp.equals("Tree") || graphtyp.equals("Random")) && i == 2) {
						third_type = Integer.parseInt(args[2]);
					}
				}catch(Exception e) {
					System.out.println(e);
				}
			}else if(args[0] == "" && args[1] != ""){
				System.out.println("Error: Graphtype is missing!");
			}else if(args[1] == "" && args[0] != "") {
				System.out.println("Error: Count of Nodes is missing!");
			}else if(args[1] == "" && args[0] == "") {
				System.out.println("Error: Graphtype and count of Nodes are missing!");
			}
		}
		startThis();
		/*
		 * String data; Scanner scan = new Scanner(System.in);
		 * 
		 * System.out.println("Trage deine Daten ein: "); data = scan.nextLine();
		 * 
		 * System.out.println("Deine Eingabe lautet: "+data);
		 */

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
		if(graphtyp.equals("Ring")) {
			System.out.println("Graphtyp: Ring");
			Ring();
		}
		if(graphtyp.equals("Random")) {
			maxNeighbours = third_type;
			System.out.println("Graphtyp: Random");
			Random();
		}
		if(graphtyp.equals("Complete")) {
			System.out.println("Graphtyp: Complete");
			Complete();
		}
		if(graphtyp.equals("Tree")) {
			System.out.println("Graphtyp: Tree");
			Tree(third_type);
		}
		setupNodeValues();
		startNodes();
	}

	private static void startNodes() {
		for (NodeAbstract node : nodes) {
			node.start();
		}

	}

	private static void setupNodeValues() {
		for (NodeAbstract node : nodes) {
			node.setupValues();
		}
	}

	private static void Random() {
		for (NodeAbstract node : nodes) {
			System.out.println("maxNeighbours: "+maxNeighbours);
			int neighboursSize = (int) ((Math.random() * (maxNeighbours - 1)) + 1);
			Node[] neighbours = new Node[neighboursSize];
			for (int n = 0; n < neighboursSize; ++n) {
				int randomNeighbour = (int) ((Math.random() * (nodeCount - 0)) + 0);
				neighbours[n] = nodes.get(randomNeighbour);
			}
			node.setupNeighbours(neighbours);
		}

	}

	private static void Tree(int branches_given) {
		for(int n = 0; n < nodes.size()/2; ++n) {
			Collections.swap(nodes, n, nodes.size()-1-n);
		}
		int branches = branches_given;
		int ab = 0;
		int bis = 0;
		for (int i = 0; i < branches; ++i) {
			if (i == 0) {
				System.out.println("Erste Node " + nodes.get(0));
				ab = (((nodes.size() / branches) * (i + 1)) - (nodes.size() / branches)) + 1;
			} else {
				ab = (((nodes.size() / branches) * (i + 1)) - (nodes.size() / branches));
			}
			System.out.println("ab: " + ab);
			if (i == branches - 1) {
				bis = nodes.size();
				System.out.println("bis1: " + bis);
			} else {
				bis = ((nodes.size() / branches) * (i + 1)) - 1;
				System.out.println("bis2: " + bis);
			}
			for (int n = ab; n < bis; ++n) {
				NodeAbstract node = nodes.get(n);
				if (n == ab) {
					node.setupNeighbours(nodes.get(0));
				} else {
					node.setupNeighbours(nodes.get(n - 1));

					if(n+1 == bis) {
						if(bis != nodes.size()) {
							node.setupNeighbours(nodes.get(n+1));
						}
					}
				}
			}
		}
		System.out.println("SetupNeighbours is done");
//		for(int n = 0; n < nodes.size()/2; ++n) {
//			Collections.swap(nodes, n, nodes.size()-1-n);
//		}
	}

	private static void Complete() {
		for (NodeAbstract node : nodes) {
			Node[] neighbours = new Node[nodes.size()];
			for (int n = 0; n < nodes.size(); ++n) {
				if (node == nodes.get(n)) {
					;
				} else {
					neighbours[n] = nodes.get(n);
				}
			}
			node.setupNeighbours(neighbours);
		}
	}

	private static void Ring() {
		int currentNode = 0;
		for (NodeAbstract node : nodes) {
			if (currentNode != nodes.size() - 1) {
				node.setupNeighbours(nodes.get(currentNode + 1));
			} else {
				node.setupNeighbours(nodes.get(0));
			}
			++currentNode;
		}
	}

	private static void createNodes() {
		nodes = new ArrayList<NodeAbstract>();

//		if (!election) {
//			for (int i = 0; i < nodeCount; ++i) {
////				String name = RandomString.createString(2);
//				if (i == 0) {
//					nodes.add(NodeFactory.getInstance(Integer.toString(i), true));
//				} else {
//					nodes.add(NodeFactory.getInstance(Integer.toString(i), false));
//				}
//			}
		if (!election) {
			for (int i = 0; i < nodeCount; ++i) {
//				String name = RandomString.createString(2);
				if (i == nodeCount-1) {
					nodes.add(NodeFactory.getInstance(Integer.toString(i), true));
				} else {
					nodes.add(NodeFactory.getInstance(Integer.toString(i), false));
				}
			}
		}
	}
}