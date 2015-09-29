package com.orinate.util.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.orinate.game.model.player.Player;

/**
 * @author Tom
 * 
 */
public class FileManager {

	private static final String PATH = "data/characters/";
	private static final String SUFFIX = ".p";
	
	public static void main(String[] args) {
		System.out.println(contains("cody"));
	}

	public static boolean contains(String username) {
		return new File(PATH + username + SUFFIX).exists();
	}

	public static Player load(String username) {
		try {
			return (Player) loadSerial(new File(PATH + username + SUFFIX));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Object loadSerial(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		Object object = in.readObject();
		in.close();
		return object;
	}

	public static void savePlayer(Player player) {
		try {
			storeSerializableClass(player, new File(PATH + player.getDefinition().getUsername() + SUFFIX));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void storeSerializableClass(Serializable o, File f) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(o);
		out.close();
	}
}
