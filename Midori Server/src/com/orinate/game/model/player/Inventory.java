package com.orinate.game.model.player;

import java.io.Serializable;

import com.orinate.game.model.container.Item;
import com.orinate.game.model.container.ItemContainer;

/**
 * @author Tom
 * @author Tyler
 */
public class Inventory implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient Player player;
	private ItemContainer<Item> items;

	public Inventory(Player player) {
		this.player = player;
		this.items = new ItemContainer<Item>(28, false);
	}

	public boolean addItem(Item item) {
		if (item.getId() < 0 || item.getAmount() < 0)
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(item)) {
			item.setAmount(items.getFreeSlots());
			items.add(item);
			player.getWriter().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItem(int itemId, int amount) {
		return addItem(new Item(itemId, amount));
	}

	public void init() {
		player.getWriter().sendContainerUpdate(93, items);
	}

	public void refresh(int... slots) {
		player.getWriter().sendUpdateItems(93, items, slots);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getItems()[index]) {
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public Item get(int slotId) {
		return items.get(slotId);
	}

	public int getFreeSlots() {
		return items.getFreeSlots();
	}

	public int getNumberOf(int itemId) {
		return items.getNumberOf(itemId);
	}

	public boolean containsItem(int itemId, int amount) {
		return items.contains(new Item(itemId, amount));
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1)))
				return true;
		}
		return false;
	}

	public boolean hasFreeSlots() {
		return items.getFreeSlots() > 0;
	}

	public void set(int slotId, Item item) {
		items.set(slotId, item);
	}

	public void deleteItem(int itemId, int amount) {
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void deleteItem(Item item) {
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(item);
		refreshItems(itemsBefore);
	}

	public void deleteItem(int slot, Item item) {
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(slot, item);
		refreshItems(itemsBefore);
	}

	public int getContainerSize() {
		return items.getSize();
	}

	public void switchItem(int fromSlot, int toSlot) {
		Item[] itemsBefore = items.getItemsCopy();
		Item fromItem = items.get(fromSlot);
		Item toItem = items.get(toSlot);
		items.set(fromSlot, toItem);
		items.set(toSlot, fromItem);
		refreshItems(itemsBefore);
	}

	public void clear() {
		items.clear();
		init();
	}

	public ItemContainer<Item> getItems() {
		return items;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
