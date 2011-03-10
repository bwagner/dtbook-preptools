package ch.sbs.utils.string;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testJoin() {
		assertEquals("[a, b]", StringUtils.join(new String[] { "a", "b" }));
	}

	@Test
	public void testJoin3() {
		assertEquals("[a, b, c]",
				StringUtils.join(new String[] { "a", "b", "c" }));
	}
}
