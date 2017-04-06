package com.lirfu.mapsolver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.lirfu.mapsolver.GamePanel.MapTool;
import com.lirfu.mapsolver.graphics.LabiryntMap;

public class Window extends JFrame {
	private final JFrame context = this;
	private static JTextArea selTool, scoreBoard;
	private GamePanel gamePanel;
	private JSlider size;
	
	public Window() {
		init();
	}
	
	private void init() {
		setTitle("Map Solver");
		setMinimumSize(new Dimension(400, 400));
		setLayout(new BorderLayout());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
//				gamePanel.destroy();
//				Storage.saveMap(gamePanel.getMapInstance());
			}
			
			@Override
			public void windowOpened(WindowEvent e) {
//				LabiryntMap map = Storage.loadCurrentMap();
//				if (map != null)
//					gamePanel.setMapInstance(map);
			}
		});
		
		scoreBoard = new JTextArea();
		scoreBoard.setText("Scores:");
		
		gamePanel = new GamePanel();
//		gamePanel.requestFocus();
		
		add(initTools(), BorderLayout.WEST);
		add(gamePanel, BorderLayout.CENTER);
		add(initBottom(), BorderLayout.SOUTH);
		add(initMenuBar(), BorderLayout.NORTH);
		add(scoreBoard, BorderLayout.EAST);
	}
	
	private JPanel initBottom() {
		JPanel stuff = new JPanel();
		stuff.setLayout(new BoxLayout(stuff, BoxLayout.X_AXIS));
		
		// Shows which tool is selected.
		selTool = new JTextArea();
		selTool.setText("Selected tool: NONE");
		
		size = new JSlider(SwingConstants.HORIZONTAL);
		size.setMinimum(10);
		size.setMaximum(60);
		size.setValue(gamePanel.getBrushSize());
		size.setPaintTicks(true);
		size.setPaintLabels(true);
		size.setMajorTickSpacing(10);
		size.setMinorTickSpacing(1);
//		size.setSnapToTicks(true);
		size.setToolTipText("Map pixel size: " + size.getValue() + "px");
		size.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				gamePanel.setBrushSize(size.getValue());
				gamePanel.repaint();
				size.setToolTipText("Map pixel size: " + size.getValue() + "px");
			}
		});
		
		stuff.add(selTool);
		stuff.add(size);
		return stuff;
	}
	
	private JScrollPane initTools() {
		JPanel tools = new JPanel();
		tools.setLayout(new BoxLayout(tools, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(tools);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
//		ToolButton start = new ToolButton("Start");
//		start.setToolTipText("Start auto-solving the map.");
//		start.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				gamePanel.startSolving();
//			}
//		});
//		ToolButton reset = new ToolButton("Reset");
//		reset.setToolTipText("Stop and reset the map.");
//		reset.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				gamePanel.destroy();
//				gamePanel.reInit();
//			}
//		});
//		ToolButton save = new ToolButton("Save map");
//		save.setToolTipText("Save current map.");
//		save.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				gamePanel.destroy();
//				Storage.saveMap(gamePanel.getMapInstance());
//			}
//		});
//		ToolButton load = new ToolButton("Load map");
//		load.setToolTipText("Load map.");
//		load.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				LabiryntMap map = Storage.loadCurrentMap();
//				if (map != null)
//					gamePanel.setMapInstance(map);
//			}
//		});
		ToolButton clear = new ToolButton("Clear");
		clear.setToolTipText("Clears the whole map.");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.getMapInstance().clearMap();
			}
		});
		
		ToolButton none = new ToolButton("None");
		none.setToolTipText("No tool selected.");
		none.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTool(MapTool.NONE);
				selTool.setText("Selected tool: NONE");
			}
		});
		ToolButton wall = new ToolButton("Wall");
		wall.setToolTipText("Draw walls.");
		wall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTool(MapTool.WALL);
				selTool.setText("Selected tool: WALL");
			}
		});
		ToolButton path = new ToolButton("Path");
		path.setToolTipText("Draw path.");
		path.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTool(MapTool.PATH);
				selTool.setText("Selected tool: PATH");
			}
		});
		ToolButton startPoint = new ToolButton("Start");
		startPoint.setToolTipText("Determine start position.");
		startPoint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTool(MapTool.START);
				selTool.setText("Selected tool: START");
			}
		});
		ToolButton finishPoint = new ToolButton("Finish");
		finishPoint.setToolTipText("Determine finish position.");
		finishPoint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTool(MapTool.FINISH);
				selTool.setText("Selected tool: FINISH");
			}
		});
		
//		tools.add(new MyJTextArea("File"));
//		tools.add(save);
//		tools.add(load);
//		tools.add(new MyJTextArea("Simulation"));
//		tools.add(start);
//		tools.add(reset);
		tools.add(new MyJTextArea("Tools"));
		tools.add(clear);
		tools.add(none);
		tools.add(wall);
		tools.add(path);
		tools.add(startPoint);
		tools.add(finishPoint);
		return scrollPane;
	}
	
	private class ToolButton extends JButton {
		private static final long serialVersionUID = 1L;
		
		protected ToolButton(String title) {
			super(title);
			
			int width = 70;
			int height = 40;
			
			setPreferredSize(new Dimension(width, height));
			setMinimumSize(new Dimension(width, height));
			setMaximumSize(new Dimension(width, height));
			setBackground(new Color(0x88AA88));
		}
	}
	
	private class MyJTextArea extends JTextArea {
		protected MyJTextArea(String text) {
			super();
			setText(text);
			setEditable(false);
			setBackground(new Color(0xDDDDDD));
			
			int width = 70;
			int height = 20;
			setPreferredSize(new Dimension(width, height));
			setMinimumSize(new Dimension(width, height));
			setMaximumSize(new Dimension(width, height));
		}
	}
	
	private JMenuBar initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		//// FILE
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save map");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.destroy();
				Storage.saveMap(gamePanel.getMapInstance());
			}
		});
		JMenuItem load = new JMenuItem("Load map");
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LabiryntMap map = Storage.loadCurrentMap();
				if (map != null)
					gamePanel.setMapInstance(map);
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchEvent(new WindowEvent(context, WindowEvent.WINDOW_CLOSING));
			}
		});
		file.add(save);
		file.add(load);
		file.addSeparator();
		file.add(exit);
		
		JMenu simulation = new JMenu("Simulation");
		JMenuItem start = new JMenuItem("Start");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.startSolving();
			}
		});
		JMenuItem reset = new JMenuItem("Reset");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.destroy();
				gamePanel.reInit();
				size.setValue(gamePanel.getBrushSize());
			}
		});
		JCheckBoxMenuItem showPaths = new JCheckBoxMenuItem("Show map paths.");
		showPaths.setSelected(false);
		showPaths.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.showPaths(showPaths.isSelected());
			}
		});
		simulation.add(start);
		simulation.add(reset);
		simulation.addSeparator();
		simulation.add(showPaths);
		
		menuBar.add(file);
		menuBar.add(simulation);
		return menuBar;
	}
}
