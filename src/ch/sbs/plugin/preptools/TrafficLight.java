package ch.sbs.plugin.preptools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class TrafficLight extends JComponent {

	public enum STATE {
		RED, YELLOW, GREEN, BLUE, OFF
	};

	private static final Map<STATE, Color> map = new HashMap<STATE, Color>();

	static {
		map.put(STATE.RED, Color.red);
		map.put(STATE.YELLOW, Color.yellow);
		map.put(STATE.GREEN, Color.green);
		map.put(STATE.BLUE, Color.blue);
		map.put(STATE.OFF, Color.gray);
	}

	private STATE defaultLightState;
	private STATE currentLightState;

	private static int DELAY = 500;
	private Thread runner;

	static final int DEFAULT_SIZE = 100;
	final int size;

	public TrafficLight() {
		this(DEFAULT_SIZE);
	}

	public TrafficLight(int theSize) {
		size = theSize;
		defaultLightState = currentLightState = STATE.RED;
		setPreferredSize(new Dimension(size, size));
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		paintOutline(g);
		paintLight(g);
	}

	private void paintLight(final Graphics g) {
		g.setColor(map.get(currentLightState));
		final int origin = size / 6;
		final int radius = size - 2 * origin;
		g.fillOval(origin, origin, radius, radius);
	}

	private void paintOutline(final Graphics g) {
		g.setColor(Color.white);
		g.fillRect(2, 2, size - 2, size - 2);
	}

	public STATE getCurrentLightState() {
		return currentLightState;
	}

	public void setCurrentLightState(final STATE currentLightState) {
		this.currentLightState = currentLightState;
		repaint();
	}

	public void turnOff() {
		setCurrentLightState(STATE.OFF);
	}

	public void sayStop() {
		setCurrentLightState(STATE.RED);
	}

	public void done() {
		setCurrentLightState(STATE.BLUE);
	}

	public void setDefaultLightState(final STATE theDefaultLightState) {
		defaultLightState = theDefaultLightState;
		currentLightState = defaultLightState;
		repaint();
	}

	public STATE getDefaultLightState() {
		return defaultLightState;
	}

	public void initiate() {
		startCycle();
	}

	private void startCycle() {
		if (runner == null) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					while (runner != null) {
						try {
							Thread.sleep(DELAY);
						} catch (InterruptedException e) {
						}
						if (currentLightState.equals(STATE.RED)) {
							setCurrentLightState(STATE.GREEN);
						}
						else if (currentLightState.equals(STATE.GREEN)) {
							setCurrentLightState(STATE.YELLOW);
						}
						else {
							setCurrentLightState(STATE.RED);
						}
						if (currentLightState.equals(defaultLightState)) {
							runner = null;
						}
					}
				}
			};
			runner = new Thread(runnable);
			runner.start();
		}
	}
}
