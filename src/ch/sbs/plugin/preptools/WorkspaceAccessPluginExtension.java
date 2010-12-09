package ch.sbs.plugin.preptools;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
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

	private final String LABEL = "VForms";

	// legacy action: replaces everything.

	private class VFormAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			final WSEditor editorAccess = pluginWorkspaceAccess
					.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
			if (editorAccess != null) {
				if (!(editorAccess.getCurrentPage() instanceof WSTextEditorPage)) {
					showDialog("This function is only available in the Text page, not the Author page.");
					return;
				}
				WSTextEditorPage aWSTextEditorPage = (WSTextEditorPage) editorAccess
						.getCurrentPage();
				final Document document = aWSTextEditorPage.getDocument();
				try {
					String text = document.getText(0, document.getLength());
					text = VFormUtil.replace(text);
					document.remove(0, document.getLength());
					document.insertString(0, text, null);

				} catch (BadLocationException e) {
					e.printStackTrace();
				}

			}
		}
	};

	// TODO: Code duplication between Actions
	// TODO: enable/disable toolbar buttons
	// TODO: navigate on the dom not the plain text!

	private class VFormStartAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			final WSEditor editorAccess = pluginWorkspaceAccess
					.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
			if (editorAccess != null) {
				if (!(editorAccess.getCurrentPage() instanceof WSTextEditorPage)) {
					showDialog("This function is only available in the Text page, not the Author page.");
					return;
				}
				WSTextEditorPage aWSTextEditorPage = (WSTextEditorPage) editorAccess
						.getCurrentPage();
				final Document document = aWSTextEditorPage.getDocument();
				try {
					String text = document.getText(0, document.getLength());
					VFormUtil.Match match = VFormUtil.find(text, 0);
					aWSTextEditorPage
							.select(match.startOffset, match.endOffset);

				} catch (BadLocationException e) {
					e.printStackTrace();
				}

			}
		}
	};

	private class VFormAcceptAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			final WSEditor editorAccess = pluginWorkspaceAccess
					.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
			if (editorAccess != null) {
				if (!(editorAccess.getCurrentPage() instanceof WSTextEditorPage)) {
					showDialog("This function is only available in the Text page, not the Author page.");
					return;
				}
				WSTextEditorPage aWSTextEditorPage = (WSTextEditorPage) editorAccess
						.getCurrentPage();
				final Document document = aWSTextEditorPage.getDocument();
				try {
					String text = document.getText(0, document.getLength());
					VFormUtil.Match match = VFormUtil.find(text,
							aWSTextEditorPage.getSelectionEnd());
					aWSTextEditorPage
							.select(match.startOffset, match.endOffset);

				} catch (BadLocationException e) {
					e.printStackTrace();
				}

			}
		}
	};

	// TODO: VForm Reject action

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

			// Check Out action
			final Action vformContinueAction = new VFormAcceptAction();

			// Show Selection Source action
			final Action selectionSourceAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					WSEditor editorAccess = pluginWorkspaceAccess
							.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
					// The action is available only in Author mode.
					if (editorAccess != null
							&& EditorPageConstants.PAGE_AUTHOR
									.equals(editorAccess.getCurrentPageID())) {
						WSAuthorEditorPage authorPageAccess = (WSAuthorEditorPage) editorAccess
								.getCurrentPage();
						AuthorDocumentController controller = authorPageAccess
								.getDocumentController();
						if (authorPageAccess.hasSelection()) {
							AuthorDocumentFragment selectionFragment;
							try {
								// Create fragment from selection
								selectionFragment = controller
										.createDocumentFragment(
												authorPageAccess
														.getSelectionStart(),
												authorPageAccess
														.getSelectionEnd() - 1);
								// Serialize
								String serializeFragmentToXML = controller
										.serializeFragmentToXML(selectionFragment);
								// Show fragment
								pluginWorkspaceAccess
										.showInformationMessage(serializeFragmentToXML);
							} catch (BadLocationException e) {
								pluginWorkspaceAccess
										.showErrorMessage("Show Selection Source operation failed: "
												+ e.getMessage());
							}
						} else {
							// No selection
							pluginWorkspaceAccess
									.showInformationMessage("No selection available.");
						}
					}
				}
			};

			pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
				/**
				 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
				 */
				@Override
				public void customizeMainMenu(JMenuBar mainMenuBar) {
					// PrepTools menu
					final JMenu menuPrepTools = createPrepToolsMenu(
							vformStartAction, vformContinueAction,
							selectionSourceAction);
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
							// Show 'Edit' toolbar
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
														vformContinueAction,
														selectionSourceAction);
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

						// Check actions status
						private void checkActionsStatus(URL editorLocation) {
							WSEditor editorAccess = pluginWorkspaceAccess
									.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
							if (editorAccess != null) {
								selectionSourceAction
										.setEnabled(EditorPageConstants.PAGE_AUTHOR
												.equals(editorAccess
														.getCurrentPageID()));
							}
							vformStartAction.setEnabled(true);
						}

						@Override
						public void editorClosed(URL editorLocation) {
							URL checkedOutUrl = openedCheckedOutUrls
									.get(editorLocation);
							if (checkedOutUrl != null) {
								if (verifyCheckInOnClose) {
									if (forceCheckIn
											|| pluginWorkspaceAccess
													.showConfirmDialog(
															"Close",
															"The closed file "
																	+ editorLocation
																	+ " is Checked Out.\n Do you want to Check In?",
															new String[] {
																	"Ok",
																	"Cancel" },
															new int[] { 0, 1 }) == 0) {
										// Save the current file.
										checkInFile(pluginWorkspaceAccess,
												null, editorLocation,
												checkedOutUrl, false);
									}
								}
								openedCheckedOutUrls.remove(editorLocation);
							}
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
								vFormButton.setText(LABEL);

								// VForm Accept
								JButton checkOutButton = new JButton(
										vformContinueAction);
								checkOutButton.setText("Accept");

								// VForm Reject
								JButton selectionSourceButton = new JButton(
										selectionSourceAction);
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
							} else if ("Project".equals(viewInfo.getViewID())) {
								// Change the 'Project' view title.
								viewInfo.setTitle("PrepTools Project");
							}
						}
					});
		}
	}

	/**
	 * Create PrepTools menu that contains the following actions:
	 * <code>Check In</code>, <code>Check Out</code> and
	 * <code>Show Selection Source<code/>
	 * 
	 * @return
	 */
	private JMenu createPrepToolsMenu(final Action checkInAction,
			final Action checkOutAction, final Action selectionSourceAction) {
		// PrepTools menu
		JMenu menuPrepTools = new JMenu("PrepTools");

		// Add Check In action on the menu
		final JMenuItem checkInItem = new JMenuItem(checkInAction);
		checkInItem.setText(LABEL);
		menuPrepTools.add(checkInItem);

		// Add Check Out action on the menu
		JMenuItem checkOutItem = new JMenuItem(checkOutAction);
		checkOutItem.setText("Check Out");
		menuPrepTools.add(checkOutItem);

		// Add Show Section Source action on the menu
		JMenuItem selectionSourceItem = new JMenuItem(selectionSourceAction);
		selectionSourceItem.setText("Show Selection Source");
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

	/**
	 * Create temporary file from checked out file.
	 * 
	 * @param checkedOutFile
	 *            The checked out file.
	 * @return Temporary file.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private File createTempFromCheckedOutFile(File checkedOutFile)
			throws IOException, FileNotFoundException {
		int indexOfPoint = checkedOutFile.getName().lastIndexOf('.');
		String fileName = checkedOutFile.getName();
		// Temporary file name
		String tempFileName = indexOfPoint > -1 ? fileName.substring(0,
				indexOfPoint) : fileName;
		// Temporary file extension
		String fileExtension = indexOfPoint > -1 ? fileName
				.substring(indexOfPoint) : null;

		// Create temporary file
		File tempFile = File.createTempFile("cms_oxy" + tempFileName,
				fileExtension, checkedOutFile.getParentFile());

		// Write the content
		copyFileContent(checkedOutFile, tempFile);

		return tempFile;
	}

	/**
	 * Copy content from one file to another.
	 * 
	 * @param initialFile
	 *            The initial file to copy the content from.
	 * @param destinationFile
	 *            The destination.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void copyFileContent(File initialFile, File destinationFile)
			throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(initialFile);
		FileOutputStream fos = new FileOutputStream(destinationFile);
		// Write the content
		int b;
		while ((b = fis.read()) != -1) {
			fos.write(b);
		}
		fos.close();
		fis.close();
	}

	/**
	 * Check In file.
	 * 
	 * @param pluginWorkspaceAccess
	 *            The plugin workspace access
	 * @param editorAccess
	 *            The editor access.
	 * @param tempFileUrl
	 *            The temporary local file URL.
	 * @param checkedOutUrl
	 *            The URL of the corresponding checked out file
	 */
	private void checkInFile(
			final StandalonePluginWorkspace pluginWorkspaceAccess,
			WSEditor editorAccess, URL tempFileUrl, URL checkedOutUrl,
			boolean openFile) {
		boolean checkIn = true;
		// Verify if the editor is modified
		if (editorAccess != null && editorAccess.isModified()) {
			// Ask to save
			if (pluginWorkspaceAccess.showConfirmDialog("Save",
					"You must save the file in order to Check In.",
					new String[] { "Ok", "Cancel" }, new int[] { 0, 1 }) == 0) {
				// Save the current file.
				editorAccess.save();
			} else {
				// Cancel.
				checkIn = false;
				pluginWorkspaceAccess
						.showInformationMessage("Check In operation was canceled.");
			}
		}

		// Perform Check In ...
		if (checkIn) {
			try {
				File tempFile = new File(tempFileUrl.getFile());

				// Copy the content of the temporary file over the original file
				copyFileContent(tempFile, new File(checkedOutUrl.getFile()));

				if (editorAccess != null) {
					// Close temporary file editor
					verifyCheckInOnClose = false;
					editorAccess.close(false);
					verifyCheckInOnClose = true;
				}

				// Delete temporary file
				tempFile.delete();

				if (openFile) {
					// Open the checked out file
					pluginWorkspaceAccess.open(checkedOutUrl);
					showDialog("Check In was performed.");
					showMessage("Check In " + checkedOutUrl.toString());
				}
			} catch (Exception e) {
				// Failed
				pluginWorkspaceAccess
						.showErrorMessage("Check In operation failed: "
								+ e.getMessage());
			}
		}
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
}