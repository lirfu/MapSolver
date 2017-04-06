package com.lirfu.mapsolver.graphics;

import java.awt.Color;
import java.awt.Graphics;

public class WIN {
	private final int width = 13, height = 4;
	private final boolean[][] graphic = new boolean[][] { { true, true, false, false }, { false, false, true, true },
			{ false, true, true, false }, { false, false, true, true }, { true, true, false, false },
			{ false, false, false, false }, { true, true, true, true }, { false, false, false, false },
			{ true, true, true, true }, { true, false, false, false }, { false, true, true, false },
			{ false, false, false, true }, { true, true, true, true } };
			
	public void paintGraphic(Graphics g, int[] size) {
		g.setColor(new Color(0xFF55AAFF));
		float operatorx = size[0] / width;
		float operatory = size[1] / height;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (graphic[x][y])
					g.fillRect((int) (x * operatorx), (int) (y * operatory), (int) (operatorx), (int) (operatory));
			}
		}
	}
}
