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

	static final int DEFAULT_SIZE = 100;
	final int size;

	// TODO: use this for tooltip
	// Tooltip contents: build stamp version
	private String info;

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

	public void off() {
		setCurrentLightState(STATE.OFF);
	}

	public void stop() {
		setCurrentLightState(STATE.RED);
	}

	public void go() {
		setCurrentLightState(STATE.GREEN);
	}

	public void inProgress() {
		setCurrentLightState(STATE.YELLOW);
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

	public void setInfo(final String theInfo) {
		info = theInfo;
	}

	public String getInfo() {
		return info;
	}
}
