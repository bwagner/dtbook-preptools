package ch.sbs.utils.preptools.vform;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

// http://www.dreamincode.net/code/snippet1287.htm

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
