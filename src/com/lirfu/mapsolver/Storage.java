package com.lirfu.mapsolver;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.lirfu.mapsolver.graphics.LabiryntMap;

public class Storage {
	public static void saveMap(LabiryntMap map) {
		System.out.println("Saving map...");
		try {
//			FileOutputStream output = new FileOutputStream("MapName.map");
//			ObjectOutputStream stream = new ObjectOutputStream(output);
//			stream.writeObject(map);
//			stream.close();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("MapName.map"));
			writer.write(map.getID() + " " + map.getSizeX() + " " + map.getSizeY() + " " + map.getStart().x + " "
					+ map.getStart().y + " " + map.getFinish().x + " " + map.getFinish().y + '\n');
			for (int x = 0; x < map.getSizeX(); x++) {
				String str = "";
				for (int y = 0; y < map.getSizeY(); y++) {
					if (map.getMapAt(x, y))
						str += "t";
					else
						str += "f";
					str += ",";
				}
				writer.write(str + '\n');
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			printError("Map file doesn't exist!");
		} catch (IOException e) {
			printError("Couldn't save map!");
		}
	}
	
	public static LabiryntMap loadCurrentMap() {
		System.out.println("Loading map...");
		try {
//			FileInputStream output = new FileInputStream("MapName.map");
//			ObjectInputStream stream = new ObjectInputStream(output);
			int id = -1;
			int sizex = 0, sizey = 0;
			Point start = null, finish = null;
			boolean[][] map = null;
			BufferedReader reader = new BufferedReader(new FileReader("MapName.map"));
			try {
				String[] tempstuff = reader.readLine().split(" ");
				id = Integer.parseInt(tempstuff[0]);
				sizex = Integer.parseInt(tempstuff[1]);
				sizey = Integer.parseInt(tempstuff[2]);
				start = new Point(Integer.parseInt(tempstuff[3]), Integer.parseInt(tempstuff[4]));
				finish = new Point(Integer.parseInt(tempstuff[5]), Integer.parseInt(tempstuff[6]));
				
				map = new boolean[sizex][sizey];
				for (int x = 0; x < sizex; x++) {
					tempstuff = reader.readLine().split(",");
					for (int y = 0; y < sizey; y++) {
						if (tempstuff[y].equals("t"))
							map[x][y] = true;
					}
				}
			} catch (EOFException e) {
				printError("File doesn't contain map!");
			}
			reader.close();
			return new LabiryntMap(id, sizex, sizey, start, finish, map);
//			stream.close();
		} catch (FileNotFoundException e) {
			printError("Map file doesn't exist!");
		} catch (IOException e) {
			printError("Couldn't load map!");
		}
		return null;
	}
	
	private static void printError(String string) {
		System.out.println("ERROR - Storage: " + string);
	}
	
	public static int getNextID() {
		return 0;
	}
}
