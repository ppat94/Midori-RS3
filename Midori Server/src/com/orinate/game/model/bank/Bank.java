package com.orinate.game.model.bank;

import java.io.Serializable;

import com.orinate.cache.parsers.IComponentDefinitions;
import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.container.ItemContainer;
import com.orinate.game.model.player.Player;

/**
 * @author Tyler
 * @author Aero
 */
public class Bank implements Serializable {

	private static final long serialVersionUID = 7600702131568125187L;
	public static final int MAX_BANK_SIZE = 503;

	private transient Player player;
	private ItemContainer<Item> bankItems;
	private Tab[] bankTabs;

	public Bank(Player player) {
		this.player = player;
		bankItems = new ItemContainer<Item>(MAX_BANK_SIZE, true);
		this.bankTabs = new Tab[10];
	}

	public void initBankSession() {
		player.getWriter().sendIComponentSettings(762, 39, 0, 871, 2622718);
		player.getWriter().sendIComponentSettings(762, 54, 0, 27, 2361214);
		player.getWriter().sendIComponentSettings(762, 132, 0, 27, 4260990);
		sendBankConfigs();
		player.getWriter().sendCS2Script(1465, "");
		player.getWriter().sendInterface(false, 1477, 13, 762);
		sendItems();
	}

	private void sendBankConfigs() {
		player.getWriter().sendConfig(110, -2013265920);
		player.getWriter().sendGlobalConfig(169, 1);
		player.getWriter().sendGlobalConfig(192, 577);
		player.getWriter().sendGlobalConfig(1038, 68);
		player.getWriter().sendGlobalConfig(96, 0);
		player.getWriter().sendGlobalConfig(95, 0);
		player.getWriter().sendGlobalConfig(1324, 3);
		player.getWriter().sendGlobalConfig(199, -1);
		player.getWriter().sendGlobalConfig(192, 577);
		player.getWriter().sendGlobalConfig(1038, 68);
		player.getWriter().sendGlobalConfig(96, 0);
		player.getWriter().sendGlobalConfig(95, 0);
	}

	public void deposit(Item item) {
		if (item.getId() > ItemDefinition.getSize() || item.getId() < 0)
			return;
		bankItems.add(item);
		sendItems();
	}

	public static void handleBankButtons(Player player, int interfaceId, int itemId, int buttonId, int opcode) {
		if (player == null)
			return;
		if (buttonId > IComponentDefinitions.getInterfaceDefinitionsComponentsSize(762))
			return;
		switch (opcode) {
		case WITHDRAW_1_OPCODE:
			player.getBank().withdraw(new Item(itemId, 1));
			break;
		}
		System.out.println(buttonId + " - " + opcode);
	}

	public void withdraw(Item item) {
		if (item.getId() > ItemDefinition.getSize() || item.getId() < 0)
			return;
		if (!player.getInventory().hasFreeSlots()) {
			player.getWriter().sendGameMessage("Your inventory is full.");
			return;
		}
		bankItems.remove(item);
		player.getInventory().addItem(item);
		sendItems();
	}

	public void sendItems() {
		/*
		 * for (Tab tab : bankTabs) { if (tab == null) continue;
		 * player.getWriter().sendContainerUpdate(95, tab.getTabItems()); }
		 */
		player.getWriter().sendContainerUpdate(95, bankItems);
	}

	public void refreshItems() {
		// copy from matrix since u use the same systems
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Tab[] getBankTabs() {
		return bankTabs;
	}

	public void setBankTabs(Tab[] bankTabs) {
		this.bankTabs = bankTabs;
	}

	public static final int WITHDRAW_1_OPCODE = 102;
}
