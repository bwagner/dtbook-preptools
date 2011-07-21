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
}
