package ch.sbs.plugin.preptools;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ch.sbs.utils.preptools.vform.FileUtils;
import ch.sbs.utils.preptools.vform.VFormUtil;
import ch.sbs.utils.preptools.vform.VFormUtil.PositionMatch;

@SuppressWarnings("serial")
abstract class AbstractVFormAction extends AbstractAction {
	/**
	 * 
	 */
	protected final WorkspaceAccessPluginExtension workspaceAccessPluginExtension;

	/**
	 * @param theWorkspaceAccessPluginExtension
	 */
	AbstractVFormAction(
			final WorkspaceAccessPluginExtension theWorkspaceAccessPluginExtension) {
		workspaceAccessPluginExtension = theWorkspaceAccessPluginExtension;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		final WSEditor editorAccess = workspaceAccessPluginExtension
				.getWsEditor();
		final WSTextEditorPage aWSTextEditorPage;
		if ((aWSTextEditorPage = WorkspaceAccessPluginExtension
				.getPage(editorAccess)) != null) {
			final Document document = aWSTextEditorPage.getDocument();
			final DocumentMetaInfo dmi = workspaceAccessPluginExtension
					.getDocumentMetaInfo(editorAccess.getEditorLocation());
			try {
				doSomething(editorAccess, aWSTextEditorPage, document, dmi);
			} catch (final BadLocationException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Hook that gets called only when editor, page, document, text have
	 * successfully been retrieved.
	 * 
	 * @param editorAccess
	 * @param aWSTextEditorPage
	 * @param document
	 * @param dmi
	 *            TODO
	 * @throws BadLocationException
	 */
	protected abstract void doSomething(final WSEditor editorAccess,
			final WSTextEditorPage aWSTextEditorPage, final Document document,
			final DocumentMetaInfo dmi) throws BadLocationException;

}

@SuppressWarnings("serial")
class VFormStartAction extends AbstractVFormAction {
	VFormStartAction(
			final WorkspaceAccessPluginExtension workspaceAccessPluginExtension) {
		super(workspaceAccessPluginExtension);
	}

	@Override
	protected void doSomething(final WSEditor editorAccess,
			final WSTextEditorPage aWSTextEditorPage, final Document document,
			DocumentMetaInfo dmi) throws BadLocationException {

		final URL editorLocation = editorAccess.getEditorLocation();
		if (dmi.doneCheckingVform()) {
			if (workspaceAccessPluginExtension
					.showConfirmDialog("The document "
							+ FileUtils.basename(editorLocation)
							+ " has already been vformed.\n Do you want to Start over?")

			) {
				dmi.setDoneCheckingVform(false);
			}
			else {
				return;
			}
		}
		else if (dmi.hasStartedCheckingVform()) {
			if (workspaceAccessPluginExtension
					.showConfirmDialog("The document "
							+ FileUtils.basename(editorLocation)
							+ " is currently being vformed.\n Do you want to Start over?")

			) {
				dmi.setDoneCheckingVform(false);
			}
			else {
				return;
			}
		}

		final VFormUtil.Match match = VFormUtil.find(
				document.getText(0, document.getLength()), 0);
		aWSTextEditorPage.select(match.startOffset, match.endOffset);

		dmi.setHasStartedCheckingVform(true);
		dmi.setDoneCheckingVform(false);
		dmi.setCurrentPositionMatch(new PositionMatch(document, match));
		workspaceAccessPluginExtension.setCurrentState(dmi);
	}
}

@SuppressWarnings("serial")
abstract class ProceedAction extends AbstractVFormAction {

	ProceedAction(
			final WorkspaceAccessPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	// TODO: do it more nicely with template method
	// .i.e. searchOn gets called from "doSomething"
	protected void searchOn(final Document document,
			final WSTextEditorPage aWSTextEditorPage,
			final WSEditor editorAccess, final int continueAt)
			throws BadLocationException {
		final String newText = document.getText(0, document.getLength());
		final VFormUtil.Match match = VFormUtil.find(newText, continueAt);
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo(editorAccess.getEditorLocation());
		if (match.equals(VFormUtil.NULL_MATCH)) {
			workspaceAccessPluginExtension
					.showDialog("You're done with v-forms!");
			dmi.done();
			match.startOffset = 0;
			match.endOffset = 0;
		}
		workspaceAccessPluginExtension.setCurrentState(dmi);
		aWSTextEditorPage.select(match.startOffset, match.endOffset);
		dmi.setCurrentPositionMatch(new PositionMatch(document, match));
	}
}

@SuppressWarnings("serial")
class VFormAcceptAction extends ProceedAction {
	VFormAcceptAction(
			final WorkspaceAccessPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	@Override
	protected void doSomething(final WSEditor editorAccess,
			final WSTextEditorPage aWSTextEditorPage, final Document document,
			DocumentMetaInfo dmi) throws BadLocationException {

		final String ELEMENT_NAME = "brl:v-form";

		final String selText = aWSTextEditorPage.getSelectedText();

		if (selText == null) {
			// TODO: this is already suspicious
			// accepting should only be possible when user got here via
			// start- ,accept-, or find-action.
			// this means the cursor must have been moved in the mean time.
			return;
		}

		if (!VFormUtil.matches(selText)) {
			// TODO: this is already suspicious
			// accepting should only be possible when user got here via
			// start- ,accept-, or find-action.
			// this means the cursor must have been moved in the mean time.
			return;
		}

		final int MATCH_START = aWSTextEditorPage.getSelectionStart();
		final int MATCH_END = aWSTextEditorPage.getSelectionEnd();
		final PositionMatch pm = dmi.getCurrentPositionMatch();
		if (MATCH_START != pm.startOffset.getOffset()
				|| MATCH_END != pm.endOffset.getOffset()
				|| dmi.manualEditOccurred()) {
			if (workspaceAccessPluginExtension
					.showConfirmDialog("Cursor position has changed!\n"
							+ "Take up where we lef off last time? [OK]"
							+ " or continue anyway [Cancel]")) {
				aWSTextEditorPage.select(pm.startOffset.getOffset(),
						pm.endOffset.getOffset());
			}
		}

		aWSTextEditorPage.setCaretPosition(MATCH_START); // unselect text

		// starting with the end, so the start position doesn't shift
		document.insertString(MATCH_END, "</" + ELEMENT_NAME + ">", null);
		document.insertString(MATCH_START, "<" + ELEMENT_NAME + ">", null);

		final int continueAt = MATCH_START + ELEMENT_NAME.length() * 2
				+ "<></>".length() + selText.length();

		searchOn(document, aWSTextEditorPage, editorAccess, continueAt);
	}
}

@SuppressWarnings("serial")
class VFormFindAction extends ProceedAction {
	VFormFindAction(
			WorkspaceAccessPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	@Override
	protected void doSomething(final WSEditor editorAccess,
			final WSTextEditorPage aWSTextEditorPage, final Document document,
			DocumentMetaInfo dmi) throws BadLocationException {

		final int MATCH_START = aWSTextEditorPage.getSelectionStart();
		final int MATCH_END = aWSTextEditorPage.getSelectionEnd();
		final PositionMatch pm = dmi.getCurrentPositionMatch();
		if (MATCH_START != pm.startOffset.getOffset()
				|| MATCH_END != pm.endOffset.getOffset()
				|| dmi.manualEditOccurred()) {
			if (workspaceAccessPluginExtension
					.showConfirmDialog("Cursor position has changed!\n"
							+ "Take up where we lef off last time? [OK]"
							+ " or continue anyway [Cancel]")) {
				aWSTextEditorPage.select(pm.startOffset.getOffset(),
						pm.endOffset.getOffset());
			}
		}

		searchOn(document, aWSTextEditorPage, editorAccess,
				aWSTextEditorPage.getSelectionEnd());
	}
}