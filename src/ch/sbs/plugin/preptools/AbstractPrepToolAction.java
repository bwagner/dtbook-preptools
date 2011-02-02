package ch.sbs.plugin.preptools;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ch.sbs.utils.preptools.FileUtils;
import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.parens.ParensUtil;
import ch.sbs.utils.preptools.vform.VFormUtil;

@SuppressWarnings("serial")
abstract class AbstractPrepToolAction extends AbstractAction {
	/**
	 * 
	 */
	protected final PrepToolsPluginExtension workspaceAccessPluginExtension;

	/**
	 * @param theWorkspaceAccessPluginExtension
	 */
	AbstractPrepToolAction(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		workspaceAccessPluginExtension = theWorkspaceAccessPluginExtension;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		if ((workspaceAccessPluginExtension.getPage()) != null) {
			try {
				doSomething();
			} catch (final BadLocationException e) {
				e.printStackTrace();
			}
			workspaceAccessPluginExtension.setCurrentState();
		}
	}

	/**
	 * Hook that gets called only when editor, page, document, text have
	 * successfully been retrieved.
	 * 
	 * @throws BadLocationException
	 */
	protected abstract void doSomething() throws BadLocationException;

	/**
	 * Utility method that returns the end position of the current selection.
	 * 
	 * @return The end position of the current selection.
	 */
	protected int getSelectionEnd() {
		final WSEditor editorAccess = workspaceAccessPluginExtension
				.getWsEditor();
		final WSTextEditorPage aWSTextEditorPage = PrepToolsPluginExtension
				.getPage(editorAccess);
		return aWSTextEditorPage.getSelectionEnd();
	}

	protected void select(final Match match) {
		workspaceAccessPluginExtension.getPage().select(match.startOffset,
				match.endOffset);
	}

	protected void select(final Match.PositionMatch pm) {
		workspaceAccessPluginExtension.getPage().select(
				pm.startOffset.getOffset(), pm.endOffset.getOffset());
	}
}

@SuppressWarnings("serial")
abstract class AbstractVFormAction extends AbstractPrepToolAction {

	AbstractVFormAction(
			PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	/**
	 * Utility method to search on in document starting at startAt.
	 * 
	 * @param aWSTextEditorPage
	 * @param editorAccess
	 * @param startAt
	 * 
	 * @throws BadLocationException
	 */
	protected void searchOn(final WSTextEditorPage aWSTextEditorPage,
			final WSEditor editorAccess, final int startAt)
			throws BadLocationException {
		final Document document = aWSTextEditorPage.getDocument();
		final String newText = document.getText(0, document.getLength());
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo(editorAccess.getEditorLocation());
		final VFormPrepTool.MetaInfo vformMetaInfo = getMetaInfo(dmi);
		final Pattern currentPattern = vformMetaInfo.getCurrentPattern();
		final Match match = VFormUtil.find(newText, startAt, currentPattern);
		if (match.equals(Match.NULL_MATCH)) {
			workspaceAccessPluginExtension
					.showDialog("You're done with v-forms!");
			vformMetaInfo.done();
			match.startOffset = 0;
			match.endOffset = 0;
		}
		workspaceAccessPluginExtension.setCurrentState(dmi);
		select(match);
		dmi.setCurrentPositionMatch(new Match.PositionMatch(document, match));
	}

	protected VFormPrepTool.MetaInfo getMetaInfo(final DocumentMetaInfo dmi) {
		return (VFormPrepTool.MetaInfo) dmi
				.getToolSpecificMetaInfo(VFormPrepTool.LABEL);
	}

	protected VFormPrepTool.MetaInfo getMetaInfo() {
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo(workspaceAccessPluginExtension
						.getWsEditor().getEditorLocation());
		return (VFormPrepTool.MetaInfo) dmi
				.getToolSpecificMetaInfo(VFormPrepTool.LABEL);
	}

}

@SuppressWarnings("serial")
class VFormStartAction extends AbstractVFormAction {
	VFormStartAction(
			final PrepToolsPluginExtension workspaceAccessPluginExtension) {
		super(workspaceAccessPluginExtension);
	}

	@Override
	protected void doSomething() throws BadLocationException {
		final WSEditor editorAccess = workspaceAccessPluginExtension
				.getWsEditor();
		final WSTextEditorPage aWSTextEditorPage = workspaceAccessPluginExtension
				.getPage();
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo();

		final URL editorLocation = editorAccess.getEditorLocation();
		final VFormPrepTool.MetaInfo vformMetaInfo = getMetaInfo(dmi);
		if (vformMetaInfo.isDone()) {
			if (workspaceAccessPluginExtension
					.showConfirmDialog(
							"v-form: Start Over?",
							"The document "
									+ FileUtils.basename(editorLocation)
									+ " has already been vformed.\n Do you want to Start over?")

			) {
				vformMetaInfo.setDone(false);
			}
			else {
				return;
			}
		}
		else if (vformMetaInfo.hasStarted()) {
			if (workspaceAccessPluginExtension
					.showConfirmDialog(
							"v-form: Start Over?",
							"The document "
									+ FileUtils.basename(editorLocation)
									+ " is currently being vformed.\n Do you want to Start over?")

			) {
				vformMetaInfo.setDone(false);
			}
			else {
				return;
			}
		}

		vformMetaInfo.setHasStarted(true);
		vformMetaInfo.setDone(false);
		searchOn(aWSTextEditorPage, editorAccess, 0);
	}
}

@SuppressWarnings("serial")
abstract class VFormProceedAction extends AbstractVFormAction {

	VFormProceedAction(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	protected DocumentMetaInfo dmi;
	protected VFormPrepTool.MetaInfo vformMetaInfo;

	/**
	 * 
	 * Hook to be implemented by subclasses. Handles selected text and returns
	 * position where to continue with search.
	 * 
	 * @param document
	 *            The document.
	 * @param selText
	 *            The current selection.
	 * @return The position where to continue with search.
	 * @throws BadLocationException
	 */
	protected abstract int handleText(final Document document,
			final String selText) throws BadLocationException;

	/**
	 * Hook to be implemented by subclasses. If true the process is aborted.
	 * 
	 * @param selText
	 * @return True if the process is to be aborted.
	 */
	protected boolean veto(final String selText) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.AbstractVFormAction#doSomething(ro.sync.exml.workspace.api.editor.WSEditor, ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage, javax.swing.text.Document, ch.sbs.plugin.preptools.DocumentMetaInfo)
	 */
	@Override
	protected void doSomething() throws BadLocationException {
		final WSEditor editorAccess = workspaceAccessPluginExtension
				.getWsEditor();
		final WSTextEditorPage aWSTextEditorPage = workspaceAccessPluginExtension
				.getPage();
		final DocumentMetaInfo theDmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo();

		dmi = theDmi;
		vformMetaInfo = getMetaInfo();
		final String selText = aWSTextEditorPage.getSelectedText();

		if (veto(selText))
			return;

		handleManualCursorMovement(aWSTextEditorPage, dmi);

		final Document document = aWSTextEditorPage.getDocument();
		final int continueAt = handleText(document, selText);

		searchOn(aWSTextEditorPage, editorAccess, continueAt);
	}

	/**
	 * Utility method to handle user's manual cursor movement.
	 * 
	 * @param aWSTextEditorPage
	 * @param dmi
	 */
	private void handleManualCursorMovement(
			final WSTextEditorPage aWSTextEditorPage, final DocumentMetaInfo dmi) {
		lastMatchStart = aWSTextEditorPage.getSelectionStart();
		lastMatchEnd = aWSTextEditorPage.getSelectionEnd();
		final Match.PositionMatch pm = dmi.getCurrentPositionMatch();
		if (lastMatchStart != pm.startOffset.getOffset()
				|| lastMatchEnd != pm.endOffset.getOffset()
				|| dmi.manualEditOccurred()) {
			if (workspaceAccessPluginExtension.showConfirmDialog(
					"v-form: Cursor", "Cursor position has changed!\n",
					"Take up where we left off last time", "continue anyway")) {
				select(pm);
			}
		}
	}

	protected int lastMatchStart;
	protected int lastMatchEnd;
}

@SuppressWarnings("serial")
class VFormAcceptAction extends VFormProceedAction {
	VFormAcceptAction(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	@Override
	protected boolean veto(final String selText) {
		return (selText == null || !VFormUtil.matches(selText,
				vformMetaInfo.getCurrentPattern()));
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.ProceedAction#handleText(javax.swing.text.Document, java.lang.String)
	 */
	@Override
	protected int handleText(final Document document, final String selText)
			throws BadLocationException {
		final String ELEMENT_NAME = "brl:v-form";
		// starting with the end, so the start position doesn't shift
		document.insertString(lastMatchEnd, "</" + ELEMENT_NAME + ">", null);
		document.insertString(lastMatchStart, "<" + ELEMENT_NAME + ">", null);

		final int continueAt = lastMatchStart + ELEMENT_NAME.length() * 2
				+ "<></>".length() + selText.length();
		return continueAt;
	}
}

@SuppressWarnings("serial")
class VFormFindAction extends VFormProceedAction {
	VFormFindAction(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.ProceedAction#handleText(javax.swing.text.Document, java.lang.String)
	 */
	@Override
	protected int handleText(final Document document, final String selText)
			throws BadLocationException {
		return getSelectionEnd();
	}
}

@SuppressWarnings("serial")
abstract class AbstractOrphanParenAction extends AbstractPrepToolAction {

	protected ParensPrepTool.MetaInfo getMetaInfo(final DocumentMetaInfo dmi) {
		return (ParensPrepTool.MetaInfo) dmi
				.getToolSpecificMetaInfo(ParensPrepTool.LABEL);
	}

	protected ParensPrepTool.MetaInfo getMetaInfo() {
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo(workspaceAccessPluginExtension
						.getWsEditor().getEditorLocation());
		return (ParensPrepTool.MetaInfo) dmi
				.getToolSpecificMetaInfo(ParensPrepTool.LABEL);
	}

	@Override
	protected void doSomething() throws BadLocationException {
		init();
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo();

		if (getMetaInfo().hasNext()) {
			select(dmi);
		}
		else {
			handleNoneFound();
		}
	}

	protected void init() {

	}

	protected void handleNoneFound() {

	}

	AbstractOrphanParenAction(
			PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	protected void select(final DocumentMetaInfo dmi) {
		select(getMetaInfo().next());
	}

}

@SuppressWarnings("serial")
class OrphanParenStartAction extends AbstractOrphanParenAction {

	OrphanParenStartAction(
			PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	@Override
	protected void init() {
		final WSTextEditorPage aWSTextEditorPage = workspaceAccessPluginExtension
				.getPage();
		final Document document = aWSTextEditorPage.getDocument();
		List<Match> orphans = null;
		try {
			orphans = ParensUtil.findOrphans(document.getText(0,
					document.getLength()));
		} catch (BadLocationException e) {
			workspaceAccessPluginExtension.showMessage(e.getMessage());
			e.printStackTrace();
		}
		final ParensPrepTool.MetaInfo parensMetaInfo = getMetaInfo();
		parensMetaInfo.set(orphans);
		parensMetaInfo.setHasStarted(true);
		parensMetaInfo.setDone(false);
	}

	@Override
	protected void handleNoneFound() {
		workspaceAccessPluginExtension.showDialog("No orphaned parens found.");
		getMetaInfo().setDone(true);
	}

}

@SuppressWarnings("serial")
class OrphanParenFindNextAction extends AbstractOrphanParenAction {

	OrphanParenFindNextAction(
			PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	@Override
	protected void handleNoneFound() {
		workspaceAccessPluginExtension
				.showDialog("You're done with orphaned parens.");
		getMetaInfo().setDone(true);
	}
}