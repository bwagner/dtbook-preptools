package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;

import ch.sbs.plugin.preptools.PrepToolLoader;

/*
 *             Lookarounds:
 *             +----------+----------+
 *             |     =    |     !    |
 *             | Positive | Negative |
 * +-----------+----------+----------+
 * | Ahead  ?  |    ?=    |    ?!    |
 * +-----------+----------+----------+
 * | Behind ?< |   ?<=    |   ?<!    |
 * +-----------+----------+----------+
 * 
 * Non-capturing parentheses: (?: ___ )
 * Switches/Groups with flags:
 * (?f) ____ (?-f) # allows turning a flag on and off within a regex
 * (?f ____ ) # keeps the flag on for the regex in parenthesis.
 * (?-f ____ ) # keeps the flag off for the regex in parenthesis.
 * Ignore-case       : i
 * Unix lines        : d Only the '\n' line terminator is recognized in the behavior of ., ^, and $. 
 * Multiline         : m expressions ^ and $ match just after (just before), a line terminator or the end of the input sequence.
 * Ignore-case       : s expression . matches any character, including a line terminator: testDotAll()
 * Unicode-case      : u Unicode-aware case folding
 * Ignore-case       : x Whitespace is ignored, and embedded comments starting with # are ignored until the end of a line.
 * Unicode Char Class: U Java7 only! (US-ASCII only) Predefined character classes and POSIX character classes are in 
 *                     conformance with Unicode Technical Standard #18: Unicode Regular Expression Annex C:
                       Compatibility Properties. (http://www.unicode.org/reports/tr18/#Compatibility_Properties)
 *
 * Capturing parentheses: ( ___ )
 * 
 * 
 * See:
 * http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
 * http://download.oracle.com/javase/7/docs/api/java/util/regex/Matcher.html
 * 
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
	public void testRoman() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ROMAN_SEARCH_REGEX);
		assertTrue(pattern.matcher("IXII.").find());
		assertTrue(pattern.matcher("IXII").find());
		assertFalse(pattern.matcher("ixviv.").find());
		assertFalse(pattern.matcher("ixviv").find());
	}

	@Test
	public void testDotAll() {
		final String regex = "<!--.*-->";
		final String regexDotAll = "(?s:" + regex + ")";
		assertTrue(Pattern.compile(regex).matcher("<!-- Die GSoA ist -->")
				.find());
		assertFalse(Pattern.compile(regex)
				.matcher("<!-- Die GSoA\n\nblabla \n ist -->").find());
		assertTrue(Pattern.compile(regexDotAll)
				.matcher("<!-- Die GSoA ist -->").find());
		assertTrue(Pattern.compile(regexDotAll)
				.matcher("<!-- Die GSoA ist -->").find());
		assertTrue(Pattern.compile(regexDotAll)
				.matcher("<!-- Die GSoA\n\nblabla \n ist -->").find());
	}

	@Test
	public void testComment() {
		final String regex = "(?s:<!--.*-->)";
		final Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher("<!-- Die GSoA ist -->").find());
		assertTrue(pattern.matcher("<!-- Die GSoA\n\nblabla \n ist -->").find());
		assertFalse(pattern.matcher("ein mE guter").find());
		assertFalse(pattern.matcher("ein Arbeiten").find());
	}

	@Test
	public void testPi1() {
		final Pattern pattern = Pattern.compile("(\\p{Pi})");
		final String input = "bla ‹ bla";
		assertTrue(pattern.matcher(input).find());
		assertEquals("bla _‹_ bla", pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testPf1() {
		final Pattern pattern = Pattern.compile("(\\p{Pf})");
		final String input = "bla › bla";
		assertTrue(pattern.matcher(input).find());
		assertEquals("bla _›_ bla", pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testPi2() {
		final Pattern pattern = Pattern.compile("(\\p{Pi})");
		final String input = "bla « bla";
		assertTrue(pattern.matcher(input).find());
		assertEquals("bla _«_ bla", pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testPf2() {
		final Pattern pattern = Pattern.compile("(\\p{Pf})");
		final String input = "bla » bla";
		assertTrue(pattern.matcher(input).find());
		assertEquals("bla _»_ bla", pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testPi3() {
		final Pattern pattern = Pattern.compile("(\\p{Pi})");
		final String input = "bla 〈 bla";
		assertFalse(pattern.matcher(input).find());
	}

	@Test
	public void testPf3() {
		final Pattern pattern = Pattern.compile("(\\p{Pf})");
		final String input = "bla 〉 bla";
		assertFalse(pattern.matcher(input).find());
	}

	@Test
	public void testBackref() {
		final Pattern pattern = Pattern.compile("(.)b\\1");
		assertTrue(pattern.matcher("aba").find());
		assertFalse(pattern.matcher("oba").find());
	}

	@Test
	public void testBackref2() {
		final Pattern pattern = Pattern.compile("(.)(.)b\\2\\1");
		assertTrue(pattern.matcher("aoboa").find());
		assertFalse(pattern.matcher("aobaa").find());
	}

	// named groups available only from Java 7 on
	// http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#groupname
	// Java 6 doesn't support them:
	// http://download.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#cg
	@Test
	public void testBackrefName() {
		final Pattern pattern = Pattern
				.compile(isJava7() ? "(?<first>)(?<second>)b\\k<second>\\k<first>"
						: "(.)(.)b\\2\\1");
		assertTrue(pattern.matcher("aoboa").find());
		assertFalse(pattern.matcher("aobaa").find());
	}

	// named groups available only from Java 7 on
	// http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#groupname
	// Java 6 doesn't support them:
	// http://download.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#cg
	// http://blogs.oracle.com/xuemingshen/entry/named_capturing_group_in_jdk7
	// http://stackoverflow.com/questions/415580/regex-named-groups-in-java
	@Test
	public void testBackrefNameReplace() {
		final Pattern pattern = Pattern
				.compile(isJava7() ? "(?<first>)(?<second>)b\\k<second>\\k<first>"
						: "(.)(.)b\\2\\1");
		assertEquals(
				"oBa aobaa",
				pattern.matcher("aoboa aobaa").replaceAll(
						isJava7() ? "${second}B${first}" : "$2B$1"));
	}

	// http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#backref
	@Test
	public void testBackrefMoreThan10() {
		final Pattern pattern = Pattern
				.compile("(.)(.)(.)(.)(.)(.)(.)(.)(.)(.)(.)(.)x\\1\\2\\3\\4\\5\\6\\7\\8\\9\\10\\11\\12");
		assertTrue(pattern.matcher("123456789abcx123456789abc").find());
		assertFalse(pattern.matcher("123456789abcx223456789abc").find());
	}

	private static boolean isJava7() {
		return JAVA_7;
	}

	private static final boolean JAVA_7 = "1.7".equals(System
			.getProperty("java.specification.version"));
}
