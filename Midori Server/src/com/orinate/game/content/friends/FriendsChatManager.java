package com.orinate.game.content.friends;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.orinate.game.model.player.Player;
import com.orinate.network.packet.impl.ClickInterfaceListener;
import com.orinate.util.Utilities;
import com.orinate.util.entity.FileManager;
import com.orinate.util.text.StringUtil;

/**
 * 
 * @author Cody
 * 
 */

public class FriendsChatManager implements Serializable {

	private static final long serialVersionUID = -3344921508750135934L;

	private static HashMap<String, FriendsChat> friendsChats;

	private Player player;
	
	private String currentChat;
	private String chatName;

	private transient FriendsChat friendsChat;
	private ChatSettings settings;
	
	private HashMap<String, Integer> ranks;

	public FriendsChatManager() {
		settings = new ChatSettings(8, 0, -1, 7);
		ranks = new HashMap<String, Integer>();
	}
	
	public static void init() {
		friendsChats = new HashMap<String, FriendsChat>();
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void load() {
		if(currentChat != null)
			joinChat(currentChat);
	}

	public void openSetup() {
		refreshChatSettings();
		player.getInterfaceManager().sendInterface(1108);
		player.getWriter().sendHideIComponent(1108, 49, true);
		player.getWriter().sendHideIComponent(1108, 63, true);
		player.getWriter().sendHideIComponent(1108, 77, true);
		player.getWriter().sendHideIComponent(1108, 91, true);
	}
	
	public void sendMessage(String message) {
		for(Player player : friendsChat.getPlayers())
			player.getWriter().receiveFriendsChatMessage(player.getDefinition().getDisplayName(), friendsChat, message);
	}
	
	public void joinChat(String name) {
		name = StringUtil.formatProtocol(name);
		FriendsChat chat = friendsChats.get(name);
		if(chat == null) {
			if(!FileManager.contains(name)) {
				player.getWriter().sendGameMessage("This chat does not exist.");
				return;
			}
			Player chatOwner = FileManager.load(name);
			if(chatOwner == null || chatOwner.getFriendsChatManager().getChatName() == null) {
				player.getWriter().sendGameMessage("This chat does not exist2.");
				return;
			}
			chat = new FriendsChat(chatOwner, chatOwner.getFriendsChatManager().getChatName());
			friendsChats.put(chatOwner.getDefinition().getUsername(), chat);
		}
		int rank = chat.getRank(player);
		if(rank < chat.getSettings().getEnterChatRank()) {
			player.getWriter().sendGameMessage("You do not have a high enough rank to join this channel.");
			return;
		}
		this.friendsChat = chat;
		this.currentChat = name;
		chat.join(player);
	}
	
	public void leaveChat(boolean logout) {
		if(friendsChat != null) {
			friendsChat.leave(player);
			if(friendsChat.getPlayers().size() == 0)
				friendsChats.remove(currentChat);
			this.friendsChat = null;
			if(!logout) {
				this.currentChat = null;
				player.getWriter().sendFriendsChat();
			}
		}
	}

	public void handleButtons(int interfaceId, int componentId, int slotId, int opcode) {
		if (interfaceId == 1108) {
			if (componentId == 1) {
				if (opcode == 102) {
					if(chatName != null) {
						player.getWriter().sendGameMessage("Sorry. You cannot change friends chat names at the moment.");
						return;
					}
					friendsChat = new FriendsChat(player, chatName = "Midori");
					refreshChatSettings();
				} else if(opcode == 25) {
					if(friendsChats.containsKey(player.getDefinition().getUsername())) {
						FriendsChat chat = friendsChats.get(player.getDefinition().getUsername());
						if(chat != null)
							if(chat.getPlayers().size() > 0) {
								player.getWriter().sendGameMessage("You cannot disable your friends chat while there are people in it.");
								return;
							}
					}
					chatName = null;
					refreshChatSettings();
				}
			} else if(componentId == 2) {
				settings.setEnterChat(getOption(opcode, componentId));
				refreshChatSettings();
			} else if(componentId == 3) {
				settings.setTalkInChat(getOption(opcode, componentId));
				refreshChatSettings();
			} else if(componentId == 4) {
				settings.setKickInChat(getOption(opcode, componentId));
				refreshChatSettings();
			} else if(componentId == 5) {
				settings.setShareLoot(getOption(opcode, componentId));
				refreshChatSettings();
			}
		} else if(interfaceId == 1427) {
			if(componentId == 33) {
				if(friendsChat == null)
					player.getWriter().sendGameMessage("Please use the command ::joinchat (name) to join a channel.");
				else
					leaveChat(false);
			} else if(componentId == 35)
				openSetup();
		}
	}
	
	public void setRank(String username, int rank) {
		ranks.put(username, rank);
		if(friendsChats.containsKey(player.getDefinition().getUsername())) {
			FriendsChat chat = friendsChats.get(player.getDefinition().getUsername());
			if(chat == null)
				return;
			chat.setRank(username, rank);
		}
	}
	
	public void refreshChatSettings() {
		if(settings == null)
			settings = new ChatSettings(8, 0, -1, 7);
		player.getWriter().sendIComponentText(1108, 1, chatName == null ? "Chat disabled" : chatName);
		player.getWriter().sendIComponentText(1108, 2, getRank(settings.enterChat));
		player.getWriter().sendIComponentText(1108, 3, getRank(settings.talkInChat));
		player.getWriter().sendIComponentText(1108, 4, getRank(settings.kickInChat));
		player.getWriter().sendIComponentText(1108, 5, getRank(settings.shareLoot));
	}
	
	public int getOption(int opcode, int componentId) {
		int option = -1;
		if(opcode == 102 && componentId != 5)
			option = -1;
		else if(opcode == 102 && componentId == 5)
			option = 8;
		else if(opcode == 25)
			option = 0;
		else if(opcode == 23)
			option = 1;
		else if(opcode == 67)
			option = 2;
		else if(opcode == 68)
			option = 3;
		else if(opcode == 74)
			option = 4;
		else if(opcode == 48)
			option = 5;
		else if(opcode == 19)
			option = 6;
		else if(opcode == 89)
			option = 7;
		return option;
	}
	
	public String getRank(int rank) {
		String text = "";
		if(rank == -1)
			text = "Anyone";
		else if (rank == 0)
			text = "Any friends";
		else if (rank == 1)
			text = "Recruit+";
		else if (rank == 2)
			text = "Corporal+";
		else if (rank == 3)
			text = "Sergeant+";
		else if (rank == 4)
			text = "Lieutenant+";
		else if (rank == 5)
			text = "Captain+";
		else if (rank == 6)
			text = "General+";
		else if(rank == 7)
			text = "Only me";
		else if(rank == 8)
			text = "No-one";
		return text;
	}

	public FriendsChat getFriendsChat() {
		return friendsChat;
	}

	public static HashMap<String, FriendsChat> getFriendsChats() {
		return friendsChats;
	}
	
	public ChatSettings getSettings() {
		return settings;
	}
	
	public String getChatName() {
		return chatName;
	}
	
	public HashMap<String, Integer> getRanks() {
		if(ranks == null)
			ranks = new HashMap<String, Integer>();
		return ranks;
	}
	
	public static class ChatSettings implements Serializable {
		
		private static final long serialVersionUID = -994024777114344713L;
		private int shareLoot;
		private int enterChat;
		private int talkInChat;
		private int kickInChat;
		
		public ChatSettings(int shareLoot, int enterChat, int talkInChat, int kickInChat) {
			this.shareLoot = shareLoot;
			this.enterChat = enterChat;
			this.talkInChat = talkInChat;
			this.kickInChat = kickInChat;
		}
		
		public void setShareLoot(int shareLoot) {
			this.shareLoot = shareLoot;
		}
		
		public void setEnterChat(int enterChat) {
			this.enterChat = enterChat;
		}
		
		public void setTalkInChat(int talkInChat) {
			this.talkInChat = talkInChat;
		}
		
		public void setKickInChat(int kickInChat) {
			this.kickInChat = kickInChat;
		}
		
		public int getShareLootRank() {
			return shareLoot;
		}
		
		public int getEnterChatRank() {
			return enterChat;
		}
		
		public int getTalkInChatRank() {
			return talkInChat;
		}
		
		public int getKickInChatRank() {
			return kickInChat;
		}
	}

}
