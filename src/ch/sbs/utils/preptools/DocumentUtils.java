package ch.sbs.utils.preptools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
	* Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
	*
	* This file is part of dtbook-preptools.
	* 	
	* dtbook-preptools is free software: you can redistribute it
	* and/or modify it under the terms of the GNU Lesser General Public
	* License as published by the Free Software Foundation, either
	* version 3 of the License, or (at your option) any later version.
	* 	
	* This program is distributed in the hope that it will be useful,
	* but WITHOUT ANY WARRANTY; without even the implied warranty of
	* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	* Lesser General Public License for more details.
	* 	
	* You should have received a copy of the GNU Lesser General Public
	* License along with this program. If not, see
	* <http://www.gnu.org/licenses/>.
	*/

public class DocumentUtils {
	/**
	 * Performs as many replacements as possible of pattern regex in document,
	 * starting at document index theStart. The replacement string may contain
	 * backreferences (e.g. "$1"). If the regex to search is to be taken
	 * literally, enclose it with \\Q und \\E.
	 * 
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
	public static int performMultipleReplacements(final Document theDocument,
			final String theRegexToSearch, final String theReplacement) {
		return performMultipleReplacements(theDocument, theRegexToSearch,
				theReplacement, 0);
	}

	/**
	 * Performs as many replacements as possible of pattern regex in document,
	 * The replacement string may contain backreferences (e.g. "$1"). If the
	 * regex to search is to be taken literally, enclose it with \\Q und \\E.
	 * 
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
	public static int performMultipleReplacements(final Document theDocument,
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
	 * backreferences (e.g. "$1"). If the regex to search is to be taken
	 * literally, enclose it with \\Q und \\E.
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
		Matcher matcher;
		try {
			matcher = pattern.matcher(theDocument.getText(0,
					theDocument.getLength()));
		} catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
		if (performedReplacement = matcher.find(theStart)) {
			doReplacements(theDocument, pattern, matcher, theReplacement);
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
