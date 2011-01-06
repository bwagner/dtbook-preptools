package ch.sbs.plugin.preptools;

import javax.swing.text.Document;

import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

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
	private String currentEditorPage;
	protected WSTextEditorPage page;
	private Document document;

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

	public void setHasStartedCheckingVform(boolean hasStartedCheckingVform) {
		this.hasStartedCheckingVform = hasStartedCheckingVform;
	}

	public boolean hasStartedCheckingVform() {
		return hasStartedCheckingVform;
	}

	public void setDtBook(boolean isDtBook) {
		this.isDtBook = isDtBook;
	}

	public boolean isDtBook() {
		return isDtBook;
	}

	public void setDoneCheckingVform(boolean isDoneCheckingVform) {
		this.isDoneCheckingVform = isDoneCheckingVform;
	}

	public boolean doneCheckingVform() {
		return isDoneCheckingVform;
	}

	public void setDocument(Document theDocument) {
		document = theDocument;
	}
}