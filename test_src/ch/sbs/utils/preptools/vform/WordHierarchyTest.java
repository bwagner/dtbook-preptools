package ch.sbs.utils.preptools.vform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.junit.Test;

import ch.sbs.utils.preptools.PermutationHelper;
import ch.sbs.utils.preptools.vform.WordHierarchyBuilder;
import ch.sbs.utils.preptools.vform.WordHierarchyBuilder.Word;

public class WordHierarchyTest {

	/*
	 * Ih -
	 * 	r
	 * 		e
	 * 			m
	 * 			n
	 * 			r
	 * 				seits
	 * 			s
	 * 				gleichen
	 * 			t -
	 * 				halber
	 * 				w -
	 * 					egen
	 * 					illen
	 * 		s
	 * 		ige
	 * 			m
	 * 			n
	 * 			s
	 * 			r
	 * 	nen
	 * 
	 */
	final String[] ihr = new String[] { "Ihnen", "Ihr", "Ihre", "Ihrem",
			"Ihren", "Ihrer", "Ihrerseits", "Ihres", "Ihresgleichen",
			"Ihrethalben", "Ihretwegen", "Ihretwillen", "Ihrige", "Ihrigem",
			"Ihrigen", "Ihriger", "Ihriges", "Ihrs", "Sie", };

	final String[] du = new String[] { "Dein", "Deine", "Deinem", "Deinen",
			"Deiner", "Deinerseits", "Deines", "Deinesgleichen",
			"Deinethalben", "Deinetwegen", "Deinetwillen", "Deinige",
			"Deinigem", "Deinigen", "Deiniger", "Deiniges", "Deins", "Dich",
			"Dir", "Du", "Euch", "Euer", "Euere", "Euerem", "Euerer", "Eueres",
			"Euers", "Euerseits", "Eure", "Eurem", "Euren", "Eurerseits",
			"Eures", "Euresgleichen", "Eurethalben", "Euretwegen",
			"Euretwillen", "Eurige", "Eurigem", "Eurigen", "Euriger",
			"Euriges", };

	final String[] shortIhr = new String[] { "Ihr", "Sie" };

	@Test
	public void testPattern() {
		Pattern pattern = Pattern.compile("Sie");
		assertTrue(pattern.matcher("Sie").find());
		pattern = Pattern.compile("(Sie)");
		assertTrue(pattern.matcher("Sie").find());
		pattern = Pattern.compile("(?:Sie)");
		assertTrue(pattern.matcher("Sie").find());
		pattern = Pattern.compile("(?:Ihr|Sie)");
		assertTrue(pattern.matcher("Sie").find());
		assertTrue(pattern.matcher("Ihr").find());
	}

	@Test
	public void testCreatorIhr() {
		final String[] vforms = ihr;
		assertEquals(19, ihr.length);
		final HashSet<String> ihrSet = new HashSet<String>();
		for (final String str : vforms) {
			ihrSet.add(str);
		}
		WordHierarchyBuilder.Word ihrTree = WordHierarchyBuilder
				.createWordTree(ihrSet, null);
		// System.out.println("result:" + ihrTree.myToString(true));
		final String regexStr = ihrTree.toRegex();
		final Pattern pattern = Pattern.compile(regexStr);
		assertTrue(pattern.matcher("Sie").find());
		for (final String str : vforms) {
			assertTrue(pattern.matcher(str).find());
		}
		assertFalse(pattern.matcher("").find());
		assertFalse(pattern.matcher("fix").find());
	}

	@Test
	public void testCreatorDu() {
		final String[] vforms = du;
		assertEquals(42, du.length);
		final HashSet<String> ihrSet = new HashSet<String>();
		for (final String str : vforms) {
			ihrSet.add(str);
		}
		Word ihrTree = WordHierarchyBuilder.createWordTree(ihrSet, null);
		// System.out.println("result:" + ihrTree.myToString(true));
		final String regexStr = ihrTree.toRegex();
		final Pattern pattern = Pattern.compile(regexStr);
		assertTrue(pattern.matcher("Eurem").find());
		for (final String str : vforms) {
			assertTrue(pattern.matcher(str).find());
		}
		assertFalse(pattern.matcher("").find());
		assertFalse(pattern.matcher("fix").find());
	}

	@Test
	public void testPermute() {
		final String[] elements = new String[] { "a", "b", "c", };
		final String[][] expected = new String[][] { { "a", "b", "c" },
				{ "a", "c", "b" }, { "b", "a", "c" }, { "b", "c", "a" },
				{ "c", "a", "b" }, { "c", "b", "a" },

		};
		final PermutationHelper x = new PermutationHelper(elements);
		int i = 0;
		for (final String[] result : x) {
			assertEquals(expected[i], result);
			++i;
		}
	}

	// This test runs for a while
	// after running it, do this in the shell (this runs even longer):
	// sort -u /home/wagnerb/aaa
	// expected output is:
	/*
	 *  1: 'Ih -'
	  2: 'nen '
	  2: 'r '
	   3: 'e '
	    4: 'm '
	    4: 'n '
	    4: 'r '
	    4: 's '
	     5: 'gleichen '
	     5: 'seits '
	result:0: 'null -'

	 */
	// @Test
	public void testIt() throws IOException {
		final String[] ihr = new String[] { "Ihnen", "Ihr", "Ihre", "Ihrem",
				"Ihren", "Ihrer", "Ihrerseits", "Ihres", "Ihresgleichen",
				"Ihrethalben", "Ihretwegen", "Ihretwillen", "Ihrige",
				"Ihrigem", "Ihrigen", "Ihriger", "Ihriges", "Ihrs", "Sie", };
		final String[] ihr2 = new String[] { "Ihnen", "Ihr", "Ihre", "Ihrem",
				"Ihren", "Ihrer", "Ihrerseits", "Ihres", "Ihresgleichen", };

		final PermutationHelper x = new PermutationHelper(ihr2);

		BufferedWriter out = new BufferedWriter(new FileWriter(
				"/home/wagnerb/aaa"));

		for (final String[] result : x) {
			WordHierarchyBuilder.Word ihrTree = WordHierarchyBuilder
					.createWordTree(result, out);

			out.write("result:" + ihrTree.myToString(false));
			out.write("\n");
		}
		out.close();
	}
}
