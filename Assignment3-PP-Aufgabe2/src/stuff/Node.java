package stuff;

import java.util.List;

import utils.customString;

public interface Node {

	public void hello(Node neighbour);

	public void wakeup(Node sender);

	public void echo(Node neighbour, List<List<Node>> allPaths);

	public boolean messageSent(Node finalNode);

	public void electionEcho(Node neighbour, customString id);

	public void electionWakeup(Node finalNode, customString id);

	public void incrementReplies(int i);

	public void changeId(customString id);

}
