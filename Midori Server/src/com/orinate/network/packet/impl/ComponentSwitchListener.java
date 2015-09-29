package com.orinate.network.packet.impl;

import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.keybind.Keybind;
import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * @author Tom
 * @author Emperor
 * 
 */
public class ComponentSwitchListener implements PacketListener {

	private static final int[] SLOTS = new int[] { 97, 102, 107, 112, 117, 122, 127, 132, 137, 142, 147, 152 };

	private static final int INVENTORY_INTERFACE = 1473;
	private static final int DEFENCE_INTERFACE = 1449;
	private static final int RANGED_INTERFACE = 1452;
	private static final int MAGIC_INTERFACE = 1461;
	private static final int MELEE_INTERFACE = 1460;
	private static final int BANK_INTERFACE = 762;

	public ComponentSwitchListener() {
	}

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		int toInterfaceHash = buffer.getIntB();
		int fromInterfaceHash = buffer.getIntB();

		buffer.getLEShort();
		int toSlot = buffer.getShortA();
		buffer.getLEShortA();
		int fromSlot = buffer.getShortA();

		int toInterfaceId = toInterfaceHash >> 16;
		int toComponentId = toInterfaceHash - (toInterfaceId << 16);

		int fromInterfaceId = fromInterfaceHash >> 16;
		int fromComponentId = fromInterfaceHash - (fromInterfaceId << 16);
		if (player.isLocked()) {
			return;
		}
		if (fromInterfaceId == BANK_INTERFACE && toInterfaceId == BANK_INTERFACE) {

			return;
		}
		if (fromInterfaceId == INVENTORY_INTERFACE && fromComponentId == 8 && toInterfaceId == INVENTORY_INTERFACE && toComponentId == 8) {
			if (toSlot < 0 || toSlot >= player.getInventory().getContainerSize() || fromSlot >= player.getInventory().getContainerSize()) {
				return;
			}
			player.getInventory().switchItem(fromSlot, toSlot);
		} else if (isAbilityInterface(fromInterfaceId) && isAbilityBar(toInterfaceId)) {
			System.out.println("Ability id: " + fromInterfaceId + ", " + fromSlot);
			Ability ability = AbilityRepository.getMapping().get(fromInterfaceId << 16 | fromSlot);
			if (ability != null) {
				int slot = -1;
				for (int i = 0; i < SLOTS.length; i++) {
					if (SLOTS[i] == toComponentId) {
						slot = i;
					}
				}
				if (player.getActionBar().isLocked()) {
					player.getActionBar().refresh(slot);
					return;
				}
				Keybind bind = new Keybind(slot);
				bind.bind(fromInterfaceId << 16 | fromSlot);
				player.getActionBar().getActiveBar().set(slot, bind);
				player.getActionBar().refresh(slot);
				player.getWriter().sendGameMessage(ability.getClass().getSimpleName() + " added.");
			}
		}
	}

	public boolean isAbilityInterface(int interfaceId) {
		return (interfaceId == DEFENCE_INTERFACE) || (interfaceId == RANGED_INTERFACE) || (interfaceId == MAGIC_INTERFACE) || (interfaceId == MELEE_INTERFACE);
	}

	public boolean isAbilityBar(int interfaceId) {
		return interfaceId == 1430;
	}

	@Override
	public boolean register() {
		return PacketRepository.register(24, this);
	}
}