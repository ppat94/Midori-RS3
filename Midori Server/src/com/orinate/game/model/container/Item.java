package com.orinate.game.model.container;

import java.io.Serializable;

import com.orinate.cache.parsers.ItemDefinition;

/**
 * @author Tom
 * 
 */
public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int amount;

	public Item(int id) {
		this.id = id;
		this.amount = 1;
	}

	public Item(int id, int amount) {
		if (amount < 0 || amount > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid amount specified.");
		this.id = id;
		this.amount = amount;
	}

	public ItemDefinition getDefinitions() {
		return ItemDefinition.forId(id);
	}

	public int getId() {
		return id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getName() {
		return getDefinitions().getName();
	}

	@Override
	public Item clone() {
		return clone(id, amount);
	}

	public Item clone(int id) {
		return clone(id, 1);
	}

	public Item clone(int id, int amount) {
		return new Item(id, amount);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Item))
			return super.equals(obj);
		Item item = (Item) obj;
		return getId() == item.getId() && getAmount() == item.getAmount();
	}

	@Override
	public String toString() {
		return "Item: " + getName() + ". Id: " + id + ". Amount: " + amount;
	}
}
