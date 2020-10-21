package stuff;

import java.util.List;

public interface Node {

	public void hello(Node neighbour);

	public void wakeup(Node sender);

	public void echo(Node neighbour, List<List<Node>> allPaths);

	public boolean messageSent(Node finalNode);

}
