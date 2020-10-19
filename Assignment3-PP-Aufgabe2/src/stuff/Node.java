package stuff;

import java.util.List;

public interface Node {

	public void hello(Node neighbour);

	public void wakeup(Node sender);

	public void echo(Node neighbour, List<List<Node>> allPaths);

	public boolean messageSent(Node finalNode);

	public void electionEcho(Node neighbour, String id);

	public void electionWakeup(Node finalNode, String id);

	public void incrementReplies(int i);

	public void changeId(String id);

}
