package ch.sbs.plugin.preptools;

import java.util.ArrayList;
import java.util.List;

public class PrepToolLoader {

	// TODO:
	// find a good way to test these regexes
	// TODO: preptools should load themselves.
	// PrepToolLoader shouldn't know or care about specific tools.
	public static List<PrepTool> loadPrepTools(
			final PrepToolsPluginExtension thePrepToolsPluginExtension) {
		final List<PrepTool> prepTools = new ArrayList<PrepTool>();
		int i = 0;
		prepTools.add(new VFormPrepTool(thePrepToolsPluginExtension, i++));
		prepTools.add(new ParensPrepTool(thePrepToolsPluginExtension, i++));
		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'o',
				"Ordinal", "\\d+\\.", "num role=\"ordinal\""));
		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'r',
				"Roman", "[IVXCMLD]+\\.", "num role=\"roman\""));
		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'u',
				"Measure", "\\d*['.,]*\\d+\\s?[A-Z]{1,2}\\b",
				"num role=\"measure\""));
		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'a',
		// FIXME: not in brl:!
				"AbbrevPeriod", "[A-ZÄÖÜ]\\.\\s?[A-ZÄÖÜ]\\.", "abbr"));
		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 't',
		// FIXME: not in brl:!
		// FIXME: the replacement is more complicated:
		// <abbr>$1</abbr>$2
				"AbbrevCapital", "(\\b[A-ZÄÖÜ]+)(\\d*\\b)", "abbr"));
		prepTools.add(new RegexPrepTool(thePrepToolsPluginExtension, i++, 'y',
		// FIXME: not in brl:!
				"Acronym", "\\b\\w*[a-z]+[A-Z]+\\w*\\b", "abbr"));
		prepTools.add(new RegexPrepTool(
				thePrepToolsPluginExtension,
				i++,
				'c',
				// FIXME: not in brl:!
				// FIXME: additional functionality required:
				// some measurement to let the user decide whether he wants to
				// use
				// accents=reduced or accent=extended or something...
				// Ask cegli or mischa
				"Accent", "\\b\\S*[çñœåæøëïáéíóúàèìòùâêîôû]\\S*\\b",
				"span brl:accents=\"reduced\""));
		return prepTools;
	}
}
