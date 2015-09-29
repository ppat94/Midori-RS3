package com.orinate.game.content.dialogue;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tom
 * 
 */
public class DialogueLoader {

	private static final Logger logger = Logger.getLogger(DialogueLoader.class.getSimpleName());
	private static List<Class<? extends Dialogue>> handledDialogues;

	@SuppressWarnings("unchecked")
	public static void init() throws Exception {
		for (File file : new File("./src/com/orinate/game/content/dialogue/impl/").listFiles()) {
			if (file == null) {
				continue;
			}

			Class<? extends Dialogue> clazz = (Class<? extends Dialogue>) Class.forName("com.orinate.game.content.dialogue.impl." + file.getName().replace(".java", ""));
			if (clazz == null) {
				continue;
			}

			add(clazz);
		}
		if (handledDialogues != null) {
			logger.info("Parsed " + handledDialogues.size() + " handled dialogue interactions.");
		}
	}

	private static void add(Class<? extends Dialogue> clazz) {
		if (handledDialogues == null) {
			handledDialogues = new LinkedList<>();
		}
		handledDialogues.add(clazz);
	}

	public static Dialogue get(Class<? extends Dialogue> clazz) {
		for (Class<? extends Dialogue> clazzs : handledDialogues) {
			if (clazzs == null) {
				continue;
			}
			if (clazzs.getSimpleName().equalsIgnoreCase(clazz.getSimpleName())) {
				try {
					return (Dialogue) clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static List<Class<? extends Dialogue>> getDialogues() {
		return handledDialogues;
	}
}
