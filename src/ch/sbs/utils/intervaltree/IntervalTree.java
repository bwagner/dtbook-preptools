package ch.sbs.utils.intervaltree;

/** An implementation of an interval tree, following the explanation.
 * from CLR.
 */
/*
 * stolen from 
 * http://git.hashcollision.org/projects/interval_trees/src/org/arabidopsis/interval/
 * and simplified.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class IntervalTree {
	private final StatisticUpdate updater;
	private final RbTree tree;

	private final Map<RbNode, Interval> intervals;
	private final Map<RbNode, Integer> max;
	private final Map<RbNode, Integer> min;

	public IntervalTree() {
		updater = new IntervalTreeStatisticUpdate();
		tree = new RbTree(updater);

		intervals = new WeakHashMap<RbNode, Interval>();
		intervals.put(RbNode.NIL, null);

		max = new WeakHashMap<RbNode, Integer>();
		max.put(RbNode.NIL, new Integer(Integer.MIN_VALUE));
		min = new WeakHashMap<RbNode, Integer>();
		min.put(RbNode.NIL, new Integer(Integer.MAX_VALUE));
	}

	public void insert(final Interval interval) {
		final RbNode node = new RbNode(interval.getLow());
		intervals.put(node, interval);
		tree.insert(node);
	}

	public int size() {
		return tree.size();
	}

	// Returns the first matching interval that we can find.
	public Interval search(final Interval interval) {

		RbNode node = tree.root();
		if (node.isNull())
			return null;

		while ((!node.isNull()) && (!getInterval(node).overlaps(interval))) {
			if (canOverlapOnLeftSide(interval, node)) {
				node = node.left;
			}
			else if (canOverlapOnRightSide(interval, node)) {
				node = node.right;
			}
			else {
				return null;
			}
		}

		// Defensive coding. node can be the NIL node, but it must
		// not be itself the null object.
		assert node != null;
		return getInterval(node);
	}

	private boolean canOverlapOnLeftSide(final Interval interval,
			final RbNode node) {
		return !node.left.isNull() && getMax(node.left) >= interval.getLow();
	}

	private boolean canOverlapOnRightSide(final Interval interval,
			final RbNode node) {
		return !node.right.isNull() && getMin(node.right) <= interval.getHigh();
	}

	// Returns all matches as a list of Intervals
	public List<Interval> searchAll(final Interval interval) {
		return tree.root().isNull() ? new ArrayList<Interval>() : _searchAll(
				interval, tree.root());
	}

	private List<Interval> _searchAll(final Interval interval, final RbNode node) {
		assert (!node.isNull());

		final List<Interval> results = new ArrayList<Interval>();
		if (getInterval(node).overlaps(interval)) {
			results.add(getInterval(node));
		}

		if (canOverlapOnLeftSide(interval, node)) {
			results.addAll(_searchAll(interval, node.left));
		}

		if (canOverlapOnRightSide(interval, node)) {
			results.addAll(_searchAll(interval, node.right));
		}

		return results;
	}

	public Interval getInterval(final RbNode node) {
		assert node != null;
		assert !node.isNull();

		assert intervals.containsKey(node);

		return intervals.get(node);
	}

	public int getMax(final RbNode node) {
		assert (node != null);
		assert (intervals.containsKey(node));

		return max.get(node);
	}

	private void setMax(final RbNode node, int value) {
		max.put(node, value);
	}

	public int getMin(final RbNode node) {
		assert (node != null);
		assert (intervals.containsKey(node));

		return min.get(node);
	}

	private void setMin(final RbNode node, int value) {
		min.put(node, value);
	}

	private class IntervalTreeStatisticUpdate implements StatisticUpdate {
		@Override
		public void update(final RbNode node) {
			setMax(node,
					max(max(getMax(node.left), getMax(node.right)),
							getInterval(node).getHigh()));

			setMin(node,
					min(min(getMin(node.left), getMin(node.right)),
							getInterval(node).getLow()));
		}

		private int max(int x, int y) {
			return x > y ? x : y;
		}

		private int min(int x, int y) {
			return x < y ? x : y;
		}

	}

	/**
	 * 
	 * Test case code: check to see that the data structure follows
	 * the right constraints of interval trees:
	 * 
	 * o. They're valid red-black trees
	 * o. getMax(node) is the maximum of any interval rooted at that node..
	 * 
	 * This code is expensive, and only meant to be used for
	 * assertions and testing.
	 */
	public boolean isValid() {
		return (tree.isValid() && hasCorrectMaxFields(tree.root) && hasCorrectMinFields(tree.root));
	}

	private boolean hasCorrectMaxFields(final RbNode node) {
		if (node.isNull())
			return true;
		return (getRealMax(node) == getMax(node)
				&& hasCorrectMaxFields(node.left) && hasCorrectMaxFields(node.right));
	}

	private boolean hasCorrectMinFields(final RbNode node) {
		if (node.isNull())
			return true;
		return (getRealMin(node) == getMin(node)
				&& hasCorrectMinFields(node.left) && hasCorrectMinFields(node.right));
	}

	private int getRealMax(final RbNode node) {
		if (node.isNull())
			return Integer.MIN_VALUE;
		final int leftMax = getRealMax(node.left);
		final int rightMax = getRealMax(node.right);
		final int nodeHigh = getInterval(node).getHigh();

		final int max1 = (leftMax > rightMax ? leftMax : rightMax);
		return (max1 > nodeHigh ? max1 : nodeHigh);
	}

	private int getRealMin(final RbNode node) {
		if (node.isNull())
			return Integer.MAX_VALUE;

		final int leftMin = getRealMin(node.left);
		final int rightMin = getRealMin(node.right);
		final int nodeLow = getInterval(node).getLow();

		final int min1 = (leftMin < rightMin ? leftMin : rightMin);
		return (min1 < nodeLow ? min1 : nodeLow);
	}

}
