package ch.sbs.utils.preptools.parens;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ch.sbs.utils.preptools.Match;

public class ParensUtilTest {

	@Test
	public void testParens() {
		final String sample = "« » ‹ › 〈 〉 ( ) [ ] { } ";
		assertEquals(0, ParensUtil.findOrphans(sample).size());
	}

	@Test
	public void testParens1() {
		final String sample = "« » ‹ › 〈 〉 ( ) [ ] {  ";
		assertEquals(1, ParensUtil.findOrphans(sample).size());
	}

	@Test
	public void testParens2() {
		final String sample = "« » ‹ › 〈  ( ) [ ] {  ";
		assertEquals(2, ParensUtil.findOrphans(sample).size());
	}

	@Test
	public void testParensLoc() {
		// ------------------------------1---------2
		// --------------------012345678901234567890123456789
		final String sample = "« » ‹ › 〈    ) [ ] {  ";
		final List<Match> orphans = ParensUtil.findOrphans(sample);
		assertEquals(3, orphans.size());
		int i = 0;
		Match match = orphans.get(i++);
		assertEquals(8, match.startOffset);
		assertEquals(9, match.endOffset);
		match = orphans.get(i++);
		assertEquals(13, match.startOffset);
		assertEquals(14, match.endOffset);
		match = orphans.get(i++);
		assertEquals(19, match.startOffset);
		assertEquals(20, match.endOffset);
	}
}
