package ch.sbs.utils.preptools.vform;

import java.util.Collection;
import java.util.HashSet;

interface WordProcessor {
	boolean processWord(final WordHierarchyBuilder.Word word);

	void preChildren(final WordHierarchyBuilder.Word word);

	void postChildren(final WordHierarchyBuilder.Word word);
}

class TestWordProcessor implements WordProcessor {
	private final Collection<String> bkup;
	private final Collection<String> ref = new HashSet<String>();

	public TestWordProcessor(final Collection<String> vocabulary) {
		bkup = new HashSet<String>();
		bkup.addAll(vocabulary);
	}

	@Override
	public boolean processWord(final WordHierarchyBuilder.Word word) {
		String realword = word.getWord();
		if (word.getWord() != null && word.isComplete()) {
			WordHierarchyBuilder.Word parent = word;
			while ((parent = parent.getParent()) != null) {
				if (parent.getWord() != null) {
					realword = parent.getWord() + realword;
				}
			}
			if (!bkup.contains(realword)) {
				System.err.println(realword + " not in vocabulary");
			}
			else {
				// System.out.println(realword + " in vocabulary");
				ref.add(realword);
			}
		}
		return true;
	}

	@Override
	public void preChildren(final WordHierarchyBuilder.Word word) {
		// nothing to do
	}

	@Override
	public void postChildren(final WordHierarchyBuilder.Word word) {
		// nothing to do
	}

	public boolean resultOk() {
		bkup.removeAll(ref);
		if (!bkup.isEmpty()) {
			System.err.println("bkup not empty! We still have:");
			for (final String str : bkup) {
				System.err.println(str);
			}

		}
		return bkup.isEmpty();
	}
}

class StringifyWordProcessor implements WordProcessor {

	public StringifyWordProcessor() {

	}

	public StringifyWordProcessor(boolean theWithId) {
		withId = theWithId;
	}

	private boolean withId;

	final StringBuilder sb = new StringBuilder();

	private static int indent;

	private static String makeIndent() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; ++i) {
			sb.append(" ");
		}
		return sb.toString();
	}

	@Override
	public boolean processWord(final WordHierarchyBuilder.Word word) {
		final boolean DEBUG = false;
		if (word.getWord() == null)
			return false;
		sb.append(makeIndent());
		if (DEBUG) {
			sb.append(indent);
			sb.append(": '");
		}
		sb.append(word);
		sb.append(" ");
		if (withId) {
			sb.append(word.getId());
			sb.append(" ");
		}
		sb.append(word.isComplete() ? "" : "-");
		if (DEBUG) {
			sb.append("'");
		}
		sb.append("\n");
		return false;
	}

	@Override
	public void preChildren(final WordHierarchyBuilder.Word word) {
		indent++;
	}

	@Override
	public void postChildren(final WordHierarchyBuilder.Word word) {
		indent--;
	}

	public String getResult() {
		return sb.toString();
	}
}

class RegexWordProcessor implements WordProcessor {

	public RegexWordProcessor() {

	}

	final StringBuilder sb = new StringBuilder();

	@Override
	public boolean processWord(final WordHierarchyBuilder.Word word) {
		if (word.getWord() == null)
			return false;
		sb.append(word);
		if (word.getChildren().isEmpty()) {
			sb.append("|");
		}
		return false;
	}

	@Override
	public void preChildren(final WordHierarchyBuilder.Word word) {
		// sb.setLength(sb.length() - 1); // chop off "|"
		sb.append("(?:");
	}

	@Override
	public void postChildren(final WordHierarchyBuilder.Word word) {
		sb.setLength(sb.length() - 1); // chop off "|"
		sb.append(")");
		sb.append(word.isComplete() ? "?" : "");
		sb.append("|");
	}

	public String getResult() {
		sb.setLength(sb.length() - 1); // chop off "|"
		return sb.toString();
	}
}
