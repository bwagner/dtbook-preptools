package ch.sbs.utils.preptools;
import java.util.Iterator;

public class PermutationHelper implements Iterator<String[]>,
		Iterable<String[]> {
	private final PermutationGenerator pg;
	private final String[] result;
	private final String[] symbols;

	public PermutationHelper(final String[] theSymbols) {
		symbols = theSymbols;
		pg = new PermutationGenerator(symbols.length);
		result = new String[symbols.length];
	}

	public String[] getNext() {
		final int[] indices = pg.getNext();
		for (int i = 0; i < indices.length; i++) {
			result[i] = symbols[indices[i]];
		}
		return result;
	}

	@Override
	public boolean hasNext() {
		return pg.hasMore();
	}

	@Override
	public String[] next() {
		return getNext();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove");
	}

	@Override
	public Iterator<String[]> iterator() {
		return this;
	}
}
