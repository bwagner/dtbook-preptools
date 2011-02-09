package ch.sbs.utils.preptools.vform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MarkupUtilTest {

	@Test
	public void testClosingElement() {
		assertEquals("brl:num",
				MarkupUtil.getClosingTag("brl:num role=\"ordinal\""));
	}
}
