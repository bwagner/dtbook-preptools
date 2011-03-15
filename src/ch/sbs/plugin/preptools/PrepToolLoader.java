package ch.sbs.plugin.preptools;

import java.util.ArrayList;
import java.util.List;

public class PrepToolLoader {

	// Ordnungszahlen
	public static final String ORDINAL_REGEX = "\\b\\d+\\.";

	// Römische Zahlen
	// case sensitive
	public static final String ROMAN_REGEX = "\\b[IVXCMLD]+\\b\\.?";

	// Zahl mit Masseinheit
	// ignore case
	// Achtung: µμ das sind zwei verschiedene Zeichen!
	// Siehe
	// http://de.selfhtml.org/html/referenz/zeichen.htm#benannte_griechisch
	// Eines ist das Mikro-Zeichen &micro; das andere ist das kleine mü &mu;
	public static final String MEASURE_REGEX = "\\b\\d*[.,'0-9]*\\d+\\s*(?iu:(([a-dg-hj-np-tv-zÅµμΩ²³]{1,4}|min|se[ck]|[km]*mol)\\s*/\\s*([a-dg-hj-np-tv-zÅµμΩ²³]{1,4}|min|se[ck]|[km]*mol)|([a-dg-hj-np-tv-zÅµμΩ²³]{1,4}|min|se[ck]|[km]*mol)))(?!\\p{L})";

	// Akronyme des Typs GmbH, GSoA, etc.
	// Abkürzungen des Typs x.y. oder x. y.
	// Grossbuchstaben(folgen) des Typs A, A-Z, MM, USA, A4
	public static final String ABBREV = "(\\b\\p{L}*\\p{Ll}\\p{Lu}\\p{L}*\\b|\\b\\p{L}{1,4}\\.\\s*\\p{L}{1,4}\\.\\s*\\p{L}{1,4}\\.\\s*\\p{L}{1,4}\\.|\\b\\p{L}{1,4}\\.\\s*\\p{L}{1,4}\\.\\s*\\p{L}{1,4}\\.|\\b\\p{L}{1,4}\\.\\s*\\p{L}{1,4}\\.|\\p{Lu}\\p{Lu}+|\\p{Lu}(?!\\p{Ll}))";

	// http://redmine.sbszh.ch/issues/show/1203
	public static final String PAGEBREAK_REGEX = "</p\\s*>\\s*(<pagenum\\s+id\\s*=\\s*\"page-\\d+\" page\\s*=\\s*\"normal\"\\s*>\\s*\\d+\\s*</pagenum\\s*>)\\s*<p\\s*>";
	public static final String PAGEBREAK_REPLACE = " $1 ";

	// http://redmine.sbszh.ch/issues/show/1201

	public static final String PLACEHOLDER = "_____";
	public static final String ACCENT_REGEX = "(\\b(?:\\p{L}*(?iu:[àâçéèêëìîïòôœùû])\\p{L}*)+\\b)";
	public static final String ACCENT_REPLACE = "<span brl:accents=\""
			+ PLACEHOLDER + "\">$1</span>";

	// TODO: preptools should load themselves.
	// PrepToolLoader shouldn't know or care about specific tools.
	public static List<PrepTool> loadPrepTools(
			final PrepToolsPluginExtension thePrepToolsPluginExtension) {
		final List<PrepTool> prepTools = new ArrayList<PrepTool>();
		int i = 0;
		prepTools.add(new VFormPrepTool(thePrepToolsPluginExtension, i++, 'o'));
		prepTools
				.add(new ParensPrepTool(thePrepToolsPluginExtension, i++, 's'));

		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'd',
				"Ordinal", ORDINAL_REGEX, "brl:num role=\"ordinal\""));

		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'r',
				"Roman", ROMAN_REGEX, "brl:num role=\"roman\""));

		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'u',
				"Measure", MEASURE_REGEX, "brl:num role=\"measure\""));

		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'v',
				"Abbreviation", ABBREV, "abbr"));

		prepTools.add(new FullRegexPrepTool(thePrepToolsPluginExtension, i++,
				'k', "Pagebreak", PAGEBREAK_REGEX, null, PAGEBREAK_REPLACE));
		prepTools.add(new AccentPrepTool(thePrepToolsPluginExtension, i++, 'a',
				"Accent", ACCENT_REGEX, "span.*?", ACCENT_REPLACE));
		return prepTools;
	}
}
