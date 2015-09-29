package com.orinate.network.packet.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.World;
import com.orinate.game.content.misc.FoodHandler;
import com.orinate.game.content.misc.LodestoneNetwork;
import com.orinate.game.content.misc.LodestoneNetwork.Lodestone;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.content.skills.impl.divination.DivinationConvert;
import com.orinate.game.content.skills.impl.divination.DivinationConvert.ConvertMode;
import com.orinate.game.content.skills.impl.firemaking.Fire;
import com.orinate.game.content.skills.impl.firemaking.Firemaking;
import com.orinate.game.content.skills.impl.herblore.HerbCleaning;
import com.orinate.game.content.skills.impl.herblore.HerbGrinding;
import com.orinate.game.content.skills.impl.prayer.AshScattering;
import com.orinate.game.content.skills.impl.prayer.BoneBurying;
import com.orinate.game.model.bank.Bank;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.EmoteHandler;
import com.orinate.game.model.player.Equipment;
import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * @author Tom
 * 
 */
public class ClickInterfaceListener implements PacketListener {

	private int[] actionBarSlots = new int[] { 97, 102, 107, 112, 117, 122, 127, 132, 137, 142, 147, 152 };
	private Map<Integer, Integer> actionBarMap;

	public static final int WEAR_ITEM_OPCODE = 25;
	public static final int DROP_ITEM_OPCODE = 19;
	public static final int OPTION_1_OPCODE = 102;
	public static final int INVENTORY_INTERFACE = 1473;
	public static final int SCREEN_INTERFACE = 1477;
	public static final int EQUIPMENT_INTERFACE = 1464;
	public static final int MINIMAP_INTERFACE = 1465;
	public static final int BANK_INTERFACE = 762;

	public ClickInterfaceListener() {
		actionBarMap = new HashMap<>();
		for (int index = 0; index < actionBarSlots.length; index++) {
			actionBarMap.put(actionBarSlots[index], index);
		}
	}

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		int interfaceHash = buffer.getIntA();
		int slotId = buffer.getLEShortA();
		int itemId = buffer.getShortA();
		if (slotId == 65535) {
			slotId = 0;
		}
		if (itemId == 65535) {
			itemId = 0;
		}
		int interfaceId = interfaceHash >> 16;
		int componentId = interfaceHash - (interfaceId << 16);
		if (player.isLocked()) {
			return;
		}
		if(!player.isOwner())
			player.getWriter().sendGameMessage("InterfaceId: "+interfaceId+" ComponentId: "+componentId+" Slot: "+slotId+" ItemId: "+itemId+" Opcode: "+opcode);
		switch (interfaceId) {
		case 590:
			switch (componentId) {
			case 8:
				EmoteHandler.handleButton(player, slotId);
				break;
			}
			break;
		case 105:
		case 389:
			player.getGrandExchange().handleWidgets(interfaceId, componentId);
			break;
		case 1427:
		case 1108:
			player.getFriendsChatManager().handleButtons(interfaceId, componentId, slotId, opcode);
			break;
		case 1092:
			// canfis = 53,
			// varrock = 51,
			// al-kharid = 40,
			// draynor village = 44,
			// lumbridge = 47,
			// edgeville = 45,
			// taverly = 50,
			Lodestone lodestone = LodestoneNetwork.getLodestoneByID(componentId);
			if (lodestone == null) {
				return;
			}

			if (player.getLodestoneNetwork().isLocked(lodestone)) {
				player.getWriter().sendGameMessage("You need to unlock the " + lodestone.name().toLowerCase() + " lodestone before you can teleport there.");
				return;
			}

			player.getInterfaceManager().closeScreenInterface();
			player.getLodestoneNetwork().teleport(lodestone);
			break;
		case BANK_INTERFACE:
			Bank.handleBankButtons(player, interfaceId, itemId, componentId, opcode);
			break;
		case 550:
			if (componentId == 42) {
				player.getWriter().sendInterface(true, 1477, 233, 1418);
				player.getWriter().sendInterface(true, 1418, 0, 1469);
				player.getWriter().sendCS2Script(8178);
				player.getWriter().sendCS2Script(103);
			} else if (componentId == 44) {
				player.getWriter().sendInterface(true, 1477, 233, 1418);
				player.getWriter().sendInterface(true, 1418, 0, 1469);
				player.getWriter().sendCS2Script(8178);
				player.getWriter().sendCS2Script(104);
			}
			break;
		case MINIMAP_INTERFACE:
			if (componentId == 0) {
				player.setRunning(!player.isRunning());
			} else if (componentId == 9) {
				player.getWriter().sendInterface(true, 1477, 84, 1421);
				player.getWriter().sendInterface(false, 1477, 85, 1422);
				player.getWriter().sendInterface(true, 1422, 48, 698);
				player.getWriter().sendIComponentSettings(1422, 28, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 29, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 30, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 31, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 32, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 33, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 34, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 35, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 36, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 37, 2, 2, 2);
				player.getWriter().sendIComponentSettings(1422, 51, 0, 21, 2);
				player.getWriter().sendGlobalConfig(622, player.getLocation().getTileHash());
				player.getWriter().sendGlobalConfig(674, player.getLocation().getTileHash());
			} else if (componentId == 10) {
				player.getInterfaceManager().sendInterface(1092);
			}
			break;
		case 131:
			if (opcode == 102) {
				if (componentId == 1) {
					player.getSkillManager().train(new DivinationConvert(player, new Object[] { ConvertMode.CONVERT_TO_ENERGY }));
				} else if (componentId == 6) {
					player.getSkillManager().train(new DivinationConvert(player, new Object[] { ConvertMode.CONVERT_TO_XP }));
				} else if (componentId == 7) {
					player.getSkillManager().train(new DivinationConvert(player, new Object[] { ConvertMode.CONVERT_TO_MORE_XP }));
				}
				player.getInterfaceManager().closeScreenInterface();
			}
			break;
		case 1422:
			if (componentId == 96) {
				player.getWriter().sendInterface(true, 1477, 84, 1482);
			}
			break;
		case 1433:
			if (componentId == 57) {
				player.logout(true);
			}
			else if (componentId == 65) {
				player.logout(false);
			}
			break;
		case 1449:
			if (slotId == 6) {
				// player.getActionBar().setDefenceType(DefenceType.DEFENCE);
			} else if (slotId == 7) {
				// player.getActionBar().setDefenceType(DefenceType.CONSTITUTION);
			}
			break;
		case 1460:
            if (componentId == 1) {
                System.out.println("Attack ability " + interfaceId + ", " + slotId + " used!");
                player.getCombatSchedule().useAbility(player.getAbilities().getAbility(interfaceId, slotId));
            }
            break;
		case 1452:
			if (componentId == 1) {
				System.out.println("Ranged ability " + interfaceId + ", " + slotId + " used!");
				player.getCombatSchedule().useAbility(player.getAbilities().getAbility(interfaceId, slotId));
			}
			break;
		case 1430:
			System.out.println("Opcode: " + opcode);
			switch (opcode) {
			case 102:
				if (componentId == 183 || componentId == 184) {
					int currentBar = player.getActionBar().getBarIndex();
					if (componentId == 183 && --currentBar < 0) {
						currentBar = 4;
					} else if (componentId == 184 && ++currentBar > 4) { // it was 182 before by mistake so the component id wasn't moving to the next bar.
						currentBar = 0;
					}
					player.getActionBar().setBarIndex(currentBar);
					player.getActionBar().refresh();
				} else if (componentId == 75) {
					if (player.getActionBar().getBarIndex() == 0) {
						return;
					}
					player.getActionBar().setBarIndex(0);
					player.getActionBar().refresh();
				} else {
					boolean isBarSlot = false;
					for (int slot : actionBarSlots) {
						if (componentId == slot) {
							isBarSlot = true;
							break;
						}
					}
					if (isBarSlot) {
						int slot = actionBarMap.get(componentId);
						player.getActionBar().perform(slot);
						System.out.println("Performing ability on slot: " + slot);
					}
				}
				break;
			case 25:
				if (componentId == 75) {
					if (player.getActionBar().getBarIndex() == 1) {
						return;
					}
					player.getActionBar().setBarIndex(1);
					player.getActionBar().refresh();
				}
				break;
			case 23:
				if (componentId == 75) {
					if (player.getActionBar().getBarIndex() == 2) {
						return;
					}
					player.getActionBar().setBarIndex(2);
					player.getActionBar().refresh();
				}
				break;
			case 67:
				if (componentId == 75) {
					if (player.getActionBar().getBarIndex() == 3) {
						return;
					}
					player.getActionBar().setBarIndex(3);
					player.getActionBar().refresh();
				}
				break;
			case 68:
				if (componentId == 75) {
					if (player.getActionBar().getBarIndex() == 4) {
						return;
					}
					player.getActionBar().setBarIndex(4);
					player.getActionBar().refresh();
				}
				break;
			}
			break;
		case SCREEN_INTERFACE:
			if (componentId == 175) {
				player.getWriter().sendConfig(3814, player.isInterfacesLocked() ? 9 : 11);
				player.setInterfacesLocked(player.isInterfacesLocked() ? false : true);
			} else {
                if (opcode == 102 && componentId == 79) {
                    player.getInterfaceManager().sendInterface(1433);
                } else {
                    if (opcode == 25 && componentId == 77) {
                        player.getActionBar().toggleSheathing();
                    } else {
                        System.out.println("Roar " + opcode + ", " + componentId);
                    }
                }
            }
			break;
		case EQUIPMENT_INTERFACE:
			if (componentId == 14 && opcode == 102) {
				if (slotId >= 16) {
					return;
				}
				Item item = player.getEquipment().getItem(slotId);
				if (item == null || !player.getInventory().addItem(item.getId(), item.getAmount())) {
					return;
				}
				player.getEquipment().getItems().set(slotId, null);
				player.getEquipment().refresh(slotId);
				player.getAppearance().refresh();
				player.getCombatSchedule().end();
			}
			break;
		case INVENTORY_INTERFACE:
			if (opcode == WEAR_ITEM_OPCODE) {
				Item item = player.getInventory().get(slotId);
				player.getCombatSchedule().end();
				if (item != null && item.getDefinitions().isWearItem()) {
					if (item.getId() != itemId) {
						return;
					}
					if (item.getDefinitions().isNoted()) {
						player.getWriter().sendGameMessage("You can not wear that item.");
						return;
					}
					int targetSlot = Equipment.getItemSlot(itemId);
					boolean isTwoHanded = targetSlot == 3 && Equipment.isTwoHandedWeapon(item);
					if (isTwoHanded && !player.getInventory().hasFreeSlots() && player.getEquipment().hasShield()) {
						player.getWriter().sendGameMessage("Not enough free space in your inventory.");
						return;
					}
					HashMap<Integer, Integer> requirments = item.getDefinitions().getWearingSkillRequiriments();
					boolean hasRequirments = true;
					if (requirments != null) {
						for (int skillId : requirments.keySet()) {
							if (skillId > 25 || skillId < 0) {
								continue;
							}
							int level = requirments.get(skillId);
							if (level < 0 || level > 120) {
								continue;
							}
							if (player.getSkills().getLevelForXp(skillId) < level) {
								if (hasRequirments) {
									player.getWriter().sendGameMessage("You are not high enough level to use this item.");
								}
								hasRequirments = false;
								String name = Skills.SKILL_NAME[skillId].toLowerCase();
								player.getWriter().sendGameMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " " + name + " level of " + level + ".");
							}
						}
					}
					if (!hasRequirments) {
						return;
					}
					player.getInventory().deleteItem(slotId, item);
					if (targetSlot == 3) {
						if (isTwoHanded && player.getEquipment().getItem(5) != null) {
							if (!player.getInventory().addItem(player.getEquipment().getItem(5).getId(), player.getEquipment().getItem(5).getAmount())) {
								player.getInventory().getItems().set(slotId, item);
								player.getInventory().refresh(slotId);
								return;
							}
							player.getEquipment().getItems().set(5, null);
						}
					} else if (targetSlot == 5) {
						if (player.getEquipment().getItem(3) != null && Equipment.isTwoHandedWeapon(player.getEquipment().getItem(3))) {
							if (!player.getInventory().addItem(player.getEquipment().getItem(3).getId(), player.getEquipment().getItem(3).getAmount())) {
								player.getInventory().getItems().set(slotId, item);
								player.getInventory().refresh(slotId);
								return;
							}
							player.getEquipment().getItems().set(3, null);
						}
					}
					if (player.getEquipment().getItem(targetSlot) != null && (itemId != player.getEquipment().getItem(targetSlot).getId() || !item.getDefinitions().isStackable())) {
						if (player.getInventory().getItems().get(slotId) == null) {
							player.getInventory().getItems().set(slotId, new Item(player.getEquipment().getItem(targetSlot).getId(), player.getEquipment().getItem(targetSlot).getAmount()));
							player.getInventory().refresh(slotId);
						} else {
							player.getInventory().addItem(new Item(player.getEquipment().getItem(targetSlot).getId(), player.getEquipment().getItem(targetSlot).getAmount()));
						}
						player.getEquipment().getItems().set(targetSlot, null);
					}
					int oldAmt = 0;
					if (player.getEquipment().getItem(targetSlot) != null) {
						oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
					}
					Item item2 = new Item(itemId, oldAmt + item.getAmount());
					player.getEquipment().getItems().set(targetSlot, item2);
					player.getEquipment().refresh(targetSlot, targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
					player.getAppearance().refresh();
				}
				if (item.getDefinitions().hasOption("Light")) {
					Fire fire = Fire.forId(itemId);
					if (fire != null) {
						player.getSkillManager().train(new Firemaking(player, fire));
					}
				}
			} else if (opcode == DROP_ITEM_OPCODE) {
				Item item = player.getInventory().get(slotId);
				if (item == null || item.getId() != itemId) {
					return;
				}
				player.getInventory().set(slotId, null);
				World.getWorld().addGroundItem(item, player.getLocation(), player, 60, false);
				player.getInventory().refresh(slotId);
			} else if (opcode == OPTION_1_OPCODE) {
				Item item = player.getInventory().get(slotId);
				if (item == null)
					return;
				if (FoodHandler.eatFood(itemId, slotId, player))
					return;
				if (item.getDefinitions().hasOption("clean") && item.getName().toLowerCase().contains("grimy")) {
					HerbCleaning.cleanHerb(player, item, slotId);
					return;
				}
				if (item.getDefinitions().hasOption("Bury") && item.getDefinitions().getName().toLowerCase().contains("bones")) {
					player.getSkillManager().train(new BoneBurying(player, BoneBurying.getBone(itemId)));
					return;
				}
				if (item.getDefinitions().hasOption("Scatter") && item.getDefinitions().getName().toLowerCase().contains("ashes")) {
					player.getSkillManager().train(new AshScattering(player, AshScattering.getAshes(itemId)));
					return;
				}
				if (item.getDefinitions().hasOption("grind")) {
					HerbGrinding.grindIngredient(player, item, slotId);
					return;
				}
			} else {
				if (opcode == 23) {
					Item item = player.getInventory().get(slotId);
					if (item == null || item.getId() != itemId) {
						return;
					}
					switch (itemId) {
					case 20767:
					case 20769:
					case 20771:
						player.getAttributes().set("currentCape", itemId);
						player.getInterfaceManager().sendInterface(20);
						short[] colours = player.getCapeColours()[itemId == 20767 ? 0 : 1];
						for (int colourId = 0; colourId < 4; colourId++) {
							player.getWriter().sendConfig(424 + colourId, colours[colourId]);
						}
						player.getWriter().sendIComponentModel(20, 55, player.getAppearance().isMale() ? ItemDefinition.forId(itemId).maleEquip1 : ItemDefinition.forId(itemId).femaleEquip1);
						break;
					}
				}
			}
			break;
		case 20:
			int capeId = -1;
			if (player.getAttributes().isSet("currentCape")) {
				capeId = player.getAttributes().getInt("currentCape");
			}
			short[] colours = player.getCapeColours()[capeId == 20767 ? 0 : 1];
			switch (componentId) {
			case 58:
				player.getCapeColours()[0] = Arrays.copyOf(ItemDefinition.forId(20767).originalModelColors, 4);
				player.getCapeColours()[1] = Arrays.copyOf(ItemDefinition.forId(20769).originalModelColors, 4);
				for (int colourId = 0; colourId < 4; colourId++) {
					player.getWriter().sendConfig(424 + colourId, colours[colourId]);
				}
				break;
			case 34:
				player.getInterfaceManager().sendInterface(19);
				player.getWriter().sendConfig(2174, colours[0]);
				break;
			default:
				player.getWriter().sendGameMessage("ComponentID: " + componentId);
				break;
			}
			break;
		default:
			if (player.getRights() >= 2) {
				player.getWriter().sendGameMessage("Unhandled [interId=" + interfaceId + ", componentId=" + componentId + ", slotId=" + slotId + ", itemId=" + itemId + ", opcode=" + opcode + "]");
			}
			break;
		}
	}

	@Override
	public boolean register() {
		return PacketRepository.register(this, 102, 25, 23, 67, 68, 74, 48, 19, 89, 1);
	}
}
