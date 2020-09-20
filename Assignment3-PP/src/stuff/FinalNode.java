package stuff;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class FinalNode extends NodeAbstract {

	protected final String name;
	protected final boolean initiator;
	private boolean awake;
	private boolean reading;
	private boolean msgSent;
	private Node president;
	private int replies;
	private int expMsg;
	private Node messenger;
	protected final Set<Node> neighbours = new HashSet<Node>();

	public FinalNode(String name, boolean initiator) {
		super(name, initiator);
		this.name = name;
		this.initiator = initiator;
		awake = false;
		msgSent = false;
		replies = 0;
		reading = false;
	}

	@Override
	public void hello(Node neighbour) {
		// Wenn messenger(der Knoten der diesen aufgeweckt hat) == dem Knoten ist der eine wakeup Nachricht bekommen soll
		// dann nichts tun
		if (!(messenger == neighbour)) {
			System.out.println(name + " ruft " + neighbour + " an");
			//Wenn es sich bei diesem Knoten um einen Initiator handelt schickt dieser sich einmal als
			//Messenger und als "president" mit.
			//Wenn nicht schick den "president" mit und sich selbst als messenger
			if(initiator) {
				if(!neighbour.isAwake()) {
					neighbour.wakeup(this, this);
				}else {
					//wenn der knoten schon wach ist dann erwartete Nachrichten -1
					neighbour.wakeup(this, this);
					--expMsg;
				}
			}else {
				if(!neighbour.isAwake()) {
					neighbour.wakeup(president, this);
				}else {
					//wenn der knoten schon wach ist dann erwartete Nachrichten -1
					neighbour.wakeup(president, this);
					--expMsg;
				}
			}
		}else {
			System.out.println(name + "ruft nicht"+neighbour+"an da er von dem angerufen wurde");
		}
	}

	@Override
	public synchronized void wakeup(Node neighbour, Node messenger) {
		//Nachrichten an sich selbst ignorieren, ansonsten replies +1
		if(messenger != this) {
			++replies;	
		}
		//Wenn der Knoten noch nicht wach ist weck ihn auf und speicher den Knoten der diesen aufgeweckt hat in messenger.
		if(!awake) {
			awake = true;
			System.out.println(name + " ist nun wach von " + messenger);
			this.messenger = messenger;
			
		}else {
			System.out.println(name + " war schon wach. Hat aber Nachricht von " + messenger+" erhalten.");
		}
		//Wenn es noch keinen president gibt dann nehme den den der messenger mitgeschitk hat
		if (president == null) {
			president = neighbour;
			System.out.println(name+ " President ist: "+ president);
		}
		
		if(!neighbours.contains(messenger)) {
				++expMsg;
		}
	}

	@Override
	public synchronized void echo(Node neighbour) {
		++replies;
		System.out.println("Echo von "+ name+" aufgerufen durch "+neighbour);
	}

	@Override
	public void setupNeighbours(Node... neighbours) {
		for (int i = 0; i < neighbours.length; i++) {
			this.neighbours.add(neighbours[i]);
		}
		expMsg = this.neighbours.size();
	}

	public void run() {
		boolean notdone = true;
		//Teste ob Knoten initiator ist und gebe Nachbarn aus
		isInitiator();
		HashSettoString();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Wenn noch keine Nachrichten versendet worden sind und der Knoten wach ist.
		//Sende Nachrichten an alle Nachbarn. Anschließend msgSent auf true.
		while (!msgSent) {
			if (awake) {
				reading = true;
				Iterator<Node> it = neighbours.iterator();
				while (it.hasNext()) {
					hello(it.next());
				}
				msgSent = true;
				reading = false;
			}
		}
		// wenn ?irgendwas? dann sende ein echo an messenger
		while(true && notdone) {
			System.out.println("Its me: "+name);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(replies == expMsg && !initiator) {
				messenger.echo(this);
				notdone = false;
			}
			else if(replies == expMsg && initiator) {
				System.out.println("Initiator fertig");
				notdone = false;
			}
			else {
				System.out.println(name+": "+replies+" Nachrichten erhalten "+ expMsg+" benötigt");
			}
		}
	}

	private void isInitiator() {
		//Wenn der Knoten ein Initiator ist, ist er automatisch wach.
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
