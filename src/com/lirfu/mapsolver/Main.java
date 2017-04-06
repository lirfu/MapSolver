package com.lirfu.mapsolver;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main {
	
	public static void main(String[] args) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					Window window = new Window();
					window.setLocation(20, 20);
					window.setVisible(true);
					window.setSize(900, 600);
					window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
