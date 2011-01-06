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
			try {
				doSomething(editorAccess, aWSTextEditorPage, document,
						document.getText(0, document.getLength()));
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
	 * @param text
	 * @throws BadLocationException
	 */
	protected abstract void doSomething(final WSEditor editorAccess,
			final WSTextEditorPage aWSTextEditorPage, final Document document,
			final String text) throws BadLocationException;

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
			final String text) throws BadLocationException {

		final URL editorLocation = editorAccess.getEditorLocation();
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo(editorLocation);
		if (dmi.isDoneCheckingVform) {
			workspaceAccessPluginExtension
					.showMessage("starting over? (document was finished)");
			if (workspaceAccessPluginExtension
					.showConfirmDialog("The document "
							+ FileUtils.basename(editorLocation)
							+ " has already been vformed.\n Do you want to Start over?")

			) {
				dmi.isDoneCheckingVform = false;
			}
			else {
				return;
			}
		}
		else if (dmi.hasStartedCheckingVform) {
			workspaceAccessPluginExtension.showMessage("starting over?");
			if (workspaceAccessPluginExtension
					.showConfirmDialog("The document "
							+ FileUtils.basename(editorLocation)
							+ " is currently being vformed.\n Do you want to Start over?")

			) {
				dmi.isDoneCheckingVform = false;
			}
			else {
				return;
			}
		}

		final VFormUtil.Match match = VFormUtil.find(text, 0);
		aWSTextEditorPage.select(match.startOffset, match.endOffset);

		dmi.hasStartedCheckingVform = true;
		dmi.isDoneCheckingVform = false;
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
	}
}

// Accept should only be enabled
// 1.) when there's selected text in the editor
// 2.) the selected text conforms to a vform.
@SuppressWarnings("serial")
class VFormAcceptAction extends ProceedAction {
	VFormAcceptAction(
			final WorkspaceAccessPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	@Override
	protected void doSomething(final WSEditor editorAccess,
			final WSTextEditorPage aWSTextEditorPage, final Document document,
			final String text) throws BadLocationException {

		final String ELEMENT_NAME = "brl:v-form";

		final String selText = aWSTextEditorPage.getSelectedText();

		if (selText == null) {
			return;
		}

		if (!VFormUtil.matches(selText)) {
			return;
		}

		final int MATCH_START = aWSTextEditorPage.getSelectionStart();
		final int MATCH_END = aWSTextEditorPage.getSelectionEnd();
		aWSTextEditorPage.setCaretPosition(MATCH_START); // unselect text

		// starting with the end, so the start position doesn't shift
		document.insertString(MATCH_END, "</" + ELEMENT_NAME + ">", null);
		document.insertString(MATCH_START, "<" + ELEMENT_NAME + ">", null);

		final int continueAt = MATCH_START + ELEMENT_NAME.length() * 2
				+ "<></>".length() + selText.length();

		searchOn(document, aWSTextEditorPage, editorAccess, continueAt);
	}
}