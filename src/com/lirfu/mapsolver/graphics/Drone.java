package com.lirfu.mapsolver.graphics;

import java.awt.Color;
import java.awt.Graphics;

/**
 * A basic single colored circle.
 * @author lirfu
 */
public class Drone extends Thread {
	private final LabiryntMap map;
	/** Position of the center. */
	public final int[] position = new int[2];
	private int brushSize;
	private final int diameter;
	private final int color;
	private double directionRad;
	/** Amount of pixels per step. */
	private int moveAmount;
	private boolean exitThread;
	private boolean finished;
	private boolean running;
	
	private final int rotateAmountDeg = 90;
	private final double rotateAmount = Math.toRadians(rotateAmountDeg);
	private final int[] nodesSize;
	private final boolean[][] nodes;
	private final int delay = 100;
	
	/**
	 * @param map Instance of the map.
	 * @param x - coordinate of center.
	 * @param y - coordinate of center.
	 * @param brushSize Size of a single map pixel.
	 * @param radius Radius of the circle.
	 * @param moveAmount Amount of pixels per step.
	 * @param color Color of the whole circle.
	 */
	public Drone(LabiryntMap map, int brushSize, int radius, int moveAmount, int color) {
		this.map = map;
		this.diameter = 2 * radius;
		this.position[0] = (int) ((map.getStart().x + 0.5) * brushSize);
		this.position[1] = (int) ((map.getStart().y + 0.5) * brushSize);
		this.brushSize = brushSize;
//		this.moveAmount = 5;
		this.moveAmount = moveAmount;
		this.color = color;
		
		this.directionRad = Math.toRadians(90); // Starts looking North.
		this.nodesSize = new int[] { map.getSizeX(), map.getSizeY() };
		this.nodes = new boolean[this.nodesSize[0]][this.nodesSize[1]];
		
		this.running = false;
		this.finished = false;
		this.exitThread = false;
		super.setDaemon(true);
		
//		System.out.println("Drone at: " + this.position[0] + ":" + this.position[1] + ", brush size: " + brushSize);
	}
	
	public void paintGraphic(Graphics g, int brushSize) {
		this.brushSize = brushSize;
		this.moveAmount = brushSize; // FIXME Too small value results in loops.
		if (!running) {
			this.position[0] = (int) ((map.getStart().x + 0.5) * brushSize);
			this.position[1] = (int) ((map.getStart().y + 0.5) * brushSize);
		}
		
		g.setColor(Color.decode(String.valueOf(color)));
		g.fillOval(this.position[0] - this.diameter / 2, this.position[1] - this.diameter / 2, this.diameter,
				this.diameter);
		g.setColor(new Color(0xFF2EFEF7));
		g.drawOval(this.position[0] - this.diameter / 2, this.position[1] - this.diameter / 2, this.diameter,
				this.diameter);
				
		g.setColor(new Color(0xFFDBA901));
		g.drawLine(this.position[0], this.position[1],
				this.position[0] + (int) ((directionVector(directionRad)[0]) * diameter / 2),
				(int) (this.position[1] + (directionVector(directionRad)[1]) * diameter / 2));
				
		// Draw nodes
		g.setColor(new Color(0xDD2222));
		for (int x = 0; x < this.nodesSize[0]; x++) {
			for (int y = 0; y < this.nodesSize[1]; y++) {
				if (this.nodes[x][y])
					g.drawRect(x * brushSize, y * brushSize, brushSize, brushSize);
			}
		}
		
		g.drawOval(position[0] - 1, position[1] - 1, 2, 2);
	}
	
	/**
	 * Attempt moving in given direction. Rotation moves do not change the position.
	 * @return true - move if plausible and done, false - can't move in that direction (for translation moves only)
	 */
	public boolean move(Direction direction, boolean simulate) {
		int[] originalPos = new int[2];
		originalPos[0] = this.position[0];
		originalPos[1] = this.position[1];
		
		switch (direction) {
			case LEFT:
				this.directionRad -= rotateAmount;
				if (this.directionRad < 0)
					this.directionRad = this.directionRad % (2 * Math.PI);
				return true;
			case RIGHT:
				this.directionRad += rotateAmount;
				if (this.directionRad > (2 * Math.PI))
					this.directionRad = this.directionRad % (2 * Math.PI);
				return true;
			case FRONT:
				this.position[0] += directionVector(directionRad)[0] * moveAmount;
				this.position[1] += directionVector(directionRad)[1] * moveAmount;
				break;
			case BACK:
				this.position[0] -= directionVector(directionRad)[0] * moveAmount;
				this.position[1] -= directionVector(directionRad)[1] * moveAmount;
				break;
			default:
				return false;
		}
		if (!testThisSite()) {
//			System.out.println("Cannot move! [" + this.position[0] + "," + this.position[1] + "]");
			this.nodes[this.position[0] / brushSize][this.position[1] / brushSize] = false; // Correct your nodes database.
			
			this.position[0] = originalPos[0];
			this.position[1] = originalPos[1];
			return false;
		}
		if (simulate) {
			this.position[0] = originalPos[0];
			this.position[1] = originalPos[1];
			return true;
		}
//		System.out.println("Moving drone: " + moveAmount + "px under " + Math.toDegrees(this.directionRad) + "°");
		return true;
	}
	
	private float[] directionVector(double direction) {
		return new float[] { (float) (-1 * Math.cos(direction)), (float) (-1 * (Math.sin(direction))) };
	}
	
	/** Checks if this move is possible. */
	private boolean testThisSite() {
		try {
			return !map.getMapAt(position[0] / brushSize, position[1] / brushSize);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("tryMoving: Index out of bounds!");
			return false;
		}
	}
	
	/**
	 * Checks if this is a node position (looks around itself).
	 * @return Array of values from 0°-(360-rotate amount)°.
	 */
	private boolean[] checkNode() {
		boolean[] check = new boolean[360 / rotateAmountDeg];
		for (int i = 0; i < check.length; i++) {
			if (move(Direction.FRONT, true))
				check[i] = true;
			move(Direction.LEFT, true);
		}
		return check;
	}
	
	/** Checks how many times has this position been visited. */
	private int checkIfVisitedHere() {
		return map.getCoverAt(position[0] / brushSize, position[1] / brushSize);
	}
	
	@Override
	public void run() {
		super.run();
		System.out.println("Drone started!");
		this.running = true;
		while (!onFinish(this.position) && !exitThread) {
			// Identify a node.
			boolean[] passages = checkNode();
			int numPassages = 0;
			for (int i = 0; i < passages.length; i++) {
				if (passages[i])
					numPassages++;
			}
			this.nodes[this.position[0] / brushSize][this.position[1] / brushSize] = numPassages > 2;
			
			synchronized (map) {
				// IT'S A NOOODEE!!!
				if (numPassages > 2)
					myWayMinimum();
				else
					descideOnTunnel();
					
				// Inform that it visited this site.
				map.setCoverAt(position[0] / brushSize, position[1] / brushSize, true);
			}
			try {
				sleep(delay);
			} catch (InterruptedException e) {
				System.out.println("Drone killed!");
				return;
			}
		}
		if (onFinish(this.position))
			this.finished = true;
		else
			this.running = false;
		this.exitThread = false;
		System.out.println("Drone stopped!");
	}
	
	private void myWayMinimum() {
		int[] check = new int[360 / rotateAmountDeg];
		int min = 100;
		
		// Find minimums.
		for (int i = 0; i < check.length; i++) {
			if (move(Direction.FRONT, false)) { // get times visited
				check[i] = checkIfVisitedHere();
				if (check[i] < min)
					min = check[i];
				move(Direction.BACK, false);
			} else // invalid move
				check[i] = -1;
			move(Direction.LEFT, false);
		}
		System.out.println("Minimum on: " + min);
		// Catch first minimum on the left and go there.
		for (int i = 0; i < check.length; i++) {
			if (check[i] == min) {
				break;
			}
			move(Direction.LEFT, false);
		}
		move(Direction.FRONT, false);
	}
	
	private void descideOnTunnel() {
		// Try going left.
		int dir = rotateAmountDeg;
		move(Direction.LEFT, false);
		while (!move(Direction.FRONT, true) || dir < 180 - rotateAmountDeg) {
			move(Direction.LEFT, false);
			dir += rotateAmountDeg;
		}
		// If it can go left, than go and wait next turn.
		if (dir < 180) {
			move(Direction.FRONT, false);
			return;
		}
		// Return to original rotation.
		while (dir > 0) {
			move(Direction.RIGHT, false);
			dir -= rotateAmountDeg;
		}
		
		// Try going front.
		if (move(Direction.FRONT, true)) {
			move(Direction.FRONT, false);
			return;
		}
		
		// Try going right.
		dir = rotateAmountDeg;
		move(Direction.RIGHT, false);
		while (!move(Direction.FRONT, true) || dir < 180 - rotateAmountDeg) {
			move(Direction.RIGHT, false);
			dir += rotateAmountDeg;
		}
		// If it can go right, than go and wait next turn.
		if (dir < 180) {
			move(Direction.FRONT, false);
			return;
		}
		
		// Otherwise go back the same way.
		move(Direction.RIGHT, false);
		move(Direction.FRONT, false);
	}
	
	public boolean onFinish(int[] position) {
		return position[0] >= map.getFinish().x * brushSize && position[0] < (map.getFinish().x + 1) * brushSize
				&& position[1] >= map.getFinish().y * brushSize && position[1] < (map.getFinish().y + 1) * brushSize;
	}
	
	public static enum Direction {
		LEFT, RIGHT, FRONT, BACK
	}
	
	public double getDirectionRad() {
		return directionRad;
	}
	
	public void setDirectionRad(double directionRad) {
		this.directionRad = directionRad;
	}
	
	public int[] getPosition() {
		return position;
	}
	
	public int getDiameter() {
		return diameter;
	}
	
	public int getColor() {
		return color;
	}
	
	public int getMoveAmount() {
		return moveAmount;
	}
	
	public double getRotateAmount() {
		return rotateAmount;
	}
	
	public boolean isFinished() {
		return this.finished;
	}
	
	public boolean isRunning() {
		return !this.finished && this.running;
	}
	
	/**
	 * Sets flag to end the thread.
	 */
	public void exitThread() {
		this.exitThread = true;
	}
}
