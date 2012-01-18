package ch.sbs.plugin.preptools;

import ro.sync.exml.ComponentsValidator;
import ro.sync.exml.editor.EditorTemplate;
import ro.sync.exml.plugin.startup.ComponentsValidatorPluginExtension;

/**
 * Default implementation. We're not using it, because it only offers filtering
 * GUI-components, while we want to keep the components but wrap them in other
 * operations (@see ch.sbs.utils.swing.MenuPlugger)
 */
/**
	* Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
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
