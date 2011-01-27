package ch.sbs.plugin.preptools;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
 * One DocumentMetaInfo object is maintained per document open in oXygen.
 * 
 */
class DocumentMetaInfo {

	interface MetaInfo {
		boolean isProcessing();
	}

	Map<String, MetaInfo> toolSpecific = new HashMap<String, MetaInfo>();

	// TODO: this class belongs in VFormPrepTool, but
	// its instances should be kept here, since the
	// information in VFormMetaInfo is per document, just
	// like DocumentMetaInfo
	static class VFormMetaInfo implements MetaInfo {
		private boolean hasStartedChecking;
		private boolean isDoneChecking;
		private Pattern currentPattern;

		public VFormMetaInfo() {
			setPatternTo3rdPP();
		}

		/**
		 * 
		 * @return true if vform pattern is set to all.
		 */
		public boolean patternIsAll() {
			return currentPattern == VFormUtil.getAllPattern();
		}

		/**
		 * Sets vform pattern to all.
		 */
		public void setPatternToAll() {
			currentPattern = VFormUtil.getAllPattern();
		}

		/**
		 * Sets vform pattern to 3rdPersonPlural.
		 */
		public void setPatternTo3rdPP() {
			currentPattern = VFormUtil.get3rdPPPattern();
		}

		/**
		 * True when done checking vforms.
		 */
		public void done() {
			setDoneChecking(true);
		}

		/**
		 * 
		 * @return if currently processing.
		 */
		@Override
		public boolean isProcessing() {
			return isProcessingVform();
		}

		/**
		 * 
		 * @return if currently processing.
		 */
		private boolean isProcessingVform() {
			return hasStartedChecking() && !doneChecking();
		}

		/**
		 * True if has started checking vform.
		 * 
		 * @param theHasStartedChecking
		 */
		void setHasStartedChecking(final boolean theHasStartedChecking) {
			hasStartedChecking = theHasStartedChecking;
		}

		/**
		 * True if has started checking vform.
		 * 
		 * @return True if has started checking vform.
		 */
		public boolean hasStartedChecking() {
			return hasStartedChecking;
		}

		/**
		 * True if has finished checking vform.
		 * 
		 * @param setDoneChecking
		 */
		public void setDoneChecking(final boolean setDoneChecking) {
			isDoneChecking = setDoneChecking;
		}

		/**
		 * True if has finished checking vform.
		 * 
		 * @return
		 */
		public boolean doneChecking() {
			return isDoneChecking;
		}

		/**
		 * 
		 * @return current vform-pattern.
		 */
		public Pattern getCurrentPattern() {
			return currentPattern;
		}

	}

	// TODO: this class belongs in ParensPrepTool
	static class OrphanedParensMetaInfo implements MetaInfo {
		private Iterator<Match.PositionMatch> orphanedParensIterator;
		private final Document document;

		OrphanedParensMetaInfo(final Document theDocument) {
			document = theDocument;
		}

		/**
		 * Sets orphaned parens.
		 * 
		 * @param theOrphanedParens
		 */
		public void set(final List<Match> theOrphanedParens) {
			final List<Match.PositionMatch> pml = new ArrayList<Match.PositionMatch>();
			for (final Match match : theOrphanedParens) {
				Match.PositionMatch mp = new Match.PositionMatch(document,
						match);
				pml.add(mp);
			}
			orphanedParensIterator = pml.iterator();
		}

		/**
		 * Iterator-function.
		 * 
		 * @return true if iterator has more orphaned parens.
		 */
		public boolean hasNext() {
			return orphanedParensIterator.hasNext();
		}

		/**
		 * Iterator-function.
		 * 
		 * @return next orphaned paren of this iterator.
		 */
		public Match.PositionMatch next() {
			return orphanedParensIterator.next();
		}

		@Override
		public boolean isProcessing() {
			return false;
		}

	}

	public final VFormMetaInfo vform;
	public final OrphanedParensMetaInfo orphanedParens;

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
		vform = new VFormMetaInfo();
		orphanedParens = new OrphanedParensMetaInfo(document);
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

	public boolean isProcessing() {
		final String label = getCurrentPrepTool().getLabel();
		return toolSpecific.get(label).isProcessing();
	}
}