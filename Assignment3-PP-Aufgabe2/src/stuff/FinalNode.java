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

	protected customBool initiator;
	private customBool awake;
	private customBool resetting;

	private Node messenger;
	//Debug Stuff
	private static customInt allEchoMessages = new customInt();
	private static customInt allWakeupMessages = new customInt(0);
	private static customInt allWinner = new customInt(0);
	private static customInt allReplies = new customInt(0);
	//
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
		//wenn der Knoten ein initiator ist dann setze die Id = name
		if (initiator) {
			id = new customString(name);
		} else {
			id = new customString();
		}
		resetting = new customBool(false);
	}

	public void run() {
		expMsg = new customInt(this.neighbours.size());
		sendNeighbours = new ArrayList<Node>(this.neighbours);
		synchroList = Collections.synchronizedList(sendNeighbours);
		// Teste ob Knoten initiator ist und gebe Nachbarn aus
		isInitiator();
		HashSettoString(neighbours);
		//Wenn dieser Knoten keine Nachbarn hat dann stopt dieser Thread
		if(neighbours.isEmpty()) {
			Thread.currentThread().stop();
		}
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
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// ******************************************************************************************************************************
	// ******************************************************************************************************************************
	// ***************************************************Election Algorithmus*******************************************************
	// ******************************************************************************************************************************
	// ******************************************************************************************************************************

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
//					System.out.println(name + " ruft " + neighbour + " an");
					neighbour.electionWakeup(this, id);
				} else {
//					System.out.println(
//							neighbour + " Hat diesem Knoten " + name + " schon eine WakeupNachricht geschickt.");
				}
			} else {
				//System.out.println(name + " ruft nicht " + neighbour + " an da er von diesem aufgeweckt wurde");
			}
		}
		sendElectionEcho();
	}

	private synchronized void sendElectionEcho() {
		//wenn der Knoten noch nicht genügend Nachrichten erhalten hat
		while (!(replies.get() == expMsg.get())) {
			try {
				synchronized (this) {
					this.wait(200);
//					System.out.println(name + ": " + replies.get() + " Nachrichten erhalten " + expMsg.get() + " benötigt");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(messenger != null) {
			messenger.electionEcho(this, id);
		}
		if (initiator.get()) {
			//Ersten name der id holen
			String substring = id.substring(0,name.length());
			//System.out.println("Substring: "+substring );
			//Prüfen ob der erste name in der id = diesem name ist. Wenn ja hat dieser Knoten gewonnen
			if (substring.equals(name)) {
				System.out.println("*****************\nKnoten " + name + " hat die Wahl gewonnen.\n*****************");	
				initiator.set(true);
				allWinner.increment();
			} else {
				initiator.set(false);
				System.out.println("Knoten " + name + " hat die Wahl verloren.");
			}
			//System.out.println("Knoten "+name+" id : "+id.get());
		}
		//reset();
		//System.out.println("AllReplies "+allReplies.get());
		//System.out.println("AllWinner "+allWinner.get());
	}
	
	public synchronized void electionWakeup(Node messenger, customString id) {
		incrementWakeupMessages();
		//System.out.println("Nachricht nummer: "+allWakeupMessages.get()+" "+messenger+" schickt "+ name + " eine Wakeup Nachricht");
		if (this.id != null) {
			//Wenn der letzte teil der id dieses Knoten dem namen entspricht fahre fort 
				if (0 > this.id.compareTo(id)) {
					this.id.set(id);
					this.id.append(name);
				}
				// wenn diese id größer ist als die übersendete
				else if (0 < this.id.compareTo(id)) {
					messenger.changeId(this.id);
				}
				// wenn beide ids den selben Wert haben dann such eine zufällig aus
				else if (0 == this.id.compareTo(id)) {
					// später machen
					;
				}
			
		}
		// Ids überprüfen
		// wenn id == null ist ist dieser Knoten kein Initiator und hat grade das erste
		// mal eine Nachricht bekommen
		if (this.id == null) {
			this.id.append(id+name);
		}
		// wenn diese id kleiner ist als die übersendete

		// Wenn der Knoten noch nicht wach ist weck ihn auf und speicher den Knoten der
		// diesen aufgeweckt hat in messenger.
		if (!awake.get()) {
			//System.out.println(name + " ist nun wach von " + messenger);
			awake.set(true);
			this.messenger = messenger;
			this.notifyAll();
		} else {
			//System.out.println(name + " war schon wach. Hat aber Nachricht von " + messenger + " erhalten.");
		}
		if(messenger == null) {
			this.messenger = messenger;
		}
		incrementReplies(0);
	}
	
	@Override
	public synchronized void electionEcho(Node neighbour, customString id) {
		incrementEchoMessages();
		//System.out.println("Nachricht nummer: "+allEchoMessages.get() +" "+neighbour+" schickt "+ name + " eine Echo Nachricht");
		if (this.id == null) {
			this.id.set(id+name);
		}
		// wenn diese id kleiner ist als die übersendete
		else if (0 > this.id.compareTo(id)) {
			this.id.set(id+name);
		}
		// wenn diese id größer ist als die übersendete
		else if (0 < this.id.compareTo(id)) {
			neighbour.changeId(this.id);
		}
		// wenn beide ids den selben Wert haben dann such eine zufällig aus
		else if (0 == this.id.compareTo(id)) {
			// später machen
			;
		}
		incrementReplies(0);
		//System.out.println("Echo von " + name + " aufgerufen durch " + neighbour);
	}
	
	// ******************************************************************************************************************************
	// ******************************************************************************************************************************
	// ***************************************************Normaler Echo Algorithmus**************************************************
	// ******************************************************************************************************************************
	// ******************************************************************************************************************************
	
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
					//System.out.println(name + " ruft " + neighbour + " an");
					neighbour.wakeup(this);
				} else {
					//System.out.println(
							//neighbour + " Hat diesem Knoten " + name + " schon eine WakeupNachricht geschickt.");
				}
			} else {
				//System.out.println(name + " ruft nicht " + neighbour + " an da er von diesem aufgeweckt wurde");
			}
		}
		sendEcho();
	}
	
	private synchronized void sendEcho() {
		while (!(replies.get() == expMsg.get())) {
			try {
				synchronized (this) {
					this.wait(200);
					//System.out.println(
					//		name + ": " + replies.get() + " Nachrichten erhalten " + expMsg.get() + " benötigt");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		preparePath();
		if (!initiator.get()) {
			messenger.echo(this, allPaths);
		} else if (initiator.get()) {
			System.out.println("Initiator fertig");
			// Gib alle bekannten Pfade aus
			for (List<Node> list : allPaths) {
				System.out.println(list);
			}
		} else {
			//System.out.println(name + ": " + replies + " Nachrichten erhalten " + expMsg + " benötigt");
		}
	}
	
	@Override
	public synchronized void wakeup(Node messenger) {
		incrementReplies(0);
		// Wenn der Knoten noch nicht wach ist weck ihn auf und speicher den Knoten der
		// diesen aufgeweckt hat in messenger.
		if (!awake.get()) {
			//System.out.println(name + " ist nun wach von " + messenger);
			this.messenger = messenger;
			awake.set(true);
			this.notifyAll();
		} else {
			//System.out.println(name + " war schon wach. Hat aber Nachricht von " + messenger + " erhalten.");
		}
	}
	
	@Override
	public synchronized void echo(Node neighbour, List<List<Node>> allPaths) {
		incrementReplies(0);
		for (List<Node> list : allPaths) {
			this.allPaths.add(list);
		}
		//System.out.println("Echo von " + name + " aufgerufen durch " + neighbour);
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
		if (i == 0) {
			allReplies.increment();
			replies.increment();
			;
		} else {
			allReplies.decrement();
			replies.decrement();
			;
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
	
	//Node zurücksetzen so das ein normaler EchoAlgorithmus durchgeführt werden kann
	private void reset() {
		resetting.set(true);;
		messenger = null;
		if(!initiator.get()) {
			awake.set(false);
		}
		replies.set(0);
		expMsg.set(neighbours.size());
		sendNeighbours = new ArrayList<Node>(neighbours);
		synchroList = Collections.synchronizedList(new ArrayList<Node>());
		for (Node node : sendNeighbours) {
			synchroList.add(node);
		}
		path = new ArrayList<Node>();
		allPaths = new ArrayList<List<Node>>();
		resetting.set(false);
		this.notifyAll();
	}
	
	public void setupValues() {
		expMsg = new customInt(this.neighbours.size());
		sendNeighbours = new ArrayList<Node>(this.neighbours);
		synchroList = Collections.synchronizedList(sendNeighbours);
	}
	
	@Override
	public void setupNeighbours(Node... neighbours) {
		for (int i = 0; i < neighbours.length; i++) {
			this.neighbours.add(neighbours[i]);
			neighbours[i].hello(this);
		}
	}
	
	@Override
	public void hello(Node neighbour) {
		if (!neighbours.contains(neighbour)) {
			neighbours.add(neighbour);
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
				this.notifyAll();
			}
			return true;
		}
	}
	
	public synchronized void changeId(customString newId) {
		synchronized(this) {
			this.id.set(newId+name);
		}
	}
	
	public static synchronized void incrementWakeupMessages() {
		allWakeupMessages.increment();
	}
	public static synchronized void incrementEchoMessages() {
		allEchoMessages.increment();
	}
}
