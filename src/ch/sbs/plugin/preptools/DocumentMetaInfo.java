package ch.sbs.plugin.preptools;

import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

/**
 * Keeps meta information about a document known to the plugin.
 * 
 * 
 */
class DocumentMetaInfo {
	boolean isDtBook;
	boolean hasStartedCheckingVform;
	boolean isDoneCheckingVform;
	String currentEditorPage;
	// boolean isOldSpelling;
	protected WSTextEditorPage page;

	public void done() {
		isDoneCheckingVform = true;
	}
}