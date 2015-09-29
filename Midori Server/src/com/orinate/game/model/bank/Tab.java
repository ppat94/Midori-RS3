package com.orinate.game.model.bank;

import com.orinate.game.model.container.Item;
import com.orinate.game.model.container.ItemContainer;

public class Tab {

	private int tabIndex;
	private ItemContainer<Item> tabItems;

	public Tab(int tabIndex) {
		this.tabIndex = tabIndex;
		this.tabItems = new ItemContainer<Item>(Bank.MAX_BANK_SIZE, true);
	}

	public ItemContainer<Item> getTabItems() {
		return tabItems;
	}

	public int getTabIndex() {
		return tabIndex;
	}
}