package com.orinate.network.packet;

import java.util.Map;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.orinate.Constants;
import com.orinate.cache.parsers.ObjectDefinition;
import com.orinate.game.World;
import com.orinate.game.content.friends.FriendsChat;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.Location;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.container.ItemContainer;
import com.orinate.game.model.player.InterfaceManager;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.player.PublicChatMessage;
import com.orinate.game.model.region.GroundItem;
import com.orinate.game.model.region.WorldObject;
import com.orinate.io.OutBuffer;
import com.orinate.util.Utilities;
import com.orinate.util.misc.LobbyConfigs;
import com.orinate.util.text.Huffman;

/**
 * @author Tom
 * @author Tyler
 */
public class PacketWriter {

	private Player player;

	public PacketWriter(Player player) {
		this.player = player;
	}

	public void sendPlayerOption(int slot, String option) {
		sendPlayerOption(option, slot, false, -1);
	}

	public void sendPlayerOption(String option, int optionSlot, boolean firstOption, int mouseIcon) {
		OutBuffer buffer = new OutBuffer();
		buffer.putVarByte(164);
		buffer.putByte(optionSlot);
		buffer.putString(option);
		buffer.putLEShort(mouseIcon);
		buffer.putByte(firstOption ? 1 : 0);
		buffer.finishVarByte();
		player.getChannel().write(buffer);
	}

	public void sendMusic(int musicId) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(130);
		/*
		 * int i_190_ = buffer.readUnsignedShort(); if (65535 == i_190_) i_190_
		 * = -1; int i_191_ = buffer.readUnsignedByte(); int i_192_ =
		 * buffer.readUnsignedShort(); int i_193_ = buffer.readUnsignedByte();
		 * int i_194_ = buffer.readUnsignedShort();
		 */
		buffer.putShort(musicId);
		buffer.putByte(100);
		buffer.putShort(100);
		buffer.putByte(100);
		buffer.putShort(100);
		player.getChannel().write(buffer);
	}

	public void sendGameMessage(Object text) {
		sendGameMessage(text, false);
	}

	public void sendGameMessage(Object text, boolean filter) {
		sendMessage(filter ? 109 : 0, text, null);
	}

	public void sendMessage(int type, Object text, Player p) {
		int maskData = 0;
		if (p != null) {
			maskData |= 0x1;
			if (p.getDefinition().hasDisplayName()) {
				maskData |= 0x2;
			}
		}
		OutBuffer stream = new OutBuffer();
		stream.putVarByte(60);
		stream.putSmart(type);
		stream.putInt(0);
		stream.putByte(maskData);
		if ((maskData & 0x1) != 0) {
			stream.putString(p.getDefinition().getDisplayName());
			if (p.getDefinition().hasDisplayName()) {
				stream.putString(p.getDefinition().getUsername());
			}
		}
		stream.putString(text.toString());
		stream.finishVarByte();
		player.write(stream);
	}
	
	public void receiveFriendsChatMessage(String name, FriendsChat chat, String message) {
		OutBuffer buffer = new OutBuffer();
	}
	
	public void sendFriendsChat() {
		FriendsChat chat = player.getFriendsChatManager().getFriendsChat();
		OutBuffer buffer = new OutBuffer(chat == null ? 3 : chat.getBlock().length + 3);
		buffer.putVarShort(155);
		if(chat != null)
			buffer.putBytes(chat.getBlock());
		buffer.finishVarShort();
		player.write(buffer);
	}

	public void sendDestroyObject(WorldObject object) {
		int chunkRotation = World.getWorld().getRotation(object.getLocation().getPlane(), object.getLocation().getX(), object.getLocation().getY());
		if (chunkRotation == 1) {
			object = new WorldObject(object);
			ObjectDefinition objectDef = ObjectDefinition.forId(object.getId());
			object.getLocation().moveLocation(0, -(objectDef.sizeY - 1), 0);
		} else if (chunkRotation == 2) {
			object = new WorldObject(object);
			ObjectDefinition objectDef = ObjectDefinition.forId(object.getId());
			object.getLocation().moveLocation(-(objectDef.sizeY - 1), 0, 0);
		}
		OutBuffer buffer = createLocationBuffer(object.getLocation());
		int localX = object.getLocation().getLocalX(player.getLastLoadedLocation(), 0);
		int localY = object.getLocation().getLocalY(player.getLastLoadedLocation(), 0);
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		buffer.putPacket(66);
		buffer.putS((offsetX << 4) | offsetY);
		buffer.putA((object.getType() << 2) + (object.getRotation() & 0x3));
		player.write(buffer);
	}

	public void sendSpawnObject(WorldObject object) {
		int chunkRotation = World.getWorld().getRotation(object.getLocation().getPlane(), object.getLocation().getX(), object.getLocation().getY());
		if (chunkRotation == 1) {
			object = new WorldObject(object);
			ObjectDefinition objectDef = ObjectDefinition.forId(object.getId());
			object.getLocation().moveLocation(0, -(objectDef.sizeY - 1), 0);
		} else if (chunkRotation == 2) {
			object = new WorldObject(object);
			ObjectDefinition objectDef = ObjectDefinition.forId(object.getId());
			object.getLocation().moveLocation(-(objectDef.sizeY - 1), 0, 0);
		}
		OutBuffer buffer = createLocationBuffer(object.getLocation());
		int localX = object.getLocation().getLocalX(player.getLastLoadedLocation(), 0);
		int localY = object.getLocation().getLocalY(player.getLastLoadedLocation(), 0);
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		buffer.putPacket(123);
		buffer.putA((object.getType() << 2) + (object.getRotation() & 0x3));
		buffer.putIntB(object.getId());
		buffer.putC((offsetX << 4) | offsetY);
		player.write(buffer);
	}

	public void sendLobbyInformation() {
		for (Map.Entry<Integer, Integer> config : LobbyConfigs.getConfigs().entrySet()) {
			if (config == null) {
				continue;
			}
			sendConfig(config.getKey(), config.getValue());
		}
		sendRootInterface(906, 0);
	}

	public void sendWorldInformation() {
		sendRootInterface(1477, 0);
		sendInterface(true, 1477, 84, 1482);
		sendInterface(true, 1477, 315, 1466);
		sendIComponentSettings(1466, 10, 0, 26, 30);
		sendInterface(true, 1477, 295, 1220);
		sendInterface(true, 1477, 129, 1473);
		sendIComponentSettings(1473, 8, -1, -1, 2097152);
		sendIComponentSettings(1473, 8, 0, 27, 15302030);
		sendIComponentSettings(1473, 0, 0, 27, 1536);
		sendInterface(true, 1477, 202, 1464);
		sendIComponentSettings(1464, 14, 0, 15, 15302654);
		sendIComponentSettings(1464, 12, 2, 7, 2);
		sendInterface(true, 1477, 325, 1458);
		sendIComponentSettings(1458, 24, 0, 28, 8388610);
		sendInterface(true, 1477, 241, 1460);
		sendInterface(true, 1477, 251, 1452);
		sendInterface(true, 1477, 261, 1461);
		sendInterface(true, 1477, 271, 1449);
		sendIComponentSettings(1460, 1, 0, 168, 10320902);
		sendIComponentSettings(1452, 1, 0, 168, 10320902);
		sendIComponentSettings(1461, 1, 0, 168, 10320902);
		sendIComponentSettings(1449, 1, 0, 168, 10320902);
		sendIComponentSettings(1460, 4, 6, 14, 2);
		sendIComponentSettings(1452, 7, 6, 14, 2);
		sendIComponentSettings(1461, 7, 6, 14, 2);
		sendIComponentSettings(1449, 7, 6, 14, 2);
		sendInterface(true, 1477, 373, 550);
		sendIComponentSettings(550, 25, 0, 500, 510);
		sendIComponentSettings(550, 23, 0, 500, 6);
		sendInterface(true, 1477, 604, 1427);
		sendIComponentSettings(1427, 23, 0, 600, 1024);
		sendInterface(true, 1477, 363, 1110);
		sendIComponentSettings(1110, 19, 0, 200, 2);
		sendIComponentSettings(1110, 24, 0, 600, 2);
		sendIComponentSettings(1110, 22, 0, 600, 1024);
		sendIComponentSettings(1110, 13, 0, 600, 1024);
		sendInterface(true, 1477, 305, 590);
		sendIComponentSettings(590, 8, 0, 160, 8388614);
		sendIComponentSettings(590, 13, 0, 11, 2);
		sendInterface(true, 1477, 343, 1416);
		sendIComponentSettings(1416, 3, 0, 2419, 30);
		sendIComponentSettings(1416, 11, 0, 11, 2359302);
		sendIComponentSettings(1416, 11, 12, 23, 4);
		sendIComponentSettings(1416, 11, 24, 24, 2097152);
		sendInterface(true, 1477, 353, 1417);
		sendIComponentSettings(1417, 16, 0, 29, 2621470);
		sendInterface(true, 1477, 174, 1431);
		sendIComponentSettings(1477, 175, 1, 1, 2);
		sendIComponentSettings(1477, 77, 1, 1, 4);
		sendInterface(true, 1477, 59, 1465);
		sendIComponentSettings(1477, 79, 1, 1, 6);
		sendInterface(true, 1477, 57, 1430);
		sendInterface(true, 1477, 33, 1433);
		sendInterface(true, 1477, 392, 1483);
		sendInterface(true, 1477, 411, 745);
		sendInterface(true, 1477, 388, 1485);
		sendInterface(true, 1477, 0, 1213);
		sendInterface(true, 1477, 73, 1448);
		sendInterface(true, 1477, 834, 557);
		sendInterface(true, 1477, 105, 137);
		sendInterface(true, 1477, 178, 1467);
		sendInterface(true, 1477, 186, 1472);
		sendInterface(true, 1477, 194, 1471);
		sendInterface(true, 1477, 335, 1470);
		sendInterface(true, 1477, 826, 464);
		sendInterface(true, 1477, 222, 182);
		sendIComponentSettings(137, 88, 0, 99, 2046);
		sendIComponentSettings(1467, 60, 0, 99, 2046);
		sendIComponentSettings(1472, 60, 0, 99, 2046);
		sendIComponentSettings(1471, 60, 0, 99, 2046);
		sendIComponentSettings(1470, 60, 0, 99, 2046);
		sendIComponentSettings(464, 61, 0, 99, 2046);
		sendIComponentSettings(182, 0, 0, 99, 2046);
		sendIComponentSettings(1477, 62, -1, -1, 2097152);
		sendInterface(true, 1477, 37, 1488);
		sendInterface(true, 1477, 21, 1215);
		sendIComponentSettings(1430, 97, -1, -1, 2195454);
		sendIComponentSettings(1430, 101, -1, -1, 2195454);
		sendIComponentSettings(1430, 102, -1, -1, 2195454);
		sendIComponentSettings(1430, 106, -1, -1, 2195454);
		sendIComponentSettings(1430, 107, -1, -1, 2195454);
		sendIComponentSettings(1430, 111, -1, -1, 2195454);
		sendIComponentSettings(1430, 112, -1, -1, 2195454);
		sendIComponentSettings(1430, 116, -1, -1, 2195454);
		sendIComponentSettings(1430, 117, -1, -1, 2195454);
		sendIComponentSettings(1430, 121, -1, -1, 2195454);
		sendIComponentSettings(1430, 122, -1, -1, 2195454);
		sendIComponentSettings(1430, 126, -1, -1, 2195454);
		sendIComponentSettings(1430, 127, -1, -1, 2195454);
		sendIComponentSettings(1430, 131, -1, -1, 2195454);
		sendIComponentSettings(1430, 132, -1, -1, 2195454);
		sendIComponentSettings(1430, 136, -1, -1, 2195454);
		sendIComponentSettings(1430, 137, -1, -1, 2195454);
		sendIComponentSettings(1430, 141, -1, -1, 2195454);
		sendIComponentSettings(1430, 142, -1, -1, 2195454);
		sendIComponentSettings(1430, 146, -1, -1, 2195454);
		sendIComponentSettings(1430, 147, -1, -1, 2195454);
		sendIComponentSettings(1430, 151, -1, -1, 2195454);
		sendIComponentSettings(1430, 152, -1, -1, 2195454);
		sendIComponentSettings(1430, 156, -1, -1, 2195454);
		sendIComponentSettings(1458, 24, 0, 28, 8388610);
		sendIComponentSettings(1430, 4, -1, -1, 0);
		sendIComponentSettings(1465, 4, -1, -1, 8388608);
		sendIComponentSettings(1430, 2, -1, -1, 262146);
		sendIComponentSettings(1430, 5, -1, -1, 0);
		sendIComponentSettings(1460, 1, 0, 168, 8485894);
		sendIComponentSettings(1452, 1, 0, 168, 8485894);
		sendIComponentSettings(1461, 1, 0, 168, 8485894);
		sendIComponentSettings(1449, 1, 0, 168, 8485894);
		sendIComponentSettings(590, 8, 0, 160, 8388614);
		sendIComponentSettings(1477, 173, 1, 7, 9175040);
		sendIComponentSettings(1477, 173, 11, 13, 9175040);
		sendIComponentSettings(1477, 173, 0, 0, 9175040);
		sendIComponentSettings(1477, 75, -1, -1, 2097152);
		sendIComponentSettings(1477, 56, 1, 7, 9175040);
		sendIComponentSettings(1477, 56, 11, 13, 9175040);
		sendIComponentSettings(1477, 56, 0, 0, 9175040);
		sendIComponentSettings(1477, 76, -1, -1, 2097152);
		sendIComponentSettings(1477, 61, 1, 7, 9175040);
		sendIComponentSettings(1477, 61, 11, 13, 9175040);
		sendIComponentSettings(1477, 61, 0, 0, 9175040);
		sendIComponentSettings(1477, 61, 3, 4, 9175040);
		sendIComponentSettings(1477, 78, -1, -1, 2097152);
		sendIComponentSettings(1477, 108, 1, 7, 9175040);
		sendIComponentSettings(1477, 108, 11, 13, 9175040);
		sendIComponentSettings(1477, 108, 0, 0, 9175040);
		sendIComponentSettings(1477, 108, 3, 4, 9175040);
		sendIComponentSettings(1477, 98, -1, -1, 2097152);
		sendIComponentSettings(1477, 180, 1, 7, 9175040);
		sendIComponentSettings(1477, 180, 11, 13, 9175040);
		sendIComponentSettings(1477, 180, 0, 0, 9175040);
		sendIComponentSettings(1477, 180, 3, 4, 9175040);
		sendIComponentSettings(1477, 99, -1, -1, 2097152);
		sendIComponentSettings(1477, 188, 1, 7, 9175040);
		sendIComponentSettings(1477, 188, 11, 13, 9175040);
		sendIComponentSettings(1477, 188, 0, 0, 9175040);
		sendIComponentSettings(1477, 188, 3, 4, 9175040);
		sendIComponentSettings(1477, 100, -1, -1, 2097152);
		sendIComponentSettings(1477, 196, 1, 7, 9175040);
		sendIComponentSettings(1477, 196, 11, 13, 9175040);
		sendIComponentSettings(1477, 196, 0, 0, 9175040);
		sendIComponentSettings(1477, 196, 3, 4, 9175040);
		sendIComponentSettings(1477, 101, -1, -1, 2097152);
		sendIComponentSettings(1477, 337, 1, 7, 9175040);
		sendIComponentSettings(1477, 337, 11, 13, 9175040);
		sendIComponentSettings(1477, 337, 0, 0, 9175040);
		sendIComponentSettings(1477, 337, 3, 4, 9175040);
		sendIComponentSettings(1477, 102, -1, -1, 2097152);
		sendIComponentSettings(1477, 828, 1, 7, 9175040);
		sendIComponentSettings(1477, 828, 11, 13, 9175040);
		sendIComponentSettings(1477, 828, 0, 0, 9175040);
		sendIComponentSettings(1477, 828, 3, 4, 9175040);
		sendIComponentSettings(1477, 103, -1, -1, 2097152);
		sendIComponentSettings(1477, 308, 1, 7, 9175040);
		sendIComponentSettings(1477, 308, 11, 13, 9175040);
		sendIComponentSettings(1477, 308, 0, 0, 9175040);
		sendIComponentSettings(1477, 308, 3, 4, 9175040);
		sendIComponentSettings(1477, 96, -1, -1, 2097152);
		sendIComponentSettings(1477, 132, 1, 7, 9175040);
		sendIComponentSettings(1477, 132, 11, 13, 9175040);
		sendIComponentSettings(1477, 132, 0, 0, 9175040);
		sendIComponentSettings(1477, 132, 3, 4, 9175040);
		sendIComponentSettings(1477, 97, -1, -1, 2097152);
		sendIComponentSettings(1477, 133, 1, 1, 2);
		sendIComponentSettings(1477, 244, 1, 7, 9175040);
		sendIComponentSettings(1477, 244, 11, 13, 9175040);
		sendIComponentSettings(1477, 244, 0, 0, 9175040);
		sendIComponentSettings(1477, 244, 3, 4, 9175040);
		sendIComponentSettings(1477, 116, -1, -1, 2097152);
		sendIComponentSettings(1477, 245, 1, 1, 2);
		sendIComponentSettings(1477, 254, 1, 7, 9175040);
		sendIComponentSettings(1477, 254, 11, 13, 9175040);
		sendIComponentSettings(1477, 254, 0, 0, 9175040);
		sendIComponentSettings(1477, 254, 3, 4, 9175040);
		sendIComponentSettings(1477, 117, -1, -1, 2097152);
		sendIComponentSettings(1477, 255, 1, 1, 2);
		sendIComponentSettings(1477, 264, 1, 7, 9175040);
		sendIComponentSettings(1477, 264, 11, 13, 9175040);
		sendIComponentSettings(1477, 264, 0, 0, 9175040);
		sendIComponentSettings(1477, 264, 3, 4, 9175040);
		sendIComponentSettings(1477, 118, -1, -1, 2097152);
		sendIComponentSettings(1477, 265, 1, 1, 2);
		sendIComponentSettings(1477, 274, 1, 7, 9175040);
		sendIComponentSettings(1477, 274, 11, 13, 9175040);
		sendIComponentSettings(1477, 274, 0, 0, 9175040);
		sendIComponentSettings(1477, 274, 3, 4, 9175040);
		sendIComponentSettings(1477, 119, -1, -1, 2097152);
		sendIComponentSettings(1477, 275, 1, 1, 2);
		sendIComponentSettings(1477, 205, 1, 7, 9175040);
		sendIComponentSettings(1477, 205, 11, 13, 9175040);
		sendIComponentSettings(1477, 205, 0, 0, 9175040);
		sendIComponentSettings(1477, 205, 3, 4, 9175040);
		sendIComponentSettings(1477, 113, -1, -1, 2097152);
		sendIComponentSettings(1477, 206, 1, 1, 2);
		sendIComponentSettings(1477, 215, 1, 7, 9175040);
		sendIComponentSettings(1477, 215, 11, 13, 9175040);
		sendIComponentSettings(1477, 215, 0, 0, 9175040);
		sendIComponentSettings(1477, 215, 3, 4, 9175040);
		sendIComponentSettings(1477, 114, -1, -1, 2097152);
		sendIComponentSettings(1477, 216, 1, 1, 2);
		sendIComponentSettings(1477, 298, 1, 7, 9175040);
		sendIComponentSettings(1477, 298, 11, 13, 9175040);
		sendIComponentSettings(1477, 298, 0, 0, 9175040);
		sendIComponentSettings(1477, 298, 3, 4, 9175040);
		sendIComponentSettings(1477, 122, -1, -1, 2097152);
		sendIComponentSettings(1477, 299, 1, 1, 2);
		sendIComponentSettings(1477, 283, 1, 7, 9175040);
		sendIComponentSettings(1477, 283, 11, 13, 9175040);
		sendIComponentSettings(1477, 283, 0, 0, 9175040);
		sendIComponentSettings(1477, 283, 3, 4, 9175040);
		sendIComponentSettings(1477, 120, -1, -1, 2097152);
		sendIComponentSettings(1477, 284, 1, 1, 2);
		sendIComponentSettings(1477, 318, 1, 7, 9175040);
		sendIComponentSettings(1477, 318, 11, 13, 9175040);
		sendIComponentSettings(1477, 318, 0, 0, 9175040);
		sendIComponentSettings(1477, 318, 3, 4, 9175040);
		sendIComponentSettings(1477, 121, -1, -1, 2097152);
		sendIComponentSettings(1477, 319, 1, 1, 2);
		sendIComponentSettings(1477, 328, 1, 7, 9175040);
		sendIComponentSettings(1477, 328, 11, 13, 9175040);
		sendIComponentSettings(1477, 328, 0, 0, 9175040);
		sendIComponentSettings(1477, 328, 3, 4, 9175040);
		sendIComponentSettings(1477, 115, -1, -1, 2097152);
		sendIComponentSettings(1477, 329, 1, 1, 2);
		sendIComponentSettings(1477, 346, 1, 7, 9175040);
		sendIComponentSettings(1477, 346, 11, 13, 9175040);
		sendIComponentSettings(1477, 346, 0, 0, 9175040);
		sendIComponentSettings(1477, 346, 3, 4, 9175040);
		sendIComponentSettings(1477, 123, -1, -1, 2097152);
		sendIComponentSettings(1477, 347, 1, 1, 2);
		sendIComponentSettings(1477, 356, 1, 7, 9175040);
		sendIComponentSettings(1477, 356, 11, 13, 9175040);
		sendIComponentSettings(1477, 356, 0, 0, 9175040);
		sendIComponentSettings(1477, 356, 3, 4, 9175040);
		sendIComponentSettings(1477, 126, -1, -1, 2097152);
		sendIComponentSettings(1477, 357, 1, 1, 2);
		sendIComponentSettings(1477, 376, 1, 7, 9175040);
		sendIComponentSettings(1477, 376, 11, 13, 9175040);
		sendIComponentSettings(1477, 376, 0, 0, 9175040);
		sendIComponentSettings(1477, 376, 3, 4, 9175040);
		sendIComponentSettings(1477, 124, -1, -1, 2097152);
		sendIComponentSettings(1477, 377, 1, 1, 2);
		sendIComponentSettings(1477, 366, 1, 7, 9175040);
		sendIComponentSettings(1477, 366, 11, 13, 9175040);
		sendIComponentSettings(1477, 366, 0, 0, 9175040);
		sendIComponentSettings(1477, 366, 3, 4, 9175040);
		sendIComponentSettings(1477, 125, -1, -1, 2097152);
		sendIComponentSettings(1477, 367, 1, 1, 2);
		sendIComponentSettings(1477, 607, 1, 7, 9175040);
		sendIComponentSettings(1477, 607, 11, 13, 9175040);
		sendIComponentSettings(1477, 607, 0, 0, 9175040);
		sendIComponentSettings(1477, 607, 3, 4, 9175040);
		sendIComponentSettings(1477, 127, -1, -1, 2097152);
		sendIComponentSettings(1477, 608, 1, 1, 2);
		sendIComponentSettings(1477, 409, 1, 7, 9175040);
		sendIComponentSettings(1477, 409, 11, 13, 9175040);
		sendIComponentSettings(1477, 409, 0, 0, 9175040);
		sendIComponentSettings(1477, 409, 3, 4, 9175040);
		sendIComponentSettings(1477, 235, 1, 2, 9175040);
		sendIComponentSettings(1477, 235, 0, 0, 9175040);
		sendIComponentSettings(1477, 235, 3, 4, 9175040);
		sendIComponentSettings(1477, 239, 1, 2, 9175040);
		sendIComponentSettings(1477, 239, 0, 0, 9175040);
		sendIComponentSettings(1477, 239, 3, 4, 9175040);
		sendIComponentSettings(1477, 292, 0, 0, 9175040);
		sendIComponentSettings(1477, 50, -1, -1, 2097152);
		sendIComponentSettings(1477, 293, 1, 1, 2);
		sendIComponentSettings(1477, 389, 1, 2, 9175040);
		sendIComponentSettings(1477, 389, 0, 0, 9175040);
		sendIComponentSettings(1477, 389, 3, 4, 9175040);
		sendIComponentSettings(1477, 393, 1, 2, 9175040);
		sendIComponentSettings(1477, 393, 0, 0, 9175040);
		sendIComponentSettings(1477, 393, 3, 4, 9175040);
		sendIComponentSettings(1477, 397, 1, 2, 9175040);
		sendIComponentSettings(1477, 397, 0, 0, 9175040);
		sendIComponentSettings(1477, 397, 3, 4, 9175040);
		sendIComponentSettings(1477, 406, 1, 2, 9175040);
		sendIComponentSettings(1477, 406, 0, 0, 9175040);
		sendIComponentSettings(1477, 406, 3, 4, 9175040);
		sendIComponentSettings(1477, 385, 1, 2, 9175040);
		sendIComponentSettings(1477, 385, 0, 0, 9175040);
		sendIComponentSettings(1477, 385, 3, 4, 9175040);
		sendIComponentSettings(1477, 412, 1, 2, 9175040);
		sendIComponentSettings(1477, 412, 0, 0, 9175040);
		sendIComponentSettings(1477, 412, 3, 4, 9175040);
		sendIComponentSettings(1477, 22, 1, 2, 9175040);
		sendIComponentSettings(1477, 22, 0, 0, 9175040);
		sendIComponentSettings(1477, 22, 3, 4, 9175040);
		sendIComponentSettings(1477, 19, 1, 2, 9175040);
		sendIComponentSettings(1477, 19, 0, 0, 9175040);
		sendIComponentSettings(1477, 19, 3, 4, 9175040);
		sendIComponentSettings(1477, 16, 1, 2, 9175040);
		sendIComponentSettings(1477, 16, 5, 5, 9175040);
		sendIComponentSettings(1477, 16, 11, 13, 9175040);
		sendIComponentSettings(1477, 16, 0, 0, 9175040);
		sendIComponentSettings(1477, 16, 3, 4, 9175040);
		sendIComponentSettings(1477, 730, 1, 2, 9175040);
		sendIComponentSettings(1477, 730, 0, 0, 9175040);
		sendIComponentSettings(1477, 730, 3, 4, 9175040);
		sendIComponentSettings(1477, 223, 1, 7, 9175040);
		sendIComponentSettings(1477, 223, 11, 13, 9175040);
		sendIComponentSettings(1477, 223, 0, 0, 9175040);
		sendIComponentSettings(1477, 223, 3, 4, 9175040);
		sendIComponentSettings(1477, 54, 1, 2, 9175040);
		sendIComponentSettings(1477, 54, 0, 0, 9175040);
		sendIComponentSettings(1477, 54, 3, 4, 9175040);
		sendIComponentSettings(1477, 12, -1, -1, 2097152);
		sendIComponentSettings(1477, 74, 1, 1, 2);
		sendIComponentSettings(1477, 95, 1, 7, 9175040);
		sendIComponentSettings(1477, 95, 11, 13, 9175040);
		sendIComponentSettings(1477, 95, 0, 0, 9175040);
		sendIComponentSettings(1477, 95, 3, 4, 9175040);
		sendIComponentSettings(1477, 7, -1, -1, 2097152);
		sendIComponentSettings(1477, 835, 1, 2, 9175040);
		sendIComponentSettings(1477, 835, 0, 0, 9175040);
		sendIComponentSettings(1477, 835, 3, 4, 9175040);
		sendConfig(3883, 1);
		sendConfig(3708, 107652099);
		sendConfig(3709, 16);
		sendConfig(3820, 8192);
		sendConfig(3829, 17408);
		sendConfig(3831, 34488382);
		sendConfig(3832, 26747877);
		sendConfig(3841, 29);
		sendConfig(3843, 163840);
		sendConfig(3852, 8192);
		sendConfig(3857, -1);
		sendConfig(3858, -1);
		sendConfig(3859, 530220768);
		sendConfig(3860, 4163);
		sendConfig(3861, 536903696);
		sendConfig(3864, -1);
		sendConfig(3865, -1);
		sendConfig(3866, -1);
		sendConfig(3867, -1);
		sendConfig(3872, 1037556012);
		sendConfig(3873, 6427);
		sendConfig(3883, 1);
		sendConfig(3890, -1);
		sendConfig(3892, 5647);
		sendConfig(3906, -1);
		sendConfig(3920, 2);
		sendConfig(3924, -1);
		sendConfig(3925, 65534);
		sendConfig(3928, -1);
		sendConfig(3930, 165496);
		sendConfig(1295, 1000);
		sendConfig(3933, 29360128);
		sendConfig(3814, player.isInterfacesLocked() ? 11 : 9);
		sendConfig(1772, 753695);// Filter game messages.to 
		sendConfig(3680, 9);
		sendConfig(659, (player.getHitpoints() * 40) * 2);
		sendConfig(3274, (player.getSkills().getLevelForXp(Skills.PRAYER) * 100));
		sendPlayerOption(1, "Follow");
		sendPlayerOption(2, "Trade");
		sendRunEnergy();
		player.getDefinition().setRights(2);
		System.out.println(player.getRights());
		sendGameMessage("Welcome to " + Constants.SERVER_NAME + ".");
	}

	public void sendRunEnergy() {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(13);
		buffer.putByte(player.getDefinition().getRunEnergy());
		player.write(buffer);
	}

	public void sendHideIComponent(int interfaceId, int componentId, boolean hidden) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(68);
		buffer.putIntV2(interfaceId << 16 | componentId);
		buffer.put128Byte(hidden ? 1 : 0);
		player.write(buffer);
	}

	public void sendIComponentSettings(int interfaceId, int componentId, int fromSlot, int toSlot, int settingsHash) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(90);
		buffer.putIntA(interfaceId << 16 | componentId);
		buffer.putIntA(settingsHash);
		buffer.putLEShortA(toSlot);
		buffer.putShort(fromSlot);
		player.write(buffer);
	}

	public void sendInterface(boolean nocliped, int windowId, int windowComponentId, int interfaceId) {
		if (player != null && player.getInterfaceManager() != null) {
			if (windowId == InterfaceManager.WINDOW_ID) {
				if (player.getInterfaceManager().containsInterface(windowComponentId, interfaceId)) {
					closeInterface(windowComponentId);
				}
				if (!player.getInterfaceManager().addInterface(windowId, windowComponentId, interfaceId)) {
					return;
				}
			}
		}
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(107);
		buffer.putIntA(windowId << 16 | windowComponentId);
		buffer.putInt(0);
		buffer.putLEShort(interfaceId);
		buffer.putIntA(0);
		buffer.putLEInt(0);
		buffer.putInt(0);
		buffer.putA(nocliped ? 1 : 0);
		player.write(buffer);
	}

	public void sendBuildSceneGraph(boolean login) {
		OutBuffer buffer = new OutBuffer();
		buffer.putVarShort(64);

		int chunkX = player.getLocation().getChunkX();
		int chunkZ = player.getLocation().getChunkY();

		if (login) {
			player.getUpdate().sendLoginData(buffer);
		}

		buffer.putA(9);
		buffer.putLEShort(chunkX);
		buffer.putByte(login ? 1 : 0);
		buffer.putShortA(chunkZ);
		buffer.putA(0);

		buffer.finishVarShort();
		player.write(buffer);
	}

	public void sendConfig(int id, int value) {
		OutBuffer buffer = new OutBuffer();
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			buffer.putPacket(9);
			buffer.putIntA(value);
			buffer.putLEShort(id);
		} else {
			buffer.putPacket(104);
			buffer.putShortA(id);
			buffer.putByte(value);
		}
		player.write(buffer);
	}

	public void sendRootInterface(int id, int type) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(1);
		buffer.putIntA(0);
		buffer.putInt(0);
		buffer.putA(0);
		buffer.putIntB(0);
		buffer.putShort(id);
		buffer.putLEInt(0);
		player.write(buffer);
	}

	public void sendPing() {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(51);
		player.write(buffer);
	}

	public void writeInterfaceInfoReset() {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(118);
		player.write(buffer);
	}

	public void sendContainerUpdate(int key, ItemContainer<Item> items) {
		sendContainerUpdate(key, key < 0, items);
	}

	public void sendContainerUpdate(int key, boolean negativeKey, ItemContainer<Item> items) {
		sendContainerUpdate(key, negativeKey, items.getItems());
	}

	public void sendContainerUpdate(int key, Item[] items) {
		sendContainerUpdate(key, key < 0, items);
	}

	public void sendContainerUpdate(int key, boolean negativeKey, Item[] container) {
		OutBuffer buffer = new OutBuffer();
		buffer.putVarShort(28);
		buffer.putShort(key);
		buffer.putByte(negativeKey ? 1 : 0);
		buffer.putShort(container.length);
		for (int currSlot = 0; currSlot < container.length; currSlot++) {
			Item item = container[currSlot];
			int id = -1, amount = 0;
			if (item != null) {
				id = item.getId();
				amount = item.getAmount();
			}
			buffer.putC(amount >= 255 ? 255 : amount);
			if (amount >= 255) {
				buffer.putIntB(amount);
			}
			buffer.putLEShort(id + 1);
		}
		buffer.finishVarShort();
		player.write(buffer);
	}

	public void sendUpdateItems(int key, ItemContainer<Item> items, int... slots) {
		sendUpdateItems(key, items.getItems(), slots);
	}

	public void sendUpdateItems(int key, Item[] items, int... slots) {
		sendUpdateItems(key, key < 0, items, slots);
	}

	public void sendUpdateItems(int key, boolean negativeKey, Item[] items, int... slots) {
		OutBuffer buffer = new OutBuffer();
		buffer.putVarShort(5);
		buffer.putShort(key);
		buffer.putByte(negativeKey ? 1 : 0);
		for (int slotId : slots) {
			if (slotId >= items.length) {
				continue;
			}
			buffer.putSmart(slotId);
			int id = -1;
			int amount = 0;
			Item item = items[slotId];
			if (item != null) {
				id = item.getId();
				amount = item.getAmount();
			}
			buffer.putShort(id + 1);
			if (id != -1) {
				buffer.putByte(amount >= 255 ? 255 : amount);
				if (amount >= 255) {
					buffer.putInt(amount);
				}
			}
		}
		buffer.finishVarShort();
		player.write(buffer);
	}

	public void sendSkillLevel(int skill) {
		OutBuffer buffer = new OutBuffer(7);
		buffer.putPacket(82);
		buffer.putS(player.getSkills().getLevel(skill));
		buffer.putIntB((int) player.getSkills().getXp(skill));
		buffer.putC(skill);
		player.write(buffer);
	}

	public void closeInterface(int windowComponentId) {
		closeInterface(player.getInterfaceManager().getTabWindow(windowComponentId), windowComponentId);
		player.getInterfaceManager().removeTab(windowComponentId);
	}

	public void closeInterface(int windowId, int windowComponentId) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(44);
		buffer.putLEInt(windowId << 16 | windowComponentId);
		player.write(buffer);
	}

	public void sendCS2Script(int scriptId, Object... params) {
		OutBuffer buffer = new OutBuffer();
		buffer.putVarShort(47);
		String parameterTypes = "";
		if (params != null) {
			for (int count = params.length - 1; count >= 0; count--) {
				if (params[count] instanceof String) {
					parameterTypes += "s";
				} else {
					parameterTypes += "i";
				}
			}
		}
		buffer.writeString(parameterTypes);
		if (params != null) {
			int index = 0;
			for (int count = parameterTypes.length() - 1; count >= 0; count--) {
				if (parameterTypes.charAt(count) == 's') {
					buffer.writeString((String) params[index++]);
				} else {
					buffer.putInt((Integer) params[index++]);
				}
			}
		}
		buffer.putInt(scriptId);
		buffer.finishVarShort();
		player.write(buffer);
	}

	public void sendLogout(boolean lobby) {
		OutBuffer stream = new OutBuffer();
		stream.putPacket(lobby ? 144 : 105);
		ChannelFuture future = player.write(stream);
		if(lobby)
			return;
		if (future != null) {
			future.addListener(ChannelFutureListener.CLOSE);
		} else {
			player.getChannel().close();
		}
	}

	public OutBuffer createLocationBuffer(Location location) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(55);
		buffer.putS(location.getLocalY(player.getLastLoadedLocation(), 0) >> 3);
		buffer.putC(location.getPlane());
		buffer.putByte(location.getLocalX(player.getLastLoadedLocation(), 0) >> 3);
		return buffer;
	}

	public void sendGroundItem(GroundItem item) {
		OutBuffer stream = createLocationBuffer(item.getLocation());
		int localX = item.getLocation().getLocalX(player.getLastLoadedLocation(), 0);
		int localY = item.getLocation().getLocalY(player.getLastLoadedLocation(), 0);
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.putPacket(79);
		stream.putShortA(item.getId());
		stream.putC((offsetX << 4) | offsetY);
		stream.putLEShort(item.getAmount());
		player.write(stream);
	}

	public void sendRemoveGroundItem(GroundItem item) {
		OutBuffer stream = createLocationBuffer(item.getLocation());
		int localX = item.getLocation().getLocalX(player.getLastLoadedLocation(), 0);
		int localY = item.getLocation().getLocalY(player.getLastLoadedLocation(), 0);
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.putPacket(152);
		stream.putByte((offsetX << 4) | offsetY);
		stream.putShortA(item.getId());
		player.write(stream);
	}

	public void sendPlayerOnIComponent(int interfaceId, int componentId) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(46);
		buffer.putInt(interfaceId << 16 | componentId);
		player.write(buffer);
	}

	public void sendIComponentText(int interfaceId, int componentId, String text) {
		OutBuffer buffer = new OutBuffer();
		buffer.putVarShort(34);
		buffer.putString(text);
		buffer.putLEInt(interfaceId << 16 | componentId);
		buffer.finishVarShort();
		player.write(buffer);
	}

	public void sendIComponentAnimation(int animationId, int interfaceId, int componentId) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(62);
		buffer.putIntA(animationId);
		buffer.putLEInt(interfaceId << 16 | componentId);
		player.write(buffer);
	}

	public void sendNPCOnIComponent(int interfaceId, int componentId, int npcId) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(91);
		buffer.putIntB(npcId);
		buffer.putIntA(interfaceId << 16 | componentId);
		player.write(buffer);
	}

	public void sendIComponentModel(int interfaceId, int componentId, int modelId) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(26);
		buffer.putIntA(modelId);
		buffer.putIntA(interfaceId << 16 | componentId);
		player.write(buffer);
	}

	public void sendMinimapFlag(int x, int y) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(6);
		buffer.putA(x);
		buffer.putS(y);
		player.write(buffer);
	}

	public void sendResetMinimapFlag() {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(6);
		buffer.putA(255);
		buffer.putS(255);
		player.write(buffer);
	}

	public void sendGlobalConfig(int id, int value) {
		OutBuffer buffer = new OutBuffer();
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			buffer.putPacket(41);
			buffer.putLEShortA(id);
			buffer.putInt(value);
		} else {
			buffer.putPacket(2);
			buffer.putLEShortA(id);
			buffer.putC(value);
		}
		player.write(buffer);
	}

	public void sendConfigByFile(int id, int value) {
		OutBuffer buffer = new OutBuffer();
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			buffer.putPacket(3);
			buffer.putIntB(value);
			buffer.putLEShort(id);
		} else {
			buffer.putPacket(50);
			buffer.putShort(id);
			buffer.putC(value);
		}
		player.write(buffer);
	}

	public void sendPowersTab() {
		sendConfig(3708, 34219010);
		sendInterface(true, 1448, 3, 1457);
		sendIComponentSettings(1457, 9, 0, 28, 10223618);
		sendInterface(true, 1448, 5, 1454);
		sendInterface(true, 1448, 7, 1439);
		sendInterface(true, 1448, 9, 1436);
		sendIComponentSettings(1430, 97, -1, -1, 11108350);
		sendIComponentSettings(1430, 101, -1, -1, 11108350);
		sendIComponentSettings(1430, 102, -1, -1, 2098176);
		sendIComponentSettings(1430, 106, -1, -1, 2098176);
		sendIComponentSettings(1430, 107, -1, -1, 2098176);
		sendIComponentSettings(1430, 111, -1, -1, 2098176);
		sendIComponentSettings(1430, 112, -1, -1, 2098176);
		sendIComponentSettings(1430, 116, -1, -1, 2098176);
		sendIComponentSettings(1430, 117, -1, -1, 2098176);
		sendIComponentSettings(1430, 121, -1, -1, 2098176);
		sendIComponentSettings(1430, 122, -1, -1, 2098176);
		sendIComponentSettings(1430, 126, -1, -1, 2098176);
		sendIComponentSettings(1430, 127, -1, -1, 2098176);
		sendIComponentSettings(1430, 131, -1, -1, 2098176);
		sendIComponentSettings(1430, 132, -1, -1, 2098176);
		sendIComponentSettings(1430, 136, -1, -1, 2098176);
		sendIComponentSettings(1430, 137, -1, -1, 2098176);
		sendIComponentSettings(1430, 141, -1, -1, 2098176);
		sendIComponentSettings(1430, 142, -1, -1, 2098176);
		sendIComponentSettings(1430, 146, -1, -1, 2098176);
		sendIComponentSettings(1430, 147, -1, -1, 2098176);
		sendIComponentSettings(1430, 151, -1, -1, 2098176);
		sendIComponentSettings(1430, 152, -1, -1, 2098176);
		sendIComponentSettings(1430, 156, -1, -1, 2098176);
		sendIComponentSettings(1436, 12, -1, -1, 11108350);
		sendIComponentSettings(1436, 16, -1, -1, 11108350);
		sendIComponentSettings(1436, 22, -1, -1, 2098176);
		sendIComponentSettings(1436, 26, -1, -1, 2098176);
		sendIComponentSettings(1436, 32, -1, -1, 2098176);
		sendIComponentSettings(1436, 36, -1, -1, 2098176);
		sendIComponentSettings(1436, 42, -1, -1, 2098176);
		sendIComponentSettings(1436, 46, -1, -1, 2098176);
		sendIComponentSettings(1436, 52, -1, -1, 2098176);
		sendIComponentSettings(1436, 56, -1, -1, 2098176);
		sendIComponentSettings(1436, 62, -1, -1, 2098176);
		sendIComponentSettings(1436, 66, -1, -1, 2098176);
		sendIComponentSettings(1436, 72, -1, -1, 2098176);
		sendIComponentSettings(1436, 76, -1, -1, 2098176);
		sendIComponentSettings(1436, 82, -1, -1, 2098176);
		sendIComponentSettings(1436, 86, -1, -1, 2098176);
		sendIComponentSettings(1436, 92, -1, -1, 2098176);
		sendIComponentSettings(1436, 96, -1, -1, 2098176);
		sendIComponentSettings(1436, 102, -1, -1, 2098176);
		sendIComponentSettings(1436, 106, -1, -1, 2098176);
		sendIComponentSettings(1436, 112, -1, -1, 2098176);
		sendIComponentSettings(1436, 116, -1, -1, 2098176);
		sendIComponentSettings(1436, 122, -1, -1, 2098176);
		sendIComponentSettings(1436, 126, -1, -1, 2098176);
		sendIComponentSettings(1458, 24, 0, 28, 8388610);
		sendIComponentSettings(1457, 9, 0, 28, 8388610);
		sendIComponentSettings(1430, 4, -1, -1, 8388608);
		sendIComponentSettings(1465, 4, -1, -1, 8388608);
		sendIComponentSettings(1430, 2, -1, -1, 8650754);
		sendIComponentSettings(1430, 5, -1, -1, 8388608);
		sendIComponentSettings(1460, 1, 0, 168, 8485894);
		sendIComponentSettings(1452, 1, 0, 168, 8485894);
		sendIComponentSettings(1461, 1, 0, 168, 8485894);
		sendIComponentSettings(1449, 1, 0, 168, 8485894);
		sendIComponentSettings(590, 8, 0, 166, 8388614);
	}

	public void sendProjectile(Entity receiver, Location start, Location end, int gfxId, int startHeight, int endHeight, int speed, int delay, int curve, int startDistanceOffset, int creatorSize) {
		sendProjectile(start, creatorSize, creatorSize, end, receiver != null ? receiver.getSize() : 1, receiver != null ? receiver.getSize() : 1, receiver, gfxId, startHeight, endHeight, delay, (Utilities.getDistance(start.getX(), start.getY(), end.getX(), end.getY()) * 30 / ((speed / 10) < 1 ? 1 : (speed / 10))), startDistanceOffset, curve);
	}

	private void sendProjectile(Location from, int fromSizeX, int fromSizeY, Location to, int toSizeX, int toSizeY, Entity lockOn, int gfxId, int startHeight, int endHeight, int delay, int speed, int slope, int angle) {
		Location src = new Location(((from.getX() << 1) + fromSizeX) >> 1, ((from.getY() << 1) + fromSizeY) >> 1, from.getPlane());
		Location dst = new Location(((to.getX() << 1) + toSizeX) >> 1, ((to.getY() << 1) + toSizeY) >> 1, to.getPlane());

		OutBuffer buffer = createLocationBuffer(src);
		buffer.putPacket(124);

		buffer.putByte(((src.getX() & 0x7) << 3) | (src.getY() & 0x7));
		buffer.putByte(dst.getX() - src.getX());
		buffer.putByte(dst.getY() - src.getY());
		buffer.putShort(lockOn == null ? 0 : (lockOn instanceof Player ? -(lockOn.getIndex() + 1) : lockOn.getIndex() + 1));
		buffer.putShort(gfxId);
		buffer.putByte(startHeight);
		buffer.putByte(endHeight);
		buffer.putShort(delay);
		buffer.putShort(delay + speed);
		buffer.putByte(angle);
		buffer.putShort(angle);
		buffer.putShort(-1);

		player.write(buffer);
	}

	public void sendPublicChatMessage(Player p, PublicChatMessage publicMessage) {
		OutBuffer stream = new OutBuffer();
		stream.putVarByte(147);
		stream.putShort(p.getIndex());
		stream.putShort(publicMessage.getEffectHash());
		stream.putByte(p.getRights());
		byte[] chatStr = new byte[250];
		chatStr[0] = (byte) publicMessage.getMessage().length();
		int offset = 1 + Huffman.encryptMessage(1, publicMessage.getMessage().length(), chatStr, 0, publicMessage.getMessage().getBytes());
		stream.putBytes(chatStr, 0, offset);
		stream.finishVarByte();
		player.write(stream);
	}
	
	public void sendURL(String url) {
		OutBuffer stream = new OutBuffer();
		stream.putVarShort(56);
		stream.putByte(0);
		stream.writeString(url);
		stream.finishVarShort();
		player.write(stream);
	}
	
}