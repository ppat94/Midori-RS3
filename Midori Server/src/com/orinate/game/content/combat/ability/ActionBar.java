package com.orinate.game.content.combat.ability;

import java.io.Serializable;

import com.orinate.game.content.keybind.Keybind;
import com.orinate.game.content.keybind.KeybindBar;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.visual.Animation;

/**
 * Handles the player's action bar.
 * 
 * @author Emperor
 * @author Tom
 * 
 */
public final class ActionBar implements Serializable {

	/**
	 * The child ids.
	 */
	public static final int[] SLOT_CHILD_IDS = new int[] { 32, 72, 76, 80, 84, 88, 92, 96, 100, 104, 108, 112 };

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 8597449475932336858L;

	/**
	 * The keybind bars.
	 */
	private final KeybindBar[] keybindBars = new KeybindBar[5];

	/**
	 * The current keybind bar index.
	 */
	private int barIndex;

	/**
	 * If the action bar is locked.
	 */
	private boolean locked;

	/**
	 * The player.
	 */
	private transient Player player;

	/**
	 * If the player is sheathing his weapon.
	 */
	private boolean sheathing;

	/**
	 * Constructs a new {@code ActionBar} {@code Object}.
	 * 
	 * @param player
	 *            The player.
	 */
	public ActionBar(Player player) {
		this.player = player;
		for (int i = 0; i < 5; i++) {
			keybindBars[i] = new KeybindBar();
		}
	}

	/**
	 * Performs an keybind action.
	 * 
	 * @param slot
	 *            The keybind slot.
	 */
	public void perform(int slot) {
		Keybind bind = getActiveBar().getSlots()[slot];
		if (bind != null) {
			if (bind.getItemId() > 0) {
				// TODO: Item bound action.
			} else {
				player.getCombatSchedule().useAbility(player.getAbilities().getAbility(bind.getAbilityId()));
			}
		}
	}

	/**
	 * Toggles the sheathing flag.
	 */
	public void toggleSheathing() {
		sheathing = !sheathing;
		player.getAnimator().animate(Animation.create(sheathing ? 18027 : 18028));
		player.getAppearance().setShowSkillLevel(sheathing);
		player.getAppearance().refresh();
	}

	/**
	 * Refreshes the action bar.
	 * 
	 * @param slot
	 *            The slots to refresh.
	 */
	public void refresh(int... slot) {
		sendSettings();
		if (slot.length < 1) {
			slot = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
		}
		for (int s : slot) {
			refreshSlot(s);
		}
		player.getWriter().sendConfig(679, player.getProperties().getAdrenaline() * 10);
	}

	/**
	 * Sends the action bar settings.
	 */
	private void sendSettings() {
		int maskData = (barIndex + 1) << 5;
		if (locked) {
			maskData |= 0x10;
		}
		player.getWriter().sendConfig(682, maskData);
	}

	/**
	 * Refreshes the keybind bar slot.
	 * 
	 * @param slot
	 *            The slot.
	 */
	public void refreshSlot(int slot) {
		Keybind k = getActiveBar().getSlots()[slot];
		player.getWriter().sendConfig(727 + slot, k != null ? k.getClientId() : 0);
		player.getWriter().sendConfig(739 + slot, 0);
		player.getWriter().sendConfig(811 + slot, k != null ? k.getItemId() : -1);
		player.getWriter().sendConfig(823 + slot, -1);
		player.getWriter().sendIComponentSettings(640, SLOT_CHILD_IDS[slot], -1, -1, k == null ? 2097152 : (locked ? 2195454 : 11108350));
	}

	/**
	 * Adds a key bind.
	 * 
	 * @param slot
	 *            The slot.
	 * @param bind
	 *            The keybind to add.
	 */
	public void addKeybind(int slot, Keybind bind) {
		getActiveBar().getSlots()[slot] = bind;
	}

	/**
	 * Gets the current active bar.
	 * 
	 * @return The key bind bar.
	 */
	public KeybindBar getActiveBar() {
		return keybindBars[barIndex];
	}

	/**
	 * Sets the current bar index.
	 * 
	 * @param barIndex
	 *            The bar index.
	 */
	public void setBarIndex(int barIndex) {
		this.barIndex = barIndex % 5;
	}

	/**
	 * Gets the current bar index.
	 * 
	 * @return The bar index.
	 */
	public int getBarIndex() {
		return barIndex;
	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked
	 *            the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the sheathing
	 */
	public boolean isSheathing() {
		return sheathing;
	}

	/**
	 * @param sheathing
	 *            the sheathing to set
	 */
	public void setSheathing(boolean sheathing) {
		this.sheathing = sheathing;
	}

}