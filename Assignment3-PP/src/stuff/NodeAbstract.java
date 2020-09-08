package stuff;

import java.util.HashSet;
import java.util.Set;

public abstract class NodeAbstract extends Thread implements Node {

	protected final String name;
	protected final boolean initiator;
	protected final Set<Node> neighbours = new HashSet<Node>();

	public NodeAbstract(String name, boolean initiator) {
		super(name);
		this.name = name;
		this.initiator = initiator;
	}

	/**
	 * Call method <code>hello</code> of each neighbour here.
	 */
	public abstract void setupNeighbours(Node... neighbours);
	
	public void run(){
		System.out.println("Start mit Knoten: "+name);
		if(initiator)
			System.out.println("Er ist der Initiator");
		else
			System.out.println("Er ist kein Initiator");
		
		System.out.println("Seine Nachrbarn sind: ");
		StringBuilder sb = new StringBuilder();
		for(Node n : neighbours) 
		 		sb.append(n.toString()).append(", ");
	}

	/** Utility method to print this node in a readable way */
	@Override
	public String toString() {
		return name;
	}

}