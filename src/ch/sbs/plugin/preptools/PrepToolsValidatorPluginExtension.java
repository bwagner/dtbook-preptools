package ch.sbs.plugin.preptools;

import ro.sync.exml.ComponentsValidator;
import ro.sync.exml.editor.EditorTemplate;
import ro.sync.exml.plugin.startup.ComponentsValidatorPluginExtension;

public class PrepToolsValidatorPluginExtension implements
		ComponentsValidatorPluginExtension {

	@Override
	public ComponentsValidator getComponentsValidator() {
		return new ComponentsValidator() {

			@Override
			public boolean validateToolbarTaggedAction(String[] toolbarOrAction) {
				return true;
			}

			@Override
			public boolean validateSHMarker(String marker) {
				return true;
			}

			@Override
			public boolean validateOptionPane(String optionPaneKey) {
				return true;
			}

			@Override
			public boolean validateOption(String optionKey) {
				return true;
			}

			@Override
			public boolean validateNewEditorTemplate(
					EditorTemplate editorTemplate) {
				return true;
			}

			@Override
			public boolean validateMenuOrTaggedAction(String[] menuOrActionPath) {
				return true;
			}

			@Override
			public boolean validateLibrary(String library) {
				return true;
			}

			@Override
			public boolean validateContentType(String contentType) {
				return true;
			}

			@Override
			public boolean validateComponent(String key) {
				return true;// ToolbarComponentsCustomizer.CUSTOM.equals(key);
			}

			@Override
			public boolean validateAccelAction(String category, String tag) {
				return true;
			}

			@Override
			public boolean isDebuggerPerspectiveAllowed() {
				return true;
			}
		};
	}

}
