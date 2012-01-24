package ch.sbs.plugin.preptools;

import java.util.regex.Pattern;

/**
 * Helper class to factor out common code in VFormActions.
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

public class VFormActionHelper {

	public static final String VFORM_TAG = "brl:v-form";
	private final PrepToolsPluginExtension prepToolsPluginExtension;

	VFormActionHelper(final PrepToolsPluginExtension thePrepToolsPluginExtension) {
		prepToolsPluginExtension = thePrepToolsPluginExtension;
	}

	public Pattern getPattern() {
		return getMetaInfo().getCurrentPattern();
	}

	public String getPrepToolName() {
		return VFormPrepTool.PREPTOOL_NAME;
	}

	/**
	 * Utility method to get tool specific metainfo.
	 * 
	 * @return tool specific metainfo.
	 */
	private final VFormPrepTool.MetaInfo getMetaInfo() {
		return (VFormPrepTool.MetaInfo) prepToolsPluginExtension
				.getDocumentMetaInfo().getToolSpecificMetaInfo(
						VFormPrepTool.PREPTOOL_NAME);
	}
}
