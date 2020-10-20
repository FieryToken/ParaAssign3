package stuff;

import java.util.List;

import utils.customString;

public interface Node {

	public void hello(Node neighbour);

	public boolean messageSent(Node finalNode);

	public void wakeup(Node messenger, customString id);

	public void echo(Node neighbour, customString id);

	public void setId(customString id);

	public String getNodeId();

}
