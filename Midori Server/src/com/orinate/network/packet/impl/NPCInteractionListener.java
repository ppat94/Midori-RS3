package com.orinate.network.packet.impl;

import com.orinate.Constants;
import com.orinate.cache.parsers.NPCDefinitions;
import com.orinate.game.World;
import com.orinate.game.content.skills.impl.divination.Wisp;
import com.orinate.game.content.skills.impl.fishing.FishDefinitions.SpotDefinitions;
import com.orinate.game.content.skills.impl.fishing.Fishing;
import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.route.RouteEvent;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * Handles the NPC interaction packets.
 * 
 * @author Emperor
 * 
 */
public final class NPCInteractionListener implements PacketListener {

	/**
	 * The attack opcode.
	 */
	private static final int ATTACK_OPCODE = 65;
	
	/**
	 * The examine opcode.
	 */
	private static final int EXAMINE_NPC = 28;
	private static final int OPTION_1 = 4;
	private static final int OPTION_2 = 33;

	@Override
	public void handle(final Player player, int opcode, InBuffer buffer) {
		int index = buffer.getShortA();
		boolean running = buffer.getByteS() == 1;
		if (player.isLocked())
			return;
		if (index < 0 || index > World.getNpcs().size()) {
			if (Constants.VERBOSE_MODE)
				System.err.println("[NPC interaction]: Invalid NPC index - " + index + ", " + running + ".");
			return;
		}
		final NPC npc = World.getNpcs().get(index);
		if (npc == null) {
			if (Constants.VERBOSE_MODE)
				System.err.println("[NPC interaction]: Invalid NPC - " + index + ", " + running + ".");
			return;
		}
		switch (opcode) {
			case OPTION_1:
				player.setRouteEvent(new RouteEvent(npc, new Runnable() {
					@Override
					public void run() {
						player.setFaceEntity(npc);
						NPCDefinitions npcDefinitions = NPCDefinitions.forId(npc.getId());
						SpotDefinitions definitions = SpotDefinitions.getDefinition(npc, npcDefinitions.options[0]);
						if (definitions != null) {
							player.getSkillManager().train(new Fishing(player, definitions));
							return;
						}
						if (player.getDialogueManager().startDialogue(npc.getId())) {
							return;
						}
						if (npc instanceof Wisp) {
							Wisp wisp = (Wisp) npc;
							wisp.harvest(player);
						}
					}
				}));
			break;
			case OPTION_2:
				player.setRouteEvent(new RouteEvent(npc, new Runnable() {
					@Override
					public void run() {
						player.setFaceEntity(npc);
						NPCDefinitions npcDefinitions = NPCDefinitions.forId(npc.getId());
						SpotDefinitions definitions = SpotDefinitions.getDefinition(npc, npcDefinitions.options[2]);
						if (definitions != null) {
							player.getSkillManager().train(new Fishing(player, definitions));
							return;
						}
					}
				}));
			break;
			case ATTACK_OPCODE:
				player.getCombatSchedule().attack(npc);
			break;
			case EXAMINE_NPC:
				String examine = "It's a " + NPCDefinitions.forId(npc.getId()).name + ".";
				player.getWriter().sendGameMessage(examine);
			break;
			default:
				if (Constants.VERBOSE_MODE) {
					System.err.println("[NPC interaction]: Unhandled opcode - " + opcode + ".");
				}
			break;
		}
	}

	@Override
	public boolean register() {
		PacketRepository.register(OPTION_1, this);
		PacketRepository.register(ATTACK_OPCODE, this);
		PacketRepository.register(33, this);
		PacketRepository.register(61, this);
		PacketRepository.register(35, this);
		PacketRepository.register(EXAMINE_NPC, this);
		return true;
	}

}