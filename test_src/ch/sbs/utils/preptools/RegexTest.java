package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;

import ch.sbs.plugin.preptools.PrepToolLoader;

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

	@Test
	public void testIgnoreCaseSwitch() {
		final String regex = "(?i)ABC(?-i)ABC";
		final Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher("abcABC").find());
		assertTrue(pattern.matcher("ABCABC").find());
		assertFalse(pattern.matcher("abcabc").find());
		assertEquals("xsABCABCty",
				pattern.matcher("xABCABCy").replaceAll("s$0t"));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testIgnoreCaseNoGroup1() {
		final String regex = "(?i)ABC(?-i)ABC";
		final Pattern pattern = Pattern.compile(regex);
		assertEquals("xsABCABCty",
				pattern.matcher("xABCABCy").replaceAll("s$1t"));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testIgnoreCaseNoGroup1b() {
		final String regex = "(?i:ABC)ABC";
		final Pattern pattern = Pattern.compile(regex);
		assertEquals("xsABCABCty",
				pattern.matcher("xABCABCy").replaceAll("s$1t"));
	}

	@Test
	public void testIgnoreCaseGroup1() {
		final String regex = "((?i:ABC)ABC)";
		final Pattern pattern = Pattern.compile(regex);
		assertEquals("xsABCABCty",
				pattern.matcher("xABCABCy").replaceAll("s$1t"));
	}

	@Test
	public void testIgnoreCaseGroup() {
		final String regex = "(?i:ABC)ABC";
		final Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher("abcABC").find());
		assertTrue(pattern.matcher("ABCABC").find());
		assertFalse(pattern.matcher("abcabc").find());
	}

	@Test
	public void testOrdinal() {
		// \d+\.
		final String regex = PrepToolLoader.ORDINAL_REGEX;
		final Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher("5.").find());
		assertTrue(pattern.matcher("23423.").find());
		assertFalse(pattern.matcher("2342").find());
	}

	@Test
	public void testRoman() {
		// \b[IVXCMLD]+\.?
		final String regex = PrepToolLoader.ROMAN_REGEX;
		final Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher("IXII.").find());
		assertTrue(pattern.matcher("IXII").find());
		assertFalse(pattern.matcher("ixviv.").find());
		assertFalse(pattern.matcher("ixviv").find());
	}

	@Test
	public void testMeasure() {
		// TODO: possibly need to improve this regex, e.g.
		// it now accepts e.g.: '.,,,..'4

		// (?i:\d*['.,]*\d+\s?[A-Z]{1,2}\b)
		final String regex = PrepToolLoader.MEASURE_REGEX;
		final Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher("5.6km weg").find());
		assertTrue(pattern.matcher("5'300.62 k dabei").find());
		assertFalse(pattern.matcher("2343").find());
	}

	@Test
	public void testAbbrevPeriod() {
		// (?i:[A-ZÄÖÜ]\.\s?[A-ZÄÖÜ]\.)
		final String regex = PrepToolLoader.ABBREV_PERIOD_REGEX;
		final Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher("z.B.").find());
		assertTrue(pattern.matcher("z. B.").find());
		assertTrue(pattern.matcher("dipl. Inf.").find());
		assertFalse(pattern.matcher("Z").find());
	}

	@Test
	public void testAbbrevCapital() {
		// (\b[A-ZÄÖÜ]+)(\d*\\b).
		final String regex = PrepToolLoader.ABBREV_CAPITAL_REGEX;
		final Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher("bloss A4 brauchen").find());
		assertEquals(
				"bloss <abbr>A</abbr>4 brauchen",
				pattern.matcher("bloss A4 brauchen").replaceAll(
						PrepToolLoader.ABBREV_CAPITAL_REPLACE));
		assertTrue(pattern.matcher("drum A geben").find());
		assertFalse(pattern.matcher("drumA454 geben").find());
	}

	@Test
	public void testAbbrevAcronym() {
		// \b\w*[a-z]+[A-Z]+\w*\b
		final String regex = PrepToolLoader.ABBREV_ACRONYM_REGEX;
		final Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher("Die GSoA ist").find());
		assertTrue(pattern.matcher("ein mE guter").find());
		assertFalse(pattern.matcher("ein Arbeiten").find());
	}
}
