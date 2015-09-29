package com.orinate.game.content.exchange;

import com.orinate.game.model.container.Item;

/**
 * @author Taylor
 */
public class GrandExchangeOffer {

	public enum OfferType {BUY, SELL}

	public enum OfferState {PENDING, PROGRESSING, ABORTED, FINISHED}

	private final Item item;

	private int amountTransacted;

	private int amountCollected;

	private int price;

	private boolean wasCollected;

	private boolean needsUpdate;

	private OfferType type;

	private OfferState state = OfferState.PENDING;

	public GrandExchangeOffer(Item item, int price, OfferType type) {
		this.item = item;
		this.price = price;
		this.type = type;
	}

	public Item getItem() {
		return item;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public OfferType getType() {
		return type;
	}

	public void setType(OfferType type) {
		this.type = type;
	}

	/**
	 * @return the state
	 */
	public OfferState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(OfferState state) {
		this.state = state;
	}

	/**
	 * @return the wasCollected
	 */
	public boolean wasCollected() {
		return wasCollected;
	}

	/**
	 * @param wasCollected
	 *            the wasCollected to set
	 */
	public void setWasCollected(boolean wasCollected) {
		this.wasCollected = wasCollected;
	}

	/**
	 * @return the amountTransacted
	 */
	public int getAmountTransacted() {
		return amountTransacted;
	}

	/**
	 * @param amountTransacted
	 *            the amountTransacted to set
	 */
	public void setAmountTransacted(int amountTransacted) {
		this.amountTransacted = amountTransacted;
	}

	/**
	 * @return the amountCollected
	 */
	public int getAmountCollected() {
		return amountCollected;
	}

	/**
	 * @param amountCollected
	 *            the amountCollected to set
	 */
	public void setAmountCollected(int amountCollected) {
		this.amountCollected = amountCollected;
	}

	public int getMoneyRecieved() {
		return item.getDefinitions().getValue() * (amountTransacted - amountCollected);
	}

	/**
	 * @return the needsUpdate
	 */
	public boolean needsUpdate() {
		return needsUpdate;
	}

	/**
	 * @param needsUpdate
	 *            the needsUpdate to set
	 */
	public void setNeedsUpdate(boolean needsUpdate) {
		this.needsUpdate = needsUpdate;
	}
}
