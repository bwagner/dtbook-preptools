package ch.sbs.plugin.preptools;

import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.undo.UndoableEditSupport;

/**
 * If several edits are performed programmatically on an oxygen document that
 * are conceptually to be regarded as a single edit, this class acts as a
 * grouper for those operations. Implement your operations as an implementation
 * of @see {@link Edit#edit()} and pass it to @see
 * {@link OxygenEditGrouper#perform(Document, Edit)}
 */
class OxygenEditGrouper {

	/**
	 * Implement @see {@link Edit#edit()} to pass as operation to @see
	 * {@link OxygenEditGrouper#perform(Document, Edit)}
	 */
	interface Edit {
		public void edit();
	}

	/**
	 * Utility to group several edits as a single undoable one.
	 * 
	 * @param document
	 *            The document to edit.
	 * @param theOperation
	 *            The edit operation to perform on the document.
	 */
	public static void perform(final Document document, final Edit theOperation) {
		final UndoableEditSupport support = (UndoableEditSupport) ((AbstractDocument) document)
				.getProperty("oxygenUndoSupport");
		support.beginUpdate();
		try {
			theOperation.edit();
		} finally {
			support.endUpdate();
		}
	}
}