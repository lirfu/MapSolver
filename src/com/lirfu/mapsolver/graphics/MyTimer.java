package com.lirfu.mapsolver.graphics;

import java.awt.Color;
import java.awt.Graphics;

public class MyTimer {
	private final int x, y;
	private long lastTime;
	private boolean start;
	private long lastDelta;
	
	/**
	 * @param position Coordinates of top-left corner.
	 */
	public MyTimer(int[] position) {
		this.lastTime = System.currentTimeMillis();
		this.x = position[0];
		this.y = position[1];
		this.start = false;
		this.lastDelta = 0;
	}
	
	public void paintGraphic(Graphics g) {
		long time = lastDelta;
		if (start)
			time = System.currentTimeMillis() - lastTime;
		lastDelta = time;
		int minute = (int) ((time / (1000 * 60)) % 60);
		int second = (int) (time / 1000) % 60;
		int other = (int) (time - second * 1000 - minute * 60000) / 10;
		g.setColor(new Color(0xFFFFFFFF));
		g.drawString("Time: " + pad(minute) + ":" + pad(second) + "." + pad(other), x, y);
	}
	
	public void start() {
		reset();
		this.start = true;
	}
	
	public void stop() {
		this.start = false;
	}
	
	public void reset() {
		this.lastTime = System.currentTimeMillis();
		this.lastDelta = 0;
		this.start = false;
	}
	
	private String pad(int value) {
		if (value < 10)
			return "0" + value;
		return "" + value;
	}
}
