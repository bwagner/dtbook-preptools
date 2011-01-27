package ch.sbs.plugin.preptools;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

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

	private final List<PrepTool> prepTools = new ArrayList<PrepTool>();

	// TODO: we should check what tool is currently active!
	// only update that tool!
	// this depends on the document!
	// hence must be part of DocumentMetaInfo!
	public void setCurrentState(final DocumentMetaInfo theDocumentMetaInfo) {
		for (final PrepTool preptool : prepTools) {
			preptool.setCurrentState(getDocumentMetaInfo());
		}
	}

	private void disableAllActions() {
		setAllActions(false);
	}

	private void setAllActions(final boolean enabled) {
		for (final PrepTool preptool : prepTools) {
			preptool.setAllActionsEnabled(enabled);
		}
	}

	private void populatePrepTools() {
		prepTools.add(new VFormPrepTool(this));
		prepTools.add(new ParensPrepTool(this));
	}

	JPanel toolbarPanel;

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

			populatePrepTools();

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
								disableAllActions();
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
									disableAllActions();
								}
							}
							else {
								removeDocumentMetaInfo(dmi);
								disableAllActions();
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
								disableAllActions();
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
										prepTools.get(0).makeToolbar();
										disableAllActions();
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
								viewInfo.setTitle("PrepTools Project");
							}
						}

					});
		}
	}

	@SuppressWarnings("serial")
	protected JMenu createPrepToolsMenu() {
		final JMenu menuPrepTools = new JMenu("PrepTools");
		final ButtonGroup group = new ButtonGroup();

		int i = 0;
		for (final PrepTool preptool : prepTools) {
			final JMenuItem item = new JRadioButtonMenuItem(
					new AbstractAction() {

						@Override
						public void actionPerformed(ActionEvent e) {
							preptool.makeToolbar();
						}
					});
			item.setText(preptool.getLabel());
			item.setMnemonic(preptool.getMnemonic());
			item.setSelected(i++ == 0); // the first is selected
			menuPrepTools.add(item);
			group.add(item);
		}

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
		final String returnString;
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
