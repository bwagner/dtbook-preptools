package ch.sbs.plugin.preptools;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.ActionMapUIResource;

import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer;
import ro.sync.exml.workspace.api.standalone.ToolbarInfo;
import ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer;
import ro.sync.exml.workspace.api.standalone.ViewInfo;
import ro.sync.ui.Icons;
import ch.sbs.utils.preptools.FileUtils;
import ch.sbs.utils.preptools.Match.PositionMatch;
import ch.sbs.utils.preptools.PropsUtils;

/**
 * Plugin extension - workspace access extension.
 */
public class PrepToolsPluginExtension implements WorkspaceAccessPluginExtension {

	private void makeVformToolbar() {
		final String LABEL = "VForms";

		toolbarPanel.removeAll();
		toolbarPanel.add(makeButton(vformStartAction, "Start", KeyEvent.VK_7));
		toolbarPanel.add(makeButton(vformFindAction, "Find", KeyEvent.VK_8));
		toolbarPanel
				.add(makeButton(vformAcceptAction, "Accept", KeyEvent.VK_9));
		toolbarPanel.add(allForms = makeCheckbox());
		toolbarPanel.add(trafficLight = new TrafficLight(26));
		toolbarPanel.add(new JLabel(" " + LABEL));
		relayout();
	}

	private void relayout() {
		Container parent = toolbarPanel;
		while (parent.getParent() != null) {
			parent = parent.getParent();
			parent.invalidate();
			parent.validate();
		}
	}

	private void makeParensToolbar() {
		final String LABEL = "Parens";

		toolbarPanel.removeAll();
		toolbarPanel.add(makeButton(orphanedParensStartAction, "Start",
				KeyEvent.VK_7));
		toolbarPanel.add(makeButton(orphanedParensFindNextAction, "Find",
				KeyEvent.VK_8));

		toolbarPanel.add(new JLabel(" " + LABEL));
		relayout();
	}

	private JCheckBox makeCheckbox() {
		final JCheckBox checkBox = new JCheckBox("All");
		checkBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					showMessage("now using all vforms");
					getDocumentMetaInfo().setVFormPatternToAll();
				}
				else {
					showMessage("now using only 3rd person plural vforms");
					getDocumentMetaInfo().setVFormPatternTo3rdPP();
				}
			}
		});
		return checkBox;
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
	protected JButton makeButton(final Action theAction, String theLabel,
			int theKeyEvent) {
		// assign accelerator key to JButton
		// http://www.stratulat.com/assign_accelerator_key_to_a_JButton.html
		final JButton jButton = new JButton(theAction);
		jButton.setText(theLabel);
		assignAcceleratorKey(theAction, theLabel, theKeyEvent, jButton);
		return jButton;
	}

	private void assignAcceleratorKey(final Action theAction, String theLabel,
			int theKeyEvent, final JComponent jComponent) {
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

	/*
	 current hypothesis:
	 	define class Task
	 		Task.Name is what gets entered in the PrepTools-Menu
	 	Every Task has a set of actions
	 		Every action has a name
	 		Every action has a button
	 		The button's names are the action's names
	 	When choosing a Task from the PrepTools-Menu
	 		the ToolbarComponentsCustomizer.CUSTOM toolbar gets purged
	 		of its current contents and the new buttons according to
	 		the chosen task get entered into the ToolbarComponentsCustomizer.CUSTOM toolbar
	 	
	 
	 */
	static final String TOOLBAR_TITLE = "PrepTools:V-Forms";

	private JPanel toolbarPanel;

	public void setCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {
		if (theDocumentMetaInfo == null) {
			return;
		}
		allForms.setSelected(theDocumentMetaInfo.vFormPatternIsAll());

		if (!theDocumentMetaInfo.isDtBook()) {
			disableVforms();
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
				vformStartAction.setEnabled(true);
				allForms.setEnabled(true);
			}
			else {
				trafficLight.stop();
				vformStartAction.setEnabled(false);
				allForms.setEnabled(false);
			}
			vformFindAction.setEnabled(false);
			vformAcceptAction.setEnabled(false);
		}
		else if (theDocumentMetaInfo.doneCheckingVform()) {
			trafficLight.done();
			if (isTextPage) {
				vformStartAction.setEnabled(true);
			}
			else {
				vformStartAction.setEnabled(false);
			}
			vformFindAction.setEnabled(false);
			vformAcceptAction.setEnabled(false);
		}
		else {
			if (isTextPage) {
				trafficLight.inProgress();
				enableAllActions();
			}
			else {
				trafficLight.stop();
				disableAllActions();
			}
		}
	}

	protected void disableVforms() {
		trafficLight.off();
		disableAllActions();
	}

	private void disableAllActions() {
		setAllActions(false);
	}

	private void enableAllActions() {
		setAllActions(true);
	}

	private void setAllActions(final boolean enabled) {
		for (final Action action : new Action[] { vformStartAction,
				vformFindAction, vformAcceptAction }) {
			action.setEnabled(enabled);
		}
		allForms.setEnabled(enabled);
	}

	/**
	 * The PrepTools messages area.
	 */
	private JTextArea prepToolsMessagesArea;

	/**
	 * Plugin workspace access.
	 */
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	/**
	 * If <code>true</code> then the plugin is running.
	 */
	private boolean runPlugin;

	private boolean applicationClosing;

	private Action vformStartAction;
	private Action vformFindAction;
	private Action vformAcceptAction;

	private Action orphanedParensStartAction;
	private Action orphanedParensFindNextAction;

	private TrafficLight trafficLight;

	private JCheckBox allForms;

	private MyCaretListener caretHandler;

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	@Override
	public void applicationStarted(
			final StandalonePluginWorkspace thePluginWorkspaceAccess) {
		pluginWorkspaceAccess = thePluginWorkspaceAccess;
		final URL resource = getClass().getResource(
				"/" + getClass().getName().replace('.', '/') + ".class");
		runPlugin = "jar".equals(resource.getProtocol()) ? true : System
				.getProperty("cms.sample.plugin") != null;
		if (runPlugin) {

			caretHandler = new MyCaretListener();
			vformStartAction = new VFormStartAction(this);
			vformFindAction = new VFormFindAction(this);
			vformAcceptAction = new VFormAcceptAction(this);

			orphanedParensStartAction = new OrphanParenStartAction(this);
			orphanedParensFindNextAction = new OrphanParenFindNextAction(this);

			pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
				/**
				 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
				 */
				@Override
				public void customizeMainMenu(final JMenuBar mainMenuBar) {
					// PrepTools menu
					final JMenu menuPrepTools = createPrepToolsMenu();
					menuPrepTools.setMnemonic(KeyEvent.VK_R);
					// Add the CMS menu before the Help menu
					mainMenuBar.add(menuPrepTools,
							mainMenuBar.getMenuCount() - 1);
				}
			});

			pluginWorkspaceAccess.addEditorChangeListener(
					new WSEditorChangeListener() {

						// explore modal dialog instead of toolbar:
						// http://www.javacoffeebreak.com/faq/faq0019.html

						@Override
						public void editorOpened(URL editorLocation) {
							final WSEditor editorAccess = getWsEditor();
							final WSTextEditorPage page = getPage(editorAccess);
							if (page == null) {
								final boolean isText = EditorPageConstants.PAGE_TEXT
										.equals(editorAccess.getCurrentPageID());
								showMessage("document could not be accessed "
										+ (isText ? "(unknown reason)"
												: "(current page is not "
														+ EditorPageConstants.PAGE_TEXT
														+ " but "
														+ editorAccess
																.getCurrentPageID()
														+ ")"));
								disableVforms();
								return;
							}
							// addCaretHandler();
						}

						@SuppressWarnings("unused")
						private void addCaretHandler() {
							final JTextArea ta = getJTextArea();
							ta.addCaretListener(caretHandler);
						}

						@Override
						public void editorClosed(final URL editorLocation) {
							final DocumentMetaInfo dmi = getDocumentMetaInfo(editorLocation);
							if (dmi.isProcessing() && !applicationClosing) {
								// we can't veto closing!
								if (showConfirmDialog(
										"v-form: Start Over?",
										"Document "
												+ FileUtils
														.basename(editorLocation)
												+ " was still being processed. Want to start over?")) {
									SwingUtilities.invokeLater(new Runnable() {
										@Override
										public void run() {
											pluginWorkspaceAccess
													.open(editorLocation);
											dmi.setPage(PrepToolsPluginExtension.this);
											setCurrentState(dmi);
											final PositionMatch match = dmi
													.getCurrentPositionMatch();
											getPage()
													.select(match.startOffset
															.getOffset(),
															match.endOffset
																	.getOffset());
											dmi.resetManualEdit();
										}
									});

								}
								else {
									removeDocumentMetaInfo(dmi);
									disableVforms();
								}
							}
							else {
								removeDocumentMetaInfo(dmi);

								// In case this was the last open document we
								// turn traffic light off. If there are more
								// documents open the trafficLight will be set
								// accordingly by editorSelected()
								disableVforms();
							}
						};

						@Override
						public void editorPageChanged(URL editorLocation) {
							final DocumentMetaInfo dmi = getDocumentMetaInfo(editorLocation);
							dmi.setCurrentEditorPage(getPageId());
							setCurrentState(dmi);
						};

						@Override
						public void editorSelected(URL editorLocation) {
							final DocumentMetaInfo dmi = getDocumentMetaInfo(editorLocation);
							if (dmi != null) {
								setCurrentState(dmi);
							}
							else {
								disableVforms();
							}
						};
					}, StandalonePluginWorkspace.MAIN_EDITING_AREA);

			pluginWorkspaceAccess
					.addToolbarComponentsCustomizer(new ToolbarComponentsCustomizer() {
						/**
						 * @see ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer#customizeToolbar(ro.sync.exml.workspace.api.standalone.ToolbarInfo)
						 */
						@Override
						public void customizeToolbar(ToolbarInfo theToolbarInfo) {
							if (ToolbarComponentsCustomizer.CUSTOM
									.equals(theToolbarInfo.getToolbarID())) {

								toolbarPanel = new JPanel();
								toolbarPanel.setLayout(new BoxLayout(
										toolbarPanel, BoxLayout.LINE_AXIS));
								theToolbarInfo
										.setComponents(new JComponent[] { toolbarPanel });
								theToolbarInfo.setTitle("PrepTools");

								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										makeVformToolbar();
										disableVforms();
									}

								});
							}
						}
					});

			pluginWorkspaceAccess
					.addViewComponentCustomizer(new ViewComponentCustomizer() {
						/**
						 * @see ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer#customizeView(ro.sync.exml.workspace.api.standalone.ViewInfo)
						 */
						@Override
						public void customizeView(ViewInfo viewInfo) {
							if (ViewComponentCustomizer.CUSTOM.equals(viewInfo
									.getViewID())) {
								prepToolsMessagesArea = new JTextArea(
										"PrepTools Session History:");
								viewInfo.setComponent(new JScrollPane(
										prepToolsMessagesArea));
								viewInfo.setTitle("PrepTools Messages");
								viewInfo.setIcon(Icons.CMS_MESSAGES_CUSTOM_VIEW);
								showMessage(getVersion());
							}
							else if ("Project".equals(viewInfo.getViewID())) {
								// Change the 'Project' view title.
								viewInfo.setTitle("PrepTools Project");
							}
						}

					});
		}
	}

	@SuppressWarnings("serial")
	protected JMenu createPrepToolsMenu() {
		JMenu menuPrepTools = new JMenu("PrepTools");

		final JMenuItem vFormItem = new JMenuItem(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				makeVformToolbar();
			}
		});
		vFormItem.setText("VForms");
		vFormItem.setMnemonic(KeyEvent.VK_V);
		menuPrepTools.add(vFormItem);

		JMenuItem parensItem = new JMenuItem(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				makeParensToolbar();
			}
		});
		parensItem.setText("Parens");
		parensItem.setMnemonic(KeyEvent.VK_P);
		menuPrepTools.add(parensItem);

		return menuPrepTools;
	}

	private final Map<URL, DocumentMetaInfo> documents = new HashMap<URL, DocumentMetaInfo>();

	DocumentMetaInfo getDocumentMetaInfo() {
		return getDocumentMetaInfo(getEditorLocation());
	}

	DocumentMetaInfo getDocumentMetaInfo(final URL editorLocation) {
		if (documents.containsKey(editorLocation)) {
			return documents.get(editorLocation);
		}
		if (getPage() == null) {
			return null;
		}

		final DocumentMetaInfo documentMetaInformation = new DocumentMetaInfo(
				this);

		documents.put(editorLocation, documentMetaInformation);
		return documentMetaInformation;
	}

	private void removeDocumentMetaInfo(final DocumentMetaInfo dmi) {
		dmi.finish();
		documents.remove(dmi.getUrl());
	}

	private String getVersion() {
		final String key = "stamp";
		final String filename = "stamp.properties";
		final String version = PropsUtils.loadForClass(this.getClass(),
				filename).getProperty(key);
		String returnString;
		if (version != null && version.length() > 0) {
			returnString = version;
		}
		else {
			returnString = "'" + key + "' not found in props file " + filename;
		}

		return returnString;
	}

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
	 */
	@Override
	public boolean applicationClosing() {
		final StringBuilder sb = new StringBuilder();
		boolean showDialog = false;
		if (runPlugin) {
			for (final URL url : documents.keySet()) {
				if (documents.get(url).isProcessing()) {
					showDialog = true;
					sb.append("\n- ");
					sb.append(FileUtils.basename(url));
				}
			}

			if (showDialog) {
				sb.insert(0,
						"The following documents are still being processed:");
				sb.append("\n\nProceed with closing anyway?");
				applicationClosing = showConfirmDialog("v-form: Close?",
						sb.toString());
				return applicationClosing;
			}
		}
		applicationClosing = true;
		return true;
	}

	void showMessage(final String msg) {
		if (prepToolsMessagesArea != null) {
			prepToolsMessagesArea.append("\n");
			prepToolsMessagesArea.append(msg);
			// crashes with NullPointer:
			// if (pluginWorkspaceAccess != null) {
			// pluginWorkspaceAccess.showView(ViewComponentCustomizer.CUSTOM,
			// true);
			// }
		}
	}

	void showDialog(final String msg) {
		pluginWorkspaceAccess.showInformationMessage(msg);
		pluginWorkspaceAccess.showView(ViewComponentCustomizer.CUSTOM, true);
	}

	boolean showConfirmDialog(final String title, final String msg) {
		return showConfirmDialog(title, msg, "Ok", "Cancel");
	}

	boolean showConfirmDialog(final String title, final String msg,
			final String confirm, final String deny) {
		return pluginWorkspaceAccess.showConfirmDialog(title, msg,
				new String[] { confirm, deny }, new int[] { 0, 1 }) == 0;
	}

	public static WSTextEditorPage getPage(final WSEditor editorAccess) {
		if (editorAccess == null) {
			return null;
		}
		if (!(editorAccess.getCurrentPage() instanceof WSTextEditorPage)) {
			// showDialog("This function is only available in the Text page, not the Author page.");
			return null;
		}
		WSTextEditorPage aWSTextEditorPage = (WSTextEditorPage) editorAccess
				.getCurrentPage();
		return aWSTextEditorPage;
	}

	public WSTextEditorPage getPage() {
		return getPage(getWsEditor());
	}

	WSEditor getWsEditor() {
		final WSEditor editorAccess = pluginWorkspaceAccess
				.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
		return editorAccess;
	}

	URL getEditorLocation() {
		return getWsEditor() != null ? getWsEditor().getEditorLocation() : null;
	}

	String getPageId() {
		return getWsEditor().getCurrentPageID();
	}

	private JTextArea getJTextArea() {
		final Object tc = getPage(getWsEditor()).getTextComponent();
		if (!(tc instanceof JTextArea)) {
			return null;
		}
		final JTextArea ta = (JTextArea) tc;
		return ta;
	}

	/*
	 * This is actually redundant to my own book-keeping in
	 * ProceedAction.handleManualCursorMovement
	 */
	class MyCaretListener implements CaretListener {

		@Override
		public void caretUpdate(CaretEvent e) {
			getDocumentMetaInfo().setManualEdit();
		}

	}
}
