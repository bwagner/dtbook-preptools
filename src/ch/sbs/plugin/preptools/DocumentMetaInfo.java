package ch.sbs.plugin.preptools;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ch.sbs.utils.preptools.Match;
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
	private WSTextEditorPage page;
	private Document document;
	private final URL url;
	private DocumentListener documentListener;
	private Match.PositionMatch currentPositionMatch;
	private Pattern currentVFormPattern;
	private Iterator<Match.PositionMatch> orphanedParensIterator;
	private PrepTool currentPrepTool;

	public DocumentMetaInfo(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		setPage(theWorkspaceAccessPluginExtension);

		setCurrentEditorPage(theWorkspaceAccessPluginExtension.getPageId());
		url = theWorkspaceAccessPluginExtension.getEditorLocation();

		setVFormPatternTo3rdPP();
	}

	/**
	 * This method normally shouldn't be called from outside. The only case is
	 * where the user accidentally closed the document which was still being
	 * processed and he wants to take up again where he left off.
	 * 
	 * @param theWorkspaceAccessPluginExtension
	 */
	public void setPage(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		page = theWorkspaceAccessPluginExtension.getPage();

		setDocument(page.getDocument());
	}

	public boolean vFormPatternIsAll() {
		return currentVFormPattern == VFormUtil.getAllPattern();
	}

	public void setVFormPatternToAll() {
		currentVFormPattern = VFormUtil.getAllPattern();
	}

	public void setVFormPatternTo3rdPP() {
		currentVFormPattern = VFormUtil.get3rdPPPattern();
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

	public boolean isProcessing() {
		return hasStartedCheckingVform() && !doneCheckingVform();
	}

	public void setHasStartedCheckingVform(final boolean hasStartedCheckingVform) {
		this.hasStartedCheckingVform = hasStartedCheckingVform;
	}

	public boolean hasStartedCheckingVform() {
		return hasStartedCheckingVform;
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

	private static boolean isDtBook(final Document document) {
		try {
			return document.getText(0, document.getLength())
					.contains("<dtbook");
		} catch (final BadLocationException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void finish() {
		document.removeDocumentListener(documentListener);
	}

	public URL getUrl() {
		return url;
	}

	public void setCurrentPositionMatch(
			final Match.PositionMatch theCurrentPositionMatch) {
		lastEditWasManual = false;
		currentPositionMatch = theCurrentPositionMatch;
	}

	public Match.PositionMatch getCurrentPositionMatch() {
		return currentPositionMatch;
	}

	public void setManualEdit() {
		lastEditWasManual = true;
	}

	public void resetManualEdit() {
		lastEditWasManual = false;
	}

	public boolean manualEditOccurred() {
		return lastEditWasManual;
	}

	public Pattern getCurrentVFormPattern() {
		return currentVFormPattern;
	}

	public void setOrphanedParens(final List<Match> theOrphanedParens) {
		final List<Match.PositionMatch> pml = new ArrayList<Match.PositionMatch>();
		for (final Match match : theOrphanedParens) {
			Match.PositionMatch mp = new Match.PositionMatch(document, match);
			pml.add(mp);
		}
		orphanedParensIterator = pml.iterator();
	}

	public boolean hasMoreOrphanedParens() {
		return orphanedParensIterator.hasNext();
	}

	public Match.PositionMatch getNextOrphanedParen() {
		return orphanedParensIterator.next();
	}

	public PrepTool getCurrentPrepTool() {
		return currentPrepTool;
	}

	public void setCurrentPrepTool(final PrepTool theCurrentPrepTool) {
		currentPrepTool = theCurrentPrepTool;
	}
}