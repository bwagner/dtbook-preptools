package ch.sbs.plugin.preptools;

import java.awt.Component;
import java.awt.event.ActionEvent;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.sync.exml.editor.EditorPageConstants;
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
import ch.sbs.utils.preptools.vform.PropsUtils;
import ch.sbs.utils.preptools.vform.VFormUtil;

/**
 * Plugin extension - workspace access extension.
 */
public class WorkspaceAccessPluginExtension implements
		ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension {

	/**
	 * Keeps meta information about a document known to the plugin.
	 * 
	 * 
	 */
	private class DocumentMetaInformation {
		boolean isDtBook;
		boolean checkingVform;
		boolean isOldSpelling;
		protected WSTextEditorPage page;
	}

	private final Map<URL, DocumentMetaInformation> documents = new HashMap<URL, DocumentMetaInformation>();

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

	private JMenu menuPrepTools;

	@SuppressWarnings("serial")
	private abstract class AbstractVFormAction extends AbstractAction {
		@Override
		public void actionPerformed(final ActionEvent arg0) {
			final WSEditor editorAccess = pluginWorkspaceAccess
					.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
			final WSTextEditorPage aWSTextEditorPage;
			if ((aWSTextEditorPage = getPage(editorAccess)) != null) {
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
				final WSTextEditorPage aWSTextEditorPage,
				final Document document, final String text)
				throws BadLocationException;

	}

	// TODO: enable/disable toolbar buttons
	// TODO: navigate on the dom not the plain text!
	@SuppressWarnings("serial")
	private class VFormStartAction extends AbstractVFormAction {
		@Override
		protected void doSomething(final WSEditor editorAccess,
				final WSTextEditorPage aWSTextEditorPage,
				final Document document, final String text)
				throws BadLocationException {

			final VFormUtil.Match match = VFormUtil.find(text, 0);
			aWSTextEditorPage.select(match.startOffset, match.endOffset);
		}
	};

	// Accept should only be enabled
	// 1.) when there's selected text in the editor
	// 2.) the selected text conforms to a vform.
	@SuppressWarnings("serial")
	private class VFormAcceptAction extends AbstractVFormAction {
		@Override
		protected void doSomething(final WSEditor editorAccess,
				final WSTextEditorPage aWSTextEditorPage,
				final Document document, final String text)
				throws BadLocationException {

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

			final String newText = document.getText(0, document.getLength());
			final VFormUtil.Match match = VFormUtil.find(newText, continueAt);
			aWSTextEditorPage.select(match.startOffset, match.endOffset);
		}
	};

	@SuppressWarnings("serial")
	private class VFormFindAction extends AbstractVFormAction {
		@Override
		protected void doSomething(final WSEditor editorAccess,
				final WSTextEditorPage aWSTextEditorPage,
				final Document document, final String text)
				throws BadLocationException {

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

			final Action VFormFindAction = new VFormFindAction();

			final Action vformAcceptAction = new VFormAcceptAction();

			menuPrepTools = createPrepToolsMenu(vformStartAction,
					VFormFindAction, vformAcceptAction);

			pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
				/**
				 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
				 */
				@Override
				public void customizeMainMenu(JMenuBar mainMenuBar) {
					// PrepTools menu
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
							// pluginWorkspaceAccess.showToolbar("Edit");
							// TODO: handle the situation that user opened
							// editor
							// with a different Editor than "text" (i.e. "grid"
							// or "author") -> So we not only check it here
							// But also in "editorSelected" and
							// "editorPageChanged"
							final WSEditor editorAccess = pluginWorkspaceAccess
									.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
							final WSTextEditorPage page = getPage(editorAccess);
							if (page == null) {
								final boolean isText = EditorPageConstants.PAGE_TEXT
										.equals(editorAccess.getCurrentPageID());
								showMessage("document could not be accessed "
										+ (isText ? "(unknown reason)"
												: "(current page is not Text but "
														+ editorAccess
																.getCurrentPageID()
														+ ")"));
								return;
							}
							final DocumentMetaInformation documentMetaInformation = new DocumentMetaInformation();
							documentMetaInformation.page = page;

							try {
								documents.put(editorLocation,
										documentMetaInformation);
								documentMetaInformation.isDtBook = page
										.getDocument()
										.getText(0,
												page.getDocument().getLength())
										.indexOf("<dtbook") != -1;
								showMessage("new doc dtbook:"
										+ documentMetaInformation.isDtBook);

							} catch (BadLocationException e) {
								showMessage("failed to get text for opened document:"
										+ editorLocation + " " + e);
								e.printStackTrace();
							}
						};

						private void checkActionsStatus(URL editorLocation) {
							WSEditor editorAccess = pluginWorkspaceAccess
									.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);

							vformStartAction.setEnabled(true);
							vformAcceptAction.setEnabled(false);
							VFormFindAction.setEnabled(false);
							final boolean text_editor = editorAccess != null
									&& EditorPageConstants.PAGE_TEXT
											.equals(editorAccess
													.getCurrentPageID());

							// checkActionStatus should be called as soon
							// as selection changes. There's no support
							// for a "SelectionChangedListener".
							vformAcceptAction.setEnabled(true);
							VFormFindAction.setEnabled(true);
						}

						@Override
						public void editorClosed(URL editorLocation) {
							documents.remove(editorLocation);
						};

						@Override
						public void editorPageChanged(URL editorLocation) {
							// This only gets called when user changes
							// editor between "Text", "Grid" and "Author"
							WSEditor editorAccess = pluginWorkspaceAccess
									.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);

							final boolean isText = EditorPageConstants.PAGE_TEXT
									.equals(editorAccess.getCurrentPageID());
							// TODO: disable PrepTools
							final int count = menuPrepTools
									.getMenuComponentCount();
							showMessage("menuEntries:" + count);
							for (int i = 0; i < count; ++i) {
								Component c = menuPrepTools.getMenuComponent(i);
								showMessage("menuEntry:" + i + " " + c + " "
										+ isText);
								// showMessage("" + c.isEnabled());
								c.setEnabled(isText);
								// showMessage("" + c.isEnabled());
							}
							if (!isText) {
								showMessage("PrepTools only available in text mode, but we're in "
										+ editorAccess.getCurrentPageID());
							}

							// FIXME: remove! Just for testing show/hide
							// toolbar!
							if (editorAccess.getCurrentPageID().equals(
									EditorPageConstants.PAGE_GRID)) {
								pluginWorkspaceAccess
										.showToolbar(ToolbarComponentsCustomizer.CUSTOM);
							}

							checkActionsStatus(editorLocation);
							// showMessage("editorPageChanged: " +
							// editorLocation);
						};

						@Override
						public void editorSelected(URL editorLocation) {
							checkActionsStatus(editorLocation);
							showMessage("editorSelected: " + editorLocation);
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
								// // VForm Start
								JButton vFormButton = new JButton(
										vformStartAction);
								vFormButton.setText("VForms");

								// VForm Find
								JButton findButton = new JButton(
										VFormFindAction);
								findButton.setText("Find");

								// VForm Accept
								JButton acceptButton = new JButton(
										vformAcceptAction);
								acceptButton.setText("Accept");

								// Add in toolbar
								List<JComponent> comps = new ArrayList<JComponent>();
								// comps.add(vFormButton);
								comps.add(findButton);
								comps.add(acceptButton);
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
							}
							else if ("Project".equals(viewInfo.getViewID())) {
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
		}
		else {
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
			final Action vformAcceptAction, final Action VFormFindAction) {
		// PrepTools menu
		JMenu menuPrepTools = new JMenu("PrepTools");

		// Add Check In action on the menu
		// final JMenuItem checkInItem = new JMenuItem(vformStartAction);
		// checkInItem.setText("VForms");
		// menuPrepTools.add(checkInItem);

		// Add Show Section Source action on the menu
		final JMenuItem vformFindItem = new JMenuItem(VFormFindAction);
		vformFindItem.setText("Find");
		vformFindItem.setEnabled(false);
		menuPrepTools.add(vformFindItem);

		// Add Check Out action on the menu
		final JMenuItem vformAcceptItem = new JMenuItem(vformAcceptAction);
		vformAcceptItem.setText("Accept");
		vformAcceptItem.setEnabled(false);
		menuPrepTools.add(vformAcceptItem);

		return menuPrepTools;
	}

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
	 */
	@Override
	public boolean applicationClosing() {
		if (runPlugin) {
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
