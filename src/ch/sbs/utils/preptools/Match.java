package ch.sbs.utils.preptools;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

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

public class Match implements Comparable<Match> {

	public static class PositionMatch {
		public PositionMatch(final Document theDocument, final Match match) {
			this(theDocument, match.startOffset, match.endOffset);
		}

		public PositionMatch(final Document theDocument, int start, int end) {
			try {
				document = theDocument;
				startOffset = document.createPosition(start);
				endOffset = document.createPosition(end);
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
			}
		}

		public Document document;
		public Position startOffset;
		public Position endOffset;
	}

	public Match(int start, int end) {
		startOffset = start;
		endOffset = end;
	}

	public int startOffset;
	public int endOffset;
	public static final Match NULL_MATCH = new Match(-1, -1);

	@Override
	public int compareTo(final Match o) {
		return startOffset - o.startOffset;
	}

	@Override
	public String toString() {
		return "Match[" + startOffset + ", " + endOffset + "]";
	}
}
