package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MarkupUtilTest {

	@Test
	public void testClosingElement() {
		assertEquals("brl:num",
				MarkupUtil.getClosingTag("brl:num role=\"ordinal\""));
	}

	@Test
	public void testClosingElement2() {
		assertEquals("brl:num",
				MarkupUtil.getClosingTag("brl:num\\s+role=\"ordinal\""));
	}
}
