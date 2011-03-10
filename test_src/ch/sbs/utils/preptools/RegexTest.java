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
		final Pattern pattern = Pattern.compile(PrepToolLoader.ORDINAL_REGEX);
		assertTrue(pattern.matcher("5.").find());
		assertFalse(pattern.matcher("a5.").find());
		assertTrue(pattern.matcher("23423.").find());
		assertFalse(pattern.matcher("2342").find());
	}

	@Test
	public void testRoman() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.ROMAN_REGEX);
		assertTrue(pattern.matcher("IXII.").find());
		assertTrue(pattern.matcher("IXII").find());
		assertFalse(pattern.matcher("ixviv.").find());
		assertFalse(pattern.matcher("ixviv").find());
	}

	@Test
	public void testMeasure() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.MEASURE_REGEX);
		assertTrue(pattern.matcher("5.6km weg").find());
		assertTrue(pattern.matcher("5.6\nkm weg").find());
		assertTrue(pattern.matcher("5'300.62 k dabei").find());
		assertFalse(pattern.matcher("2343").find());
	}

	@Test
	public void testMeasureAngstrom() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.MEASURE_REGEX);
		assertTrue(pattern.matcher("Die Länge beträgt 50 Å.").find());
	}

	@Test
	public void testMeasureKmh() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "4 km/h";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasurekwH() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "4.5 kwH";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureCm() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "4'555'555.00 cm";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureMW() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "1 MW";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasurekN() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "-3,4kN";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasurekWh() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "10000 kWh";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureAngstroem() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "1 Å";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureL() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "45 l";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureMg() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "34.46 mg";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasuremol() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "400.000.000 mol";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasuremikrom() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "345 μm";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureDontMatch() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_REGEX + ")");
		final String focus = "fggfdfg 5 sdsf sdfsdsdfsf4szfd 4. September 1/2\n34 μ";
		final String input = "bla " + focus + " blu";
		assertEquals(input, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testAbbrevPeriod() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_PERIOD_REGEX);
		assertTrue(pattern.matcher("z.B.").find());
		assertTrue(pattern.matcher("z. B.").find());
		assertTrue(pattern.matcher("dipl. Inf.").find());
		assertTrue(pattern.matcher("a.\n          b.").find());
		assertFalse(pattern.matcher("Z").find());
	}

	@Test
	public void testAbbrevPeriodMoreChars() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_PERIOD_REGEX);
		assertTrue(pattern.matcher("z.Å.").find());
	}

	@Test
	public void testAbbrevCapital() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_CAPITAL_REGEX);
		assertTrue(pattern.matcher("bloss A4 brauchen").find());
		assertEquals(
				"bloss <abbr>A</abbr>4 brauchen",
				pattern.matcher("bloss A4 brauchen").replaceAll(
						PrepToolLoader.ABBREV_CAPITAL_REPLACE));
		assertTrue(pattern.matcher("drum A geben").find());
		assertFalse(pattern.matcher("drumA454 geben").find());
	}

	@Test
	public void testAbbrevCapitalMoreChars() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_CAPITAL_REGEX);
		assertTrue(pattern.matcher("bloss É4 brauchen").find());
	}

	@Test
	public void testAbbrevAcronym() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_ACRONYM_REGEX);
		assertTrue(pattern.matcher("Die GSoA ist").find());
		assertTrue(pattern.matcher("ein mE guter").find());
		assertFalse(pattern.matcher("ein Arbeiten").find());
	}

	@Test
	public void testAbbrevAcronymMoreChars() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_ACRONYM_REGEX);
		assertTrue(pattern.matcher("Die GSöA ist").find());
		assertTrue(pattern.matcher("ein mÉ guter").find());
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

	// public static final String PAGEBREAK_REGEX =
	// "</p>\\s*(<pagenum.*?</pagenum\\s*>)\\s*<p>";
	@Test
	public void testPagebreak1() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.PAGEBREAK_REGEX);
		final String inner = "<pagenum id=\"page-20\" page=\"normal\">20</pagenum>";
		final String input = "</p>\n\t" + inner + "\n\t<p>";
		assertTrue(pattern.matcher(input).find());
		assertEquals(
				" " + inner + " ",
				pattern.matcher(input).replaceAll(
						PrepToolLoader.PAGEBREAK_REPLACE));
	}

	@Test
	public void testPagebreak2() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.PAGEBREAK_REGEX);
		final String input = "</p>\n\t<pagenum id=\"page-20\" page=\"normal\">20</pagenum>\n\t<p>";
		assertTrue(pattern.matcher(input).find());
		assertEquals(
				" <pagenum id=\"page-20\" page=\"normal\">20</pagenum> ",
				pattern.matcher(input).replaceAll(
						PrepToolLoader.PAGEBREAK_REPLACE));
	}
}
