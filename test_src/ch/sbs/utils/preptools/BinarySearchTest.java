package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

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
