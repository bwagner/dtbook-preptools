package ch.sbs.utils.preptools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class MetaUtils {
	private static final String OUR_PLACE_HOLDER = "______";
	private static final String PREFIX = "prod:PrepTool:";
	private static final String META_TAG = ">(\\s*)(<meta.*?>)(\\s*</head\\s*>)";
	private static final String REPLACE_META_TAG = ">$1$2$1<meta name=\""
			+ PREFIX + OUR_PLACE_HOLDER + "\" content=\"done\"/>$3";
	private static final Pattern META_PATTERN = Pattern.compile(META_TAG);

	private static final String HEAD_TAG = ">(\\s*)(</head\\s*>)";
	private static final String REPLACE_HEAD_TAG = ">$1<meta name=\"" + PREFIX
			+ OUR_PLACE_HOLDER + "\" content=\"done\"/>$1$2";
	private static final Pattern HEAD_PATTERN = Pattern.compile(HEAD_TAG);

	public static void insertPrepToolInfo(final Document document,
			final String preptool) {
		try {
			final int headEnd = document.getText(0, document.getLength())
					.indexOf("</head");
			if (headEnd < 0) {
				throw new RuntimeException(
						"document does not contain closing tag for element \"head\"");
			}
			Matcher matcher;
			String replacement;
			Pattern pattern;
			// This is done rather awkwardly, because I don't want to get the
			// whole text in the document and perform the replacement on it
			// and then insert the changed text back into the document, but
			// rather, figure out what part of the text will be replaced,
			// delete that from the document, then insert the replacement.
			if ((matcher = (pattern = META_PATTERN).matcher(document.getText(0,
					document.getLength()))).find()) {
				replacement = REPLACE_META_TAG;
			}
			else if ((matcher = (pattern = HEAD_PATTERN).matcher(document
					.getText(0, document.getLength()))).find()) {
				replacement = REPLACE_HEAD_TAG;
			}
			else {
				throw new RuntimeException(
						"Document didn't contain expected regex.");
			}
			final int start = matcher.start();
			final int end = matcher.end();
			final String tobeReplaced = document.getText(start, end - start);
			matcher = pattern.matcher(tobeReplaced);
			replacement = matcher.replaceAll(replacement);
			document.remove(start, end - start);
			document.insertString(start,
					replacement.replace(OUR_PLACE_HOLDER, preptool), null);

		} catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
}
