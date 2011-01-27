package ch.sbs.plugin.preptools;

import java.net.URL;
import java.util.HashMap;
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
class DocumentMetaInfo {

	interface MetaInfo {
		boolean isProcessing();
	}

	Map<String, MetaInfo> toolSpecific = new HashMap<String, MetaInfo>();

	public final VFormPrepTool.MetaInfo vform;
	public final ParensPrepTool.MetaInfo orphanedParens;

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
		vform = new VFormPrepTool.MetaInfo();
		orphanedParens = new ParensPrepTool.MetaInfo(document);
		toolSpecific.put(VFormPrepTool.LABEL, vform);
		toolSpecific.put(ParensPrepTool.LABEL, orphanedParens);

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
	 * last edit was manual.
	 * 
	 */
	public void setManualEdit() {
		lastEditWasManual = true;
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

	public MetaInfo getCurrentToolSpecificMetaInfo() {
		final String label = getCurrentPrepTool().getLabel();
		return toolSpecific.get(label);
	}

	public boolean isProcessing() {
		return getCurrentToolSpecificMetaInfo().isProcessing();
	}
}