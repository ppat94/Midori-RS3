package com.orinate.game.model.update;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.orinate.Constants;
import com.orinate.game.World;
import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Bar;
import com.orinate.game.model.update.masks.Hit;
import com.orinate.io.OutBuffer;
import com.orinate.util.Utilities;

public class NPCUpdate {

	private Player player;
	private LinkedList<NPC> localNPCs;

	public void reset() {
		localNPCs.clear();
	}

	public NPCUpdate(Player player) {
		this.player = player;
		localNPCs = new LinkedList<NPC>();
	}

	public void send() {
		// boolean largeSceneView = player.hasLargeSceneView();
		OutBuffer stream = new OutBuffer();
		OutBuffer updateBlockData = new OutBuffer();
		stream.putVarShort(/* largeSceneView ? 67 : */53);
		processLocalNPCsInform(stream, updateBlockData, false);
		stream.putBytes(updateBlockData.buffer(), 0, updateBlockData.offset());
		stream.finishVarShort();
		player.write(stream);
	}

	private void processLocalNPCsInform(OutBuffer stream, OutBuffer updateBlockData, boolean largeSceneView) {
		stream.setBitAccess();
		processInScreenNPCs(stream, updateBlockData, largeSceneView);
		addInScreenNPCs(stream, updateBlockData, largeSceneView);
		if (updateBlockData.offset() > 0)
			stream.putBits(15, 32767);
		stream.setByteAccess();
	}

	private void processInScreenNPCs(OutBuffer stream, OutBuffer updateBlockData, boolean largeSceneView) {
		stream.putBits(8, localNPCs.size());
		for (Iterator<NPC> it = localNPCs.iterator(); it.hasNext();) {
			NPC n = it.next();
			if (n.isFinished() || !n.getLocation().withinDistance(player.getLocation(), largeSceneView ? 126 : 14) || n.isTeleporting()) {
				stream.putBits(1, 1);
				stream.putBits(2, 3);
				it.remove();
				continue;
			}
			boolean needUpdate = n.needMasksUpdate();
			boolean walkUpdate = n.getNextWalkDirection() != -1;
			stream.putBits(1, (needUpdate || walkUpdate) ? 1 : 0);
			if (walkUpdate) {
				stream.putBits(2, n.getNextRunDirection() == -1 ? 1 : 2);
				if (n.getNextRunDirection() != -1)
					stream.putBits(1, 1);
				stream.putBits(3, Utilities.getNpcMoveDirection(n.getNextWalkDirection()));
				if (n.getNextRunDirection() != -1)
					stream.putBits(3, Utilities.getNpcMoveDirection(n.getNextRunDirection()));
				stream.putBits(1, needUpdate ? 1 : 0);
			} else if (needUpdate)
				stream.putBits(2, 0);
			if (needUpdate)
				appendUpdateBlock(n, updateBlockData, false);
		}
	}

	private void addInScreenNPCs(OutBuffer stream, OutBuffer updateBlockData, boolean largeSceneView) {
		for (int regionId : player.getMapRegionsIds()) {
			List<Integer> indexes = World.getWorld().getRegion(regionId).getNpcsIndexes();
			if (indexes == null)
				continue;
			for (int npcIndex : indexes) {
				if (localNPCs.size() == Constants.NPC_UPDATE_LIMIT)
					break;
				NPC n = World.getNpcs().get(npcIndex);
				if (n == null || n.isFinished() || localNPCs.contains(n) || !n.getLocation().withinDistance(player.getLocation(), /*
																																 * largeSceneView
																																 * ?
																																 * 126
																																 * :
																																 */14) || n.isDead())
					continue;
				stream.putBits(15, n.getIndex());
				boolean needUpdate = n.needMasksUpdate() || n.getLastFaceEntity() != -1;
				int x = n.getLocation().getX() - player.getLocation().getX();
				int y = n.getLocation().getY() - player.getLocation().getY();
				if (largeSceneView) {
					if (x < 127)
						x += 256;
					if (y < 127)
						y += 256;
				} else {
					if (x < 15)
						x += 32;
					if (y < 15)
						y += 32;
				}
				stream.putBits(3, (n.getDirection() >> 11) - 4);
				stream.putBits(largeSceneView ? 8 : 5, y);
				stream.putBits(1, needUpdate ? 1 : 0);
				stream.putBits(1, n.isTeleporting() ? 1 : 0);
				stream.putBits(largeSceneView ? 8 : 5, x);
				stream.putBits(2, n.getLocation().getPlane());
				stream.putBits(15, n.getId());
				localNPCs.add(n);
				if (needUpdate)
					appendUpdateBlock(n, updateBlockData, true);
			}
		}
	}

	private void appendUpdateBlock(NPC n, OutBuffer data, boolean added) {
		int maskData = 0;

		if (n.getFaceEntity() != -1) {
			maskData |= 0x40;
		}
		if (n.getGlowColor() != null) {
			maskData |= 0x4000000;
		}
		// 0x20000
		// 0x2000
		// 0x4
		if (n.getCachedHits().size() > 0 || n.getCachedBars().size() > 0) {
			maskData |= 0x4;
		}
		if (n.getGraphics() != null) {
			maskData |= 0x10;
		}
		if (n.getAnimation() != null) {
			maskData |= 0x2;
		}
		// 0x400
		if (n.getGraphics3() != null) {
			maskData |= 0x2000000;
		}
		// 0x40000
		// 0x400000
		// 0x100000
		// 0x80
		// 0x800
		if (n.getFaceLocation() != null) {
			maskData |= 0x1;
		}
		// 0x100
		// 0x8000
		if (n.getGraphics2() != null) {
			maskData |= 0x1000000;
		}
		// 0x800000
		if (n.getNextTransformation() != -1) {
			maskData |= 0x20;
		}
		// 0x80000
		if (n.getGraphics1() != null) {
			maskData |= 0x4000;
		}
		// 0x200000

		// START FLAGS
		if (maskData > 0xff)
			maskData |= 0x8;
		if (maskData > 0xffff)
			maskData |= 0x1000;
		if (maskData > 0xffffff)
			maskData |= 0x10000;
		data.putShort(0);
		data.putByte(maskData);
		if (maskData > 0xff)
			data.putByte(maskData >> 8);
		if (maskData > 0xffff)
			data.putByte(maskData >> 16);
		if (maskData > 0xffffff)
			data.putByte(maskData >> 24);
		// END FLAGS

		if (n.getFaceEntity() != -1) {
			updateFaceEntity(n, data);
		}
		if (n.getGlowColor() != null) {
			updateColor(n, data);
		}
		if (n.getCachedHits().size() > 0 || n.getCachedBars().size() > 0) {
			updateHitsAndBars(n, data);
		}
		if (n.getGraphics() != null) {
			updateGraphics(n, data);
		}
		if (n.getAnimation() != null) {
			updateAnimation(n, data);
		}
		if (n.getGraphics3() != null) {
			updateGraphics3(n, data);
		}
		if (n.getFaceLocation() != null) {
			updateFaceLocation(n, data);
		}
		if (n.getGraphics2() != null) {
			updateGraphics2(n, data);
		}
		if (n.getNextTransformation() != -1) {
			updateTransformation(n, data);
		}
		if (n.getGraphics1() != null) {
			updateGraphics1(n, data);
		}
	}

	private void updateHitsAndBars(NPC npc, OutBuffer buffer) {
		int size = npc.getCachedHits().size();
		buffer.putC(size);
		if (size > 0) {
			for (Hit hit : npc.getCachedHits()) {
				if (hit == null)
					continue;
				int hitType = hit.getType().getCode();
				buffer.putSmart(hitType);
				if (hitType == 32767) {
					buffer.putSmart(hitType);
					buffer.putSmart(hit.getDamage());
					buffer.putSmart(hit.getSecondaryType());
					buffer.putSmart(hit.getSecondayDamage());
				} else if (32766 != hitType) {
					buffer.putSmart(hit.getDamage());
				} else {
					buffer.putByte(hit.getDamage());
				}
				buffer.putSmart(hit.getDelay());
			}
		}
		int barSize = npc.getCachedBars().size();
		buffer.putS(barSize);
		if (barSize > 0) {
			for (Bar bar : npc.getCachedBars()) {
				if (bar == null)
					continue;
				buffer.putSmart(bar.getType());
				int maxPercentage = bar.getMaxPercentage();
				int percentage = bar.getPercentage(npc);
				buffer.putSmart(bar.shouldDisplay(npc) ? maxPercentage != percentage ? 1 : 0 : 32767);
				if (bar.shouldDisplay(npc)) {
					buffer.putSmart(0);
					buffer.putC(maxPercentage);
					if (maxPercentage != percentage) {
						buffer.putC(bar.getPercentage(npc));
					}
				}
			}
		}
	}

	private void updateTransformation(NPC n, OutBuffer data) {
		data.putBigSmart(n.getNextTransformation());
	}

	private void updateFaceLocation(NPC n, OutBuffer data) {
		data.putShort((n.getFaceLocation().getX() << 1) + 1);
		data.putLEShortA((n.getFaceLocation().getY() << 1) + 1);
	}

	private void updateFaceEntity(NPC n, OutBuffer data) {
		data.putShort(n.getFaceEntity() == -2 ? n.getLastFaceEntity() : n.getFaceEntity());
	}

	private void updateColor(NPC n, OutBuffer block) {
		block.putS(n.getGlowColor().getColor());
		block.putC((n.getGlowColor().getColor() >> 8));
		block.putByte((n.getGlowColor().getColor() >> 16));
		block.putA((n.getGlowColor().getColor() >> 24));
		block.putLEShortA(n.getGlowColor().getDelay());
		block.putShort(n.getGlowColor().getDuration());
	}

	private void updateGraphics(NPC n, OutBuffer block) {
		block.putShort(n.getGraphics().getId());
		block.putLEInt(n.getGraphics().getSettingsHash());
		block.putC(n.getGraphics().getRotationSettingsHash());
	}

	private void updateGraphics1(NPC n, OutBuffer block) {
		block.putLEShort(n.getGraphics().getId());
		block.putIntA(n.getGraphics().getSettingsHash());
		block.putS(n.getGraphics().getRotationSettingsHash());
	}

	private void updateGraphics2(NPC n, OutBuffer block) {
		block.putLEShortA(n.getGraphics().getId());
		block.putIntA(n.getGraphics().getSettingsHash());
		block.putA(n.getGraphics().getRotationSettingsHash());
	}

	private void updateGraphics3(NPC n, OutBuffer block) {
		block.putLEShortA(n.getGraphics().getId());
		block.putIntB(n.getGraphics().getSettingsHash());
		block.putByte(n.getGraphics().getRotationSettingsHash());
	}

	private void updateAnimation(NPC n, OutBuffer block) {
		for (int anim : n.getAnimation().getIds())
			block.putIntSmart(anim == 0 ? -1 : anim);
		block.putSmart(n.getAnimation().getSpeed());
	}
}