package ch.sbs.plugin.preptools;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: preptools should load themselves. PrepToolLoader shouldn't know or care
 * about specific tools.
 */
public class PrepToolLoader {

	// Ordnungszahlen
	public static final String ORDINAL_NUM_TYPE = "ordinal";
	public static final String ORDINAL_SEARCH_REGEX = "((?<![,'.])\\b\\d+\\.(?!\\d))(\\s*)(</|&ndash;)?";
	public static final String ORDINAL_TAG = makeNumTag(ORDINAL_NUM_TYPE);
	public static final String ORDINAL_SKIP_REGEX = makeNumProtectRegex(ORDINAL_NUM_TYPE);

	// Römische Zahlen
	// case sensitive
	public static final String ROMAN_NUM_TYPE = "roman";
	public static final String ROMAN_SEARCH_REGEX = "\\b[IVXCMLD]+\\b\\.?";
	public static final String ROMAN_TAG = makeNumTag(ROMAN_NUM_TYPE);
	public static final String ROMAN_SKIP_REGEX = makeNumProtectRegex(ROMAN_NUM_TYPE);

	// Zahl mit Masseinheit
	// ignore case
	// Achtung: µμ das sind zwei verschiedene Zeichen!
	// Siehe
	// http://de.selfhtml.org/html/referenz/zeichen.htm#benannte_griechisch
	// Eines ist das Mikro-Zeichen &micro; das andere ist das kleine mü &mu;
	public static final String MEASURE_NUM_TYPE = "measure";
	public static final String MEASURE_SEARCH_REGEX = "\\b\\d*[.,'0-9]*\\d+\\s*(?iu:(([a-dg-hj-np-tv-zÅµμΩ²³]{1,4}|min|se[ck]|[km]*mol)\\s*/\\s*([a-dg-hj-np-tv-zÅµμΩ²³]{1,4}|min|se[ck]|[km]*mol)|([a-dg-hj-np-tv-zÅµμΩ²³]{1,4}|min|se[ck]|[km]*mol)))(?!\\p{L})";
	public static final String MEASURE_TAG = makeNumTag(MEASURE_NUM_TYPE);
	public static final String MEASURE_SKIP_REGEX = makeNumProtectRegex(MEASURE_NUM_TYPE);

	// Akronyme des Typs GmbH, GSoA, etc.
	// Abkürzungen des Typs x.y. oder x. y.
	// Grossbuchstaben(folgen) des Typs A, A-Z, MM, USA, A4
	public static final String ABBREV_SEARCH_REGEX = "(\\b\\p{L}*\\p{Ll}\\p{Lu}\\p{L}*\\b|\\b\\p{L}{1,4}\\.(?:\\s*\\p{L}{1,4}\\.){1,3}|\\p{Lu}\\p{Lu}+|\\p{Lu}(?!\\p{Ll}))|(\\b\\p{L}{1,4}\\.\\s+)(\\d)";
	public static final String ABBREV_TAG = "abbr";

	// http://redmine.sbszh.ch/issues/show/1203
	public static final String PAGEBREAK_SEARCH_REGEX = "(</p\\s*>\\s*(<pagenum\\s+id\\s*=\\s*\"page-\\d+\" page\\s*=\\s*\"normal\"\\s*>\\s*\\d+\\s*</pagenum\\s*>))(\\s*)<p\\s*>";
	public static final String PAGEBREAK_REPLACE = " $2 ";
	// http://redmine.sbszh.ch/issues/show/1272
	public static final String PAGEBREAK_REPLACE2 = "$1$3<p class=\"precedingemptyline\">";

	// http://redmine.sbszh.ch/issues/show/1201

	public static final String PLACEHOLDER = "_____";
	public static final String ACCENT_SEARCH_REGEX = "(\\b(?:\\p{L}*(?iu:[àâçéèêëìîïòôœùû])\\p{L}*)+\\b)";
	public static final String ACCENT_REPLACE = "<span brl:accents=\""
			+ PLACEHOLDER + "\">$1</span>";
	public static final String ACCENT_SKIP_REGEX = "span.*?";

	public static List<PrepTool> loadPrepTools(
			final PrepToolsPluginExtension thePrepToolsPluginExtension) {
		final List<PrepTool> prepTools = new ArrayList<PrepTool>();
		int i = 0;

		prepTools.add(new VFormPrepTool(thePrepToolsPluginExtension, i++, 'o'));

		prepTools
				.add(new ParensPrepTool(thePrepToolsPluginExtension, i++, 's'));

		prepTools.add(new FullRegexPrepTool(thePrepToolsPluginExtension, i++,
				'd', "Ordinal", ORDINAL_SEARCH_REGEX, ORDINAL_TAG,
				ORDINAL_SKIP_REGEX, null, new OrdinalChangeAction(
						thePrepToolsPluginExtension, "Change",
						ORDINAL_SEARCH_REGEX, "Ordinal")));

		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'r',
				"Roman", ROMAN_SEARCH_REGEX, ROMAN_TAG, ROMAN_SKIP_REGEX));

		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'u',
				"Measure", MEASURE_SEARCH_REGEX, MEASURE_TAG,
				MEASURE_SKIP_REGEX));

		prepTools.add(new FullRegexPrepTool(thePrepToolsPluginExtension, i++,
				'v', "Abbreviation", ABBREV_SEARCH_REGEX, ABBREV_TAG,
				ABBREV_TAG, null, new AbbrevStartAction(
						thePrepToolsPluginExtension, "Start",
						ABBREV_SEARCH_REGEX, "Abbreviation"),
				new AbbrevFindAction(thePrepToolsPluginExtension, "Find",
						ABBREV_SEARCH_REGEX, "Abbreviation"),
				new AbbrevChangeAction(thePrepToolsPluginExtension, "Change",
						ABBREV_SEARCH_REGEX, "Abbreviation")));

		prepTools.add(new PageBreakPrepTool(thePrepToolsPluginExtension, i++,
				'k'));

		prepTools
				.add(new AccentPrepTool(thePrepToolsPluginExtension, i++, 'a'));

		return prepTools;
	}

	private static String makeNumTag(final String role) {
		return "brl:num role=\"" + role + "\"";
	}

	private static String makeNumProtectRegex(final String role) {
		return "brl:num\\s+role\\s*=\\s*\"" + role + "\"";
	}
}
