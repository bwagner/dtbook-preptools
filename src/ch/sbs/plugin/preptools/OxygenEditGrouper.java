package ch.sbs.plugin.preptools;

import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.undo.UndoableEditSupport;

class OxygenEditGrouper {

	interface Edit {
		public void edit();
	}

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