package com.orinate.network.packet.impl;

import com.orinate.game.content.friends.Friend;
import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * 
 * @author Tyler
 * 
 */
public class InputPacketListener implements PacketListener {

	public static final int ADD_FRIEND_OPCODE = 56;
	public static final int OPTION_CLICK_OPCODE = 13;
	public static final int REMOVE_FRIEND_OPCODE = 80;
	public static final int ADD_FRIEND_NOTE = 16;

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		if (opcode == ADD_FRIEND_OPCODE) {
			String input = buffer.getString();
			if (player.getFriendsListManager().addFriend(input)) {
				player.getWriter().sendGameMessage("Changes will take effect on your friends list in the next 60 seconds.");
				return;
			}
		} else if (opcode == ADD_FRIEND_NOTE) {
			String username = buffer.getString();
			String inputNote = buffer.getString();
			System.out.println(username + " - " + inputNote);
			Friend friend = player.getFriendsListManager().forFriend(username);
			if (friend != null) {
				if (inputNote.length() > 30)
					inputNote = inputNote.substring(0, 30);
				friend.setFriendNote(inputNote);
				player.getFriendsListManager().sendUpdateFriend(friend);
			}
		} else if (opcode == REMOVE_FRIEND_OPCODE) {
			String input = buffer.getString();
			if (player.getFriendsListManager().removeFriend(input)) {
				player.getWriter().sendGameMessage("Changes will take effect on your friends list in the next 60 seconds.");
				return;
			}
		} else if (opcode == OPTION_CLICK_OPCODE) {
			int indexId = buffer.getLEShortA();
			String username = buffer.getString();
			int component = buffer.getByteS();
			int interfaceHash = buffer.getIntB();
			int interfaceId = interfaceHash >> 16;
			if (interfaceId == 550) {
				if (component == 8) {
					if (player.getFriendsListManager().removeFriend(username)) {
						player.getWriter().sendGameMessage("Changes will take effect on your friends list in the next 60 seconds.");
						return;
					}
				} else if (component == 7) {
					Friend friend = player.getFriendsListManager().forFriend(username);
					if (friend != null) {
						player.getWriter().sendInterface(false, 1477, 234, 451);
						player.getWriter().sendCS2Script(8178);
						player.getWriter().sendCS2Script(9206, 29556743, 29556742, 29556758, 0, indexId);
					}
				}
			}
		}
	}

	@Override
	public boolean register() {
		return PacketRepository.register(this, ADD_FRIEND_NOTE, ADD_FRIEND_OPCODE, OPTION_CLICK_OPCODE, REMOVE_FRIEND_OPCODE);
	}

}
