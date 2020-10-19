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

public class FinalNode extends NodeAbstract {

	protected final String name;
	private String id;

	protected customBool initiator;
	private customBool awake;

	private Node messenger;

	private customInt replies;
	private customInt expMsg;

	protected final Set<Node> neighbours = new HashSet<Node>();
	private List<Node> sendNeighbours;
	private List<Node> synchroList;

	private List<Node> path = new ArrayList<Node>();
	private List<List<Node>> allPaths = new ArrayList<List<Node>>();

	public FinalNode(String name, boolean initiator) {
		super(name, initiator);
		this.name = name;
		this.initiator = new customBool(initiator);
		awake = new customBool(false);
		replies = new customInt(0);
		if (initiator) {
			id = name;
		} else {
			id = null;
		}
	}

	@Override
	public void hello(Node neighbour) {
		if (!neighbours.contains(neighbour)) {
			neighbours.add(neighbour);
		}
	}

	@Override
	public synchronized void wakeup(Node messenger) {
		incrementReplies(0);
		// Wenn der Knoten noch nicht wach ist weck ihn auf und speicher den Knoten der
		// diesen aufgeweckt hat in messenger.
		if (!awake.get()) {
			System.out.println(name + " ist nun wach von " + messenger);
			this.messenger = messenger;
			awake.set(true);
			this.notifyAll();
		} else {
			System.out.println(name + " war schon wach. Hat aber Nachricht von " + messenger + " erhalten.");
		}
	}

	public synchronized void electionWakeup(Node messenger, String id) {
		// Ids überprüfen
		// wenn id == null ist ist dieser Knoten kein Initiator und hat grade das erste
		// mal eine Nachricht bekommen
		if (this.id == null) {
			this.id = id;
		}
		// wenn diese id kleiner ist als die übersendete
		else if (0 > this.id.compareTo(id)) {
			System.out.println(name+ " compareId "+this.id.compareTo(id)+ " mit "+ id);
			if(initiator.get()) {
				initiator.set(false);
			}
			this.messenger = messenger;
			this.id = id;
			List<Node> resend = getAllSendNeighbours();
			System.out.println("Resend: "+resend);
			for(Node node : resend) {
				System.out.println(name+" Sendet an "+ node+ " eine neue Nachricht");
				node.electionWakeup(this, this.id);
				expMsg.increment();
			}
			//an alle Knoten an die dieser Knoten schon geschickt hat neu schicken.
			
		}
		// wenn diese id größer ist als die übersendete
		else if (0 < this.id.compareTo(id)) {
			;
		}
		// wenn beide ids den selben Wert haben dann such eine zufällig aus
		else if (0 == this.id.compareTo(id)) {
			// später machen
			;
		}
		// Wenn der Knoten noch nicht wach ist weck ihn auf und speicher den Knoten der
		// diesen aufgeweckt hat in messenger.
		if (!awake.get()) {
			System.out.println(name + " ist nun wach von " + messenger);
			awake.set(true);
			this.messenger = messenger;
			this.notifyAll();
		} else {
			System.out.println(name + " war schon wach. Hat aber Nachricht von " + messenger + " erhalten.");
		}
		incrementReplies(0);
	}
	
	private synchronized List<Node> getAllSendNeighbours(){
		List<Node> allSendNeighbours = new ArrayList<>(sendNeighbours);
		System.out.println("AllsendNeighbours: "+allSendNeighbours);
		System.out.println("synchroList: "+synchroList);
		allSendNeighbours.removeAll(synchroList);
		return allSendNeighbours;
	}

	@Override
	public synchronized void echo(Node neighbour, List<List<Node>> allPaths) {
		incrementReplies(0);
		for (List<Node> list : allPaths) {
			this.allPaths.add(list);
		}
		System.out.println("Echo von " + name + " aufgerufen durch " + neighbour);
	}
	
	@Override
	public synchronized void electionEcho(Node neighbour, List<List<Node>> allPaths, String id) {
		if (id == null) {
			this.id = id;
		}
		// wenn diese id kleiner ist als die übersendete
		else if (0 > this.id.compareTo(id)) {
			this.id = id;
		}
		// wenn diese id größer ist als die übersendete
		else if (0 < this.id.compareTo(id)) {
			;
		}
		// wenn beide ids den selben Wert haben dann such eine zufällig aus
		else if (0 == this.id.compareTo(id)) {
			// später machen
			;
		}
		incrementReplies(0);
		for (List<Node> list : allPaths) {
			this.allPaths.add(list);
		}
		System.out.println("Echo von " + name + " aufgerufen durch " + neighbour);
	}

	public void run() {
		expMsg = new customInt(this.neighbours.size());
		sendNeighbours = new ArrayList<Node>(neighbours);
		synchroList = Collections.synchronizedList(new ArrayList<Node>());
		for(Node node : sendNeighbours) {
			synchroList.add(node);
		}
//		synchroList = Collections.synchronizedList(sendNeighbours);
		// Teste ob Knoten initiator ist und gebe Nachbarn aus
		isInitiator();
		HashSettoString(neighbours);
		// Wenn Knoten nicht wach ist: wait()
		while (!awake.get()) {
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sendElectionWakeup();
		//sendWakeup();
	}

	private void sendElectionWakeup() {
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
					neighbour.electionWakeup(this,id);
				} else {
					System.out.println(
							neighbour + " Hat diesem Knoten " + name + " schon eine WakeupNachricht geschickt.");
				}
			} else {
				System.out.println(name + " ruft nicht " + neighbour + " an da er von diesem aufgeweckt wurde");
			}
		}
		sendElectionEcho();
	}
	
	private void sendWakeup() {
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
				System.out.println(name + " ruft nicht " + neighbour + " an da er von diesem aufgeweckt wurde");
			}
		}
		sendEcho();
	}
	
	private synchronized void sendElectionEcho() {
		while (!(replies.get() == expMsg.get())) {
			try {
				synchronized (this) {
					this.wait(200);
					System.out.println(name + ": " + replies.get() + " Nachrichten erhalten " + expMsg.get() + " benötigt");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		preparePath();
		if (!initiator.get()) {
			messenger.electionEcho(this, allPaths,id);
		} else if (initiator.get()) {
			if(name.equals(id)) {
				System.out.println("*****************");
				System.out.println("Knoten "+name+" hat die Wahl gewonnen.");
				System.out.println("*****************");
			}else {
				System.out.println("Knoten "+name+" hat die Wahl verloren.");
			}
			System.out.println("Initiator fertig");
			// Gib alle bekannten Pfade aus
			for (List<Node> list : allPaths) {
				System.out.println(list);
			}
		} else {
			System.out.println(name + ": " + replies + " Nachrichten erhalten " + expMsg + " benötigt");
		}
	}

	private synchronized void sendEcho() {
		while (!(replies.get() == expMsg.get())) {
			try {
				synchronized (this) {
					this.wait(200);
					System.out.println(name + ": " + replies.get() + " Nachrichten erhalten " + expMsg.get() + " benötigt");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		preparePath();
		if (!initiator.get()) {
			messenger.echo(this, allPaths);
		} else if (initiator.get()) {
			if(name.equals(id)) {
				System.out.println("*****************");
				System.out.println("Knoten "+name+" hat die Wahl gewonnen.");
				System.out.println("*****************");
			}else {
				System.out.println("Knoten "+name+" hat die Wahl verloren.");
			}
			System.out.println("Initiator fertig");
			// Gib alle bekannten Pfade aus
			for (List<Node> list : allPaths) {
				System.out.println(list);
			}
		} else {
			System.out.println(name + ": " + replies + " Nachrichten erhalten " + expMsg + " benötigt");
		}
	}

	private void preparePath() {
		// Pfad vorbereiten
		// Wenn dieser Knoten ein Blatt ist
		if (allPaths.isEmpty()) {
			path.add(this);
			allPaths.add(path);
		} else {
			// Wenn nicht füge diesen Knoten an das Ende aller bisher bekannten Pfade
			for (List<Node> list : allPaths) {
				list.add(this);
			}
		}

	}

	// sicherstellen das replies nur von einem Thread erhöht wird
	public synchronized void incrementReplies(int i) {
		if(i == 0) {
			replies.increment();;
		}else {
			replies.decrement();;
		}
		this.notifyAll();
	}

	private void isInitiator() {
		// Wenn der Knoten ein Initiator ist, ist er automatisch wach.
		if (initiator.get()) {
			System.out.println("Knoten " + name + " ist der Initiator");
			awake.set(true);
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
				// Dieser Knoten erwartet eine nachricht weniger da er keine wakeup nachricht
				// von dem Knoten bekommen wird.
				// Da er diesem schon eine geschickt hat
				expMsg.decrement();;
				this.notifyAll();
			}
			return true;
		}
	}
}
