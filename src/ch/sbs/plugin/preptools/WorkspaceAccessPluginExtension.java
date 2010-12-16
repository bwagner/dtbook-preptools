package ch.sbs.plugin.preptools;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.structure.AuthorPopupMenuCustomizer;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer;
import ro.sync.exml.workspace.api.standalone.ToolbarInfo;
import ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer;
import ro.sync.exml.workspace.api.standalone.ViewInfo;
import ro.sync.ui.Icons;
import ch.sbs.utils.preptools.vform.PropsUtils;
import ch.sbs.utils.preptools.vform.VFormUtil;

/**
 * Plugin extension - workspace access extension.
 */
public class WorkspaceAccessPluginExtension implements
		ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension {

	/**
	 * Map between the URL of the temporary local file that contains the content
	 * of the checked out file and the URL of the checked out file.
	 */
	private final Map<URL, URL> openedCheckedOutUrls = new HashMap<URL, URL>();

	/**
	 * If <code>true</code> the editor will be verified for Check In on close.
	 */
	private boolean verifyCheckInOnClose = true;

	/**
	 * If <code>true</code> the document is Checked Out, it will be Checked In
	 * on close.
	 */
	private boolean forceCheckIn;

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

	private abstract class AbstractVFormAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			final WSEditor editorAccess = pluginWorkspaceAccess
					.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
			final WSTextEditorPage aWSTextEditorPage;
			if ((aWSTextEditorPage = getPage(editorAccess)) != null) {
				final Document document = aWSTextEditorPage.getDocument();
				try {
					doSomething(editorAccess, aWSTextEditorPage, document,
							document.getText(0, document.getLength()));
				} catch (BadLocationException e) {
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
				final WSTextEditorPage aWSTextEditorPage,
				final Document document, final String text)
				throws BadLocationException;

	}

	/**
	 * legacy action: replaces everything. TODO: doesn't care about enclosing
	 * <brl:v-form>
	 */
	private class VFormAction extends AbstractVFormAction {
		@Override
		protected void doSomething(final WSEditor editorAccess,
				final WSTextEditorPage aWSTextEditorPage,
				final Document document, final String text)
				throws BadLocationException {

			document.remove(0, document.getLength());
			document.insertString(0, VFormUtil.replace(text), null);
		}
	};

	// TODO: enable/disable toolbar buttons
	// TODO: navigate on the dom not the plain text!
	private class VFormStartAction extends AbstractVFormAction {
		@Override
		protected void doSomething(final WSEditor editorAccess,
				final WSTextEditorPage aWSTextEditorPage,
				final Document document, final String text)
				throws BadLocationException {

			showMessage("vform start action");
			final VFormUtil.Match match = VFormUtil.find(text, 0);
			aWSTextEditorPage.select(match.startOffset, match.endOffset);
		}
	};

	// Accept should only be enabled
	// 1.) when there's selected text in the editor
	// 2.) the selected text conforms to a vform.
	private class VFormAcceptAction extends AbstractVFormAction {
		@Override
		protected void doSomething(final WSEditor editorAccess,
				final WSTextEditorPage aWSTextEditorPage,
				final Document document, final String text)
				throws BadLocationException {

			// enclose selection with "br:v-form"

			showMessage("vform accept action");
			final String selText = aWSTextEditorPage.getSelectedText();

			if (selText == null)
				return;

			final String newText = VFormUtil.wrap(selText);

			final int TEXT_START = aWSTextEditorPage.getSelectionStart();
			aWSTextEditorPage.deleteSelection();

			String text3 = document.getText(0, document.getLength());
			text3 = text3.substring(0, TEXT_START) + newText
					+ text3.substring(TEXT_START);

			document.remove(0, document.getLength());
			document.insertString(0, text3, null);

			final VFormUtil.Match match = VFormUtil.find(text, TEXT_START
					+ newText.length());
			aWSTextEditorPage.select(match.startOffset, match.endOffset);
		}
	};

	private class VFormRejectAction extends AbstractVFormAction {
		@Override
		protected void doSomething(final WSEditor editorAccess,
				final WSTextEditorPage aWSTextEditorPage,
				final Document document, final String text)
				throws BadLocationException {

			showMessage("vform reject action");
			// don't enclose selection with "br:v-form"

			final VFormUtil.Match match = VFormUtil.find(text,
					aWSTextEditorPage.getSelectionEnd());
			aWSTextEditorPage.select(match.startOffset, match.endOffset);
		}
	};

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	@Override
	public void applicationStarted(
			final StandalonePluginWorkspace pluginWorkspaceAccess) {
		URL resource = getClass().getResource(
				"/" + getClass().getName().replace('.', '/') + ".class");
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.runPlugin = "jar".equals(resource.getProtocol()) ? true : System
				.getProperty("cms.sample.plugin") != null;
		if (runPlugin) {
			final Action vformStartAction = new VFormStartAction();

			final Action vformAcceptAction = new VFormAcceptAction();

			final Action vformRejectAction = new VFormRejectAction();

			pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
				/**
				 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
				 */
				@Override
				public void customizeMainMenu(JMenuBar mainMenuBar) {
					// PrepTools menu
					final JMenu menuPrepTools = createPrepToolsMenu(
							vformStartAction, vformAcceptAction,
							vformRejectAction);
					// Add the PrepTools menu before the Help menu
					mainMenuBar.add(menuPrepTools,
							mainMenuBar.getMenuCount() - 1);
				}
			});

			pluginWorkspaceAccess.addEditorChangeListener(
					new WSEditorChangeListener() {

						@Override
						public void editorOpened(URL editorLocation) {
							checkActionsStatus(editorLocation);
							customizePopupMenu();
							pluginWorkspaceAccess.showToolbar("Edit");
						};

						// Customize popup menu
						private void customizePopupMenu() {
							WSEditor editorAccess = pluginWorkspaceAccess
									.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
							// Customize menu for Author page
							if (editorAccess != null
									&& EditorPageConstants.PAGE_AUTHOR
											.equals(editorAccess
													.getCurrentPageID())) {
								WSAuthorEditorPage authorPageAccess = (WSAuthorEditorPage) editorAccess
										.getCurrentPage();
								authorPageAccess
										.setPopUpMenuCustomizer(new AuthorPopupMenuCustomizer() {
											// Customize popup menu
											@Override
											public void customizePopUpMenu(
													Object popUp,
													AuthorAccess authorAccess) {
												// PrepTools menu
												JMenu menuPrepTools = createPrepToolsMenu(
														vformStartAction,
														vformAcceptAction,
														vformRejectAction);
												// Add the PrepTools menu
												((JPopupMenu) popUp).add(
														menuPrepTools, 0);
												// Add 'Open in external
												// application' action

												final URL selectedUrl;
												try {
													final String selectedText = authorAccess
															.getEditorAccess()
															.getSelectedText();
													if (selectedText != null) {
														selectedUrl = new URL(
																selectedText);
														// Open selected url in
														// system application
														((JPopupMenu) popUp)
																.add(new JMenuItem(
																		new AbstractAction(
																				"Open in system application") {
																			@Override
																			public void actionPerformed(
																					ActionEvent e) {
																				pluginWorkspaceAccess
																						.openInExternalApplication(
																								selectedUrl,
																								true);
																			}
																		}), 0);
													}
												} catch (MalformedURLException e) {
												}
											}
										});
							}
						}

						private void checkActionsStatus(URL editorLocation) {
							WSEditor editorAccess = pluginWorkspaceAccess
									.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);

							vformStartAction.setEnabled(true);
							vformAcceptAction.setEnabled(false);
							vformRejectAction.setEnabled(false);
							final boolean text_editor = editorAccess != null
									&& EditorPageConstants.PAGE_TEXT
											.equals(editorAccess
													.getCurrentPageID());
							vformAcceptAction.setEnabled(text_editor
									&& VFormUtil.matches(getPage(editorAccess)
											.getSelectedText()));
							vformRejectAction.setEnabled(text_editor
									&& VFormUtil.matches(getPage(editorAccess)
											.getSelectedText()));

							// FIXME! checkActionStatus should be called as soon
							// as selection changes
							vformAcceptAction.setEnabled(true);
							vformRejectAction.setEnabled(true);
						}

						@Override
						public void editorClosed(URL editorLocation) {
						};

						@Override
						public void editorPageChanged(URL editorLocation) {
							checkActionsStatus(editorLocation);
							customizePopupMenu();
						};

						@Override
						public void editorSelected(URL editorLocation) {
							checkActionsStatus(editorLocation);
							customizePopupMenu();
						};
					}, StandalonePluginWorkspace.MAIN_EDITING_AREA);

			pluginWorkspaceAccess
					.addToolbarComponentsCustomizer(new ToolbarComponentsCustomizer() {
						/**
						 * @see ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer#customizeToolbar(ro.sync.exml.workspace.api.standalone.ToolbarInfo)
						 */
						@Override
						public void customizeToolbar(ToolbarInfo toolbarInfo) {
							if (ToolbarComponentsCustomizer.CUSTOM
									.equals(toolbarInfo.getToolbarID())) {
								// VForm Start
								JButton vFormButton = new JButton(
										vformStartAction);
								vFormButton.setText("VForms");

								// VForm Accept
								JButton checkOutButton = new JButton(
										vformAcceptAction);
								checkOutButton.setText("Accept");

								// VForm Reject
								JButton selectionSourceButton = new JButton(
										vformRejectAction);
								selectionSourceButton.setText("Reject");

								// Add in toolbar
								List<JComponent> comps = new ArrayList<JComponent>();
								comps.add(vFormButton);
								comps.add(checkOutButton);
								comps.add(selectionSourceButton);
								toolbarInfo.setComponents(comps
										.toArray(new JComponent[0]));

								// Set title
								toolbarInfo.setTitle("PrepTools");
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
							} else if ("Project".equals(viewInfo.getViewID())) {
								// Change the 'Project' view title.
								viewInfo.setTitle("PrepTools Project");
							}
						}

					});
		}
	}

	private String getVersion() {
		final String key = "stamp";
		final String filename = "stamp.properties";
		final String version = PropsUtils.loadForClass(this.getClass(),
				filename).getProperty(key);
		if (version != null && version.length() > 0) {
			return version;
		} else {
			return "'" + key + "' not found in props file " + filename;
		}
	}

	/**
	 * Create PrepTools menu that contains the following actions:
	 * <code>Check In</code>, <code>Check Out</code> and
	 * <code>Show Selection Source<code/>
	 * 
	 * @return
	 */
	private JMenu createPrepToolsMenu(final Action vformStartAction,
			final Action vformAcceptAction, final Action vformRejectAction) {
		// PrepTools menu
		JMenu menuPrepTools = new JMenu("PrepTools");

		// Add Check In action on the menu
		final JMenuItem checkInItem = new JMenuItem(vformStartAction);
		checkInItem.setText("VForms");
		menuPrepTools.add(checkInItem);

		// Add Check Out action on the menu
		JMenuItem checkOutItem = new JMenuItem(vformAcceptAction);
		checkOutItem.setText("Accept");
		menuPrepTools.add(checkOutItem);

		// Add Show Section Source action on the menu
		JMenuItem selectionSourceItem = new JMenuItem(vformRejectAction);
		selectionSourceItem.setText("Reject");
		menuPrepTools.add(selectionSourceItem);

		return menuPrepTools;
	}

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
	 */
	@Override
	public boolean applicationClosing() {
		if (runPlugin) {
			if (!openedCheckedOutUrls.isEmpty()) {
				int result = pluginWorkspaceAccess
						.showConfirmDialog(
								"Close",
								"There are some opened Checked Out files.\n Do you want to Check In?",
								new String[] { "Check In All",
										"Don't Check In", "Cancel" },
								new int[] { 0, 1, 2 });
				// Check In
				if (result == 0) {
					verifyCheckInOnClose = true;
					forceCheckIn = true;
					// Don't Check In
				} else if (result == 1) {
					verifyCheckInOnClose = false;
					// Cancel
				} else if (result == 2) {
					return false;
				}
			}
		}
		return true;
	}

	private void showMessage(final String msg) {
		if (prepToolsMessagesArea != null) {
			prepToolsMessagesArea.append("\n");
			prepToolsMessagesArea.append(msg);
		}
	}

	private void showDialog(final String msg) {
		pluginWorkspaceAccess.showInformationMessage("Running v-form");
		pluginWorkspaceAccess.showView(ViewComponentCustomizer.CUSTOM, true);
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
}
