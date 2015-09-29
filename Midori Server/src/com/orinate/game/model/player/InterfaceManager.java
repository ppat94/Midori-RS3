package com.orinate.game.model.player;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Trent
 * 
 */
public class InterfaceManager {

	public static final int CHATBOX_AREA = 234;
	public static final int WINDOW_ID = 1477;
	public static final int SCREEN_AREA = 238;
	public static final int INVENTORY_AREA = 13;

	private Player player;
	private ConcurrentHashMap<Integer, int[]> openInterfaces;

	public InterfaceManager(Player player) {
		this.player = player;
		this.openInterfaces = new ConcurrentHashMap<Integer, int[]>();
	}

	public boolean addInterface(int windowId, int tabId, int childId) {
		if (openInterfaces.containsKey(tabId)) {
			player.getWriter().closeInterface(tabId);
		}
		openInterfaces.put(tabId, new int[] { childId, windowId });
		return openInterfaces.get(tabId)[0] == childId;
	}

	public boolean containsInterface(int tabId, int childId) {
		if (childId == WINDOW_ID) {
			return true;
		}
		if (!openInterfaces.containsKey(tabId)) {
			return false;
		}
		return openInterfaces.get(tabId)[0] == childId;
	}

	public void sendInterface(int interfaceId) {
		player.getWriter().sendInterface(false, WINDOW_ID, SCREEN_AREA, interfaceId);
	}

	public void sendInventoryInterface(int interfaceId) {
		player.getWriter().sendInterface(false, WINDOW_ID, INVENTORY_AREA, interfaceId);
	}

	public void sendChatBoxInterface(int interfaceId) {
		player.getWriter().sendInterface(true, WINDOW_ID, CHATBOX_AREA, interfaceId);
	}

	public boolean containsScreenInterface() {
		return containsTab(SCREEN_AREA);
	}

	public boolean containsChatboxInterface() {
		return containsTab(CHATBOX_AREA);
	}

	public boolean containsInventoryInterface() {
		return containsTab(INVENTORY_AREA);
	}

	private boolean containsTab(int tabId) {
		return openInterfaces.containsKey(tabId);
	}

	public void closeChatboxInterface() {
		player.getWriter().closeInterface(CHATBOX_AREA);
	}

	public void closeInventoryInterface() {
		player.getWriter().closeInterface(INVENTORY_AREA);
	}

	public void closeScreenInterface() {
		player.getWriter().closeInterface(SCREEN_AREA);
	}

	public int getTabWindow(int tabId) {
		if (!openInterfaces.containsKey(tabId)) {
			return WINDOW_ID;
		}
		return openInterfaces.get(tabId)[1];
	}

	public boolean removeTab(int tabId) {
		return openInterfaces.remove(tabId) != null;
	}

	public boolean containsInterface(int childId) {
		if (childId == 1477) {
			return true;
		}
		for (int[] value : openInterfaces.values()) {
			if (value[0] == childId) {
				return true;
			}
		}
		return false;
	}
}
