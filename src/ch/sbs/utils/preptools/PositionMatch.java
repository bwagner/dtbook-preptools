package ch.sbs.utils.preptools;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

/**
 * 
 * Stores a document and two Positions that represent a match.
 * Position: Represents a location within a document. It is intended to abstract
 * away implementation details of the document and enable specification of
 * positions within the document that are capable of tracking of change as the
 * document is edited.
 * 
 */
public class PositionMatch {

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