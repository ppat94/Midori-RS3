package com.orinate.network.packet.impl;

import com.orinate.game.World;
import com.orinate.game.content.skills.Skill;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.route.EntityStrategy;
import com.orinate.game.model.route.RouteEvent;
import com.orinate.game.model.route.RouteFinder;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * 
 * @author Tyler
 * 
 */
public class PlayerOptionPacketListener implements PacketListener {

	private static final int FOLLOW_PLAYER_OPCODE = 45;

	@Override
	public void handle(final Player player, int opcode, InBuffer buffer) {
		int indexId = buffer.getShort();
		final boolean running = buffer.getByteA() == 1;
		if (indexId > World.getPlayers().size() || player.isLocked())
			return;
		final Player localPlayer = World.getPlayers().get(indexId);
		if (localPlayer == null)
			return;
		if (opcode == FOLLOW_PLAYER_OPCODE) {
			if (!player.isRunning() && running)
				player.setRunning(true);
			player.setFirstStep(true);
			player.setFaceEntity(localPlayer);
			player.setRouteEvent(new RouteEvent(localPlayer, new Runnable() {

				@Override
				public void run() {
					player.getSkillManager().train(new Skill(player) {

						@Override
						public boolean onSkillStart() {
							localPlayer.getAttributes().set("followed_by", player);
							return true;
						}

						@Override
						public boolean canProcess() {
							return localPlayer.getLocation().withinDistance(player.getLocation(), 20) && (localPlayer.getLocation().getPlane() == player.getLocation().getPlane());
						}

						@Override
						public boolean onProcess() {
							int lastFaceEntity = localPlayer.getLastFaceEntity();
							if (lastFaceEntity == player.getClientIndex() && localPlayer.getAttributes().get("followed_by") == player) {
								player.addWalkSteps(localPlayer.getLocation().getX(), localPlayer.getLocation().getY(), 25, true);
								localPlayer.addWalkSteps(player.getLocation().getX(), player.getLocation().getY(), 25, true);
							} else {
								int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getPlane(), player.getSize(), new EntityStrategy(localPlayer), true);
								if (steps == -1)
									return false;
								if (steps > 0) {
									player.resetWalkSteps();
									int[] bufferX = RouteFinder.getLastPathBufferX();
									int[] bufferY = RouteFinder.getLastPathBufferY();
									for (int step = steps - 1; step >= 0; step--) {
										if (!player.addWalkSteps(bufferX[step], bufferY[step], 25, true))
											break;
									}
								}
								return true;
							}
							return true;
						}

						@Override
						public void onSkillEnd() {
							player.setFaceEntity(null);
						}

						@Override
						public int getSkillDelay() {
							return 0;
						}
					});
				}
			}));
		}
	}

	@Override
	public boolean register() {
		return PacketRepository.register(this, 45, 46);
	}

}
