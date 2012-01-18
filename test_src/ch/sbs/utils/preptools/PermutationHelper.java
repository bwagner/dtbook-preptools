package ch.sbs.utils.preptools;
import java.util.Iterator;

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
