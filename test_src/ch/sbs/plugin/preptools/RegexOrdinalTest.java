package ch.sbs.plugin.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.junit.Test;

import ch.sbs.utils.preptools.DocumentTestUtil;
import ch.sbs.utils.preptools.DocumentUtils;

public class RegexOrdinalTest {

	@Test
	public void testOrdinal() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertTrue(pattern.matcher("5.").find());
		assertFalse(pattern.matcher("a5.").find());
		assertTrue(pattern.matcher("23423.").find());
		assertFalse(pattern.matcher("2342").find());
	}

	@Test
	public void testBug1275Match() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertTrue(pattern.matcher("bla 23. bla").find());
		assertTrue(pattern.matcher("bla 345000. bla").find());
		assertTrue(pattern.matcher("bla 1. bla").find());
		assertTrue(pattern.matcher("bla 0. bla").find());
		assertTrue(pattern.matcher("bla 1.-9. bla").find());
	}

	@Test
	public void testBug1275NoMatch() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertFalse(pattern.matcher("bla 34,4.5 bla").find());
		assertFalse(pattern.matcher("bla 2,4. bla").find());
		assertFalse(pattern.matcher("bla 45'44. bla").find());
		assertFalse(pattern.matcher("bla 345'000. bla").find());
		assertFalse(pattern.matcher("bla 23.00 bla").find());
		assertFalse(pattern.matcher("bla 45.0 bla").find());
		assertFalse(pattern.matcher("bla 45.34,50 bla").find());
		assertFalse(pattern.matcher("bla 100.000.000. bla").find());
	}

	@Test
	public void testFeature1416_ndsp_newline_noun() {
		final String input = "bla 23.\nbla";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals("bla <brl:num role=\"ordinal\">23.</brl:num>&nbsp;bla",
				OrdinalChangeAction.feature1416(pattern, input));
	}

	@Test
	public void testFeature1416_ndsp_blank_noun() {
		final String input = "bla 23. bla";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals("bla <brl:num role=\"ordinal\">23.</brl:num>&nbsp;bla",
				OrdinalChangeAction.feature1416(pattern, input));
	}

	@Test
	public void testFeature1416_no_ndsp_no_blank() {
		final String input = "bla 23.bla";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals("bla <brl:num role=\"ordinal\">23.</brl:num>bla",
				OrdinalChangeAction.feature1416(pattern, input));
	}

	@Test
	public void testFeature1416_no_ndsp_blank_p() {
		final String input = "<p>am 25. </p>";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals("<p>am <brl:num role=\"ordinal\">25.</brl:num> </p>",
				OrdinalChangeAction.feature1416(pattern, input));
	}

	@Test
	public void testFeature1416_no_ndsp_ndash() {
		final String input = "<p>am 25. &ndash; blablabla</p>";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals(
				"<p>am <brl:num role=\"ordinal\">25.</brl:num> &ndash; blablabla</p>",
				OrdinalChangeAction.feature1416(pattern, input));
	}

	@Test
	public void testDocUtilFeature1416() throws BadLocationException {
		final StringBuilder sb1 = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		sb1.append("1.\n        ");
		sb2.append("<brl:num role=\"ordinal\">1.</brl:num>&nbsp;");
		final Document document = DocumentTestUtil.makeDocument(sb1.toString());
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		final String feature1416 = OrdinalChangeAction.feature1416(pattern,
				sb1.toString());
		final int count = DocumentUtils.performReplacement(document,
				PrepToolLoader.ORDINAL_SEARCH_REGEX, feature1416);
		assertEquals(sb2.toString(), document.getText(0, document.getLength()));
		assertEquals(1, count);

	}
}
