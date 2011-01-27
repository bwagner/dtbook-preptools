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
		assertFalse(VFormUtil.matches(VFormUtil.wrap("Deine")));
	}

	@Test
	public void testReplaceKeep() {
		assertEquals(Match.NULL_MATCH, VFormUtil.find("Sieb", 0));
		final Match m = VFormUtil.find("Sieben können Sie haben.", 0);
		assertEquals(14, m.startOffset);
		assertEquals(14 + "Sie".length(), m.endOffset);
	}

	@Test
	public void testMatch() {

		// ____________________________1___________________2
		// __________________01234567890123456789012345678901234
		final String text = "Das können Sie zu Ihren Akten legen.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(11, match.startOffset);
		assertEquals(14, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testMatchBoundary() {

		// ____________1___________________2
		// ____________01234567890123456789012345678901234
		final String text = "Dann können Sie's Ihrem Kollegen geben.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(12, match.startOffset);
		assertEquals(15, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testNoMatch() {

		final String text = "Dann kann Anna es ihrem Kollegen geben.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testMatch1() {

		// ____________________________1_________2_________3
		// __________________01234567890123456789012345678901234
		final String text = "Dann kann Anna es Ihrem Kollegen geben.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

	}

	@Test
	public void testNoMatch1() {

		final String text = "Dann kann Anna es <brl:v-form>Ihrem</brl:v-form> Kollegen geben.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testSettingPatternAll() {
		// ____________________________1_________2_________3
		// __________________01234567890123456789012345678901234
		final String text = "Dann kann Anna es Deinem Kollegen geben.";

		Match match = VFormUtil.find(text, 0, VFormUtil.getAllPattern());

		assertEquals(18, match.startOffset);
		assertEquals(24, match.endOffset);

	}

	@Test
	public void testSettingPattern3rdPP() {
		// ____________________________1_________2_________3
		// __________________01234567890123456789012345678901234
		final String text = "Dann kann Anna es Deinem Kollegen geben.";

		Match match = VFormUtil.find(text, 0, VFormUtil.get3rdPPPattern());

		assertEquals(match, Match.NULL_MATCH);

	}
}
