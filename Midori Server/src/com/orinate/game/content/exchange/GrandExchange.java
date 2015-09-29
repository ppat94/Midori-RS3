package com.orinate.game.content.exchange;

import java.io.Serializable;

import com.orinate.game.World;
import com.orinate.game.content.exchange.GrandExchangeOffer.OfferState;
import com.orinate.game.content.exchange.GrandExchangeOffer.OfferType;
import com.orinate.game.model.player.Player;

/**
 * @author Taylor Moon
 */
public class GrandExchange implements Serializable {

	private static final long serialVersionUID = 1L;

	private Player player;

	private GrandExchangeOffer offer;

	private GrandExchangeOfferSet offers = new GrandExchangeOfferSet();

	public GrandExchange(Player player) {
		this.player = player;
	}

	public void open() {
		refresh();
		player.getInterfaceManager().sendInterface(GrandExchangeConstants.MAIN_INTERFACE);
		player.getWriter().sendConfig(GrandExchangeConstants.SWITCH_SCREEN_CONFIG, -1);
		player.getWriter().sendConfigByFile(199, -1);
		player.getWriter().sendConfigByFile(3678, -1);
	}

	public void handleWidgets(int interfaceId, int widgetId) {
		offer = ((GrandExchangeOffer) player.getAttributes().get("grand_exchange_offer"));
		player.getWriter().sendGameMessage("Button: " + widgetId);
		switch (widgetId) {
		case 170:
		case 181:
		case 193:
		case 206:
		case 222:
		case 238:
			openPurchaseScreen(getSlotForWidget(widgetId, false));
			player.getAttributes().set("viewing_offer", getSlotForWidget(widgetId, false));
			break;
		case 175:
		case 187:
		case 199:
		case 212:
		case 228:
		case 244:
			openSellScreen(getSlotForWidget(widgetId, false));
			player.getAttributes().set("viewing_offer", getSlotForWidget(widgetId, false));
			break;
		case 164:
			confirmOffer(offer, (int) player.getAttributes().get("viewing_offer"));
			player.getAttributes().remove("viewing_offer");
			player.getAttributes().remove("grand_exchange_offer");
			break;
		case 28:
		case 30:
		case 33:
		case 34:
		case 36:
			openCollectionScreen(getSlotForWidget(widgetId, true));
			break;
		}
	}

	public void update() {
		if (!offers.hasOffers()) {
			GrandExchangeDatabase.unRegister(this);
		}
		for (GrandExchangeOffer offer : offers.toArray()) {
			if (offer == null) {
				continue;
			}
			switch (offer.getState()) {
			case FINISHED:
				if (offer.wasCollected()) {
					offers.removeOffer(offer);
					player.write(offers.offerToPacket(offer));
					return;
				}
				break;
			case PENDING:
				// jagex uses this state to do GE database shit.
				offer.setState(OfferState.PROGRESSING);
				offer.setNeedsUpdate(true);
				break;
			case PROGRESSING:
				if (offer.needsUpdate()) {
					player.write(offers.offerToPacket(offer));
					offer.setNeedsUpdate(false);
				}
				checkOffers();
				if (offer.needsUpdate()) {
					player.getWriter().sendGameMessage("<col=66ffff>Grand Exchange: Finished " + offer.getType().toString().toLowerCase() + "ing " + offer.getAmountTransacted() + "x " + offer.getItem().getDefinitions().getName().toLowerCase() + ".");
					player.write(offers.offerToPacket(offer));
					offer.setNeedsUpdate(false);
				}
				break;
			case ABORTED:
				player.write(offers.offerToPacket(offer));
				break;
			default:
				break;
			}
		}
	}

	private void refresh() {
		player.getWriter().sendConfig(GrandExchangeConstants.SWITCH_SCREEN_CONFIG, -1);
		player.getWriter().sendConfig(GrandExchangeConstants.ITEM_ID_CONFIG, -1);
		player.getWriter().sendConfig(GrandExchangeConstants.MAIN_SCREEN_CONFIG, -1);
		player.getWriter().sendConfig(GrandExchangeConstants.PRICE_PER_CONFIG, 0);
		player.getWriter().sendConfig(GrandExchangeConstants.QUANITY_CONFIG, 0);
	}

	private void openPurchaseScreen(int slot) {
		refresh();
		player.getWriter().sendConfigByFile(741, -1);
		player.getWriter().sendConfigByFile(743, -1);
		player.getWriter().sendIComponentSettings(449, 21, -1, -1, 0);
		player.getWriter().sendConfigByFile(744, 0);
		player.getWriter().sendIComponentSettings(449, 21, -1, -1, 0);
		player.getWriter().sendConfig(GrandExchangeConstants.SWITCH_SCREEN_CONFIG, slot);
		player.getWriter().sendConfig(GrandExchangeConstants.MAIN_SCREEN_CONFIG, slot + 1);
		player.getWriter().sendInterface(true, 1477, 234, 1418);
		player.getWriter().sendInterface(true, 1418, 0, 389);
		player.getWriter().sendCS2Script(570, "Grand Exchange Item Search");
	}

	private void openSellScreen(int slot) {
		refresh();
		player.getWriter().sendConfig(GrandExchangeConstants.MAIN_SCREEN_CONFIG, 0);
		player.getWriter().sendConfig(GrandExchangeConstants.SWITCH_SCREEN_CONFIG, 1);
		player.getWriter().sendIComponentSettings(107, 18, 0, 28, 1026);
		player.getWriter().sendContainerUpdate(4, player.getInventory().getItems());
	}

	private void openCollectionScreen(int slot) {
		GrandExchangeOffer offer = offers.getOffer(slot);
		if (offer == null) {
			player.getWriter().sendGameMessage("An error occured - Please wait 60 seconds and try again.");
			return;
		}
		player.getWriter().sendConfig(GrandExchangeConstants.MAIN_SCREEN_CONFIG, slot);
		player.getWriter().sendConfig(GrandExchangeConstants.SWITCH_SCREEN_CONFIG,  offer.getType().equals(OfferType.SELL) ? 0 : 1);
	}
	
	public void confirmOffer(GrandExchangeOffer offer, int slot) {
		player.getWriter().sendGameMessage("Slot: "+slot);
		if (offer == null) {
			return;
		}
		if (offers.getOffer(slot) != null) {
			player.getWriter().sendGameMessage("An error occured - Please wait 60 seconds and try again.");
			return;
		}
		switch (offer.getType()) {
		case BUY:
			if (player.getAvailableMoney() < offer.getPrice()) {
				player.getWriter().sendGameMessage("You don't have enough gold for this item!");
				return;
			}
			break;
		case SELL:
			if (player.getInventory().getNumberOf(offer.getItem().getId()) < offer.getItem().getAmount()) {
				player.getWriter().sendGameMessage("You don't have enough of this item to sell!");
				return;
			}
			break;
		default:
			break;
		}
		if (!offers.hasOffers()) {
			GrandExchangeDatabase.register(this);
		}
		offers.setOffer(slot, offer);
		player.write(offers.offerToPacket(offer));
		refresh();
	}

	private int getSlotForWidget(int widget, boolean collection) {
		if (collection) {
			switch (widget) {
			case 28:
				return 0;
			case 30:
				return 1;
			case 33:
				return 2;
			case 34:
				return 3;
			case 36:
				return 4;
			}
			return -1;
		}
		switch (widget) {
		case 170:
		case 175:
			return 0;
		case 181:
		case 187:
			return 1;
		case 193:
		case 199:
			return 2;
		case 206:
		case 213:
			return 3;
		case 222:
		case 228:
			return 4;
		case 238:
		case 244:
			return 5;
		}
		return -1;
	}

	private boolean checkOffers() {
		//TODO load via serialization
		for (Player player : World.getPlayers()) {
			for (GrandExchangeOffer offer : player.getGrandExchange().getOfferSet().toArray()) {
				for (GrandExchangeOffer myOffer : offers.toArray()) {
					if (myOffer == null || offer == null) {
						continue;
					}
					if (offer.getItem().getId() == myOffer.getItem().getId()) {
						if (offer.getItem().getAmount() > myOffer.getItem().getAmount()) {
							myOffer.setAmountTransacted(myOffer.getItem().getAmount());
							myOffer.setState(OfferState.FINISHED);
							myOffer.setNeedsUpdate(true);
							offer.setAmountTransacted(myOffer.getItem().getAmount());
							offer.setNeedsUpdate(true);
							return true;
						} else if(offer.getItem().getAmount() == myOffer.getItem().getAmount()) {
							myOffer.setAmountTransacted(myOffer.getItem().getAmount());
							myOffer.setState(OfferState.FINISHED);
							myOffer.setNeedsUpdate(true);
							offer.setAmountTransacted(offer.getItem().getAmount());
							offer.setState(OfferState.FINISHED);
							offer.setNeedsUpdate(true);
							return true;
						} else if(offer.getItem().getAmount() < myOffer.getItem().getAmount()) {
							offer.setAmountTransacted(offer.getItem().getAmount());
							offer.setState(OfferState.FINISHED);
							offer.setNeedsUpdate(true);
							myOffer.setAmountTransacted(offer.getItem().getAmount());
							myOffer.setNeedsUpdate(true);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public GrandExchangeOfferSet getOfferSet() {
		return offers;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public GrandExchangeOffer getOffer() {
		return offer;
	}
}
