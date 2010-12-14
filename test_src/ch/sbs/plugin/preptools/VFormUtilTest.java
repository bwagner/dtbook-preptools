package ch.sbs.plugin.preptools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.sbs.utils.preptools.vform.VFormUtil;

public class VFormUtilTest {

	@Test
	public void testReplaceKeepPart() {
		assertEquals("Deintegration", VFormUtil.replace("Deintegration"));
		assertEquals("Sieb", VFormUtil.replace("Sieb"));
	}

	@Test
	public void testReplaceKeep() {
		assertEquals(VFormUtil.NULL_MATCH, VFormUtil.find("Sieb", 0));
		final VFormUtil.Match m = VFormUtil.find("Sieben können Sie haben.", 0);
		assertEquals(14, m.startOffset);
		assertEquals(14 + "Sie".length(), m.endOffset);
	}

	@Test
	public void testReplaceNothing() {
		assertEquals("nix", VFormUtil.replace("nix"));
	}

	@Test
	public void testDeinetwegen() {
		assertEquals(wrap("Deinetwegen") + " ist es schief gegangen.",
				VFormUtil.replace("Deinetwegen ist es schief gegangen."));
	}

	@Test
	public void testReplace() {
		assertEquals(wrap("Sie"), VFormUtil.replace("Sie"));
		assertEquals(wrap("Ihre"), VFormUtil.replace("Ihre"));
		assertEquals(wrap("Ihr"), VFormUtil.replace("Ihr"));
		assertEquals(wrap("Ihren"), VFormUtil.replace("Ihren"));
		assertEquals(wrap("Ihrem"), VFormUtil.replace("Ihrem"));
		assertEquals(wrap("Ihres"), VFormUtil.replace("Ihres"));
		assertEquals(wrap("Deine"), VFormUtil.replace("Deine"));
		assertEquals(wrap("Dein"), VFormUtil.replace("Dein"));
		assertEquals("Das können " + wrap("Sie") + " zu " + wrap("Ihren")
				+ " Akten legen.",
				VFormUtil.replace("Das können Sie zu Ihren Akten legen."));
	}

	@Test
	public void testMatch() {

		// ____________1___________________2
		// ____________01234567890123456789012345678901234
		final String text = "Das können Sie zu Ihren Akten legen.";

		VFormUtil.Match match = VFormUtil.find(text, 0);

		assertEquals(11, match.startOffset);
		assertEquals(14, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(VFormUtil.NULL_MATCH, match);

	}

	@Test
	public void testMatchBoundary() {

		// ____________1___________________2
		// ____________01234567890123456789012345678901234
		final String text = "Dann können Sie's Ihrem Kollegen geben.";

		VFormUtil.Match match = VFormUtil.find(text, 0);

		assertEquals(12, match.startOffset);
		assertEquals(15, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(VFormUtil.NULL_MATCH, match);

	}

	@Test
	public void testNoMatch() {

		final String text = "Dann kann Anna es ihrem Kollegen geben.";

		VFormUtil.Match match = VFormUtil.find(text, 0);

		assertEquals(VFormUtil.NULL_MATCH, match);

	}

	@Test
	public void testEur() {

		assertEquals("Das kostet 50 Eur.",
				VFormUtil.replace("Das kostet 50 Eur."));
		assertEquals("Was kostet " + wrap("Eure") + " Lösung?",
				VFormUtil.replace("Was kostet Eure Lösung?"));

	}

	private static final String wrap(final String theString) {
		return "<brl:v-form>" + theString + "</brl:v-form>";
	}
}
