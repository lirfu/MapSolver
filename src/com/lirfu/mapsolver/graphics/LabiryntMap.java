package com.lirfu.mapsolver.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class LabiryntMap {
	private final int ID;
	private int sizex = 30, sizey = 30;
	/** Coordinates are [x][y]. <code>true</code> for wall, <code>false</code> for path. */
	private boolean[][] bitmap = new boolean[sizex][sizey];
	private final int[] coverLevels = new int[] { 0x333333, 0x555555, 0x777777, 0x999999, 0xBBBBBB, 0xDDDDDD };
	private int[][] mapCover;
	private Point start = new Point(1, 5), end = new Point(5, 5);
	
	public LabiryntMap(int ID) {
		this.ID = ID;
		// Init cover.
		mapCover = new int[sizex][sizey];
		// Default starting map.
		boolean[][] StartMap = new boolean[][] { { true, true, true, true, true, true, true },
				{ true, false, false, false, true, false, true }, { true, false, true, false, true, false, true },
				{ true, false, true, false, false, false, true }, { true, false, true, true, true, true, true },
				{ true, false, false, false, false, false, true }, { true, false, true, true, true, false, true },
				{ true, false, false, false, false, false, true }, { true, true, true, true, true, true, true } };
		// Set default map, if out of bounds draw walls.
		for (int i = 0; i < sizex; i++) {
			for (int j = 0; j < sizey; j++) {
				this.bitmap[i][j] = true;
				try {
					this.bitmap[i][j] = StartMap[i][j];
				} catch (IndexOutOfBoundsException e) {
				}
			}
		}
	}
	
	public LabiryntMap(int ID, int sizex, int sizey, Point start, Point finish, boolean[][] map) {
		this.ID = ID;
		this.sizex = sizex;
		this.sizey = sizey;
		this.start = start;
		this.end = finish;
		this.bitmap = map;
		
		// Init cover.
		mapCover = new int[sizex][sizey];
	}
	
	public void paintGraphic(Graphics g, int brushSize, boolean showPath) {
//		System.out.println("Map brush: " + brushSize);
		// print map
		for (int i = 0; i < this.sizex; i++) {
			for (int j = 0; j < this.sizey; j++) {
				if (i == 0 || j == 0 || i == this.sizex - 1 || j == this.sizey - 1) { // Frame
					g.setColor(new Color(0xFF805F2B));
					g.fillRect(i * brushSize, j * brushSize, brushSize, brushSize);
				} else if (this.bitmap[i][j] || !(this.bitmap[i][j] || showPath)) {
					g.setColor(new Color(0xFF000000));
					g.fillRect(i * brushSize, j * brushSize, brushSize, brushSize);
				}
			}
		}
		
		// print cover
		for (int i = 1; i < this.sizex - 1; i++) {
			for (int j = 1; j < this.sizey - 1; j++) {
				if ((mapCover[i][j] == 0 && showPath || mapCover[i][j] != 0) && !bitmap[i][j]) {
//					g.setColor(new Color(this.coverLevels[mapCover[i][j]]));
					int beenTimes;
					if (mapCover[i][j] < this.coverLevels.length)
						beenTimes = this.coverLevels[mapCover[i][j]];
					else
						beenTimes = this.coverLevels[this.coverLevels.length - 1];
					g.setColor(new Color(beenTimes));
					g.fillRect(i * brushSize, j * brushSize, brushSize, brushSize);
				}
			}
		}
		// print start and finish
		g.setColor(new Color(0xFFFF0000));
		g.fillRect(start.x * brushSize, start.y * brushSize, brushSize, brushSize);
		g.setColor(new Color(0xFF00FF00));
		if (showPath)
			g.fillRect(end.x * brushSize, end.y * brushSize, brushSize, brushSize);
	}
	
	public void drawMap(int x, int y, boolean value) {
		this.bitmap[x][y] = value;
		this.bitmap[start.x][start.y] = false;
		this.bitmap[end.x][end.y] = false;
	}
	
	public boolean isOutBounds(int x, int y) {
		return x <= 0 || y <= 0 || x >= sizex - 1 || y >= sizey - 1;
	}
	
	/** Clears the cover of visited area. */
	public void clearCover() {
		mapCover = new int[sizex][sizey];
	}
	
	public void clearMap() {
		for (int x = 0; x < sizex; x++)
			for (int y = 0; y < sizey; y++) {
				this.bitmap[x][y] = true;
			}
		this.bitmap[this.end.x][this.end.y] = false;
		this.bitmap[this.start.x][this.start.y] = false;
	}
	
	//////// Getters and Setters
	public boolean getMapAt(int x, int y) {
		return this.bitmap[x][y];
	}
	
	public int getCoverAt(int x, int y) {
		return this.mapCover[x][y];
	}
	
	public void setCoverAt(int x, int y, boolean value) {
		if (value)
			this.mapCover[x][y]++;
	}
	
	public Point getStart() {
		return new Point(this.start);
	}
	
	public void setStart(int x, int y) {
		this.bitmap[this.start.x][this.start.y] = true;
		this.start = new Point(x, y);
		this.bitmap[this.start.x][this.start.y] = false;
	}
	
	public Point getFinish() {
		return new Point(this.end);
	}
	
	public void setFinish(int x, int y) {
		this.bitmap[this.end.x][this.end.y] = true;
		this.end = new Point(x, y);
		this.bitmap[this.end.x][this.end.y] = false;
	}
	
	public int getSizeX() {
		return this.sizex;
	}
	
	public int getSizeY() {
		return this.sizey;
	}
	
	public int getID() {
		return this.ID;
	}
}
