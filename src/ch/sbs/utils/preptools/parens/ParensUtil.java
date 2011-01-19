package ch.sbs.utils.preptools.parens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.sbs.utils.preptools.Match;

public class ParensUtil {

	public static final char LAQUO = '«';
	public static final char RAQUO = '»';
	public static final char LSAQUO = '‹';
	public static final char RSAQUO = '›';
	public static final char LANG = '〈';
	public static final char RANG = '〉';
	public static final char LBRACE = '{';
	public static final char RBRACE = '}';
	public static final char LBRACKET = '[';
	public static final char RBRACKET = ']';
	public static final char LPAREN = '(';
	public static final char RPAREN = ')';

	public static final char[] ALL_CHARS = new char[] { LAQUO, RAQUO, LSAQUO,
			RSAQUO, LANG, RANG, LBRACE, RBRACE, LBRACKET, RBRACKET, LPAREN,
			RPAREN, };

	public static final String OPENING = LAQUO + "|" + LSAQUO + "|" + LANG
			+ "|\\" + LBRACE + "|\\" + LBRACKET + "|\\" + LPAREN;

	public static final String CLOSING = RAQUO + "|" + RSAQUO + "|" + RANG
			+ "|\\" + RBRACE + "|\\" + RBRACKET + "|\\" + RPAREN;

	public static final String ALL = OPENING + "|" + CLOSING;

	public static final Pattern pattern = Pattern.compile(ALL);

	public static final Map<Character, Character> pairs = new HashMap<Character, Character>();

	static {
		pairs.put(LAQUO, RAQUO);
		pairs.put(LSAQUO, RSAQUO);
		pairs.put(LANG, RANG);
		pairs.put(LBRACE, RBRACE);
		pairs.put(LBRACKET, RBRACKET);
		pairs.put(LPAREN, RPAREN);
	}

	/**
	 * Finds list of potentially orphaned parens.
	 * 
	 * @param theText
	 *            The text to search
	 * @return list of potentially orphaned parens. It can be empty.
	 */
	public static List<Match> findOrphans(final String theText) {

		// for each paren, keep a list of matches.
		final Map<Character, List<Match>> occurrences = new HashMap<Character, List<Match>>();

		for (final char ch : ALL_CHARS) {
			occurrences.put(ch, new ArrayList<Match>());
		}
		final Matcher matcher = pattern.matcher(theText);

		while (matcher.find()) {
			final char matchChar = matcher.group().charAt(0);
			final Match match = new Match(matcher.start(), matcher.end());
			occurrences.get(matchChar).add(match);
		}
		final List<Match> potentialOrphans = new ArrayList<Match>();
		for (final char ch : pairs.keySet()) {
			if (occurrences.get(ch).size() == occurrences.get(pairs.get(ch))
					.size()) {
				occurrences.remove(ch);
				occurrences.remove(pairs.get(ch));
			}
		}
		for (final List<Match> occurence : occurrences.values()) {
			potentialOrphans.addAll(occurence);
		}

		// TODO: it's kinda stupid to order them by appearance if we
		// have this information while gathering the matches.
		// The difficulty is that matches will get deleted in between.
		Collections.sort(potentialOrphans, new Comparator<Match>() {
			/**
			 * Returns a negative integer, zero, or a positive integer as the
			 * first argument is less than, equal to, or greater than the second
			 * 
			 * @param o1
			 * @param o2
			 * @return
			 */
			@Override
			public int compare(final Match o1, final Match o2) {
				return o1.startOffset - o2.startOffset;
			}
		});
		return potentialOrphans;
	}
}
