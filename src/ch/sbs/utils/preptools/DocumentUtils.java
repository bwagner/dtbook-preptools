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
	 * @param document
	 *            The document on which to perform the replacement.
	 * @param regex
	 *            The regex for which to search.
	 * @param replacement
	 *            The replacement for the found regex, may include
	 *            backreferences (e.g. "$1")
	 * @return The number of replacements applied.
	 */
	public static int performReplacement(final Document document,
			final String regex, final String replacement) {
		return performReplacement(document, regex, replacement, 0);
	}

	/**
	 * Performs as many replacements as possible of pattern regex in document,
	 * The replacement string may contain backreferences (e.g. "$1").
	 * 
	 * @param document
	 *            The document on which to perform the replacement.
	 * @param regex
	 *            The regex for which to search.
	 * @param replacement
	 *            The replacement for the found regex, may include
	 *            backreferences (e.g. "$1")
	 * @param theStart
	 *            The index from where on to start the search.
	 * @return The number of replacements applied.
	 */
	public static int performReplacement(final Document document,
			final String regex, final String replacement, int theStart) {
		final Pattern pattern = Pattern.compile(regex);
		int replacements = 0;
		try {
			Matcher matcher;
			int start = theStart;
			while ((matcher = pattern.matcher(document.getText(0,
					document.getLength()))).find(start)) {
				++replacements;
				start = matcher.start();
				final int length = matcher.end() - start;
				final String matchedText = document.getText(start, length);
				document.remove(start, length);
				final Matcher matcher2 = pattern.matcher(matchedText);
				final String replaceAll = matcher2.replaceAll(replacement);
				document.insertString(start, replaceAll, null);
				start += replaceAll.length();
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
	 * @param document
	 *            The document on which to perform the replacement.
	 * @param regex
	 *            The regex for which to search.
	 * @param replacement
	 *            The replacement for the found regex, may include
	 *            backreferences (e.g. "$1")
	 * @param theStart
	 *            The index from where on to start the search.
	 * @return True if a replacement was applied, false otherwise.
	 */
	public static boolean performSingleReplacement(final Document document,
			final String regex, final String replacement, int theStart) {
		final Pattern pattern = Pattern.compile(regex);
		int replacements = 0;
		try {
			Matcher matcher;
			int start = theStart;
			if ((matcher = pattern.matcher(document.getText(0,
					document.getLength()))).find(start)) {
				++replacements;
				start = matcher.start();
				final int length = matcher.end() - start;
				final String matchedText = document.getText(start, length);
				document.remove(start, length);
				final Matcher matcher2 = pattern.matcher(matchedText);
				final String replaceAll = matcher2.replaceAll(replacement);
				document.insertString(start, replaceAll, null);
				start += replaceAll.length();
			}
		} catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
		return replacements > 0;
	}
}
