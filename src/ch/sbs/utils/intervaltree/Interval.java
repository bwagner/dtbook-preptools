package ch.sbs.utils.intervaltree;

// Quick and dirty interval class
// 
/*
 * stolen from 
 * http://git.hashcollision.org/projects/interval_trees/src/org/arabidopsis/interval/
 * and simplified.
 */
public class Interval implements Comparable<Interval> {
	private final int low;
	private final int high;

	public Interval(int theLow, int theHigh) {
		assert theLow <= theHigh;
		low = theLow;
		high = theHigh;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (getClass().equals(other.getClass())) {
			final Interval otherInterval = (Interval) other;
			return (low == otherInterval.low && high == otherInterval.high);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return low;
	}

	@Override
	public int compareTo(final Interval other) {
		if (low < other.low)
			return -1;
		if (low > other.low)
			return 1;

		if (high < other.high)
			return -1;
		if (high > other.high)
			return 1;

		return 0;
	}

	@Override
	public String toString() {
		return "Interval[" + low + ", " + high + "]";
	}

	/**
	 * Returns true if this interval overlaps the other.
	 */
	public boolean overlaps(final Interval other) {
		return (low <= other.high && other.low <= high);
	}

	public int getLow() {
		return low;
	}

	public int getHigh() {
		return high;
	}

}
