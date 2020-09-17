package stuff;

public interface Node {

	public void hello(Node neighbour);

	public void wakeup(Node neighbour, Node sender);

	public void echo(Node neighbour);

	public boolean isAwake();
}
