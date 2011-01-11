package ch.sbs.plugin.preptools;

import java.net.URL;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ch.sbs.utils.preptools.vform.FileUtils;
import ch.sbs.utils.preptools.vform.VFormUtil.PositionMatch;

/**
 * Keeps meta information about a document known to the plugin.
 * 
 * 
 */
class DocumentMetaInfo {
	private boolean isDtBook;
	private boolean hasStartedCheckingVform;
	private boolean isDoneCheckingVform;
	// private boolean isOldSpelling;
	private boolean lastEditWasManual;
	private String currentEditorPage;
	protected WSTextEditorPage page;
	private Document document;
	private URL url;
	private DocumentListener documentListener;
	private PositionMatch currentPositionMatch;

	public String getCurrentEditorPage() {
		return currentEditorPage;
	}

	public void setCurrentEditorPage(final String theCurrentEditorPage) {
		currentEditorPage = theCurrentEditorPage;
	}

	public void done() {
		setDoneCheckingVform(true);
	}

	public boolean isDone() {
		return doneCheckingVform();
	}

	public boolean isProcessing() {
		return hasStartedCheckingVform() && !doneCheckingVform();
	}

	public void setHasStartedCheckingVform(final boolean hasStartedCheckingVform) {
		this.hasStartedCheckingVform = hasStartedCheckingVform;
	}

	public boolean hasStartedCheckingVform() {
		return hasStartedCheckingVform;
	}

	public void setDtBook(final boolean isDtBook) {
		this.isDtBook = isDtBook;
	}

	public boolean isDtBook() {
		return isDtBook;
	}

	public void setDoneCheckingVform(final boolean isDoneCheckingVform) {
		this.isDoneCheckingVform = isDoneCheckingVform;
	}

	public boolean doneCheckingVform() {
		return isDoneCheckingVform;
	}

	public void setDocument(
			final Document theDocument,
			final WorkspaceAccessPluginExtension theWorkspaceAccessPluginExtension) {
		document = theDocument;
		documentListener = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				setManualEditOccurred(true);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				setManualEditOccurred(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				setManualEditOccurred(true);
			}
		};
		document.addDocumentListener(documentListener);
	}

	public void finish() {
		document.removeDocumentListener(documentListener);
	}

	public Document getDocument() {
		return document;
	}

	public String shortUrl() {
		return FileUtils.basename(url);
	}

	public void setUrl(final URL theUrl) {
		url = theUrl;
	}

	public URL getUrl() {
		return url;
	}

	public void setCurrentPositionMatch(
			final PositionMatch theCurrentPositionMatch) {
		setManualEditOccurred(false);
		currentPositionMatch = theCurrentPositionMatch;
	}

	public PositionMatch getCurrentPositionMatch() {
		return currentPositionMatch;
	}

	public void setManualEditOccurred(boolean manualEditOccurred) {
		lastEditWasManual = manualEditOccurred;
	}

	public boolean manualEditOccurred() {
		return lastEditWasManual;
	}
}