package com.orinate.network.packet.impl;

import com.orinate.cache.parsers.ObjectDefinition;
import com.orinate.game.World;
import com.orinate.game.content.skills.impl.mining.NormalMining;
import com.orinate.game.content.skills.impl.runecrafting.Runecrafting;
import com.orinate.game.content.skills.impl.woodcutting.Woodcutting;
import com.orinate.game.model.Location;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.region.WorldObject;
import com.orinate.game.model.route.RouteEvent;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

public class ObjectClickListener implements PacketListener {

	public static final int EXAMINE_OBJECT = 51;

	@Override
	public void handle(final Player player, int opcode, InBuffer buffer) {
		boolean running = buffer.getByteS() == 1;
		int baseX = buffer.getLEShort();
		int baseY = buffer.getLEShort();
		final int objectId = buffer.getLEInt();
		Location location = new Location(baseX, baseY, player.getLocation().getPlane());
		int regionId = location.getRegionId();
		if (player.isLocked() || !player.getMapRegionsIds().contains(regionId)) {
			return;
		}
		final WorldObject object = World.getWorld().getRegion(regionId).getObject(objectId, location);
		if (object == null || object.getId() != objectId) {
			return;
		}
		player.resetAll(true, true);
		if (running) {
			player.setRunning(running);
		}
		player.setFirstStep(true);
		switch (opcode) {
		case 3:
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					player.resetAll(true, true);
					player.setFaceLocation(new Location(object.getLocation().getCoordFaceX(ObjectDefinition.forId(object.getId()).sizeX, ObjectDefinition.forId(object.getId()).sizeY, object.getRotation()), object.getLocation().getCoordFaceY(ObjectDefinition.forId(object.getId()).sizeX, ObjectDefinition.forId(object.getId()).sizeY, object.getRotation()), player.getLocation().getPlane()));
					if (ObjectDefinition.forId(object.getId()).name.toLowerCase().contains("rock")) {
						NormalMining.handle(player, object, ObjectDefinition.forId(object.getId()).name.toLowerCase());
						return;
					}
					if (ObjectDefinition.forId(object.getId()).name.toLowerCase().equals("energy rift")) {
						player.getInterfaceManager().sendInterface(131);
						return;
					}
					switch (object.getId()) {
					case 70755:
					case 70756:
					case 70757:
					case 70758:
					case 70759:
					case 70760:
					case 70761:
					case 70762:
					case 70763:
					case 70764:
					case 70765:
					case 70766:
						// player.getSkillManager().train(new Bonfires(player,
						// object));
						break;
					}
					if (ObjectDefinition.forId(objectId).containsAction("Chop down")) {
						player.getSkillManager().train(new Woodcutting(player, object, Woodcutting.getTree(objectId)));
						return;
					}
					if (ObjectDefinition.forId(objectId).containsAction("craft-rune")) {
						player.getSkillManager().train(new Runecrafting(player, object, Runecrafting.getAltar(objectId)));
						return;
					}
					if (ObjectDefinition.forId(objectId).containsAction("enter") && ObjectDefinition.forId(objectId).name.contains("Mysterious ruins")) {
						Runecrafting.enterTemple(player, objectId);
						return;
					}
				}
			}));
			break;
		case 51:
			player.getWriter().sendResetMinimapFlag();
			player.getWriter().sendGameMessage("Object [ID=" + object.getId() + ", Loc=[X=" + object.getLocation().getX() + ", Y=" + object.getLocation().getY() + ", Plane=" + object.getLocation().getPlane() + "]]");
			break;
		}
		player.getWriter().sendGameMessage("Object [ID=" + object.getId() + ", Loc=[X=" + object.getLocation().getX() + ", Y=" + object.getLocation().getY() + ", Plane=" + object.getLocation().getPlane() + "]]");
	}

	@Override
	public boolean register() {
		return PacketRepository.register(this, 51, 3);
	}

}