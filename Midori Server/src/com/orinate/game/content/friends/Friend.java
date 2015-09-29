package com.orinate.game.content.friends;

import java.io.Serializable;

import com.orinate.util.Utilities;

/**
 * Represents a single {@code Friend} instance.
 * 
 * @author Tyler
 * 
 */
public class Friend implements Serializable {

	/**
	 * Generated SerialVersionUID.
	 */
	private static final long serialVersionUID = 7286350293830024110L;

	private String username, lastKnownDisplay, friendNote;
	private int rank;

	/**
	 * Constructs a new {@code Friend} instance.
	 * 
	 * @param username
	 *            The {@code Friend}'s username.
	 */
	public Friend(String username) {
		this.username = username;
		this.lastKnownDisplay = username;
		this.friendNote = "";
	}

	public String getUsername() {
		return Utilities.formatPlayerNameForDisplay(username);
	}

	public String getFriendNote() {
		return friendNote;
	}

	public void setFriendNote(String friendNote) {
		this.friendNote = friendNote;
	}

	public String getLastKnownDisplay() {
		return Utilities.formatPlayerNameForDisplay(lastKnownDisplay);
	}

	public void setLastKnownDisplay(String lastKnownDisplay) {
		this.lastKnownDisplay = lastKnownDisplay;
	}
}
