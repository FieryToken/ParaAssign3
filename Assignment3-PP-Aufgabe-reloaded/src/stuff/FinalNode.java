package stuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import utils.customBool;
import utils.customInt;
import utils.customString;

public class FinalNode extends NodeAbstract {

	protected final String name;
	private customString id;

	protected final boolean initiator;
	private boolean awake;
	private customBool gotReset;

	private Node messenger;

	private customInt replies;
	private customInt expMsg;

	protected final Set<Node> neighbours = new HashSet<Node>();

	private List<Node> newSynchroList;
	private List<Node> sendNeighbours;
	private List<Node> synchroList;

	private List<Node> path = new ArrayList<Node>();
	private List<List<Node>> allPaths = new ArrayList<List<Node>>();

	private static customInt allWinner = new customInt(0);

	public FinalNode(String name, boolean initiator) {
		super(name, initiator);
		this.name = name;
		this.initiator = initiator;
		awake = false;
		replies = new customInt(0);
		gotReset = new customBool(false);
		if (initiator) {
			id = new customString(name);
		} else {
			id = new customString();
		}
	}

	public void run() {
//		expMsg = this.neighbours.size();
//		sendNeighbours = new ArrayList<Node>(neighbours);
//		synchroList = Collections.synchronizedList(sendNeighbours);
		// Teste ob Knoten initiator ist und gebe Nachbarn aus
		isInitiator();
		HashSettoString(neighbours);
		// Wenn Knoten nicht wach ist: wait()
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

	@Override
	public synchronized void wakeup(Node messenger, customString id) {
		if(this.messenger != messenger) {
			incrementReplies();
		}
		if (this.id.get() != "") {
			if (0 > this.id.compareTo(id)) {
				this.id.set(id.get());
				this.messenger = messenger;
				reset();
			} else if (0 < this.id.compareTo(id)) {
				messenger.setId(this.id);
				((FinalNode) messenger).setMessenger(this);
				((FinalNode) messenger).reset();
			} else if (0 == this.id.compareTo(id)) {
				;
			}
		}else {
			this.id.set(id.get());
		}
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
	public synchronized void echo(Node neighbour, customString id) {
		incrementReplies();
//		if(0 > this.id.compareTo(id)) {
//			this.id.set(id.get());
//			this.messenger = neighbour;
//			reset();
//		}else if(0 < this.id.compareTo(id)) {
//			neighbour.setId(this.id);
//			((FinalNode) neighbour).setMessenger(this);
//			((FinalNode) neighbour).reset();
//		}else if(0 == this.id.compareTo(id)) {
//			;
//		}
		System.out.println("Echo von " + name + " aufgerufen durch " + neighbour);
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
					if (!newMessageReceived(neighbour)) {
						synchroList.remove(neighbour);
						System.out.println(name + " ruft " + neighbour + " an");
						neighbour.wakeup(this, id);
					} else {
						System.out.println(
								neighbour + " Hat diesem Knoten " + name + " schon eine WakeupNachricht geschickt.");
					}
				} else {
					System.out.println(name + " ruft nicht " + neighbour + " an da er von diesem aufgeweckt wurde");
				}
			}
		}
		sendEcho();
	}

	private void resend() {
		if (awake) {
			Iterator<Node> it = neighbours.iterator();
			while (it.hasNext() && !gotReset.get()) {
				Node neighbour = it.next();
				// Wenn messenger(der Knoten der diesen aufgeweckt hat) == dem Knoten ist der
				// eine wakeup Nachricht bekommen soll
				// dann nichts tun
				if (!(messenger == neighbour && !gotReset.get())) {
					// Wenn dieser Knoten noch in der sendNeighbours Liste des Knoten enthalten ist
					// den er anrufen will. Ruf diesen an.
					// Dann entferne den Angerufenen Knoten aus dieser sendNeighbour Liste
					// Und wecke den Knoten auf
					if (!newMessageReceived(neighbour) && !gotReset.get()) {
						synchroList.remove(neighbour);
						System.out.println(name + " ruft " + neighbour + " an");
						if(this.id.get() != neighbour.getNodeId() && !gotReset.get()) {
							neighbour.wakeup(this, id);
						}
					} else {
						System.out.println(
								neighbour + " Hat diesem Knoten " + name + " schon eine WakeupNachricht geschickt.");
					}
				} else {
					System.out.println(name + " ruft nicht " + neighbour + " an da er von diesem aufgeweckt wurde");
				}
			}
		}
	}

	private synchronized void sendEcho() {
		while (!(replies.get() == expMsg.get() )) {
			try {
				synchronized (this) {
					this.wait(200);
					System.out.println(
							name + ": " + replies.get() + " Nachrichten erhalten " + expMsg.get() + " benötigt");
					if (gotReset.get()) {
						gotReset.set(false);
						resend();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (messenger != null) {
			messenger.echo(this, id);
		} else if (initiator) {
			if (this.id.get() == name) {
				System.out.println("Knoten " + name + " hat gewonnen!!!!!!!" + this.id.get());
				allWinner.increment();
				System.out.println("Gewinner: " + allWinner.get() + " !!!!!!!");
			} else {
				System.out.println("Knoten " + name + " hat verloren....... " + this.id.get());
			}
			System.out.println("Initiator fertig");
		} else {
			System.out.println(name + ": " + replies + " Nachrichten erhalten " + expMsg + " benötigt");
		}
	}

	// sicherstellen das replies nur von einem Thread erhöht wird
	private synchronized void incrementReplies() {
		replies.increment();
		;
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

	public void setupValues() {
		expMsg = new customInt(this.neighbours.size());
		sendNeighbours = new ArrayList<Node>(this.neighbours);
//		synchroList = Collections.synchronizedList(new ArrayList<Node>());	
//		for (Node node : sendNeighbours) {
//			synchroList.add(node);
//		}
		newSynchroList = Collections.synchronizedList(new ArrayList<Node>());
		synchroList = Collections.synchronizedList(sendNeighbours);
	}

	@Override
	public void setupNeighbours(Node... neighbours) {
		for (int i = 0; i < neighbours.length; i++) {
			this.neighbours.add(neighbours[i]);
			neighbours[i].hello(this);
		}
	}

	private boolean newMessageReceived(Node messenger) {
		if (newSynchroList.contains(messenger)) {
			return true;
		} else {
			synchronized (this) {
				// Dieser Knoten erwartet eine nachricht weniger da er keine wakeup nachricht
				// von dem Knoten bekommen wird.
				// Da er diesem schon eine geschickt hat
//				System.out.println("expMsg von " + name + " wird um 1 verringert auf " + expMsg);
				// --expMsg;
				this.notifyAll();
			}
			return false;
		}
	}

	public boolean messageSent(Node messenger) {
		if (synchroList.contains(messenger)) {
			return false;
		} else {
			synchronized (this) {
				// Dieser Knoten erwartet eine nachricht weniger da er keine wakeup nachricht
				// von dem Knoten bekommen wird.
				// Da er diesem schon eine geschickt hat
				expMsg.decrement();
				;
				this.notifyAll();
			}
			return true;
		}
	}

	@Override
	public void hello(Node neighbour) {
		if (!neighbours.contains(neighbour)) {
			neighbours.add(neighbour);
		}
	}

	// reset all Values
	public synchronized void reset() {
		gotReset.set(true);
		System.out.println(name + " wurde zurückgesetzt!!!!!!!!!!!!");
		replies.set(1);
		expMsg.set(this.neighbours.size());
		newSynchroList = new ArrayList<Node>();
		gotReset.set(true);
	}

	public void setMessenger(Node messenger) {
		this.messenger = messenger;
	}

	@Override
	public void setId(customString id) {
		this.id.set(id.get());
	}

	@Override
	public String getNodeId() {
		// TODO Auto-generated method stub
		return this.id.get();
	}

}
