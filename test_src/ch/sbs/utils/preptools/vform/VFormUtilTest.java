package ch.sbs.utils.preptools.vform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.sbs.utils.preptools.Match;

public class VFormUtilTest {

	@Test
	public void testMatchesNull() {
		assertFalse(VFormUtil.matches(null));
	}

	@Test
	public void testReplaceMatches() {
		assertTrue(VFormUtil.matches("Deine"));
		assertFalse(VFormUtil.matches("Sieb"));
	}

	@Test
	public void testNoMatchesMarkup() {
		assertTrue(VFormUtil.matches("Deine"));
		assertFalse(VFormUtil.matches(MarkupUtil.wrap("Deine", "brl:v-form")));
	}

	@Test
	public void testReplaceKeep() {
		assertEquals(Match.NULL_MATCH, VFormUtil.find("Sieb", 0));
		final String text = "Sieben können Sie haben.";
		final Match match = VFormUtil.find(text, 0);
		final String vform = "Sie";
		assertEquals(text.lastIndexOf(vform), match.startOffset);
		assertEquals(text.lastIndexOf(vform) + vform.length(), match.endOffset);
	}

	@Test
	public void testSkip() {
		assertEquals(Match.NULL_MATCH, VFormUtil.find("Sieb", 0));
		final Match m = VFormUtil.find(
				"Sieben können " + MarkupUtil.wrap("Sie", "brl:v-form")
						+ " haben.", 0);
		assertEquals(Match.NULL_MATCH, m);
	}

	@Test
	public void testSkipLiteral() {
		assertEquals(Match.NULL_MATCH, VFormUtil.find("Sieb", 0));
		final Match m = VFormUtil.find(
				"Sieben können " + MarkupUtil.wrap("Sie", "brl:literal")
						+ " haben.", 0);
		assertEquals(Match.NULL_MATCH, m);
	}

	@Test
	public void testMatch() {

		final String text = "Das können Sie zu Ihren Akten legen.";

		Match match = VFormUtil.find(text, 0);

		final String vform = "Sie";
		assertEquals(text.indexOf(vform), match.startOffset);
		assertEquals(text.indexOf(vform) + vform.length(), match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		final String vform2 = "Ihren";
		assertEquals(text.indexOf(vform2), match.startOffset);
		assertEquals(text.indexOf(vform2) + vform2.length(), match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testMatchBoundary() {

		final String text = "Dann können Sie's Ihrem Kollegen geben.";

		Match match = VFormUtil.find(text, 0);

		final String vform = "Sie";
		assertEquals(text.indexOf(vform), match.startOffset);
		assertEquals(text.indexOf(vform) + vform.length(), match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		final String vform2 = "Ihrem";
		assertEquals(text.indexOf(vform2), match.startOffset);
		assertEquals(text.indexOf(vform2) + vform2.length(), match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testNoMatch() {

		final String text = "Dann kann Anna es ihrem Kollegen geben.";

		final Match match = VFormUtil.find(text, 0);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testMatch1() {

		final String text = "Dann kann Anna es Ihrem Kollegen geben.";

		final Match match = VFormUtil.find(text, 0);

		final String vform = "Ihrem";
		assertEquals(text.indexOf(vform), match.startOffset);
		assertEquals(text.indexOf(vform) + vform.length(), match.endOffset);

	}

	@Test
	public void testNoMatch1() {

		assertEquals(
				Match.NULL_MATCH,
				VFormUtil.find(
						"Dann kann Anna es "
								+ MarkupUtil.wrap("Ihrem", "brl:v-form")
								+ " Kollegen geben.", 0));

	}

	@Test
	public void testSettingPatternAll() {

		final String text = "Dann kann Anna es Deinem Kollegen geben.";

		final Match match = VFormUtil.find(text, 0, VFormUtil.getAllPattern());

		final String vform = "Deinem";
		assertEquals(text.indexOf(vform), match.startOffset);
		assertEquals(text.indexOf(vform) + vform.length(), match.endOffset);

	}

	@Test
	public void testSettingPattern3rdPP() {

		assertEquals(VFormUtil.find("Dann kann Anna es Deinem Kollegen geben.",
				0, VFormUtil.get3rdPPPattern()), Match.NULL_MATCH);

	}
}
