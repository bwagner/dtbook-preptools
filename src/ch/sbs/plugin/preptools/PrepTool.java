package ch.sbs.plugin.preptools;

import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.text.Document;

import ro.sync.exml.editor.EditorPageConstants;
import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.vform.VFormUtil;

/**
 * PrepTool provides PrepTool-Specific:
 * - menuItemNr
 * - DocumentMetaInfo.MetaInfo (this is also document-specific)
 * - toolbar
 * - actions
 * 
 */
abstract class PrepTool {

	protected final PrepToolsPluginExtension prepToolsPluginExtension;
	private final int menuItemNr;
	private final int MNEMONIC;

	/**
	 * @param thePrepToolsPluginExtension
	 * @param theMenuItemNr
	 * @param theMnemonic
	 */
	PrepTool(final PrepToolsPluginExtension thePrepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic) {
		prepToolsPluginExtension = thePrepToolsPluginExtension;
		menuItemNr = theMenuItemNr;
		MNEMONIC = theMnemonic;
	}

	/**
	 * 
	 * Optional hook to make document-specific, tool-specific
	 * DocumentMetaInfo.MetaInfo
	 * 
	 * @param document
	 * @return DocumentMetaInfo.MetaInfo
	 */
	public DocumentMetaInfo.MetaInfo makeMetaInfo(final Document document) {
		return new DocumentMetaInfo.MetaInfo();
	}

	public void activate() {
		final DocumentMetaInfo documentMetaInfo = prepToolsPluginExtension
				.getDocumentMetaInfo();
		if (documentMetaInfo != null) {
			documentMetaInfo.setCurrentPrepTool(this);
		}
		prepToolsPluginExtension.selectPrepToolItem(menuItemNr);
		makeToolbar();
	}

	private void makeToolbar() {
		final List<JComponent> components = getComponents();
		prepToolsPluginExtension.toolbarPanel.removeAll();
		for (final JComponent component : components) {
			prepToolsPluginExtension.toolbarPanel.add(component);
		}
		prepToolsPluginExtension.toolbarPanel
				.add(trafficLight = new TrafficLight(26));
		prepToolsPluginExtension.toolbarPanel.add(new JLabel(" " + getLabel()));
		setCurrentState(prepToolsPluginExtension.getDocumentMetaInfo());
		relayout();
	}

	private void relayout() {
		Container parent = prepToolsPluginExtension.toolbarPanel;
		while (parent.getParent() != null) {
			parent = parent.getParent();
			parent.invalidate();
			parent.validate();
		}
	}

	/**
	 * Utility method. Makes button.
	 * 
	 * @param theAction
	 *            The action associated with the button
	 * @return the newly created button.
	 */
	protected JButton makeButton(final AbstractPrepToolAction theAction,
			int theKeyEvent) {
		// assign accelerator key to JButton
		// http://www.stratulat.com/assign_accelerator_key_to_a_JButton.html
		final JButton jButton = new JButton(theAction);
		jButton.setText(theAction.getName());
		assignAcceleratorKey(theAction, theAction.getName(), theKeyEvent,
				jButton);
		return jButton;
	}

	private static void assignAcceleratorKey(final Action theAction,
			final String theLabel, int theKeyEvent, final JComponent jComponent) {
		final InputMap keyMap = new ComponentInputMap(jComponent);
		keyMap.put(
				KeyStroke.getKeyStroke(theKeyEvent, InputEvent.CTRL_DOWN_MASK
						| InputEvent.SHIFT_DOWN_MASK), theLabel);
		final ActionMap actionMap = new ActionMapUIResource();
		actionMap.put(theLabel, theAction);
		SwingUtilities.replaceUIActionMap(jComponent, actionMap);
		SwingUtilities.replaceUIInputMap(jComponent,
				JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
	}

	public void setAllActionsEnabled(boolean enabled) {
		final List<Action> allActions = getAllActions();
		for (final Action action : allActions) {
			action.setEnabled(enabled);
		}
		final JComponent[] additionalComponents = getAdditionalComponents();
		for (final JComponent component : additionalComponents) {
			component.setEnabled(enabled);
		}
		if (enabled) {
			enableStuff();
		}
		else {
			disableStuff();
		}
	}

	/**
	 * optional hook to enable gui stuff
	 */
	protected void enableStuff() {

	}

	/**
	 * optional hook to disable gui stuff
	 */
	protected void disableStuff() {
		trafficLight.off();
	}

	/**
	 * hook to provide label
	 */
	protected abstract String getLabel();

	/**
	 * hook to provide components
	 */
	protected abstract List<JComponent> getComponents();

	/**
	 * hook to provide all actions
	 */
	protected abstract List<Action> getAllActions();

	/**
	 * optional hook to provide all additional components that aren't covered
	 * with hook getAllActions().
	 */
	protected JComponent[] getAdditionalComponents() {
		return new JComponent[0];
	}

	public int getMnemonic() {
		return MNEMONIC;
	}

	public void setCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {
		if (theDocumentMetaInfo != null && theDocumentMetaInfo.isDtBook()) {
			prepToolsPluginExtension.enableMenuPrepTools();
			doSetCurrentState(theDocumentMetaInfo);
			updateTrafficLight(theDocumentMetaInfo);
			if (theDocumentMetaInfo.isDone()) {
				prepToolsPluginExtension.setPrepToolItemDone(menuItemNr);
			}
		}
		else {
			prepToolsPluginExtension.disableMenuPrepTools();
			setAllActionsEnabled(false);
		}
	}

	private void updateTrafficLight(final DocumentMetaInfo theDocumentMetaInfo) {
		final boolean isTextPage = isTextPage(theDocumentMetaInfo);
		if (!theDocumentMetaInfo.hasStarted()) { // not started
			if (isTextPage) {
				trafficLight.go();
			}
			else {
				trafficLight.stop();
			}
		}
		else if (theDocumentMetaInfo.isDone()) { // done
			trafficLight.done();
		}
		else { // inProgress
			if (isTextPage) {
				trafficLight.inProgress();
			}
			else {
				trafficLight.stop();
			}
		}

	}

	/**
	 * optional hook to updated current state of this tool
	 */
	protected void doSetCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {

	}

	protected boolean isTextPage(final DocumentMetaInfo theDocumentMetaInfo) {
		return theDocumentMetaInfo.getCurrentEditorPage().equals(
				EditorPageConstants.PAGE_TEXT);
	}

	/**
	 * Optional hook for PrepTools to provide the regex they need to skip.
	 * 
	 * @return Tag regex to skip
	 */
	public String getTagRegexToSkip() {
		return null;
	}

	private TrafficLight trafficLight;
}

/**
 * Common superclass for RegexPrepTool and VFormPrepTool
 */
abstract class AbstractMarkupPrepTool extends PrepTool {

	protected AbstractPrepToolAction startAction;

	protected AbstractPrepToolAction findAction;

	protected AbstractPrepToolAction changeAction;

	protected abstract AbstractPrepToolAction makeStartAction();

	protected abstract AbstractPrepToolAction makeFindAction();

	protected abstract AbstractPrepToolAction makeChangeAction();

	@Override
	protected List<Action> getAllActions() {
		if (startAction == null) {
			startAction = makeStartAction();
			findAction = makeFindAction();
			changeAction = makeChangeAction();
		}
		final List<Action> list = new ArrayList<Action>();
		list.add(startAction);
		list.add(findAction);
		list.add(changeAction);
		return list;
	}

	@Override
	protected List<JComponent> getComponents() {
		getAllActions();
		final List<JComponent> list = new ArrayList<JComponent>();
		list.add(makeButton(startAction, KeyEvent.VK_F5));
		list.add(makeButton(findAction, KeyEvent.VK_F6));
		list.add(makeButton(changeAction, KeyEvent.VK_F7));
		return list;
	}

	AbstractMarkupPrepTool(
			final PrepToolsPluginExtension thePrepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic) {
		super(thePrepToolsPluginExtension, theMenuItemNr, theMnemonic);
	}

	@Override
	public void doSetCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {

		final boolean isTextPage = isTextPage(theDocumentMetaInfo);

		/*
		 TODO: make this table driven or something
			 We have:
			               possible states
			 - hasStarted: 0 1 1 0 1 1
			 - isDone:     0 0 1 0 0 1
			 - isTextPage: 0 0 0 1 1 1
			 ---------------------------
			 - traffic:    0 0 3 1 2 3
			 - start:      0 0 0 1 1 1 = isTextPage
			 - find:       0 0 0 0 1 0 = isTextPage && hasStarted && !isDone
			 - change:     0 0 0 0 1 0 = isTextPage && hasStarted && !isDone
			 - allforms:   0 0 0 1 1 1 = isTextPage
			 UI elements
			 - traffic:    stop:0/go:1/inProgress:2/done:3
			 - start:      disabled:0/enabled:1
			 - find:       disabled:0/enabled:1
			 - change:     disabled:0/enabled:1
			 - allforms:   disabled:0/enabled:1
		 */
		if (!theDocumentMetaInfo.hasStarted()) { // not started
			setAllActionsEnabled(false);
		}
		else if (theDocumentMetaInfo.isDone()) { // done
			setAllActionsEnabled(false);
		}
		else { // inProgress
			setAllActionsEnabled(isTextPage);
		}
		startAction.setEnabled(isTextPage);
	}
}

/**
 * 
 * RegexPrepTool can be used as a blackbox class that can be configured
 * with
 * - a regex pattern to search,
 * - a menu item number,
 * - a mnemonic for the menu entry,
 * - a label for the menu entry.
 * - a tag to insert,
 * - a regex pattern to skip during the search,
 * 
 * Why do we implement getTagRegexToSkip here when it's called in the
 * topmost class of the hierarchy?
 * Because it's something tunneled through all intermediate classes.
 * The skipping functionality is only relevant to RegexPrepTool, i.e. the
 * document needs to be protected from inserted tags only for RegexPrepTool.
 */
class RegexPrepTool extends AbstractMarkupPrepTool {

	private final String LABEL;
	private final String TAG_REGEX_TO_SKIP;
	final String PATTERN_TO_SEARCH;
	private final String TAG_TO_INSERT;

	/**
	 * @param thePrepToolsPluginExtension
	 * @param theMenuItemNr
	 * @param theMnemonic
	 * @param theLabel
	 * @param thePatternToSearch
	 * @param theTagToInsert
	 * @param tagRegexToSkip
	 */
	RegexPrepTool(final PrepToolsPluginExtension thePrepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic, final String theLabel,
			final String thePatternToSearch, final String theTagToInsert,
			final String tagRegexToSkip) {
		super(thePrepToolsPluginExtension, theMenuItemNr, theMnemonic);
		LABEL = theLabel;
		PATTERN_TO_SEARCH = thePatternToSearch;
		TAG_TO_INSERT = theTagToInsert;
		TAG_REGEX_TO_SKIP = tagRegexToSkip;
	}

	/**
	 * @param thePrepToolsPluginExtension
	 * @param theMenuItemNr
	 * @param theMnemonic
	 * @param theLabel
	 * @param thePatternToSearch
	 * @param theTag
	 */
	RegexPrepTool(final PrepToolsPluginExtension thePrepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic, final String theLabel,
			final String thePatternToSearch, final String theTag) {
		this(thePrepToolsPluginExtension, theMenuItemNr, theMnemonic, theLabel,
				thePatternToSearch, theTag, theTag);
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.PrepTool#getTagRegexToSkip()
	 */
	@Override
	public String getTagRegexToSkip() {
		return TAG_REGEX_TO_SKIP;
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.PrepTool#getLabel()
	 */
	@Override
	protected String getLabel() {
		return LABEL;
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.AbstractMarkupPrepTool#makeStartAction()
	 */
	@Override
	protected AbstractPrepToolAction makeStartAction() {
		return new RegexStartAction(prepToolsPluginExtension,
				AbstractPrepToolAction.START, PATTERN_TO_SEARCH, LABEL);
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.AbstractMarkupPrepTool#makeFindAction()
	 */
	@Override
	protected AbstractPrepToolAction makeFindAction() {
		return new RegexFindAction(prepToolsPluginExtension,
				AbstractPrepToolAction.FIND, PATTERN_TO_SEARCH, LABEL);
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.AbstractMarkupPrepTool#makeChangeAction()
	 */
	@Override
	protected AbstractPrepToolAction makeChangeAction() {
		return new RegexChangeAction(prepToolsPluginExtension,
				AbstractPrepToolAction.CHANGE, PATTERN_TO_SEARCH, LABEL,
				TAG_TO_INSERT);
	}

}

/**
 * FullRegexPrepTool can be used as a blackbox class that can be configured
 * with
 * - a regex pattern to search,
 * - a menu item number,
 * - a mnemonic for the menu entry,
 * - a label for the menu entry.
 * - a replace string to insert, (note that this is more generic than the super
 * class where you can simply add a tag)
 * - a regex pattern to skip during the search,
 * 
 */
class FullRegexPrepTool extends RegexPrepTool {

	private final String REPLACE_STRING;

	private final String CHANGE_BUTTON_LABEL;

	// null: we don't want the functionality provided by the superclass.
	// We provide our own replace string.
	private static final String TAG_TO_INSERT = null;

	// null: we don't want the functionality provided by the superclass.
	// This probably could be improved: Why not protect this class from
	// its own insertions?
	private static final String TAG_REGEX_TO_SKIP = null;

	private AbstractPrepToolAction changeAction;

	/**
	 * @param thePrepToolsPluginExtension
	 * @param theMenuItemNr
	 * @param theMnemonic
	 * @param theLabel
	 * @param thePatternToSearch
	 * @param theReplaceString
	 * @param theChangeButtonLabel
	 */
	FullRegexPrepTool(
			final PrepToolsPluginExtension thePrepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic, final String theLabel,
			final String thePatternToSearch, final String theReplaceString,
			final String theChangeButtonLabel) {
		this(thePrepToolsPluginExtension, theMenuItemNr, theMnemonic, theLabel,
				thePatternToSearch, theReplaceString, theChangeButtonLabel,
				null);
	}

	/**
	 * @param thePrepToolsPluginExtension
	 * @param theMenuItemNr
	 * @param theMnemonic
	 * @param theLabel
	 * @param thePatternToSearch
	 * @param theReplaceString
	 * @param theChangeButtonLabel
	 * @param theChangeAction
	 */
	FullRegexPrepTool(
			final PrepToolsPluginExtension thePrepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic, final String theLabel,
			final String thePatternToSearch, final String theReplaceString,
			final String theChangeButtonLabel,
			final AbstractPrepToolAction theChangeAction) {
		super(thePrepToolsPluginExtension, theMenuItemNr, theMnemonic,
				theLabel, thePatternToSearch, TAG_TO_INSERT, TAG_REGEX_TO_SKIP);
		REPLACE_STRING = theReplaceString;
		CHANGE_BUTTON_LABEL = theChangeButtonLabel;
		changeAction = theChangeAction;
	}

	/**
	 * @param thePrepToolsPluginExtension
	 * @param theMenuItemNr
	 * @param theMnemonic
	 * @param theLabel
	 * @param thePatternToSearch
	 * @param theReplaceString
	 * @param thePatternToSkip
	 * @param theChangeButtonLabel
	 * @param theChangeAction
	 */
	FullRegexPrepTool(
			final PrepToolsPluginExtension thePrepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic, final String theLabel,
			final String thePatternToSearch, final String theReplaceString,
			final String thePatternToSkip, final String theChangeButtonLabel,
			final AbstractPrepToolAction theChangeAction) {
		super(thePrepToolsPluginExtension, theMenuItemNr, theMnemonic,
				theLabel, thePatternToSearch, TAG_TO_INSERT, thePatternToSkip);
		REPLACE_STRING = theReplaceString;
		if (theChangeButtonLabel != null) {
			CHANGE_BUTTON_LABEL = theChangeButtonLabel;
		}
		else if (theChangeAction != null) {
			CHANGE_BUTTON_LABEL = theChangeAction.getName();
		}
		else {
			CHANGE_BUTTON_LABEL = "*ERR: either set label or action!";
		}
		changeAction = theChangeAction;
	}

	@Override
	protected AbstractPrepToolAction makeChangeAction() {
		if (changeAction == null) {
			changeAction = new FullRegexChangeAction(prepToolsPluginExtension,
					CHANGE_BUTTON_LABEL, PATTERN_TO_SEARCH, getLabel(),
					REPLACE_STRING);
		}
		return changeAction;
	}
}

class PageBreakPrepTool extends FullRegexPrepTool {

	private final AbstractPrepToolAction INSERT_PEL_ACTION;

	PageBreakPrepTool(
			final PrepToolsPluginExtension thePrepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic) {
		super(thePrepToolsPluginExtension, theMenuItemNr, theMnemonic,
				"Pagebreak", PrepToolLoader.PAGEBREAK_SEARCH_REGEX,
				PrepToolLoader.PAGEBREAK_REPLACE, "Join");
		INSERT_PEL_ACTION = makePelAction();
	}

	@Override
	protected List<Action> getAllActions() {
		final List<Action> actions = super.getAllActions();
		actions.add(INSERT_PEL_ACTION);
		return actions;
	}

	@Override
	protected List<JComponent> getComponents() {
		getAllActions();
		final List<JComponent> components = super.getComponents();
		components.add(makeButton(INSERT_PEL_ACTION, KeyEvent.VK_F8));
		return components;
	}

	private AbstractPrepToolAction makePelAction() {
		return new FullRegexChangeAction(prepToolsPluginExtension,
				"Insert PEL", PATTERN_TO_SEARCH, getLabel(),
				PrepToolLoader.PAGEBREAK_REPLACE2);
	}
}

class AccentPrepTool extends RegexPrepTool {

	static final String LABEL = "Accent";

	private static final String replaceString = PrepToolLoader.ACCENT_REPLACE;

	// null: We have our own
	private static final String TAG_TO_INSERT = null;

	AccentPrepTool(final PrepToolsPluginExtension thePrepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic) {
		super(thePrepToolsPluginExtension, theMenuItemNr, theMnemonic, LABEL,
				PrepToolLoader.ACCENT_SEARCH_REGEX, TAG_TO_INSERT,
				PrepToolLoader.ACCENT_SKIP_REGEX);
	}

	@Override
	protected AbstractPrepToolAction makeStartAction() {
		return new RegexStartAction(prepToolsPluginExtension,
				AbstractPrepToolAction.START, PATTERN_TO_SEARCH, LABEL);
	}

	@Override
	protected AbstractPrepToolAction makeFindAction() {
		return new AccentChangeAction(prepToolsPluginExtension, "Swiss",
				PATTERN_TO_SEARCH, LABEL, replaceString.replace(
						PrepToolLoader.PLACEHOLDER, "detailed"));
	}

	@Override
	protected AbstractPrepToolAction makeChangeAction() {
		return new AccentChangeAction(prepToolsPluginExtension, "Foreign",
				PATTERN_TO_SEARCH, LABEL, replaceString.replace(
						PrepToolLoader.PLACEHOLDER, "reduced"));
	}

}

class VFormPrepTool extends AbstractMarkupPrepTool {

	@Override
	public DocumentMetaInfo.MetaInfo makeMetaInfo(final Document document) {
		return new MetaInfo();
	}

	static class MetaInfo extends DocumentMetaInfo.MetaInfo {
		private Pattern currentPattern;

		public MetaInfo() {
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
		 * 
		 * @return current vform-pattern.
		 */
		public Pattern getCurrentPattern() {
			return currentPattern;
		}

	}

	static final String LABEL = "VForms";

	VFormPrepTool(final PrepToolsPluginExtension prepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic) {
		super(prepToolsPluginExtension, theMenuItemNr, theMnemonic);
	}

	private JCheckBox allForms;

	@Override
	protected String getLabel() {
		return LABEL;
	}

	@Override
	public String getTagRegexToSkip() {
		return VFormActionHelper.VFORM_TAG;
	}

	@Override
	protected List<JComponent> getComponents() {
		final List<JComponent> components = super.getComponents();
		components.add(components.size() - 1, allForms = makeCheckbox());
		return components;
	}

	@Override
	public void doSetCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {
		super.doSetCurrentState(theDocumentMetaInfo);
		allForms.setSelected(getMetaInfo(theDocumentMetaInfo).patternIsAll());
		allForms.setEnabled(isTextPage(theDocumentMetaInfo));
	}

	private MetaInfo getMetaInfo(final DocumentMetaInfo theDocumentMetaInfo) {
		return (MetaInfo) theDocumentMetaInfo.getToolSpecificMetaInfo(LABEL);
	}

	private MetaInfo getMetaInfo() {
		return getMetaInfo(prepToolsPluginExtension.getDocumentMetaInfo());
	}

	private JCheckBox makeCheckbox() {
		final JCheckBox checkBox = new JCheckBox("All");
		checkBox.setMnemonic('A');
		checkBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					getMetaInfo().setPatternToAll();
				}
				else {
					getMetaInfo().setPatternTo3rdPP();
				}
			}
		});
		return checkBox;
	}

	@Override
	protected JComponent[] getAdditionalComponents() {
		return new JComponent[] { allForms };
	}

	@Override
	protected AbstractPrepToolAction makeStartAction() {
		return new VFormStartAction(prepToolsPluginExtension, null);
	}

	@Override
	protected AbstractPrepToolAction makeFindAction() {
		return new VFormFindAction(prepToolsPluginExtension, null);
	}

	@Override
	protected AbstractPrepToolAction makeChangeAction() {
		return new VFormChangeAction(prepToolsPluginExtension, null);
	}
}

class ParensPrepTool extends PrepTool {

	@Override
	public DocumentMetaInfo.MetaInfo makeMetaInfo(final Document document) {
		return new MetaInfo(document);
	}

	static class MetaInfo extends DocumentMetaInfo.MetaInfo {
		private Iterator<Match.PositionMatch> orphanedParensIterator;
		private final Document document;

		MetaInfo(final Document theDocument) {
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
				final Match.PositionMatch mp = new Match.PositionMatch(
						document, match);
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

	}

	static final String LABEL = "Parens";

	ParensPrepTool(final PrepToolsPluginExtension prepToolsPluginExtension,
			int theMenuItemNr, int theMnemonic) {
		super(prepToolsPluginExtension, theMenuItemNr, theMnemonic);
	}

	private final AbstractPrepToolAction startAction = new OrphanParenStartAction(
			prepToolsPluginExtension);
	private final AbstractPrepToolAction findNextAction = new OrphanParenFindNextAction(
			prepToolsPluginExtension);

	@Override
	protected String getLabel() {
		return LABEL;
	}

	@Override
	protected List<JComponent> getComponents() {
		final List<JComponent> list = new ArrayList<JComponent>();
		list.add(makeButton(startAction, KeyEvent.VK_F5));
		list.add(makeButton(findNextAction, KeyEvent.VK_F6));
		return list;
	}

	@Override
	public void doSetCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {
		startAction.setEnabled(true);
		findNextAction.setEnabled(theDocumentMetaInfo.isProcessing());
	}

	@Override
	protected List<Action> getAllActions() {
		final List<Action> list = new ArrayList<Action>();
		list.add(startAction);
		list.add(findNextAction);
		return list;
	}
}