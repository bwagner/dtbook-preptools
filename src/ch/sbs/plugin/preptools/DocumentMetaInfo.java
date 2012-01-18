package ch.sbs.plugin.preptools;

import java.net.URL;
import java.util.Map;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ch.sbs.utils.preptools.Match;

/**
 * Keeps meta information about a document known to the plugin.
 * One DocumentMetaInfo object is maintained per document open in oXygen.
 * 
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

class DocumentMetaInfo {

	static class MetaInfo {
		private boolean hasStarted;
		private boolean isDone;
		private boolean cancelled;

		/**
		 * True when started processing but isn't done yet.
		 */
		public boolean isProcessing() {
			return hasStarted && !isDone;
		}

		/**
		 * True when started processing.
		 */
		public void setHasStarted(boolean theHasStarted) {
			hasStarted = theHasStarted;
		}

		/**
		 * True when started processing.
		 */
		public boolean hasStarted() {
			return hasStarted;
		}

		public void setDone(boolean theIsDone) {
			isDone = theIsDone;
		}

		/**
		 * True when done processing.
		 */
		public void done() {
			setDone(true);
		}

		/**
		 * True when done processing.
		 */
		public boolean isDone() {
			return isDone;
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public void setCancelled(boolean theCancelled) {
			cancelled = theCancelled;
		}

	}

	final Map<String, MetaInfo> toolSpecific;

	public MetaInfo getToolSpecificMetaInfo(final String label) {
		return toolSpecific.get(label);
	}

	// general
	private boolean isDtBook;
	private boolean lastEditWasManual;
	private String currentEditorPage;
	private WSTextEditorPage page;
	private Document document;
	private final URL url;
	private DocumentListener documentListener;
	private Match.PositionMatch currentPositionMatch;
	private PrepTool currentPrepTool;

	public DocumentMetaInfo(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		setPageAndDocument(theWorkspaceAccessPluginExtension);
		toolSpecific = theWorkspaceAccessPluginExtension
				.getToolSpecificMetaInfos(document);
		setCurrentEditorPage(theWorkspaceAccessPluginExtension.getPageId());
		url = theWorkspaceAccessPluginExtension.getEditorLocation();
	}

	/**
	 * This method normally shouldn't be called from outside. The only case is
	 * where the user accidentally closed the document which was still being
	 * processed and he wants to take up again where he left off.
	 * 
	 * @param theWorkspaceAccessPluginExtension
	 */
	public void setPageAndDocument(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		page = theWorkspaceAccessPluginExtension.getPage();

		setDocument(page.getDocument());
	}

	/**
	 * 
	 * @return current editor page (i.e. "Text", "Grid", "Author")
	 */
	public String getCurrentEditorPage() {
		return currentEditorPage;
	}

	/**
	 * Sets current editor page.
	 * 
	 * @param theCurrentEditorPage
	 */
	public void setCurrentEditorPage(final String theCurrentEditorPage) {
		currentEditorPage = theCurrentEditorPage;
	}

	/**
	 * 
	 * @return True if current document is a dtbook.
	 */
	public boolean isDtBook() {
		return isDtBook;
	}

	/**
	 * Sets document.
	 * 
	 * @param theDocument
	 */
	private void setDocument(final Document theDocument) {
		document = theDocument;
		documentListener = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				lastEditWasManual = true;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				lastEditWasManual = true;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				lastEditWasManual = true;
			}
		};
		document.addDocumentListener(documentListener);
		isDtBook = isDtBook(document);
	}

	/**
	 * 
	 * @param document
	 * @return True if given document is a dtbook.
	 */
	private static boolean isDtBook(final Document document) {
		try {
			return document.getText(0, document.getLength())
					.contains("<dtbook");
		} catch (final BadLocationException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * Cleans up.
	 */
	public void finish() {
		document.removeDocumentListener(documentListener);
	}

	/**
	 * accessor for url
	 * 
	 * @return url
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * oxygen, or rather Swing specific.
	 * Sets current position match.
	 * 
	 * @param theMatch
	 */
	public void setCurrentPositionMatch(final Match theMatch) {
		setCurrentPositionMatch(new Match.PositionMatch(document,
				theMatch.startOffset, theMatch.endOffset));
	}

	/**
	 * oxygen, or rather Swing specific.
	 * Sets current position match.
	 * 
	 * @param theStart
	 * @param theEnd
	 * 
	 * @param theCurrentPositionMatch
	 */
	public void setCurrentPositionMatch(int theStart, int theEnd) {
		setCurrentPositionMatch(new Match.PositionMatch(document, theStart,
				theEnd));
	}

	/**
	 * oxygen, or rather Swing specific.
	 * Sets current position match.
	 * 
	 * @param theCurrentPositionMatch
	 */
	public void setCurrentPositionMatch(
			final Match.PositionMatch theCurrentPositionMatch) {
		lastEditWasManual = false;
		currentPositionMatch = theCurrentPositionMatch;
	}

	/**
	 * oxygen, or rather Swing specific.
	 * Gets current position match.
	 * 
	 * @return
	 */
	public Match.PositionMatch getCurrentPositionMatch() {
		return currentPositionMatch;
	}

	/**
	 * last edit was not manual.
	 * 
	 */
	public void resetManualEdit() {
		lastEditWasManual = false;
	}

	/**
	 * @return True if last edit was manual.
	 */
	public boolean manualEditOccurred() {
		return lastEditWasManual;
	}

	/**
	 * @return the current preptool
	 */
	public PrepTool getCurrentPrepTool() {
		return currentPrepTool;
	}

	/**
	 * Sets current preptool.
	 * 
	 * @param theCurrentPrepTool
	 */
	public void setCurrentPrepTool(final PrepTool theCurrentPrepTool) {
		currentPrepTool = theCurrentPrepTool;
	}

	public Document getDocument() {
		return document;
	}

	public void setCurrentState() {
		currentPrepTool.setCurrentState(this);
	}

	public MetaInfo getCurrentToolSpecificMetaInfo() {
		if (currentPrepTool == null) {
			return null;
		}
		final String label = currentPrepTool.getPrepToolName();
		return getToolSpecificMetaInfo(label);
	}

	public boolean isProcessing() {
		final MetaInfo currentToolSpecificMetaInfo = getCurrentToolSpecificMetaInfo();
		return currentToolSpecificMetaInfo != null
				&& currentToolSpecificMetaInfo.isProcessing();
	}

	public boolean hasStarted() {
		final MetaInfo currentToolSpecificMetaInfo = getCurrentToolSpecificMetaInfo();
		return currentToolSpecificMetaInfo != null
				&& getCurrentToolSpecificMetaInfo().hasStarted();
	}

	public void setHasStarted(boolean theHasStarted) {
		final MetaInfo currentToolSpecificMetaInfo = getCurrentToolSpecificMetaInfo();
		if (currentToolSpecificMetaInfo != null) {
			getCurrentToolSpecificMetaInfo().setHasStarted(theHasStarted);
		}
	}

	public boolean isDone() {
		final MetaInfo currentToolSpecificMetaInfo = getCurrentToolSpecificMetaInfo();
		return currentToolSpecificMetaInfo != null
				&& getCurrentToolSpecificMetaInfo().isDone();
	}

	public void setDone(boolean theIsDone) {
		final MetaInfo currentToolSpecificMetaInfo = getCurrentToolSpecificMetaInfo();
		if (currentToolSpecificMetaInfo != null) {
			getCurrentToolSpecificMetaInfo().setDone(theIsDone);
		}
	}

	public boolean isCancelled() {
		final MetaInfo currentToolSpecificMetaInfo = getCurrentToolSpecificMetaInfo();
		return currentToolSpecificMetaInfo != null
				&& getCurrentToolSpecificMetaInfo().isCancelled();
	}

	public void setDCancelled(boolean theIsCancelled) {
		final MetaInfo currentToolSpecificMetaInfo = getCurrentToolSpecificMetaInfo();
		if (currentToolSpecificMetaInfo != null) {
			getCurrentToolSpecificMetaInfo().setCancelled(theIsCancelled);
		}
	}
}
