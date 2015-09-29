package com.orinate.game.content.friends;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.orinate.game.content.friends.FriendsChatManager.ChatSettings;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.player.Player.GameState;
import com.orinate.io.OutBuffer;
import com.orinate.util.text.StringUtil;

public class FriendsChat implements Serializable {

	private static final long serialVersionUID = -4368604596475333844L;
	
	private String owner;
	private String name;
	
	private ChatSettings settings;
	
	private HashMap<String, Integer> ranks;
	
	private CopyOnWriteArrayList<Player> players;
	private ConcurrentHashMap<String, Long> banned;
	
	private byte[] block;
	
	public FriendsChat(Player player, String name) {
		this.owner = player.getDefinition().getUsername();
		this.name = name;
		this.settings = player.getFriendsChatManager().getSettings();
		this.players = new CopyOnWriteArrayList<Player> ();
		this.banned = new ConcurrentHashMap<String, Long>();
		this.ranks = player.getFriendsChatManager().getRanks();
	}
	
	public int getRank(Player player) {
		if(player.getDefinition().getUsername().equals(StringUtil.formatProtocol(owner)))
			return 7;
		else if(player.getRights() == 2)
			return 127;
		else if(!getRanks().containsKey(player.getDefinition().getUsername()))
			return -1;
		return ranks.get(player.getDefinition().getUsername());
	}
	
	public void setRank(String username, int rank) {
		ranks.put(username, rank);
		refreshChat();
	}
	
	public void join(Player player) {
		players.add(player);
		refreshChat();
	}
	
	public void leave(Player player) {
		players.remove(player);
		refreshChat();
	}
	
	public void refreshChat() {
		OutBuffer buffer = new OutBuffer();
		buffer.putString(StringUtil.formatName(owner));
		buffer.putByte(0);
		buffer.putString(name);
		int kickOffset = buffer.offset();
		buffer.putByte(0);
		buffer.putByte(players.size());
		for(Player player : players) {
			buffer.putString(player.getDefinition().getDisplayName());
			buffer.putByte(player.getDefinition().hasDisplayName() ? 1 : 0);
			if(player.getDefinition().hasDisplayName()) //might be the other way around. Username first, then displayname.
				buffer.putString(StringUtil.formatName(player.getDefinition().getUsername()));
			buffer.putShort(player.getGameState() == GameState.IN_LOBBY ? 1104 : 1); //worldId
			int rank = getRank(player);
			buffer.putByte(rank);
			buffer.putString(player.getGameState() == GameState.IN_LOBBY ? "Lobby" : "Atrium");
		}
		block = new byte[buffer.offset()];
		buffer.offset(0);
		buffer.getBytes(block, 0, block.length);
		for(Player player : players) {
			block[kickOffset] = (byte) (player.getDefinition().getUsername().equalsIgnoreCase(owner) ? 0 : settings.getKickInChatRank());
			player.getWriter().sendFriendsChat();
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public ChatSettings getSettings() {
		return settings;
	}
	
	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}
	
	public HashMap<String, Integer> getRanks() {
		if(ranks == null)
			ranks = new HashMap<String, Integer>();
		return ranks;
	}
	
	public byte[] getBlock() {
		return block;
	}

}
