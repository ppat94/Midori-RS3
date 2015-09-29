package com.orinate.network.packet.impl;

import com.orinate.game.content.exchange.GrandExchangeOffer;
import com.orinate.game.content.exchange.GrandExchangeOffer.OfferType;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

public class ExchangeSearchPacketListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		int id = buffer.getShort();
		Item item = new Item(id, 1);
		GrandExchangeOffer offer = new GrandExchangeOffer(item, item.getDefinitions().getValue(), OfferType.BUY);
		player.getAttributes().set("grand_exchange_offer", offer);
		player.getWriter().sendConfig(135, item.getId());
		player.getWriter().sendConfig(136, 1);
		player.getWriter().sendConfig(137, item.getDefinitions().getValue());
		player.getWriter().closeInterface(1477, 234);
	}

	@Override
	public boolean register() {
		return PacketRepository.register(72, this);
	}

}