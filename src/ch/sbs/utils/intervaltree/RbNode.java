package ch.sbs.utils.intervaltree;

/*
 * stolen from 
 * http://git.hashcollision.org/projects/interval_trees/src/org/arabidopsis/interval/
 * and simplified.
 */
public class RbNode {
	public int key;
	public boolean color;
	public RbNode parent;
	public RbNode left;
	public RbNode right;

	public static boolean BLACK = false;
	public static boolean RED = true;

	private RbNode() {
		// Default constructor is only meant to be used for the
		// construction of the NIL node.
	}

	public RbNode(int theKey) {
		parent = NIL;
		left = NIL;
		right = NIL;
		key = theKey;
		color = RED;
	}

	static RbNode NIL;
	static {
		NIL = new RbNode();
		NIL.color = BLACK;
		NIL.parent = NIL;
		NIL.left = NIL;
		NIL.right = NIL;
	}

	public boolean isNull() {
		return this == NIL;
	}

	@Override
	public String toString() {
		if (this == NIL) {
			return "nil";
		}
		return "(" + key + " " + (color == RED ? "RED" : "BLACK") + " ("
				+ left.toString() + ", " + right.toString() + ")";
	}
}
