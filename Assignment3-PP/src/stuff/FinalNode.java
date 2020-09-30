package stuff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FinalNode extends NodeAbstract {

	protected final String name;

	protected final boolean initiator;
	private boolean awake;
	private boolean msgSent;

	private Node messenger;

	private int replies;
	private int expMsg;

	protected final Set<Node> neighbours = new HashSet<Node>();
	// private Set<Node> sendNeighbours = new HashSet<Node>();
	private List<Node> sendNeighbours;

	public FinalNode(String name, boolean initiator) {
		super(name, initiator);
		this.name = name;
		this.initiator = initiator;
		awake = false;
		msgSent = false;
		replies = 0;
	}

	@Override
	public void hello(Node neighbour) {
		if (!neighbours.contains(neighbour)) {
			neighbours.add(neighbour);
		}
	}

	@Override
	public synchronized void wakeup(Node messenger) {
		++replies;
		// Wenn der Knoten noch nicht wach ist weck ihn auf und speicher den Knoten der
		// diesen aufgeweckt hat in messenger.
		if (!awake) {
			awake = true;
			System.out.println(name + " ist nun wach von " + messenger);
			this.messenger = messenger;
		} else {
			System.out.println(name + " war schon wach. Hat aber Nachricht von " + messenger + " erhalten.");
		}
	}

	@Override
	public synchronized void echo(Node neighbour) {
		++replies;
		System.out.println("Echo von " + name + " aufgerufen durch " + neighbour);
	}

	public void run() {
		expMsg = this.neighbours.size();
		sendNeighbours = new ArrayList<Node>(neighbours);
		// Teste ob Knoten initiator ist und gebe Nachbarn aus
		isInitiator();
		HashSettoString(neighbours);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Wenn noch keine Nachrichten versendet worden sind und der Knoten wach ist.
		// Sende Nachrichten an alle Nachbarn. Anschließend msgSent auf true.
		while (!msgSent) {
			if (awake) {
				Iterator<Node> it = neighbours.iterator();
				// war vorher in der hello method
				while (it.hasNext()) {
					Node neighbour = it.next();
					// Wenn messenger(der Knoten der diesen aufgeweckt hat) == dem Knoten ist der
					// eine wakeup Nachricht bekommen soll
					// dann nichts tun
					if (!(messenger == neighbour)) {
						System.out.println(name + " überprüft " + neighbour + " anzurufen");
						//Wenn dieser Knoten noch in der sendNeighbours Liste des Knoten enthalten ist den er anrufen will. Ruf diesen an.
						//Dann entferne den Angerufenen Knoten aus dieser sendNeighbour Liste
						//Und wecke den Knoten auf
						//************************************************************
						//************************* Problem *************************
						//************************************************************
						//Wenn a -> c anruft 
						//und c -> a gleichzeitig anruft
						// kann es passieren das sich beide eine wakeup nachricht schicken was nicht vorgesehen ist da über jede kante nur eine nachricht pro Typ geschickt werden soll
						// Passiert in 80% der Fälle needs to be Fixed da so der Algorithmus niemals funktionieren wird
	
						if (!neighbour.sendMessage(this)) {
							sendNeighbours.remove(neighbour);
							System.out.println(name + " ruft " + neighbour + " an");
							neighbour.wakeup(this);
						} else {
							System.out.println(neighbour + " Hat diesem Knoten " + name
									+ " schon eine WakeupNachricht geschickt.");
						}
					} else {
						System.out.println(name + "ruft nicht " + neighbour
								+ " an da er von diesem Knoten bereits eine Wakeup Nachricht bekommen hat");
					}
					// bis hier
				}
				msgSent = true;
			}
		}
		// wenn ?irgendwas? dann sende ein echo an messenger
		sendEcho();
	}

	private void sendEcho() {
		boolean notdone = true;
		while (true && notdone) {
			System.out.println("Its me: " + name);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (replies == expMsg && !initiator) {
				messenger.echo(this);
				notdone = false;
			} else if (replies == expMsg && initiator) {
				System.out.println("Initiator fertig");
				notdone = false;
			} else {
				System.out.println(name + ": " + replies + " Nachrichten erhalten " + expMsg + " benötigt");
			}
		}
	}

	private void isInitiator() {
		// Wenn der Knoten ein Initiator ist, ist er automatisch wach.
		if (initiator) {
			System.out.println("Knoten " + name + " ist der Initiator");
			awake = true;
		} else
			System.out.println("Knoten " + name + " ist NICHT der Initiator");
	}

	public boolean isAwake() {
		return awake;
	}

	private void HashSettoString(Set<?> set) {
		System.out.println("Nachbarn von Knoten " + name + ": "
				+ set.stream().map(Object::toString).collect(Collectors.joining(",")));
	}

	@Override
	public void setupNeighbours(Node... neighbours) {
		for (int i = 0; i < neighbours.length; i++) {
			this.neighbours.add(neighbours[i]);
			neighbours[i].hello(this);
		}
	}

	public boolean sendMessage(Node messenger) {
		if (sendNeighbours.contains(messenger)) {
			return false;
		} else {
			--expMsg;
			return true;
		}
	}

}
