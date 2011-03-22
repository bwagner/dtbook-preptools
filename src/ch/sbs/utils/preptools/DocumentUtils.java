package ch.sbs.utils.preptools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DocumentUtils {
	public static int performReplacement(final Document document,
			final String regex, final String replacement) {
		final Pattern pattern = Pattern.compile(regex);
		int replacements = 0;
		try {
			Matcher matcher;
			int start = 0;
			while ((matcher = pattern.matcher(document.getText(0,
					document.getLength()))).find(start)) {
				++replacements;
				start = matcher.start();
				final int length = matcher.end() - start;
				final String matchedText = document.getText(start, length);
				final Matcher matcher2 = pattern.matcher(matchedText);
				document.remove(start, length);
				final String replaceAll = matcher2.replaceAll(replacement);
				document.insertString(start, replaceAll, null);
				start += replaceAll.length();
			}
		} catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
		return replacements;
	}
}
