package ch.sbs.utils.intervaltree;

// Implementation of red-black trees.  We try to stay close
// to the pseudocode described in 'Introduction to Algorithms'
// by CLR.

// This implementation also can take in a StatisticUpdate instance to
// maintain auxillary information for each RbNode.  We call update()
// whenever an RbNode is inserted, in two phases:
//
// 1.  Insertion as a leaf.
// 2.  Rotation to maintain red-black invariants.
//
// update() is propagated up ancestors, although this might be
// overkill.
/*
 * stolen from 
 * http://git.hashcollision.org/projects/interval_trees/src/org/arabidopsis/interval/
 * and simplified.
 */
interface StatisticUpdate {
	void update(final RbNode node);
}

public class RbTree {
	RbNode root;
	private static final RbNode NIL = RbNode.NIL;
	private final StatisticUpdate updater;

	public RbTree(final StatisticUpdate theUpdater) {
		root = NIL;
		updater = theUpdater;
	}

	public RbTree() {
		this(null);
	}

	public void insert(RbNode x) {
		assert (x != null);
		assert (!x.isNull());

		treeInsert(x);
		x.color = RbNode.RED;
		while (x != root && x.parent.color == RbNode.RED) {
			if (x.parent == x.parent.parent.left) {
				final RbNode y = x.parent.parent.right;
				if (y.color == RbNode.RED) {
					x.parent.color = RbNode.BLACK;
					y.color = RbNode.BLACK;
					x.parent.parent.color = RbNode.RED;
					x = x.parent.parent;
				}
				else {
					if (x == x.parent.right) {
						x = x.parent;
						leftRotate(x);
					}
					x.parent.color = RbNode.BLACK;
					x.parent.parent.color = RbNode.RED;
					rightRotate(x.parent.parent);
				}
			}
			else {
				final RbNode y = x.parent.parent.left;
				if (y.color == RbNode.RED) {
					x.parent.color = RbNode.BLACK;
					y.color = RbNode.BLACK;
					x.parent.parent.color = RbNode.RED;
					x = x.parent.parent;
				}
				else {
					if (x == x.parent.left) {
						x = x.parent;
						rightRotate(x);
					}
					x.parent.color = RbNode.BLACK;
					x.parent.parent.color = RbNode.RED;
					leftRotate(x.parent.parent);
				}
			}
		}
		root.color = RbNode.BLACK;
	}

	public RbNode get(int key) {
		RbNode node = root;
		while (node != NIL) {
			if (key == node.key) {
				return node;
			}
			if (key < node.key) {
				node = node.left;
			}
			else {
				node = node.right;
			}
		}
		return NIL;
	}

	public RbNode root() {
		return root;
	}

	public RbNode minimum(RbNode node) {
		assert (node != null);
		assert (!node.isNull());
		while (!node.left.isNull()) {
			node = node.left;
		}
		return node;
	}

	public RbNode maximum(RbNode node) {
		assert (node != null);
		assert (!node.isNull());
		while (!node.right.isNull()) {
			node = node.right;
		}
		return node;
	}

	public RbNode successor(RbNode x) {
		assert (x != null);
		assert (!x.isNull());
		if (!x.right.isNull()) {
			return minimum(x.right);
		}
		RbNode y = x.parent;
		while ((!y.isNull()) && x == y.right) {
			x = y;
			y = y.parent;
		}
		return y;
	}

	public RbNode predecessor(RbNode x) {
		assert (x != null);
		assert (!x.isNull());

		if (!x.left.isNull()) {
			return maximum(x.left);
		}
		RbNode y = x.parent;
		while ((!y.isNull()) && x == y.left) {
			x = y;
			y = y.parent;
		}
		return y;
	}

	void leftRotate(final RbNode x) {
		final RbNode y = x.right;
		x.right = y.left;
		if (y.left != NIL) {
			y.left.parent = x;
		}
		y.parent = x.parent;
		if (x.parent == NIL) {
			root = y;
		}
		else {
			if (x.parent.left == x) {
				x.parent.left = y;
			}
			else {
				x.parent.right = y;
			}
		}
		y.left = x;
		x.parent = y;

		applyUpdate(x);
		// no need to apply update on y, since it'll y is an ancestor
		// of x, and will be touched by applyUpdate().
	}

	void rightRotate(final RbNode x) {
		final RbNode y = x.left;
		x.left = y.right;
		if (y.right != NIL) {
			y.right.parent = x;
		}
		y.parent = x.parent;
		if (x.parent == NIL) {
			root = y;
		}
		else {
			if (x.parent.right == x) {
				x.parent.right = y;
			}
			else {
				x.parent.left = y;
			}
		}
		y.right = x;
		x.parent = y;

		applyUpdate(x);
		// no need to apply update on y, since it'll y is an ancestor
		// of x, and will be touched by applyUpdate().
	}

	// Note: treeInsert is package protected because it does NOT
	// maintain RB constraints.
	void treeInsert(final RbNode x) {
		RbNode node = root;
		RbNode y = NIL;
		while (node != NIL) {
			y = node;
			if (x.key <= node.key) {
				node = node.left;
			}
			else {
				node = node.right;
			}
		}
		x.parent = y;

		if (y == NIL) {
			root = x;
			x.left = x.right = NIL;
		}
		else {
			if (x.key <= y.key) {
				y.left = x;
			}
			else {
				y.right = x;
			}
		}

		applyUpdate(x);
	}

	// Applies the statistic update on the node and its ancestors.
	private void applyUpdate(RbNode node) {
		if (updater == null)
			return;
		while (!node.isNull()) {
			updater.update(node);
			node = node.parent;
		}
	}

	/**
	 * Returns the number of nodes in the tree.
	 */
	public int size() {
		return _size(root);
	}

	private int _size(final RbNode node) {
		if (node.isNull())
			return 0;
		return 1 + _size(node.left) + _size(node.right);
	}

	/**
	 * 
	 * Test code: make sure that the tree has all the properties
	 * defined by Red Black trees:
	 * 
	 * o. Root is black.
	 * 
	 * o. NIL is black.
	 * 
	 * o. Red nodes have black children.
	 * 
	 * o. Every path from root to leaves contains the same number of
	 * black nodes.
	 * 
	 * Calling this function will be expensive, as is meant for
	 * assertion or test code.
	 */
	public boolean isValid() {
		if (root.color != RbNode.BLACK) {
			return false;
		}
		if (NIL.color != RbNode.BLACK) {
			return false;
		}
		if (!allRedNodesFollowConstraints(root)) {
			return false;
		}
		if (!isBalancedBlackHeight(root)) {
			return false;
		}
		return true;
	}

	private boolean allRedNodesFollowConstraints(final RbNode node) {
		if (node.isNull())
			return true;

		if (node.color == RbNode.BLACK) {
			return (allRedNodesFollowConstraints(node.left) && allRedNodesFollowConstraints(node.right));
		}

		// At this point, we know we're on a RED node.
		return (node.left.color == RbNode.BLACK
				&& node.right.color == RbNode.BLACK
				&& allRedNodesFollowConstraints(node.left) && allRedNodesFollowConstraints(node.right));
	}

	// Check that both ends are equally balanced in terms of black height.
	private boolean isBalancedBlackHeight(final RbNode node) {
		if (node.isNull())
			return true;
		return (blackHeight(node.left) == blackHeight(node.right)
				&& isBalancedBlackHeight(node.left) && isBalancedBlackHeight(node.right));
	}

	// The black height of a node should be left/right equal.
	private int blackHeight(final RbNode node) {
		if (node.isNull())
			return 0;
		int leftBlackHeight = blackHeight(node.left);
		if (node.color == RbNode.BLACK) {
			return leftBlackHeight + 1;
		}
		else {
			return leftBlackHeight;
		}
	}

}
