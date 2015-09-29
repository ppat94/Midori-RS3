package com.orinate.game.content.friends;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.orinate.game.World;
import com.orinate.game.model.player.Player;
import com.orinate.io.OutBuffer;
import com.orinate.util.Utilities;
import com.orinate.util.entity.FileManager;

/**
 * Class used for managing/processing all of the {@code Player}'s friends.
 * 
 * @author Tyler
 * 
 */
public class FriendListManager implements Serializable {

	/**
	 * Generated Serial VersionUID.
	 */
	private static final long serialVersionUID = -1110314984682979969L;
	/**
	 * The {@code Player} instance.
	 */
	private transient Player player;
	/**
	 * The {@code List} instance to store the friends in.
	 */
	private List<Friend> friends;

	/**
	 * Constructs a new {@code FriendListManager} instance.
	 * 
	 * @param player
	 *            The {@code Player} instance.
	 */
	public FriendListManager(Player player) {
		this.player = player;
		this.friends = new ArrayList<Friend>(400);
	}

	public void loadFriendsList() {
		if (friends.isEmpty()) {
			sendUnlockFriendsList();
			return;
		}
		sendFriends();
	}

	public boolean addFriend(String username) {
		if (friends.size() > 400) {
			player.getWriter().sendGameMessage("Your friends list is currently full.");
			return false;
		}
		if (username.contains("<img="))
			return false;
		if (username.equalsIgnoreCase(player.getDefinition().getUsername())) {
			player.getWriter().sendGameMessage("You can't add yourself to your own friends list");
			return false;
		}
		if (!FileManager.contains(Utilities.formatPlayerNameForDisplay(username))) {
			player.getWriter().sendGameMessage("Unable to add friend - unknown player.");
			return false;
		}
		Friend friend = new Friend(username);
		boolean online = World.getPlayer(username) != null && World.getPlayer(username).isOnline();
		player.getChannel().write(generateFriendsBlock(friend, online ? 1 : 0, online, false));
		friends.add(friend);
		player.getFriendsChatManager().setRank(username, 0);
		return true;
	}

	public boolean removeFriend(String username) {
		if (!friends.contains(forFriend(username))) {
			player.getWriter().sendGameMessage("That player is not on your friends list.");
			return false;
		}
		return friends.remove(forFriend(username));
	}

	public void sendFriends() {
		for (Friend friend : friends) {
			if (friend == null) {
				continue;
			}
			boolean online = World.getPlayer(friend.getUsername()) != null && World.getPlayer(friend.getUsername()).isOnline();
			player.getChannel().write(generateFriendsBlock(friend, online ? 1 : 0, false, false));// TODO
																									// if
																									// in
																									// lobby.
		}
	}

	public void sendUpdateFriend(Friend friend) {
		boolean online = World.getPlayer(friend.getUsername()) != null && World.getPlayer(friend.getUsername()).isOnline();
		player.getChannel().write(generateFriendsBlock(friend, online ? 1 : 0, false, false));// TODO
																								// if
																								// in
																								// lobby.
	}

	private OutBuffer generateFriendsBlock(Friend friend, int worldId, boolean warned, boolean lobby) {
		OutBuffer buffer = new OutBuffer();
		buffer.putVarShort(16);
		buffer.putByte(warned ? 1 : 0);
		buffer.putString(friend.getUsername());
		buffer.putString(friend.getLastKnownDisplay().equals(friend.getUsername()) ? "" : friend.getUsername());
		buffer.putShort(worldId);
		HashMap<String, Integer> ranks = player.getFriendsChatManager().getRanks();
		int rank = 0;
		if (ranks != null)
			rank = ranks.containsKey(friend.getUsername()) ? ranks.get(friend.getUsername()) : 0;
		buffer.putByte(rank);
		buffer.putByte(0);
		if (worldId > 0) {
			// i_348_=0 i_349=32776 - Warned=false Username=EGT Storm WorldId=34
			// Rank=0 Mask=0 FriendNote=cunt nuggetnigger
			buffer.writeString(new StringBuilder(((lobby) ? " Lobby " : " World ")).append(worldId).toString());
			buffer.putByte(worldId == 1118 ? 4 : 0);// 4 - lobby 8 - old school,
													// -1 - offline
			buffer.putInt(worldId == 1118 ? 0 : 32776);
		}
		buffer.putString(friend.getFriendNote() == null ? "" : friend.getFriendNote());// faggot
																						// you
																						// have
																						// to
																						// write
																						// this
																						// -
																						// tyler
		buffer.finishVarShort();
		return buffer;
	}

	private void sendUnlockFriendsList() {
		OutBuffer buffer = new OutBuffer();
		buffer.putVarShort(16);
		buffer.finishVarShort();
		player.getChannel().write(buffer);
	}

	public Friend forFriend(String username) {
		for (Friend friend : friends) {
			if (friend == null)
				continue;
			if (friend.getUsername().equalsIgnoreCase(username))
				return friend;
		}
		return null;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<Friend> getFriends() {
		return friends;
	}
}
