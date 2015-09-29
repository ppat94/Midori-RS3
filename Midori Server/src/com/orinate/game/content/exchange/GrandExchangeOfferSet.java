package com.orinate.game.content.exchange;

import java.io.Serializable;

import com.orinate.game.content.exchange.GrandExchangeOffer.OfferState;
import com.orinate.io.OutBuffer;

/**
 * @author Taylor
 */
public class GrandExchangeOfferSet implements Serializable {

	private static final long serialVersionUID = 7510597850942798875L;
	private GrandExchangeOffer[] offers = new GrandExchangeOffer[6];
	
	public void setOffer(int slot, GrandExchangeOffer offer) {
		offers[slot] = offer;
	}
	
	public GrandExchangeOffer getOffer(int slot) {
		return offers[slot];
	}
	
	public int getSlot(GrandExchangeOffer offer) {
		for (int geOffer = 0; geOffer < offers.length; geOffer++) {
			if (offers[geOffer] == null) {
				continue;
			}
			if (offers[geOffer].equals(offer)) {
				return geOffer;
			}
 		}
		return -1;
	}
	
	public void removeOffer(GrandExchangeOffer offer) {
		offers[getSlot(offer)] = null;
	}
	
	public void removeOffer(int slot) {
		offers[slot] = null;
	}
	
	public boolean hasOffers() {
		for (GrandExchangeOffer offer : offers) {
			if (offer != null) {
				return true;
			}
		}
		return false;
	}

	public OutBuffer offerToPacket(GrandExchangeOffer offer) {
		OutBuffer buffer = new OutBuffer();
		buffer.putPacket(17);
		buffer.putByte(getSlot(offer));
		buffer.putByte(offer.getState().equals(OfferState.PENDING) ? 1 : offer.getState().equals(OfferState.ABORTED) ? -3 : offer.getState().equals(OfferState.FINISHED) ? 5 : 4);
		buffer.putShort(offer == null ? -1 : offer.getItem().getId());
		buffer.putInt(offer == null ? -1 : offer.getPrice());
		buffer.putInt(offer == null ? -1 : offer.getItem().getAmount());
		buffer.putInt(offer == null ? -1 : offer.getAmountTransacted());
		buffer.putInt(offer == null ? -1 : offer.getMoneyRecieved());
		return buffer;
	}
	
	public GrandExchangeOffer[] toArray() {
		return offers;
	}
}
