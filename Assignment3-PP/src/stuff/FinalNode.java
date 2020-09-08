package stuff;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class FinalNode extends NodeAbstract {

	protected final String name;
	protected final boolean initiator;
	private boolean awake;
	private boolean msgSent;
	private Node president;
	protected final Set<Node> neighbours = new HashSet<Node>();

	public FinalNode(String name, boolean initiator) {
		super(name, initiator);
		this.name = name;
		this.initiator = initiator;
		awake = false;
		msgSent = false;
	}

	@Override
	public void hello(Node neighbour) {
		if (!neighbour.isAwake()) {
			System.out.println(name + " ruft " + neighbour + " an");
			if(initiator) {
				neighbour.wakeup(this);
			}else {
				neighbour.wakeup(president);
			}
		} else {
			System.out.println(neighbour + " ist schon wach");
		}
	}

	@Override
	public synchronized void wakeup(Node neighbour) {
		awake = true;
		System.out.println(name + " ist nun wach von " + neighbour);
		if (president == null) {
			president = neighbour;
			System.out.println(name+ " wurde von "+ president+" aufgeweckt");
		}
	}

	@Override
	public void echo(Node neighbour, Object data) {

	}

	@Override
	public void setupNeighbours(Node... neighbours) {
		for (int i = 0; i < neighbours.length; i++) {
			this.neighbours.add(neighbours[i]);
		}
	}

	public void run() {
		isInitiator();
		HashSettoString();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (!msgSent) {
			if (awake) {
				Iterator<Node> it = neighbours.iterator();
				while (it.hasNext()) {
					hello(it.next());
				}
				msgSent = true;
			}
		}
	}

	private void isInitiator() {
		if (initiator) {
			System.out.println("Knoten " + name + " ist der Initiator");
			awake = true;
		} else
			System.out.println("Knoten " + name + " ist NICHT der Initiator");
	}

	public boolean isAwake() {
		return awake;
	}

	private void HashSettoString() {
		System.out.println("Nachbarn von Knoten " + name + ": "
				+ neighbours.stream().map(Node::toString).collect(Collectors.joining(",")));
	}

}
