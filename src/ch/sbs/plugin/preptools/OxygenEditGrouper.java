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
