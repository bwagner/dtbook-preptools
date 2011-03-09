package ch.sbs.utils.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuPlugger {

	@SuppressWarnings("serial")
	static class DefaultActionWrapper extends MenuPlugger.ActionWrapper {

		public DefaultActionWrapper(final Action a) {
			super(a);
		}

		public DefaultActionWrapper() {
		}

		@Override
		protected boolean pre(final Action theWrappedAction,
				final ActionEvent theActionEvent) {
			System.out.println("pre:" + theWrappedAction);
			return true;
		}

		@Override
		protected void post(final Action theWrappedAction,
				final ActionEvent theActionEvent) {
			System.out.println("post:" + theWrappedAction);
		}

	}

	@SuppressWarnings("serial")
	public static class ActionWrapper extends AbstractAction {

		private Action wrappedAction;

		public ActionWrapper() {

		}

		public ActionWrapper(final Action theWrappedAction) {
			wrappedAction = theWrappedAction;
		}

		public void setWrappedAction(final Action theWrappedAction) {
			wrappedAction = theWrappedAction;
		}

		@Override
		public void actionPerformed(ActionEvent theActionEvent) {
			if (pre(wrappedAction, theActionEvent)) {
				if (wrappedAction != null) {
					wrappedAction.actionPerformed(theActionEvent);
				}
				post(wrappedAction, theActionEvent);
			}
		}

		/**
		 * Optional hook to be performed before the wrappedAction is.
		 * Possibility to veto the wrappedAction to be performed by returning
		 * false.
		 * 
		 * @param theWrappedAction
		 * @param theActionEvent
		 * @return true if the wrappedAction is to be performed
		 */
		protected boolean pre(final Action theWrappedAction,
				final ActionEvent theActionEvent) {
			return true;
		}

		/**
		 * Optional hook to be performed after the wrappedAction is.
		 * Is only called if pre returned true.
		 * 
		 * @param theWrappedAction
		 * @param theActionEvent
		 */
		protected void post(final Action theWrappedAction,
				final ActionEvent theActionEvent) {

		}
	}

	/**
	 * Navigates all menus in theJMenuBar and tries to find an entry with the
	 * given label. When found, the attached action is wrapped in the given
	 * theActionWrapper.
	 * thLabels is an array of strings to support multi-language.
	 * 
	 * @param theComponents
	 * @param theLabels
	 * @param theActionWrapper
	 * @return true if the JMenuItem with the given label was found
	 */
	public static boolean plug(final Component[] theComponents,
			final String[] theLabels, final ActionWrapper theActionWrapper) {
		for (final Component component : theComponents) {
			if (component instanceof JMenu) {
				if (plug(((JMenu) component).getMenuComponents(), theLabels,
						theActionWrapper)) {
					return true;
				}
			}
			else if (component instanceof JMenuItem) {
				final JMenuItem m = (JMenuItem) component;
				for (final String LABEL : theLabels) {
					if (LABEL.equals(m.getText())) {
						theActionWrapper.setWrappedAction(m.getAction());
						m.setAction(theActionWrapper);
						m.setText(LABEL);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Navigates all menus in theJMenuBar and tries to find an entry with the
	 * given label. When found, the attached action is wrapped in the given
	 * theActionWrapper.
	 * thLabels is an array of strings to support multi-language.
	 * 
	 * @param theJMenuBar
	 * @param theLabels
	 * @param theActionWrapper
	 * @return true if the JMenuItem with the given label was found
	 */
	public static boolean plug(final JMenuBar theJMenuBar,
			final String[] theLabels, final ActionWrapper theActionWrapper) {
		return plug(theJMenuBar.getComponents(), theLabels, theActionWrapper);
	}
}