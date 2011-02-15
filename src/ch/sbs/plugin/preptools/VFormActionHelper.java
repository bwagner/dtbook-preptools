package ch.sbs.plugin.preptools;

import java.util.regex.Pattern;

/**
 * Helper class to factor out common code in VFormActions.
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

	public String getProcessName() {
		return VFormPrepTool.LABEL;
	}

	/**
	 * Utility method to get tool specific metainfo.
	 * Covariant return type. (VFormPrepTool.MetaInfo is a subclass of
	 * DocumentMetaInfo.MetaInfo)
	 * 
	 * @return tool specific metainfo.
	 */
	protected final VFormPrepTool.MetaInfo getMetaInfo() {
		return (VFormPrepTool.MetaInfo) prepToolsPluginExtension
				.getDocumentMetaInfo().getToolSpecificMetaInfo(
						VFormPrepTool.LABEL);
	}
}