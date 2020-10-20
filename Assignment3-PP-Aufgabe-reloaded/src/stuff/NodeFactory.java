package stuff;

public class NodeFactory{

	public static FinalNode getInstance(String string, boolean b) {
		FinalNode node = new FinalNode(string, b);
		return node;
	}

}