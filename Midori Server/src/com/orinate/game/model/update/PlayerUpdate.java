package com.orinate.game.model.update;

import java.security.MessageDigest;

import com.orinate.game.World;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Bar;
import com.orinate.game.model.update.masks.Hit;
import com.orinate.io.OutBuffer;
import com.orinate.util.Utilities;

/**
 * @author Tom
 * @author Trent
 * @author Tyler
 */
public class PlayerUpdate {

	private Player player;

	private byte[] slotFlags;
	private Player[] localPlayers;
	private int[] localIndexes;
	private int[] outIndexes;
	private int[] regionHashes;
	private byte[][] cachedAppearances;

	private int localsCount;
	private int globalsCount;
	private int sentDataLength;

	public PlayerUpdate(Player player) {
		this.player = player;
		this.slotFlags = new byte[2048];
		this.localPlayers = new Player[2048];
		this.localIndexes = new int[2500];
		this.outIndexes = new int[2048];
		this.regionHashes = new int[2048];
		this.cachedAppearances = new byte[2500][];
	}

	public void sendLoginData(OutBuffer buffer) {
		buffer.setBitAccess();

		buffer.putBits(30, player.getLocation().getTileHash());
		localPlayers[player.getIndex()] = player;
		localIndexes[localsCount++] = player.getIndex();

		for (int index = 1; index < 2048; index++) {
			if (index == player.getIndex()) {
				continue;
			}

			Player player = World.getPlayers().get(index);
			buffer.putBits(18, regionHashes[index] = player == null ? 0 : player.getLocation().getRegionHash());

			outIndexes[globalsCount++] = index;
		}

		buffer.setByteAccess();
	}

	public void send() {
		OutBuffer buffer = new OutBuffer();
		OutBuffer block = new OutBuffer();

		buffer.putVarShort(110);

		localUpdate(buffer, block, true);
		localUpdate(buffer, block, false);
		globalUpdate(buffer, block, true);
		globalUpdate(buffer, block, false);

		buffer.putBytes(block.buffer(), 0, block.offset());
		buffer.finishVarShort();

		player.write(buffer);
	}

	private void localUpdate(OutBuffer buffer, OutBuffer block, boolean nsn0) {
		buffer.setBitAccess();
		int skipCount = 0;

		for (int index = 0; index < localsCount; index++) {
			int playerIndex = localIndexes[index];
			if (nsn0 ? (0x1 & slotFlags[playerIndex]) != 0 : (0x1 & slotFlags[playerIndex]) == 0) {
				continue;
			}
			if (skipCount > 0) {
				skipCount--;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 0x2);
				continue;
			}

			Player p = localPlayers[playerIndex];
			if (remove(p)) {
				buffer.putBits(1, 1);
				buffer.putBits(1, 0);
				buffer.putBits(2, 0);
				regionHashes[playerIndex] = p.getLastLocation() == null ? p.getLocation().getRegionHash() : p.getLastLocation().getRegionHash();

				int regionHash = p.getLocation().getRegionHash();
				if (regionHash == regionHashes[playerIndex]) {
					buffer.putBits(1, 0);
				} else {
					buffer.putBits(1, 1);
					hashUpdate(buffer, p, regionHashes[playerIndex], regionHash);
					regionHashes[playerIndex] = regionHash;
				}

				localPlayers[playerIndex] = null;
			} else {
				boolean appearanceUpdate = appearanceUpdate(p.getIndex(), p.getAppearance().getHash());
				boolean requiresUpdate = appearanceUpdate || p.needMasksUpdate();
				if (requiresUpdate) {
					updateBlocks(p, block, false);
				}

				if (p.isTeleporting()) {
					buffer.putBits(1, 1);
					buffer.putBits(1, requiresUpdate ? 1 : 0);
					buffer.putBits(2, 3);

					int xOffset = p.getLocation().getX() - p.getLastLocation().getX();
					int yOffset = p.getLocation().getY() - p.getLastLocation().getY();
					int planeOffset = p.getLocation().getPlane() - p.getLastLocation().getPlane();
					if (Math.abs(p.getLocation().getX() - p.getLastLocation().getX()) <= 14 && Math.abs(p.getLocation().getY() - p.getLastLocation().getY()) <= 14) {
						buffer.putBits(1, 0);
						if (xOffset < 0) {
							xOffset += 32;
						}
						if (yOffset < 0) {
							yOffset += 32;
						}
						buffer.putBits(15, (yOffset) + (xOffset << 5) + (planeOffset << 10) + (4 << 12));
					} else {
						buffer.putBits(1, 1);
						buffer.putBits(3, 4);
						buffer.putBits(30, (yOffset & 0x3fff) + ((xOffset & 0x3fff) << 14) + ((planeOffset & 0x3) << 28));
					}
				} else if (p.getNextWalkDirection() != -1) {
					if (p.isFirstStep()) {
						buffer.putBits(1, 1);
						buffer.putBits(1, requiresUpdate ? 1 : 0);
						buffer.putBits(2, 3);

						int xOffset = p.getLocation().getX() - p.getLastLocation().getX();
						int yOffset = p.getLocation().getY() - p.getLastLocation().getY();
						int planeOffset = p.getLocation().getPlane() - p.getLastLocation().getPlane();
						buffer.putBits(1, 0);
						if (xOffset < 0) {
							xOffset += 32;
						}
						if (yOffset < 0) {
							yOffset += 32;
						}
						buffer.putBits(15, (yOffset) + (xOffset << 5) + (planeOffset << 10) + ((p.isRunning() ? 3 : 2) << 12));
					} else {
						int dx = Utilities.DIRECTION_DELTA_X[p.getNextWalkDirection()];
						int dy = Utilities.DIRECTION_DELTA_Y[p.getNextWalkDirection()];
						boolean running;
						int opcode;
						if (p.getNextRunDirection() != -1) {
							dx += Utilities.DIRECTION_DELTA_X[p.getNextRunDirection()];
							dy += Utilities.DIRECTION_DELTA_Y[p.getNextRunDirection()];
							opcode = Utilities.getPlayerRunningDirection(dx, dy);
							if (opcode == -1) {
								running = false;
								opcode = Utilities.getPlayerWalkingDirection(dx, dy);
							} else
								running = true;
						} else {
							running = false;
							opcode = Utilities.getPlayerWalkingDirection(dx, dy);
						}
						buffer.putBits(1, 1);
						if ((dx == 0 && dy == 0)) {
							buffer.putBits(1, 1);
							buffer.putBits(2, 0);
							if (!requiresUpdate) {
								updateBlocks(p, block, false);
							}
						} else {
							if (running) {
								buffer.putBits(1, requiresUpdate ? 1 : 0);
								buffer.putBits(2, 2);
								buffer.putBits(4, opcode);
							} else {
								buffer.putBits(1, requiresUpdate ? 1 : 0);
								buffer.putBits(2, 1);
								buffer.putBits(3, opcode);
								buffer.putBits(1, 0);
							}
						}
					}
				} else if (requiresUpdate) {
					buffer.putBits(1, 1);
					buffer.putBits(1, 1);
					buffer.putBits(2, 0);
				} else {
					buffer.putBits(1, 0);
					for (int idx = index + 1; idx < localsCount; idx++) {
						int p2Index = localIndexes[idx];
						if (nsn0 ? (0x1 & slotFlags[p2Index]) != 0 : (0x1 & slotFlags[p2Index]) == 0) {
							continue;
						}
						Player p2 = localPlayers[p2Index];
						if (remove(p2) || p2.getNextWalkDirection() != -1 || p2.isTeleporting() || appearanceUpdate(p2.getIndex(), p2.getAppearance().getHash())) {
							break;
						}
						skipCount++;
					}
					putSkip(buffer, skipCount);
					slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 0x2);
				}
			}
		}
		buffer.setByteAccess();
	}

	private void globalUpdate(OutBuffer buffer, OutBuffer block, boolean nsn2) {
		buffer.setBitAccess();
		int skipCount = 0;
		for (int index = 0; index < globalsCount; index++) {
			int playerIndex = outIndexes[index];
			if (nsn2 ? (0x1 & slotFlags[playerIndex]) == 0 : (0x1 & slotFlags[playerIndex]) != 0) {
				continue;
			}
			if (skipCount > 0) {
				skipCount--;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				continue;
			}
			Player p = World.getPlayers().get(playerIndex);
			if (add(p)) {
				buffer.putBits(1, 1);
				buffer.putBits(2, 0);
				int hash = p.getLocation().getRegionHash();
				if (hash == regionHashes[playerIndex]) {
					buffer.putBits(1, 0);
				} else {
					buffer.putBits(1, 1);
					hashUpdate(buffer, p, regionHashes[playerIndex], hash);
					regionHashes[playerIndex] = hash;
				}
				buffer.putBits(6, p.getLocation().getXInRegion());
				buffer.putBits(6, p.getLocation().getYInRegion());
				updateBlocks(p, block, true);
				buffer.putBits(1, 1);
				localPlayers[p.getIndex()] = p;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
			} else {
				int hash = p == null ? regionHashes[playerIndex] : p.getLocation().getRegionHash();
				if (p != null && hash != regionHashes[playerIndex]) {
					buffer.putBits(1, 1);
					hashUpdate(buffer, p, regionHashes[playerIndex], hash);
					regionHashes[playerIndex] = hash;
				} else {
					buffer.putBits(1, 0);
					for (int idx = index + 1; idx < globalsCount; idx++) {
						int p2Index = outIndexes[idx];
						if (nsn2 ? (0x1 & slotFlags[p2Index]) == 0 : (0x1 & slotFlags[p2Index]) != 0) {
							continue;
						}
						Player p2 = World.getPlayers().get(p2Index);
						if (add(p2) || (p2 != null && p2.getLocation().getRegionHash() != regionHashes[p2Index])) {
							break;
						}
						skipCount++;
					}
					putSkip(buffer, skipCount);
					slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 0x2);
				}
			}
		}
		buffer.setByteAccess();
	}

	private void putSkip(OutBuffer buffer, int skipCount) {
		buffer.putBits(2, skipCount == 0 ? 0 : skipCount > 255 ? 3 : (skipCount > 31 ? 2 : 1));
		if (skipCount > 0) {
			buffer.putBits(skipCount > 255 ? 11 : (skipCount > 31 ? 8 : 5), skipCount);
		}
	}

	private void updateBlocks(Player p, OutBuffer block, boolean added) {
		int maskData = 0x0;
		if (p.getGraphics1() != null) {
			maskData |= 0x100;
		}
		if (p.getCachedHits().size() > 0 || p.getCachedBars().size() > 0) {
			maskData |= 0x10;
		}
		if (p.getGraphics() != null) {
			maskData |= 0x20;
		}
		if (p.getFaceEntity() != -2 || (added && p.getLastFaceEntity() != -1)) {
			maskData |= 0x4;
		}
		if (p.getGlowColor() != null) {
			maskData |= 0x100000;
		}
		if (p.getAnimation() != null) {
			maskData |= 0x1;
		}
		if (appearanceUpdate(p.getIndex(), p.getAppearance().getHash())) {
			maskData |= 0x2;
		}
		if (p.getGraphics2() != null) {
			maskData |= 0x800;
		}
		if (p.getFaceLocation() != null) {
			maskData |= 0x80;
		}
		if (p.getGraphics3() != null) {
			maskData |= 0x80000;
		}
		if (maskData >= 0x100) {
			maskData |= 0x8;
		}
		if (maskData >= 0x10000) {
			maskData |= 0x4000;
		}
		block.putShort(0);
		block.putByte(maskData);
		if (maskData >= 0x100) {
			block.putByte(maskData >> 8);
		}
		if (maskData >= 0x10000) {
			block.putByte(maskData >> 16);
		}
		if (p.getGraphics1() != null) {
			updateGraphics1(p, block);
		}
		if (p.getCachedHits().size() > 0 || p.getCachedBars().size() > 0) {
			updateHitsAndBars(p, block);
		}
		if (p.getGraphics() != null) {
			updateGraphics(p, block);
		}
		if (p.getFaceEntity() != -2 || (added && p.getLastFaceEntity() != -1)) {
			updateFaceEntity(p, block);
		}
		if (p.getGlowColor() != null) {
			updateColor(p, block);
		}
		if (p.getAnimation() != null) {
			updateAnimation(p, block);
		}
		if (appearanceUpdate(p.getIndex(), p.getAppearance().getHash())) {
			updateAppearance(p, block);
		}
		if (p.getGraphics2() != null) {
			updateGraphics2(p, block);
		}
		if (p.getFaceLocation() != null) {
			updateFaceDirection(p, block);
		}
		if (p.getGraphics3() != null) {
			updateGraphics3(p, block);
		}
	}

	private void updateFaceDirection(Player p, OutBuffer block) {
		block.putLEShort(p.getDirection());
	}

	private void updateFaceEntity(Player p, OutBuffer block) {
		block.putLEShortA(p.getFaceEntity() == -2 ? p.getLastFaceEntity() : p.getFaceEntity());
	}

	private void updateHitsAndBars(Player p, OutBuffer buffer) {
		int size = p.getCachedHits().size();
		buffer.putByte(size);
		if (size > 0) {
			for (Hit hit : p.getCachedHits()) {
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
					buffer.putC(hit.getDamage());
				}
				buffer.putSmart(hit.getDelay());
			}
		}
		int barSize = p.getCachedBars().size();
		buffer.putByte(barSize);
		if (barSize > 0) {
			for (Bar bar : p.getCachedBars()) {
				if (bar == null)
					continue;
				buffer.putSmart(bar.getType());
				int maxPercentage = bar.getMaxPercentage();
				int percentage = bar.getPercentage(p);
				buffer.putSmart(bar.shouldDisplay(p) ? maxPercentage != percentage ? 1 : 0 : 32767);
				if (bar.shouldDisplay(p)) {
					buffer.putSmart(0);
					buffer.putByte(maxPercentage);
					if (maxPercentage != percentage) {
						buffer.putByte(bar.getPercentage(p));
					}
				}
			}
		}
	}

	private void updateGraphics(Player p, OutBuffer block) {
		block.putShort(p.getGraphics().getId());
		block.putLEInt(p.getGraphics().getSettingsHash());
		block.putC(p.getGraphics().getRotationSettingsHash());
	}

	private void updateGraphics1(Player p, OutBuffer block) {
		block.putLEShort(p.getGraphics().getId());
		block.putLEInt(p.getGraphics().getSettingsHash());
		block.putA(p.getGraphics().getRotationSettingsHash());
	}

	private void updateGraphics2(Player p, OutBuffer block) {
		block.putShort(p.getGraphics().getId());
		block.putLEInt(p.getGraphics().getSettingsHash());
		block.putA(p.getGraphics().getRotationSettingsHash());
	}

	private void updateGraphics3(Player p, OutBuffer block) {
		block.putLEShortA(p.getGraphics().getId());
		block.putIntA(p.getGraphics().getSettingsHash());
		block.putC(p.getGraphics().getRotationSettingsHash());
	}

	private void updateAnimation(Player p, OutBuffer block) {
		for (int anim : p.getAnimation().getIds())
			block.putBigSmart(anim);
		block.putSmart(p.getAnimation().getSpeed());
	}

	private void updateColor(Player p, OutBuffer block) {
		block.putC(p.getGlowColor().getColor());
		block.putC((p.getGlowColor().getColor() >> 8));
		block.putS((p.getGlowColor().getColor() >> 16));
		block.putC((p.getGlowColor().getColor() >> 24));
		block.putShortA(p.getGlowColor().getDelay());
		block.putLEShortA(p.getGlowColor().getDuration());
	}

	private void updateAppearance(Player p, OutBuffer block) {
		byte[] renderData = p.getAppearance().getData();
		sentDataLength += renderData.length;
		cachedAppearances[p.getIndex()] = p.getAppearance().getHash();
		block.putS(renderData.length);
		block.putBytes(renderData);
	}

	private boolean appearanceUpdate(int index, byte[] hash) {
		if (sentDataLength > ((7500 - 500) / 2) || hash == null) {
			return false;
		}
		return cachedAppearances[index] == null || !MessageDigest.isEqual(cachedAppearances[index], hash);
	}

	private void hashUpdate(OutBuffer buffer, Player p, int lastRegionHash, int currentRegionHash) {
		int lastRegionX = lastRegionHash >> 8;
		int lastRegionY = 0xff & lastRegionHash;
		int lastPlane = lastRegionHash >> 16;
		int currentRegionX = currentRegionHash >> 8;
		int currentRegionY = 0xff & currentRegionHash;
		int currentPlane = currentRegionHash >> 16;
		int planeOffset = currentPlane - lastPlane;
		if (lastRegionX == currentRegionX && lastRegionY == currentRegionY) {
			buffer.putBits(2, 1);
			buffer.putBits(2, planeOffset);
		} else if (Math.abs(currentRegionX - lastRegionX) <= 1 && Math.abs(currentRegionY - lastRegionY) <= 1) {
			int opcode;
			int dx = currentRegionX - lastRegionX;
			int dy = currentRegionY - lastRegionY;
			if (dx == -1 && dy == -1) {
				opcode = 0;
			} else if (dx == 1 && dy == -1) {
				opcode = 2;
			} else if (dx == -1 && dy == 1) {
				opcode = 5;
			} else if (dx == 1 && dy == 1) {
				opcode = 7;
			} else if (dy == -1) {
				opcode = 1;
			} else if (dx == -1) {
				opcode = 3;
			} else if (dx == 1) {
				opcode = 4;
			} else {
				opcode = 6;
			}
			buffer.putBits(2, 2);
			buffer.putBits(5, (planeOffset << 3) + (opcode & 0x7));
		} else {
			int xOffset = currentRegionX - lastRegionX;
			int yOffset = currentRegionY - lastRegionY;
			buffer.putBits(2, 3);
			buffer.putBits(20, (yOffset & 0xff) + ((xOffset & 0xff) << 8) + (planeOffset << 16) + ((p.isRunning() ? 3 : 2) << 18));
		}
	}

	public void reset() {
		this.sentDataLength = 0;
		this.localsCount = 0;
		this.globalsCount = 0;
		for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
			slotFlags[playerIndex] >>= 1;
			Player player = localPlayers[playerIndex];
			if (player == null) {
				outIndexes[globalsCount++] = playerIndex;
			} else {
				localIndexes[localsCount++] = playerIndex;
			}
		}
	}

	private boolean remove(Player p) {
		return p.isFinished() || !player.getLocation().withinDistance(p.getLocation(), 14);
	}

	private boolean add(Player p) {
		return p != null && !p.isFinished() && player.getLocation().withinDistance(p.getLocation(), 14);
	}

	public Player getPlayer() {
		return player;
	}

	public Player[] getLocalPlayers() {
		return localPlayers;
	}
}