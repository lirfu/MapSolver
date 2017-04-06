package com.lirfu.mapsolver;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import com.lirfu.mapsolver.graphics.Drone;
import com.lirfu.mapsolver.graphics.Drone.Direction;
import com.lirfu.mapsolver.graphics.LabiryntMap;
import com.lirfu.mapsolver.graphics.MyTimer;

public class GamePanel extends JPanel {
	private final JPanel context = this;
	private boolean keepRefreshingFlag;
	private Drone drone;
	private LabiryntMap map;
	private Thread MapThread;
	private MapTool currentTool = MapTool.NONE;
	private MyTimer timer = null;
	private boolean showMapPaths = false;
	
	private int[] mouseCursor = null;
	private final int initAmount = 2;
	private int initNum;
	/** Determines the width of the brush for map. It's also used by drone for determining the amount of pixels per step. */
	private int brushSize = 50;
	
	public GamePanel() {
		init(true);
	}
	
	private void init(boolean newMap) {
		this.keepRefreshingFlag = true;
		this.initNum = initAmount;
		if (newMap)
			this.map = new LabiryntMap(Storage.getNextID());
		else
			this.map.clearCover();
//		drone = new Drone(this, (int) ((start.x + 0.5) * brushSize), (int) ((start.y + 0.5) * brushSize), brushSize / 3,
//				brushSize, 0xFF444455);
		this.timer = new MyTimer(new int[] { 10, 10 });
		
		setFocusable(true);
		requestFocus();
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
//				System.out.println("Pressed key: " + e.getKeyChar());
				if (e.isControlDown()) {
					switch (e.getKeyChar()) {
						case 'z':
						case 'Z':
							System.out.println("Bloody Ctrl+z");
							setMapInstance(Storage.loadCurrentMap());
							break;
					}
					return;
				}
				
				Direction dir = Direction.FRONT;
				switch (e.getKeyChar()) {
					case 'w':
						dir = Direction.FRONT;
						break;
					case 'a':
						dir = Direction.LEFT;
						break;
					case 's':
						dir = Direction.BACK;
						break;
					case 'd':
						dir = Direction.RIGHT;
						break;
				}
				drone.move(dir, false);
				repaint();
			}
		});
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				mouseCursor = null;
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				mouseDraw(arg0);
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseDraw(e);
				mouseCursor = new int[] { e.getX() / brushSize, e.getY() / brushSize };
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseCursor = new int[] { e.getX() / brushSize, e.getY() / brushSize };
				repaint();
			}
		});
		
		MapThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Map thread started!");
				while (keepRefreshingFlag) {
					try {
						synchronized (context) {
							context.repaint();
						}
						MapThread.sleep(100);
					} catch (InterruptedException e) {
						System.out.println("Map thread killed!");
						return;
					}
				}
				keepRefreshingFlag = true;
				System.out.println("Map thread killed!");
			}
		});
		MapThread.setDaemon(true);
	}
	
	private void mouseDraw(MouseEvent arg0) {
		int x = arg0.getX() / brushSize, y = arg0.getY() / brushSize;
		if (map.isOutBounds(x, y))
			return;
		System.out.println("Mouse: (" + x + "," + y + ")");
		switch (currentTool) {
			case WALL:
				map.drawMap(x, y, true);
				context.repaint();
				return;
			case PATH:
				map.drawMap(x, y, false);
				context.repaint();
				return;
			case START:
				map.setStart(x, y);
				context.repaint();
				return;
			case FINISH:
				map.setFinish(x, y);
				context.repaint();
				return;
			default:
				return;
		}
	}
	
	public synchronized void startSolving() {
		if (MapThread.isAlive())
			this.notify(); // wake mapThread
		else
			MapThread.start(); // start it
		drone.start();
		
		timer.reset();
		timer.start();
	}
	
	public synchronized void destroy() {
		try {
			MapThread.wait();
		} catch (InterruptedException | IllegalMonitorStateException e1) {
			System.out.println("Couldn't await map thread! Exception: " + e1 + " State: " + MapThread.getState());
			keepRefreshingFlag = false;
		}
		try {
			drone.exitThread();
			drone.join();
		} catch (InterruptedException e) {
			System.out.println("Drone joining interrupted!");
		}
		timer.stop();
		
		context.repaint();
	}
	
	public synchronized void reInit() {
		init(false);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (initNum > 0) {
			brushSize = Math.min(getWidth(), getHeight()) / Math.max(map.getSizeX(), map.getSizeY());
			System.out.println("Width: " + getWidth() + " Height: " + getHeight());
//			brushSize = Math.min(getWidth(), getHeight()) / Math.max(map.getSizeX(), map.getSizeY());
			drone = new Drone(this.map, brushSize, brushSize / 3, 10, 0xFF444455);
			initNum--;
		}
		
		map.paintGraphic(g, brushSize, !drone.isRunning() || showMapPaths);
		drone.paintGraphic(g, brushSize);
		
		if (drone.isFinished())
			timer.stop();
//			new WIN().paintGraphic(g, new int[] { getWidth(), getHeight() });
		
		g.setColor(new Color(0xFFFFFFFF));
		if (mouseCursor != null) {
			g.drawRect(mouseCursor[0] * brushSize, mouseCursor[1] * brushSize, brushSize, brushSize);
		}
		
		try {
			timer.paintGraphic(g);
		} catch (NullPointerException e) {
		}
	}
	
	public void setTool(MapTool tool) {
		this.currentTool = tool;
	}
	
	public LabiryntMap getMapInstance() {
		return this.map;
	}
	
	public void setMapInstance(LabiryntMap map) {
		this.map = map;
		reInit();
		repaint();
	}
	
	/** Choose weather to draw paths even while the drone is searching. */
	public void showPaths(boolean show) {
		this.showMapPaths = show;
	}
	
	public int getBrushSize() {
		return this.brushSize;
	}
	
	public synchronized void setBrushSize(int size) {
		this.brushSize = size;
	}
	
	public static enum MapTool {
		NONE, WALL, PATH, START, FINISH
	}
}
