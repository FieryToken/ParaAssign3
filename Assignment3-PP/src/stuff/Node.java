package stuff;

import java.util.List;
import java.util.Set;

public interface Node {

	public void hello(Node neighbour);

	public void wakeup(Node sender);

	public void echo(Node neighbour);

	public boolean isAwake();

	public boolean sendMessage(Node finalNode);

}
