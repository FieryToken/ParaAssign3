package stuff;

public interface Node {

	public void hello(Node neighbour);

	public void wakeup(Node neighbour);

	public void echo(Node neighbour, Object data);

	public boolean isAwake();
}
