package ch.sbs.utils.preptools;

import java.util.List;

// http://en.literateprograms.org/Binary_search_%28Java%29

/**
 * 
 * Classic Binary search.
 * Returns:
 * - a positive index > 0 if the element was found
 * - a negative index < 0 if the element was not found. This negative index
 * points to the index where the searched value would need to be inserted.
 * - 0 could either mean the element was found at position 0 or it would need
 * to be inserted at position 0. To distinguish the two cases the element at
 * position 0 must be compared with the searched value. If they are the same,
 * the searched value was found, if they aren't the searched value is smaller
 * than all values in list.
 */
public class BinarySearch {
	static <T extends Comparable<? super T>> int search(T value, List<T> values) {
		int left = 0, right = values.size() - 1;
		T midVal;
		int mid;
		do {
			mid = (left + right) / 2;
			midVal = values.get(mid);
			if (value.compareTo(midVal) < 0)
				right = mid - 1;
			else if (value.compareTo(midVal) > 0)
				left = mid + 1;
			else
				return mid;
		} while (left <= right);
		return (value.compareTo(midVal) < 0) ? -mid : -(mid + 1);
	}
}
