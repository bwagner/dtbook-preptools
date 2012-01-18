package ch.sbs.plugin.preptools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

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
