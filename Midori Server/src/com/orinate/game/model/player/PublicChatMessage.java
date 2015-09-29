package com.orinate.game.model.player;

import com.orinate.util.Utilities;

/**
 * @author Tom
 * 
 */
public class PublicChatMessage {

	private String message;
	private int effectHash;

	public PublicChatMessage(String message, int effectHash) {
		this.message = Utilities.fixChatMessage(message);
		this.effectHash = effectHash;
	}

	public String getMessage() {
		return message;
	}

	public int getEffectHash() {
		return effectHash;
	}
}
