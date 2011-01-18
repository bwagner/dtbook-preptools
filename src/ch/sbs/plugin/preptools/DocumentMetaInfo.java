package ch.sbs.plugin.preptools;

import java.net.URL;
import java.util.regex.Pattern;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ch.sbs.utils.preptools.FileUtils;
import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.Match.PositionMatch;
import ch.sbs.utils.preptools.vform.VFormUtil;

/**
 * Keeps meta information about a document known to the plugin.
 * 
 * 
 */
class DocumentMetaInfo {
	private boolean isDtBook;
	private boolean hasStartedCheckingVform;
	private boolean isDoneCheckingVform;
	private boolean lastEditWasManual;
	private String currentEditorPage;
	protected WSTextEditorPage page;
	private Document document;
	private URL url;
	private DocumentListener documentListener;
	private Match.PositionMatch currentPositionMatch;
	private Pattern currentVFormPattern;

	public boolean vFormPatternIsAll() {
		return currentVFormPattern == VFormUtil.getAllPattern();
	}

	public void setVFormPatternToAll() {
		setCurrentVFormPattern(VFormUtil.getAllPattern());
	}

	public void setVFormPatternTo3rdPP() {
		setCurrentVFormPattern(VFormUtil.get3rdPPPattern());
	}

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
			final Match.PositionMatch theCurrentPositionMatch) {
		setManualEditOccurred(false);
		currentPositionMatch = theCurrentPositionMatch;
	}

	public Match.PositionMatch getCurrentPositionMatch() {
		return currentPositionMatch;
	}

	public void setManualEditOccurred(boolean manualEditOccurred) {
		lastEditWasManual = manualEditOccurred;
	}

	public boolean manualEditOccurred() {
		return lastEditWasManual;
	}

	public void setCurrentVFormPattern(Pattern currentVFormPattern) {
		this.currentVFormPattern = currentVFormPattern;
	}

	public Pattern getCurrentVFormPattern() {
		return currentVFormPattern;
	}
}