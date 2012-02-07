package ch.sbs.plugin.preptools;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
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
import javax.swing.text.Document;

import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.view.graphics.Rectangle;
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
import ch.sbs.plugin.preptools.DocumentMetaInfo.PrepToolState;
import ch.sbs.utils.preptools.FileUtils;
import ch.sbs.utils.preptools.PositionMatch;
import ch.sbs.utils.preptools.PropsUtils;
import ch.sbs.utils.preptools.RegionSkipper;
import ch.sbs.utils.string.StringUtils;
import ch.sbs.utils.swing.MenuPlugger;

/**
 * Plugin extension - workspace access extension.
 * NOTE: the build file expects this class to be called ...Extension.
 * 
 */
/**
 * Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print
 * Disabled
 * 
 * This file is part of dtbook-preptools.
 * 
 * dtbook-preptools is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

public class PrepToolsPluginExtension implements WorkspaceAccessPluginExtension {

	private List<PrepTool> prepTools;
	private Set<String> tagRegexesToSkip;

	/**
	 * A method to support DocumentMetaInfo's independence of specific
	 * preptools.
	 * 
	 * @param document
	 * @return
	 */
	Map<String, PrepToolState> getToolSpecificMetaInfos(final Document document) {
		final Map<String, PrepToolState> toolSpecific = new HashMap<String, PrepToolState>();
		for (final PrepTool preptool : prepTools) {
			toolSpecific.put(preptool.getPrepToolName(),
					preptool.makeMetaInfo(document));
		}
		return toolSpecific;
	}

	private void disableAllActions(final DocumentMetaInfo theDocumentMetaInfo) {
		setAllActions(theDocumentMetaInfo, false);
	}

	private void setAllActions(final DocumentMetaInfo theDocumentMetaInfo,
			final boolean enabled) {
		if (theDocumentMetaInfo != null) {
			final PrepTool currentPrepTool = theDocumentMetaInfo
					.getCurrentPrepTool();
			if (currentPrepTool != null) {
				currentPrepTool.setAllActionsEnabled(enabled);
			}
		}
	}

	private void populatePrepTools() {
		prepTools = PrepToolLoader.loadPrepTools(this);
		tagRegexesToSkip = new HashSet<String>();
		for (final PrepTool prepTool : prepTools) {
			final String tagRegexToSkip = prepTool.getTagRegexToSkip();
			if (tagRegexToSkip != null) {
				tagRegexesToSkip.add(tagRegexToSkip);
			}
		}
	}

	public RegionSkipper makeSkipper() {
		final RegionSkipper compositeSkipper = RegionSkipper
				.getDefaultSkipper();
		for (final String tagRegexToSkip : tagRegexesToSkip) {
			compositeSkipper.addPattern(RegionSkipper
					.makeMarkupRegex(tagRegexToSkip));
		}
		return compositeSkipper;
	}

	private static void setBold(final JMenuItem item) {
		item.setFont(item.getFont().deriveFont(Font.BOLD));
	}

	private static void setPlain(final JMenuItem item) {
		item.setFont(item.getFont().deriveFont(Font.PLAIN));
	}

	private void setPrepToolItemDone(final JMenuItem item) {
		setPlain(item);
	}

	void disableMenuPrepTools() {
		menuPrepTools.setEnabled(false);
	}

	void enableMenuPrepTools() {
		menuPrepTools.setEnabled(true);
	}

	void setPrepToolItemDone(int i) {
		setPrepToolItemDone(menuPrepTools.getItem(i));
	}

	void setPrepToolItemNormal(final JMenuItem item) {
		setBold(item);
	}

	private void setPrepToolItemNormal(int i) {
		setPrepToolItemNormal(menuPrepTools.getItem(i));
	}

	private void updatePrepToolItems() {
		int i = 0;
		for (final PrepTool preptool : prepTools) {
			if (getDocumentMetaInfo().getToolSpecificMetaInfo(
					preptool.getPrepToolName()).isDone()) {
				setPrepToolItemDone(i);
			}
			else {
				setPrepToolItemNormal(i);
			}
			i++;
		}
	}

	void selectPrepToolItem(int i) {
		menuPrepTools.getItem(i).setSelected(true);
	}

	private PrepTool getDefaultPrepTool() {
		return prepTools.get(0);
	}

	private PrepTool getCurrentPrepTool() {
		final DocumentMetaInfo documentMetaInfo = getDocumentMetaInfo();
		return documentMetaInfo != null ? documentMetaInfo.getCurrentPrepTool()
				: null;
	}

	/**
	 * Finds the first uncompleted PrepTool by simply iterating through the
	 * entire list and checking each PrepTool whether it is done. As soon as
	 * a PrepTool is found that isn't done yet, it's activated.
	 * If all PrepTools are done, an according dialog is shown.
	 * 
	 */
	public void chooseNextUncompletedPrepTool() {
		boolean found = false;
		final Iterator<PrepTool> iter = prepTools.iterator();
		int i = 0;
		PrepTool preptool = null;
		while (!found && iter.hasNext()) {
			preptool = iter.next();
			final PrepToolState mi = getDocumentMetaInfo()
					.getToolSpecificMetaInfo(preptool.getPrepToolName());
			if (!(found = !mi.isDone())) {
				++i;
			}
		}
		if (found) {
			selectPrepToolItem(i);
			preptool.activate();
		}
		else {
			showDialog("All PrepTools done.");
		}
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
	private double oxygenVersion;

	private boolean applicationClosing;

	private MyCaretListener caretHandler;

	private JMenu menuPrepTools;

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	@Override
	public void applicationStarted(
			final StandalonePluginWorkspace thePluginWorkspaceAccess) {
		pluginWorkspaceAccess = thePluginWorkspaceAccess;
		oxygenVersion = Double.parseDouble(pluginWorkspaceAccess.getVersion());
		caretHandler = new MyCaretListener();

		populatePrepTools();

		pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
			 */
			@Override
			public void customizeMainMenu(final JMenuBar mainMenuBar) {
				customizeExistingMenuEntries(mainMenuBar);
				// PrepTools menu
				menuPrepTools = createPrepToolsMenu();
				menuPrepTools.setMnemonic(KeyEvent.VK_R);
				// Add the Preptools menu before the Help menu
				mainMenuBar.add(menuPrepTools, mainMenuBar.getMenuCount() - 1);
			}

			/**
			 * Customizes existing entries in the menu bar: We're maintaining
			 * the original functionality, adding to it as follows:
			 * - offer canceling the operation
			 * - add cleanup bookkeeping if operation was made.
			 * 
			 * @param mainMenuBar
			 */
			@SuppressWarnings("serial")
			private void customizeExistingMenuEntries(final JMenuBar mainMenuBar) {
				final String[] revertLabel = { "Revert...", "Rückgängig..." };
				boolean plug = MenuPlugger.plug(mainMenuBar, revertLabel,
						new MenuPlugger.ActionWrapper() {

							@Override
							protected boolean pre(
									final Action theWrappedAction,
									final ActionEvent theActionEvent) {
								final DocumentMetaInfo dmi = getDocumentMetaInfo();
								if (dmi.isProcessing()) {
									return showConfirmDialog("Revert",
											"Document is being processed. Sure you want to Revert?");
								}
								return true;
							}

							@Override
							protected void post(final Action theWrappedAction,
									final ActionEvent theActionEvent) {
								// Recreate the state like before starting to
								// use the current tool.
								// Unfortunately, we don't know whether the user
								// canceled the Revert action. But we'll
								// restart anyway. No big deal.
								final PrepToolState mi = getDocumentMetaInfo()
										.getCurrentToolSpecificMetaInfo();
								if (mi.hasStarted()) {
									mi.setHasStarted(false);
									mi.setDone(false);
									getCurrentPrepTool().setCurrentState(
											getDocumentMetaInfo());
								}

							}

						});
				if (!plug) {
					pluginWorkspaceAccess.showErrorMessage("plugging \""
							+ StringUtils.join(revertLabel) + "\" failed.");
				}
				final String[] saveAsLabel = { "Save As...",
						"Datei speichern unter..." };
				plug = MenuPlugger.plug(mainMenuBar, saveAsLabel,
						new MenuPlugger.ActionWrapper() {

							private DocumentMetaInfo dmi;

							@Override
							protected boolean pre(
									final Action theWrappedAction,
									final ActionEvent theActionEvent) {
								dmi = getDocumentMetaInfo();
								if (dmi.isProcessing()) {
									return showConfirmDialog("Save As",
											"Document is being processed. Sure you want to Save As?");
								}
								return true;
							}

							@Override
							protected void post(final Action theWrappedAction,
									final ActionEvent theActionEvent) {
								removeDocumentMetaInfo(dmi);
								consolidatePrepTools(getEditorLocation());
							}

						});
				if (!plug) {
					pluginWorkspaceAccess.showErrorMessage("plugging \""
							+ StringUtils.join(saveAsLabel) + "\" failed.");
				}
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
							return;
						}
						// we don't do much else here since in every case
						// editorSelected will be called after editorOpened.

						// addCaretHandler();
					}

					@SuppressWarnings("unused")
					private void addCaretHandler() {
						final JTextArea ta = getJTextArea();
						ta.addCaretListener(caretHandler);
					}

					/**
					 * IMPORTANT: Don't add annotation @Override here!
					 * 
					 * This implementation is supposed to work with both oXygen
					 * versions 12.1 and 12.2.
					 * Only in versions > 12.1 is this method going to be
					 * called.
					 * 
					 * @param editorLocation
					 * @return
					 */

					// @Override IMPORTANT: Don't add annotation @Override here!
					public boolean editorAboutToBeClosed(
							final URL editorLocation) {
						final DocumentMetaInfo dmi = getDocumentMetaInfo(editorLocation);
						if (dmi.isProcessing() && !applicationClosing) {
							// we can veto closing in version 12.2 !
							return (!showConfirmDialog(
									"Continue?",
									"Document "
											+ FileUtils
													.basename(editorLocation)
											+ " is still being processed. Do you want to continue processing?",
									"Continue Processing", "Close Anyway"));
						}
						return true; // true means editor will be closed, false
										// means editor remains open.
					}

					@Override
					public void editorClosed(final URL editorLocation) {
						final DocumentMetaInfo dmi = getDocumentMetaInfo(editorLocation);
						if (dmi.isProcessing() && !applicationClosing
								&& oxygenVersion < 12.2) {
							// we can't veto closing in
							// oxygen versions below 12.2!
							if (showConfirmDialog(
									"Reopen and Continue?",
									"Document "
											+ FileUtils
													.basename(editorLocation)
											+ " was still being processed. Want to reopen and continue?",
									"Reopen", "Close Anyway")) {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										pluginWorkspaceAccess
												.open(editorLocation);
										dmi.setPageAndDocument(PrepToolsPluginExtension.this);
										dmi.setCurrentState();
										final PositionMatch match = dmi
												.getCurrentPositionMatch();
										getPage().select(
												match.startOffset.getOffset(),
												match.endOffset.getOffset());
										dmi.resetManualEdit();
									}
								});

							}
							else {
								removeDocumentMetaInfo(dmi);
							}
						}
						else {
							removeDocumentMetaInfo(dmi);
						}
					};

					@Override
					public void editorPageChanged(URL editorLocation) {
						final DocumentMetaInfo dmi = getDocumentMetaInfo(editorLocation);
						dmi.setCurrentEditorPage(getPageId());
						dmi.setCurrentState();
					};

					@Override
					public void editorSelected(URL editorLocation) {
						consolidatePrepTools(editorLocation);
					};
				}, StandalonePluginWorkspace.MAIN_EDITING_AREA);

		pluginWorkspaceAccess
				.addToolbarComponentsCustomizer(new ToolbarComponentsCustomizer() {
					/**
					 * @see ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer#customizeToolbar(ro.sync.exml.workspace.api.standalone.ToolbarInfo)
					 */
					@Override
					public void customizeToolbar(ToolbarInfo theToolbarInfo) {
						if (Ids.TOOLBAR_ID.equals(theToolbarInfo.getToolbarID())) {

							toolbarPanel = new JPanel();
							toolbarPanel.setLayout(new BoxLayout(toolbarPanel,
									BoxLayout.LINE_AXIS));
							theToolbarInfo
									.setComponents(new JComponent[] { toolbarPanel });
							theToolbarInfo.setTitle("PrepTools");

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									getDefaultPrepTool().activate();
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
						if (Ids.VIEW_ID.equals(viewInfo.getViewID())) {
							prepToolsMessagesArea = new JTextArea(
									"PrepTools Session History:");
							viewInfo.setComponent(new JScrollPane(
									prepToolsMessagesArea));
							viewInfo.setTitle("PrepTools Messages");
							viewInfo.setIcon(Icons
									.getIcon(Icons.CMS_MESSAGES_CUSTOM_VIEW_STRING));
							showMessage(getVersion());
						}
						else if ("Project".equals(viewInfo.getViewID())) {
							viewInfo.setTitle("PrepTools Project");
						}
					}

				});
	}

	@SuppressWarnings("serial")
	protected JMenu createPrepToolsMenu() {
		final JMenu menuPrepTools = new JMenu("PrepTools");
		final ButtonGroup group = new ButtonGroup();

		for (final PrepTool preptool : prepTools) {
			final JMenuItem item = new JRadioButtonMenuItem(
					new AbstractAction() {

						@Override
						public void actionPerformed(ActionEvent e) {
							final PrepToolState prepToolState = getDocumentMetaInfo()
									.getCurrentToolSpecificMetaInfo();
							final PrepTool currentPrepTool = getCurrentPrepTool();
							final String label = currentPrepTool
									.getPrepToolName();
							if (preptool != currentPrepTool
									&& (!prepToolState.isProcessing() || showConfirmDialog(
											"Switching:",
											"Not Yet Done Processing " + label
													+ "!", "Switch Anyway",
											"Cancel"))) {
								prepToolState.setHasStarted(false);
								preptool.activate();
							}
							else {
								// Re-select the current preptool, otherwise
								// the radio button of the newly selected
								// preptool will remain selected.
								selectPrepToolItem(prepTools
										.indexOf(currentPrepTool));
							}
						}
					});
			item.setText(preptool.getPrepToolName());
			item.setMnemonic(preptool.getMnemonic());
			menuPrepTools.add(item);
			group.add(item);
		}

		return menuPrepTools;
	}

	private final Map<URL, DocumentMetaInfo> documentMetaInfos = new HashMap<URL, DocumentMetaInfo>();

	DocumentMetaInfo getDocumentMetaInfo() {
		return getDocumentMetaInfo(getEditorLocation());
	}

	DocumentMetaInfo getDocumentMetaInfo(final URL editorLocation) {
		if (documentMetaInfos.containsKey(editorLocation)) {
			return documentMetaInfos.get(editorLocation);
		}
		if (getPage() == null) {
			return null;
		}

		final DocumentMetaInfo documentMetaInformation = new DocumentMetaInfo(
				this);

		documentMetaInfos.put(editorLocation, documentMetaInformation);
		return documentMetaInformation;
	}

	private void removeDocumentMetaInfo(final DocumentMetaInfo dmi) {
		disableAllActions(dmi);
		dmi.finish();
		documentMetaInfos.remove(dmi.getUrl());
		if (documentMetaInfos.isEmpty()) {
			disableMenuPrepTools();
		}
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

		final String oxygenVersion = "\nOxygen-Version:"
				+ pluginWorkspaceAccess.getVersion() + "\n";

		return returnString + oxygenVersion;
	}

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
	 */
	@Override
	public boolean applicationClosing() {
		final StringBuilder sb = new StringBuilder();
		boolean showDialog = false;
		for (final URL url : documentMetaInfos.keySet()) {
			if (documentMetaInfos.get(url).isProcessing()) {
				showDialog = true;
				sb.append("\n- ");
				sb.append(FileUtils.basename(url));
			}
		}

		if (showDialog) {
			sb.insert(0, "The following documents are still being processed:");
			sb.append("\n\nProceed with closing anyway?");
			applicationClosing = showConfirmDialog("Close?", sb.toString());
			return applicationClosing;
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

	final String DIALOG_HEADER = "PrepTools: ";

	void showDialog(final String msg) {
		pluginWorkspaceAccess.showConfirmDialog(DIALOG_HEADER, msg,
				new String[] { "OK" }, new int[] { 0 });
		pluginWorkspaceAccess.showView(Ids.VIEW_ID, true);
	}

	boolean showConfirmDialog(final String title, final String msg) {
		return showConfirmDialog(title, msg, "Ok", "Cancel");
	}

	/**
	 * @param title
	 * @param msg
	 * @param trueText
	 *            String to indicate the "true" button
	 * @param falseText
	 *            String to indicate the "false" button
	 * @return true if user clicked "trueText", false if user clicked
	 *         "falseText"
	 */
	boolean showConfirmDialog(final String title, final String msg,
			final String trueText, final String falseText) {
		return pluginWorkspaceAccess.showConfirmDialog(DIALOG_HEADER + title,
				msg, new String[] { trueText, falseText }, new int[] { 0, 1 }) == 0;
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
		return tc instanceof JTextArea ? (JTextArea) tc : null;
	}

	/**
	 * 
	 * Utility method to select text. Scrolls the found position off the view
	 * borders.
	 * 
	 * @param start
	 * @param end
	 */
	protected void select(int start, int end) {
		final int OFFSET = 200;
		final JTextArea textArea = getJTextArea();
		getPage().select(start, end);
		final Rectangle r = getPage().modelToViewRectangle(end);
		textArea.scrollRectToVisible(new java.awt.Rectangle(r.x, r.y - OFFSET
				/ 2, r.width, r.height + OFFSET));
	}

	private void consolidatePrepTools(final URL editorLocation) {
		final DocumentMetaInfo dmi = getDocumentMetaInfo(editorLocation);
		if (dmi != null) {
			PrepTool currentPrepTool = dmi.getCurrentPrepTool();
			if (currentPrepTool == null) {
				currentPrepTool = getDefaultPrepTool();
			}
			updatePrepToolItems();
			currentPrepTool.activate();
		}
	}

	/*
	 * This is actually redundant to my own book-keeping in
	 * ProceedAction.handleManualCursorMovement
	 * 
	 * Also: The caretUpdate will be called when moving the 
	 * caret from my code, not only when the user moves the 
	 * caret within the editor!
	 */
	class MyCaretListener implements CaretListener {

		@Override
		public void caretUpdate(CaretEvent e) {
			// getDocumentMetaInfo().setManualEdit();
			showMessage("caret moved! " + e);
		}

	}
}
