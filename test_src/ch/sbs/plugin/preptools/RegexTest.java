package ch.sbs.plugin.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;

/*
 * 
 *             +----------+----------+
 *             |     =    |     !    |
 *             | Positive | Negative |
 * +-----------+----------+----------+
 * | Ahead  ?  |    ?=    |    ?!    |
 * +-----------+----------+----------+
 * | Behind ?< |   ?<=    |   ?<!    |
 * +-----------+----------+----------+
 */

public class RegexTest {
	// see java.util.regex.Pattern:2488: lookbehind must have fixed length
	@Test(expected = PatternSyntaxException.class)
	public void testPositiveLookBehindMustHaveFixedLength() {
		Pattern.compile("(?<=k\\s*)");
	}

	// see java.util.regex.Pattern:2488: lookbehind must have fixed length
	@Test(expected = PatternSyntaxException.class)
	public void testNegativeLookBehindMustHaveFixedLength() {
		Pattern.compile("(?<!k\\s*)");
	}

	@Test
	public void testRegexNegation() {

		final String regex = "(?<!bar)foo(?!bar)";
		final Pattern pattern = Pattern.compile(regex);

		assertFalse(pattern.matcher("barfoobar").find());
		assertFalse(pattern.matcher("barfoo").find());
		assertFalse(pattern.matcher("foobar").find());

		assertTrue(pattern.matcher("foo").find());
		assertTrue(pattern.matcher("bar foo bar").find());
	}

	@Test
	public void testRegexNegationvForm() {

		// final String regex =
		// "(?<!<\\s*brl\\s*:\\s*vform\\s*>\\s*)Sie(?!<\\s*/\\s*brl\\s*:\\s*vform\\s*>)";
		final String regex = "(?<!<brl:v-form>)Sie(?!</brl:v-form>)";
		final Pattern pattern = Pattern.compile(regex);
		assertFalse(pattern.matcher(
				"Das können <brl:v-form>Sie</brl:v-form> nicht.").find());
		assertFalse(pattern.matcher(
				"Das können <brl:v-form> Sie</brl:v-form> nicht.").find());
		assertFalse(pattern.matcher(
				"Das können <brl:v-form>Sie </brl:v-form> nicht.").find());

		assertTrue(pattern.matcher(
				"Das können <brl:v-form> Sie </brl:v-form> nicht.").find());

		assertTrue(pattern.matcher(
				"Das können <brl:v-form >Sie </brl:v-form> nicht.").find());

	}

	@Test
	public void testRegexNegationvFormWithSpaces() {

		// final String regex =
		// "(?<!<\\s*brl\\s*:\\s*vform\\s*>\\s*)Sie(?!<\\s*/\\s*brl\\s*:\\s*vform\\s*>)";
		final String regex = "(?<!<brl:v-form>)Sie(?!<\\s*/\\s*brl\\s*:\\s*v-form\\s*>)";
		final Pattern pattern = Pattern.compile(regex);
		assertFalse(pattern.matcher(
				"Das können <   brl:v-form>Sie</brl:v-form> nicht.").find());
		assertFalse(pattern.matcher(
				"Das können <brl:v-form> Sie</brl:v-form> nicht.").find());
		assertFalse(pattern.matcher(
				"Das können <brl:v-form>Sie </brl:v-form> nicht.").find());

		assertTrue(pattern.matcher(
				"Das können <brl:v-form> Sie </brl:v-form> nicht.").find());

		assertTrue(pattern.matcher(
				"Das können <brl:v-form >Sie </brl:v-form> nicht.").find());

	}

	@Test
	public void testNegativeLookahead() { // (?! ___ )
		// This regex asks:
		// Is there "John, Blank, a word that's not Smith, a capital letter
		// and a collection of word chars?
		final String regex = "John (?!Smith)[A-Z]\\w+";
		final Pattern pattern = Pattern.compile(regex);

		assertFalse(pattern.matcher("John Smith").find());
		assertTrue(pattern.matcher("John Jackson").find());
		assertTrue(pattern.matcher("John Westling").find());
		assertTrue(pattern.matcher("John Holmes").find());

	}

	@Test
	public void testPositiveLookAhead() { // (?= ___ )
		// This regex asks:
		// Is there a string that starts with 255 ?
		final String regex = "(?=255).*";
		final Pattern pattern = Pattern.compile(regex);

		assertTrue(pattern.matcher("255.0.0.1").find());
		assertTrue(pattern.matcher(" 255.0.0.1").find());
		assertTrue(pattern.matcher("255").find());
		assertTrue(pattern.matcher(" 255").find());
		assertFalse(pattern.matcher("25").find());
		assertEquals(
				"http://<ho>255.0.0.1. There, </ho>",
				pattern.matcher("http://255.0.0.1. There, ").replaceAll(
						"<ho>$0</ho>"));
		assertEquals("<ho>255.0.0.1. There, </ho>",
				pattern.matcher("255.0.0.1. There, ").replaceAll("<ho>$0</ho>"));
	}

	@Test
	public void testPositiveLookAheadWithAnchor() { // (?= ___ )
		// This regex asks:
		// Is there a string that starts with 255 ?
		final String regex = "(?=^255).*";
		final Pattern pattern = Pattern.compile(regex);

		assertTrue(pattern.matcher("255.0.0.1").find());
		assertFalse(pattern.matcher(" 255.0.0.1").find());
		assertTrue(pattern.matcher("255").find());
		assertFalse(pattern.matcher(" 255").find());
		assertFalse(pattern.matcher("25").find());
		assertEquals(
				"http://255.0.0.1. There, ",
				pattern.matcher("http://255.0.0.1. There, ").replaceAll(
						"<ho>$0</ho>"));
		assertEquals("<ho>255.0.0.1. There, </ho>",
				pattern.matcher("255.0.0.1. There, ").replaceAll("<ho>$0</ho>"));
	}

	@Test
	public void testPositiveLookBehind() { // (?<= ___ )
		// This regex asks:
		// Is there a non white space sequence preceded by http:// ?
		final String regex = "(?<=http://)\\S+";
		final Pattern pattern = Pattern.compile(regex);

		assertFalse(pattern.matcher("The Java2s website is found at ").find());
		assertTrue(pattern.matcher("http://www.java2s.com. There, ").find());
		assertFalse(pattern.matcher("you can find some Java examples.").find());
		assertEquals(
				"http://<ho>www.java2s.com.</ho> There, ",
				pattern.matcher("http://www.java2s.com. There, ").replaceAll(
						"<ho>$0</ho>"));
	}

	@Test
	public void testNegativeLookBehind() { // (?<! ___ )
		// http://www.lexemetech.com/2007/10/zero-width-negative-lookbehind.html
		// This regex asks:
		// Is there a number not preceded by + or - ?
		final String regex = "(?<![-+\\d])(\\d+)";
		final Pattern pattern = Pattern.compile(regex);

		assertFalse(pattern.matcher("+152 -444").find());
		assertTrue(pattern.matcher("+152 423 -444 -3 3").find());
		assertEquals("+152 <ho>423</ho> -444", pattern.matcher("+152 423 -444")
				.replaceAll("<ho>$1</ho>"));
		assertEquals("+152 +423 -444", pattern.matcher("+152 423 -444")
				.replaceAll("+$1"));
	}
}
