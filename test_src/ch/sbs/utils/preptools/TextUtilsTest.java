package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextUtilsTest {
	@Test
	public void testI() {
		assertEquals("(?i:bla)", TextUtils.wrapI("bla"));
	}

	@Test
	public void testMultiline() {
		assertEquals("(?m:bla)", TextUtils.wrapMultiline("bla"));
	}

	@Test
	public void testDotAll() {
		assertEquals("(?s:bla)", TextUtils.wrapDotAll("bla"));
	}

	@Test
	public void testUI() {
		assertEquals("(?u:(?i:bla))", TextUtils.wrapIU("bla"));
	}

	@Test
	public void testX() {
		assertEquals("(?x:bla)", TextUtils.wrapComments("bla"));
	}

	@Test
	public void testProtectForSearchString() {
		final String txt = "Dieser Text enthält leider keine Klammer. Das tut ihm Leid.Restlos.";
		final String regex = "leid.r";
		assertEquals(
				"Dieser Text enthält Leid. R keine Klammer. Das tut ihm Leid. Restlos.",
				txt.replaceAll(TextUtils.wrapI(regex), "Leid. R"));
		assertEquals(
				"Dieser Text enthält leider keine Klammer. Das tut ihm Leid. Restlos.",
				txt.replaceAll(
						TextUtils.wrapI(TextUtils.quoteRegexMeta(regex)),
						"Leid. R"));
	}

	@Test(expected = AssertionError.class)
	public void testProtectDoesNotWorkForReplacementString() {
		assertEquals("b-$1-a",
				"bla".replaceAll("(l)", TextUtils.quoteRegexMeta("-$1-")));
	}
}
