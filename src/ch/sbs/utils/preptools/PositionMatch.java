package ch.sbs.utils.preptools;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

public class PositionMatch {
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