package ch.sbs.utils.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Allows to wrap Actions in a given MenuBar, surrounding by optional pre- and
 * post-operations, optionally vetoing actions.
 * 
 * This utility is used in stead of
 * http://www.oxygenxml.com/doc/ug-editor/concepts
 * /components-validation-plugin.html
 * 
 * which only allows *filtering* of GUI-components.
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
	 * theLabels is an array of strings to support multi-language.
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
	 * theLabels is an array of strings to support multi-language.
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
