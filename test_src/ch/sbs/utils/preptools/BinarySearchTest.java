package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

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

public class BinarySearchTest {

	@Test
	public void testStringSearch() {
		assertEquals(
				-1,
				BinarySearch.search("Billy",
						Arrays.asList("Anny", "Emmy", "Grammy")));
	}

	@Test
	public void testStringSearch5() {
		assertEquals(
				0,
				BinarySearch.search("Aaron",
						Arrays.asList("Anny", "Emmy", "Grammy")));
	}

	@Test
	public void testStringSearch7() {
		assertEquals(
				0,
				BinarySearch.search("Anny",
						Arrays.asList("Anny", "Emmy", "Grammy")));
	}

	@Test
	public void testStringSearch6() {
		assertEquals(
				-3,
				BinarySearch.search("Xavier",
						Arrays.asList("Anny", "Emmy", "Grammy")));
	}

	@Test
	public void testStringSearch2() {
		assertEquals(
				0,
				BinarySearch.search("Anny",
						Arrays.asList("Anny", "Emmy", "Grammy")));
	}

	@Test
	public void testStringSearch3() {
		assertEquals(
				1,
				BinarySearch.search("Emmy",
						Arrays.asList("Anny", "Emmy", "Grammy")));
	}

	@Test
	public void testStringSearch4() {
		assertEquals(
				2,
				BinarySearch.search("Grammy",
						Arrays.asList("Anny", "Emmy", "Grammy")));
	}

	@Test
	public void testIntegerSearch() {
		assertEquals(-1, BinarySearch.search(2, Arrays.asList(1, 3, 4)));
	}

	@Test
	public void testIntegerSearch0() {
		assertEquals(0, BinarySearch.search(1, Arrays.asList(1, 3, 4)));
	}

	@Test
	public void testIntegerSearch00() {
		assertEquals(0, BinarySearch.search(0, Arrays.asList(1, 3, 4)));
	}

	@Test
	public void testIntegerSearch1() {
		assertEquals(1, BinarySearch.search(3, Arrays.asList(1, 3, 4)));
	}

	@Test
	public void testIntegerSearch2() {
		assertEquals(2, BinarySearch.search(4, Arrays.asList(1, 3, 4)));
	}

}
