
			// ATTENTION: generated file. Do not edit it, edit the build.xml instead.
			package ch.sbs.plugin.preptools;

			import ro.sync.exml.plugin.Plugin;
			import ro.sync.exml.plugin.PluginDescriptor;

			public class WorkspaceAccessPlugin extends Plugin {

				private static WorkspaceAccessPlugin instance = null;

				public WorkspaceAccessPlugin(PluginDescriptor descriptor) {
					super(descriptor);

					if (instance != null) {
						throw new IllegalStateException("Already instantiated!");
					}
					instance = this;
				}

				public static WorkspaceAccessPlugin getInstance() {
					return instance;
				}
			}
		