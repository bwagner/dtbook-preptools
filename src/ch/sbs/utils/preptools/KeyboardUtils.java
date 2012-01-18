package ch.sbs.utils.preptools;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

// http://www.dreamincode.net/code/snippet1287.htm

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

public class KeyboardUtils {
	public static boolean all_keys_on() {
		return numlock(true) && capslock(true) && scrolllock(true);
	}

	public static boolean all_keys_off() {
		return numlock(false) && capslock(false) && scrolllock(false);
	}

	public static boolean numlock(boolean b) {
		final Toolkit tool = Toolkit.getDefaultToolkit();
		try {
			tool.setLockingKeyState(KeyEvent.VK_NUM_LOCK, b);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean capslock(boolean b) {
		final Toolkit tool = Toolkit.getDefaultToolkit();
		try {
			tool.setLockingKeyState(KeyEvent.VK_CAPS_LOCK, b);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean scrolllock(boolean b) {
		final Toolkit tool = Toolkit.getDefaultToolkit();
		try {
			tool.setLockingKeyState(KeyEvent.VK_SCROLL_LOCK, b);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void main(final String[] args) {
		System.out.println(numlock(args.length > 0) ? "worked." : "didn't.");
	}
}
