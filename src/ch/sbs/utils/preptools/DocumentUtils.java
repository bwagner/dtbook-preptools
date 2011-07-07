package ch.sbs.utils.preptools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DocumentUtils {
	/**
	 * Performs as many replacements as possible of pattern regex in document,
	 * starting at document index theStart. The replacement string may contain
	 * backreferences (e.g. "$1").
	 * 
	 * @param theDocument
	 *            The document on which to perform the replacement.
	 * @param theRegexToSearch
	 *            The regex for which to search.
	 * @param theReplacement
	 *            The replacement for the found regex, may include
	 *            backreferences (e.g. "$1")
	 * @return The number of replacements applied.
	 */
	public static int performReplacement(final Document theDocument,
			final String theRegexToSearch, final String theReplacement) {
		return performReplacement(theDocument, theRegexToSearch,
				theReplacement, 0);
	}

	/**
	 * Performs as many replacements as possible of pattern regex in document,
	 * The replacement string may contain backreferences (e.g. "$1").
	 * 
	 * @param theDocument
	 *            The document on which to perform the replacement.
	 * @param theRegexToSearch
	 *            The regex for which to search.
	 * @param replacement
	 *            The replacement for the found regex, may include
	 *            backreferences (e.g. "$1")
	 * @param theStart
	 *            The index from where on to start the search.
	 * @return The number of replacements applied.
	 */
	public static int performReplacement(final Document theDocument,
			final String theRegexToSearch, final String theReplacement,
			int theStart) {
		final Pattern pattern = Pattern.compile(theRegexToSearch);
		int replacements = 0;
		try {
			final Matcher matcher = pattern.matcher(theDocument.getText(0,
					theDocument.getLength()));
			int start = theStart;
			while (matcher.find(start)) {
				++replacements;
				start = doReplacements(theDocument, pattern, matcher,
						theReplacement);
				// need to reset matcher since document has changed.
				matcher.reset(theDocument.getText(0, theDocument.getLength()));
			}
		} catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
		return replacements;
	}

	/**
	 * Performs a single replacement of pattern regex in document, starting at
	 * document index theStart. The replacement string may contain
	 * backreferences (e.g. "$1").
	 * 
	 * @param theDocument
	 *            The document on which to perform the replacement.
	 * @param regex
	 *            The regex for which to search.
	 * @param theReplacement
	 *            The replacement for the found regex, may include
	 *            backreferences (e.g. "$1")
	 * @param theStart
	 *            The index from where on to start the search.
	 * @return True if a replacement was applied, false otherwise.
	 */
	public static boolean performSingleReplacement(final Document theDocument,
			final String regex, final String theReplacement, int theStart) {
		final Pattern pattern = Pattern.compile(regex);
		boolean performedReplacement = false;
		try {
			final Matcher matcher = pattern.matcher(theDocument.getText(0,
					theDocument.getLength()));
			if (performedReplacement = matcher.find(theStart)) {
				doReplacements(theDocument, pattern, matcher, theReplacement);
			}
		} catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
		return performedReplacement;
	}

	private static int doReplacements(final Document theDocument,
			final Pattern thePattern, final Matcher theMatcher,
			final String theReplacement) {
		int start = theMatcher.start();
		final int length = theMatcher.end() - start;
		try {
			final String matchedText = theDocument.getText(start, length);
			theDocument.remove(start, length);
			final String replaceAll = thePattern.matcher(matchedText)
					.replaceAll(theReplacement);
			theDocument.insertString(start, replaceAll, null);
			start += replaceAll.length();
		} catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
		return start;
	}
}
