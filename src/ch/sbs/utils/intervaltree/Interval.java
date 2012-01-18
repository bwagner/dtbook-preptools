package ch.sbs.utils.intervaltree;

// Quick and dirty interval class
// 
/*
 * stolen from 
 * http://git.hashcollision.org/projects/interval_trees/src/org/arabidopsis/interval/
 * and simplified.
 */
/**
	* Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
	*
	* This file is part of dtbook-preptools.
	* 	
	* dtbook-preptools is free software: you can redistribute it
	* and/or modify it under the terms of the GNU Lesser General Public
	* License as published by the Free Software Foundation, either
	* version 3 of the License, or (at your option) any later version.
	* 	
	* This program is distributed in the hope that it will be useful,
	* but WITHOUT ANY WARRANTY; without even the implied warranty of
	* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	* Lesser General Public License for more details.
	* 	
	* You should have received a copy of the GNU Lesser General Public
	* License along with this program. If not, see
	* <http://www.gnu.org/licenses/>.
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
