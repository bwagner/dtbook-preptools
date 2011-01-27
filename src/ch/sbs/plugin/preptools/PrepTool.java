package ch.sbs.plugin.preptools;

import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

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

import ro.sync.exml.editor.EditorPageConstants;

abstract class PrepTool {

	protected final PrepToolsPluginExtension prepToolsPluginExtension;

	/**
	 * @param thePrepToolsPluginExtension
	 */
	PrepTool(final PrepToolsPluginExtension thePrepToolsPluginExtension) {
		prepToolsPluginExtension = thePrepToolsPluginExtension;
	}

	public void makeToolbar() {
		final JComponent[] components = getComponents();
		prepToolsPluginExtension.toolbarPanel.removeAll();
		for (final JComponent component : components) {
			prepToolsPluginExtension.toolbarPanel.add(component);
		}
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
	 * @param theLabel
	 *            The label for the button.
	 * @return the newly created button.
	 */
	protected JButton makeButton(final Action theAction, final String theLabel,
			int theKeyEvent) {
		// assign accelerator key to JButton
		// http://www.stratulat.com/assign_accelerator_key_to_a_JButton.html
		final JButton jButton = new JButton(theAction);
		jButton.setText(theLabel);
		assignAcceleratorKey(theAction, theLabel, theKeyEvent, jButton);
		return jButton;
	}

	private void assignAcceleratorKey(final Action theAction,
			final String theLabel, int theKeyEvent, final JComponent jComponent) {
		final InputMap keyMap = new ComponentInputMap(jComponent);
		keyMap.put(
				KeyStroke.getKeyStroke(theKeyEvent, InputEvent.CTRL_DOWN_MASK
						| InputEvent.ALT_DOWN_MASK), theLabel);
		final ActionMap actionMap = new ActionMapUIResource();
		actionMap.put(theLabel, theAction);
		SwingUtilities.replaceUIActionMap(jComponent, actionMap);
		SwingUtilities.replaceUIInputMap(jComponent,
				JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
	}

	public void setAllActionsEnabled(boolean enabled) {
		final Action[] allActions = getAllActions();
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
	 * optional hookto enable gui stuff
	 */
	protected void enableStuff() {

	}

	/**
	 * optional hook to disable gui stuff
	 */
	protected void disableStuff() {

	}

	/**
	 * hook to provide label
	 */
	protected abstract String getLabel();

	/**
	 * hook to provide components
	 */
	protected abstract JComponent[] getComponents();

	/**
	 * hook to provide all actions
	 */
	protected abstract Action[] getAllActions();

	/**
	 * optional hook to provide all additional components that aren't covered
	 * with hook getAllActions().
	 */
	protected JComponent[] getAdditionalComponents() {
		return new JComponent[0];
	}

	/**
	 * hook to provide mnemonic for this tool
	 */
	public abstract int getMnemonic();

	/**
	 * optional hook to updated current state of this tool
	 */
	public void setCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {
	}
}

class VFormPrepTool extends PrepTool {

	VFormPrepTool(PrepToolsPluginExtension prepToolsPluginExtension) {
		super(prepToolsPluginExtension);
	}

	private TrafficLight trafficLight;

	private JCheckBox allForms;

	private final Action startAction = new VFormStartAction(
			prepToolsPluginExtension);
	private final Action findAction = new VFormFindAction(
			prepToolsPluginExtension);
	private final Action acceptAction = new VFormAcceptAction(
			prepToolsPluginExtension);

	@Override
	protected String getLabel() {
		return "VForms";
	}

	@Override
	protected JComponent[] getComponents() {
		return new JComponent[] {
				makeButton(startAction, "Start", KeyEvent.VK_7),
				makeButton(findAction, "Find", KeyEvent.VK_8),
				makeButton(acceptAction, "Accept", KeyEvent.VK_9),
				allForms = makeCheckbox(), trafficLight = new TrafficLight(26) };
	}

	@Override
	public int getMnemonic() {
		return KeyEvent.VK_V;
	}

	@Override
	public void setCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {
		if (theDocumentMetaInfo == null) {
			return;
		}
		allForms.setSelected(theDocumentMetaInfo.vFormPatternIsAll());

		if (!theDocumentMetaInfo.isDtBook()) { // TODO: this belongs in
												// superclass
			setAllActionsEnabled(false);
			return;
		}
		final boolean isTextPage = theDocumentMetaInfo.getCurrentEditorPage()
				.equals(EditorPageConstants.PAGE_TEXT);

		/*
		 TODO: make this table driven or something
			 We have:
			 - isTextPage: true/false
			                           possible states
			 - hasStartedCheckingVform  0 1 1
			 - doneCheckingVform        0 0 1
			 UI elements
			 - trafficLight:      go/inProgress/stop/done
			 - vformStartAction:  enabled/disabled
			 - vformFindAction:   enabled/disabled
			 - vformAcceptAction: enabled/disabled
		 */
		if (!theDocumentMetaInfo.hasStartedCheckingVform()) {
			if (isTextPage) {
				trafficLight.go();
				startAction.setEnabled(true);
				allForms.setEnabled(true);
			}
			else {
				trafficLight.stop();
				startAction.setEnabled(false);
				allForms.setEnabled(false);
			}
			findAction.setEnabled(false);
			acceptAction.setEnabled(false);
		}
		else if (theDocumentMetaInfo.doneCheckingVform()) {
			trafficLight.done();
			if (isTextPage) {
				startAction.setEnabled(true);
			}
			else {
				startAction.setEnabled(false);
			}
			findAction.setEnabled(false);
			acceptAction.setEnabled(false);
		}
		else {
			if (isTextPage) {
				trafficLight.inProgress();
				setAllActionsEnabled(true);
			}
			else {
				trafficLight.stop();
				setAllActionsEnabled(false);
			}
		}
	}

	private JCheckBox makeCheckbox() {
		final JCheckBox checkBox = new JCheckBox("All");
		checkBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					prepToolsPluginExtension.getDocumentMetaInfo()
							.setVFormPatternToAll();
				}
				else {
					prepToolsPluginExtension.getDocumentMetaInfo()
							.setVFormPatternTo3rdPP();
				}
			}
		});
		return checkBox;
	}

	@Override
	protected Action[] getAllActions() {
		return new Action[] { startAction, acceptAction, findAction };
	}

	@Override
	protected JComponent[] getAdditionalComponents() {
		return new JComponent[] { allForms };
	}

	@Override
	protected void disableStuff() {
		trafficLight.off();
	}
}

class ParensPrepTool extends PrepTool {

	ParensPrepTool(PrepToolsPluginExtension prepToolsPluginExtension) {
		super(prepToolsPluginExtension);
	}

	private final Action startAction = new OrphanParenStartAction(
			prepToolsPluginExtension);
	private final Action findNextAction = new OrphanParenFindNextAction(
			prepToolsPluginExtension);

	@Override
	protected String getLabel() {
		return "Parens";
	}

	@Override
	protected JComponent[] getComponents() {
		return new JComponent[] {
				makeButton(startAction, "Start", KeyEvent.VK_7),
				makeButton(findNextAction, "Find", KeyEvent.VK_8) };
	}

	@Override
	public int getMnemonic() {
		return KeyEvent.VK_P;
	}

	@Override
	public void setCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {
		setAllActionsEnabled(true);
	}

	@Override
	protected Action[] getAllActions() {
		return new Action[] { startAction, findNextAction };
	}
}