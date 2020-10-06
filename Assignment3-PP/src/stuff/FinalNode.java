package stuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FinalNode extends NodeAbstract {

	protected final String name;

	protected final boolean initiator;
	private boolean awake;

	private Node messenger;

	private int replies;
	private int expMsg;

	protected final Set<Node> neighbours = new HashSet<Node>();
	private List<Node> sendNeighbours;
	private List<Node> synchroList;
	
//	private List<Node> path = new ArrayList<Node>();				
//	private List<List<Node>> allPaths = new ArrayList<List<Node>>();

	public FinalNode(String name, boolean initiator) {
		super(name, initiator);
		this.name = name;
		this.initiator = initiator;
		awake = false;
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
		incrementReplies();
		// Wenn der Knoten noch nicht wach ist weck ihn auf und speicher den Knoten der
		// diesen aufgeweckt hat in messenger.
		if (!awake) {
			System.out.println(name + " ist nun wach von " + messenger);
			this.messenger = messenger;
			awake = true;
			this.notifyAll();
		} else {
			System.out.println(name + " war schon wach. Hat aber Nachricht von " + messenger + " erhalten.");
		}
	}

	@Override
	public synchronized void echo(Node neighbour) {
		incrementReplies();
		System.out.println("Echo von " + name + " aufgerufen durch " + neighbour);
	}

	public void run() {
		expMsg = this.neighbours.size();
		sendNeighbours = new ArrayList<Node>(neighbours);
		synchroList = Collections.synchronizedList(sendNeighbours);
		// Teste ob Knoten initiator ist und gebe Nachbarn aus
		isInitiator();
		HashSettoString(neighbours);
		//Wenn Knoten nicht wach ist: wait()
		while (!awake) {
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sendWakeup();
	}


	
	private void sendWakeup() {
		if (awake) {
			Iterator<Node> it = neighbours.iterator();
			while (it.hasNext()) {
				Node neighbour = it.next();
				// Wenn messenger(der Knoten der diesen aufgeweckt hat) == dem Knoten ist der
				// eine wakeup Nachricht bekommen soll
				// dann nichts tun
				if (!(messenger == neighbour)) {
					// Wenn dieser Knoten noch in der sendNeighbours Liste des Knoten enthalten ist
					// den er anrufen will. Ruf diesen an.
					// Dann entferne den Angerufenen Knoten aus dieser sendNeighbour Liste
					// Und wecke den Knoten auf
					if (!neighbour.messageSent(this)) {
						synchroList.remove(neighbour);
						System.out.println(name + " ruft " + neighbour + " an");
						neighbour.wakeup(this);
					} else {
						System.out.println(
								neighbour + " Hat diesem Knoten " + name + " schon eine WakeupNachricht geschickt.");
					}
				} else {
					System.out.println(name + " ruft nicht " + neighbour
							+ " an da er von diesem aufgeweckt wurde");
				}
			}
		}
		sendEcho();
	}
	
	//******alte sendEcho() version******
//		private void sendEcho() {
//		boolean notdone = true;
//		while (notdone) {
//			if (replies == expMsg && !initiator) {
//				path.add(this);
//				messenger.echo(this);
//				notdone = false;
//			} else if (replies == expMsg && initiator) {
//				System.out.println("Initiator fertig");
//				notdone = false;
//			} else {
//				System.out.println(name + ": " + replies + " Nachrichten erhalten " + expMsg + " benötigt");
//			}
//		}
//	}
	
	private void sendEcho() {
		while (!(replies == expMsg)) {
			try {
				synchronized (this) {
					this.wait(200);
					System.out.println(name + ": " + replies + " Nachrichten erhalten " + expMsg + " benötigt");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!initiator) {
			messenger.echo(this);
		} else if (initiator) {
			System.out.println("Initiator fertig");
		} else {
			System.out.println(name + ": " + replies + " Nachrichten erhalten " + expMsg + " benötigt");
		}
	}
	
	//sicherstellen das replies nur von einem Thread erhöht wird
	private synchronized void incrementReplies() {
		++replies;
		this.notifyAll();
	}
	
	private void isInitiator() {
		// Wenn der Knoten ein Initiator ist, ist er automatisch wach.
		if (initiator) {
			System.out.println("Knoten " + name + " ist der Initiator");
			awake = true;
		} else
			System.out.println("Knoten " + name + " ist NICHT der Initiator");
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

	public boolean messageSent(Node messenger) {
		if (synchroList.contains(messenger)) {
			return false;
		} else {
			synchronized (this) {
				//Dieser Knoten erwartet eine nachricht weniger da er keine wakeup nachricht von dem Knoten bekommen wird. 
				//Da er diesem schon eine geschickt hat
				--expMsg;
				this.notifyAll();
			}
			return true;
		}
	}

}
