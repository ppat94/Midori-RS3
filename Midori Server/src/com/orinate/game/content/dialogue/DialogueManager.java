package com.orinate.game.content.dialogue;

import java.util.Arrays;

import com.orinate.game.content.dialogue.Dialogue.Options;
import com.orinate.game.model.player.Player;

/**
 * @author Tom
 * 
 */
public class DialogueManager {

	private Dialogue dialogue;
	private Player player;

	public DialogueManager(Player player) {
		this.player = player;
	}

	public boolean startDialogue(int npcId) {
		for (Class<? extends Dialogue> clazz : DialogueLoader.getDialogues()) {
			if (clazz == null) {
				continue;
			}
			try {
				Dialogue dialogue = (Dialogue) clazz.newInstance();
				if (dialogue.npcId() == npcId) {
					startDialogue(clazz);
					return true;
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void startDialogue(Class<? extends Dialogue> clazz, Object... params) {
		dialogue = DialogueLoader.get(clazz);
		if (dialogue == null) {
			return;
		}
		dialogue.setParameters(Arrays.asList(params));
		dialogue.setPlayer(player);
		dialogue.onStart();
	}

	public void continueDialogue(int option) {
		if (dialogue == null) {
			return;
		}
		dialogue.process(Options.forComponent(option));
	}

	public void finishDialogue() {
		if (dialogue == null) {
			return;
		}
		dialogue = null;
		if (player.getInterfaceManager().containsChatboxInterface()) {
			player.getInterfaceManager().closeChatboxInterface();
		}
	}
}
