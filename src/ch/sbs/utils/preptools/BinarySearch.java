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
