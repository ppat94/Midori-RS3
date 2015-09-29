package com.orinate.game.content;

import java.util.LinkedList;
import java.util.List;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.World;
import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.content.skills.impl.firemaking.Bonfire;
import com.orinate.game.model.Location;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.player.Bonuses;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.region.WorldObject;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.model.visual.Animation;
import com.orinate.util.Utilities;
import com.orinate.util.text.StringUtil;

/**
 * @author Tom
 * 
 */
public class CommandsHandler {

	public static void handleCommand(Player player, String command) {
		String[] parsed = command.split(" ");
		command = parsed[0].toLowerCase();
		List<String> args = arguments(parsed);
		switch (player.getRights()) {
		case 2:
			processAdminCommand(player, command, args);
		case 1:
			processModeratorCommand(player, command, args);
		case 0:
			processPlayerCommand(player, command, args);
			break;
		}
	}

	private static void processPlayerCommand(Player player, String command, List<String> args) {
		switch (command) {
		case "players":
			int lobbyPlayers = World.getLobbyPlayers().size();
			int worldPlayers = World.getPlayers().size() - lobbyPlayers;
			player.getWriter().sendGameMessage("There is currently " + worldPlayers +(worldPlayers == 1 ? " player in game & " : " players in game & ") + lobbyPlayers + (lobbyPlayers == 1  ? " player in lobby." : " players in lobby."));
			break;
		}
		if (command.equalsIgnoreCase("ge")) {
			player.getGrandExchange().open();
		}
		if(command.equalsIgnoreCase("joinchat")) {
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < args.size(); i++) {
				builder.append(args.get(i));
				if((i + 1) < args.size())
					builder.append(" ");
			}
			String name = builder.toString();
			if(player.getFriendsChatManager().getFriendsChat() != null) {
				player.getWriter().sendGameMessage("Already in chat.");
				return;
			}
			player.getFriendsChatManager().joinChat(name);
		}
		if (command.equalsIgnoreCase("itemn")) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < args.size(); i++) {
				builder.append(args.get(i)).append(i == args.size() - 1 ? "" : " ");
			}
			if (builder.toString().endsWith("false") || builder.toString().endsWith("true")) {
				builder.replace(builder.toString().endsWith("false") ? builder.toString().length() - 6 : builder.toString().length() - 5, builder.toString().length(), "");
			}
			boolean onlyWearItem = Boolean.parseBoolean(args.get(args.size() - 1));
			ItemDefinition def = ItemDefinition.forName(builder.toString(), onlyWearItem);
			if (def != null)
				player.getInventory().addItem(new Item(def.getId(), 1));
		}
		if (command.equalsIgnoreCase("item")) {
			int id = Integer.valueOf(args.get(0));
			int count;
			if (args.size() == 1) {
				count = 1;
			} else {
				count = Integer.valueOf(args.get(1));
			}
			player.getWriter().sendGameMessage(ItemDefinition.forId(id).getName() + " was spawned.");
			player.getInventory().addItem(id, count);
		}
		if (command.equalsIgnoreCase("copy")) {
			String name = "";
			for (int i = 0; i < args.size(); i++) {
				name += args.get(i) + ((i == args.size() - 1) ? "" : " ");
			}
			Player target = World.getPlayer(name);
			if (target == null) {
				return;
			}

			player.getInventory().clear();
			player.getEquipment().clear();

			player.getEquipment().refresh();
			player.getInventory().refresh();

			for (int eIndex = 0; eIndex < 15; eIndex++) {
				if (target.getEquipment().getItem(eIndex) != null) {
					Item atIndex = target.getEquipment().getItem(eIndex);
					player.getEquipment().getItems().set(eIndex, atIndex);
				}
			}

			for (int iIndex = 0; iIndex < 28; iIndex++) {
				if (target.getInventory().getItems().get(iIndex) != null) {
					Item atIndex = target.getInventory().getItems().get(iIndex);
					player.getInventory().getItems().set(iIndex, atIndex);
				}
			}

			player.getEquipment().init();
			player.getInventory().init();
			player.getAppearance().refresh();

			player.sendMessage("You have successfully copied: " + target.getDefinition().getUsername() + "'s look!");
		}
		if (command.equalsIgnoreCase("master")) {
			for (int skill = 0; skill <= 25; skill++) {
				player.getSkills().addXp(skill, 120000000);
			}
			player.totalRefresh();
		}
        //yells 2 times in-game because the rights are admin and normal player for now.
		/*if(command.equalsIgnoreCase("yell")) {
			String message = "";
			for (int i = 0; i < args.size(); i++) {
				message += args.get(i) + ((i == args.size() - 1) ? "" : " ");
			}
			yell(player, message);
		}*/
	}

	private static void processModeratorCommand(Player player, String command, List<String> args) {

	}

	private static void processAdminCommand(final Player player, String command, List<String> args) {
		if (command.equalsIgnoreCase("script")) {
			int scriptId = Integer.valueOf(args.get(0));
			Object[] objects = new Object[args.size() - 1];
			for (int i = 1; i < args.size(); i++) {
				objects[i] = args.get(i);
			}
			player.getWriter().sendCS2Script(scriptId, objects);
		}
		if (command.equalsIgnoreCase("max")) {
			player.getProperties().drainAdrenaline(player.getProperties().getAdrenaline() - 100, player);
		}
		if (command.equalsIgnoreCase("music")) {
			player.getWriter().sendMusic(Integer.valueOf(args.get(0)));
		}
		if(command.equalsIgnoreCase("joinchat")) {
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < args.size(); i++) {
				builder.append(args.get(i));
				if((i + 1) < args.size())
					builder.append(" ");
			}
			String name = builder.toString();
			if(player.getFriendsChatManager().getFriendsChat() != null) {
				player.getWriter().sendGameMessage("Already in chat.");
				return;
			}
			player.getFriendsChatManager().joinChat(name);
		}
		if(command.equalsIgnoreCase("refreshchat")) {
			if(player.getFriendsChatManager().getFriendsChat() != null)
				player.getFriendsChatManager().getFriendsChat().refreshChat();
		}
		if (command.equalsIgnoreCase("unlockall")) {
			player.getLodestoneNetwork().activateAll();
		}
		if (command.equalsIgnoreCase("configloop")) {
			for (int i = 0; i < 2000; i++) {
				player.getWriter().sendConfig(i, i);
			}
		}
		if (command.equalsIgnoreCase("pot")) {
			player.getSkills().set(0, 115);
			player.getSkills().set(1, 115);
			player.getSkills().set(2, 115);
			player.getSkills().set(4, 115);
			player.getSkills().set(6, 115);
		}
		if (command.equalsIgnoreCase("db")) {
			Bonuses b = player.getProperties().getBonuses();

			System.out.println("Armour: " + b.getArmour());
			System.out.println("Lifepoints: " + b.getLifepoints());
			System.out.println("Prayer: " + b.getPrayer());
			for (int i = 0; i < 2; i++) {
				for (CombatStyle s : CombatStyle.values()) {
					boolean offhand = i == 1;
					String prefix = (!offhand ? "On-hand" : "Off-hand") + " " + s.name().toLowerCase();
					System.out.println(prefix + " damage: " + player.getProperties().getDamageBonus(offhand, s));
					System.out.println(prefix + " accuracy: " + player.getProperties().getAccuracyBonus(offhand, s));
					System.out.println(prefix + " critical: " + player.getProperties().getCriticalHitBonus(offhand, s));
				}
			}
		}
		if (command.equalsIgnoreCase("copy")) {
			String name = "";
			for (int i = 0; i < args.size(); i++) {
				name += args.get(i) + ((i == args.size() - 1) ? "" : " ");
			}
			Player target = World.getPlayer(name);
			if (target == null) {
				return;
			}

			player.getInventory().clear();
			player.getEquipment().clear();

			player.getEquipment().refresh();
			player.getInventory().refresh();

			for (int eIndex = 0; eIndex < 15; eIndex++) {
				if (target.getEquipment().getItem(eIndex) != null) {
					Item atIndex = target.getEquipment().getItem(eIndex);
					player.getEquipment().getItems().set(eIndex, atIndex);
				}
			}

			for (int iIndex = 0; iIndex < 28; iIndex++) {
				if (target.getInventory().getItems().get(iIndex) != null) {
					Item atIndex = target.getInventory().getItems().get(iIndex);
					player.getInventory().getItems().set(iIndex, atIndex);
				}
			}

			player.getEquipment().init();
			player.getInventory().init();
			player.getAppearance().refresh();

			player.sendMessage("You have successfully copied: " + target.getDefinition().getUsername() + "'s look!");
		}
		if (command.equalsIgnoreCase("setrights")) {
			String name = "";
			for (int i = 1; i < args.size(); i++) {
				name += args.get(i) + ((i == args.size() - 1) ? "" : " ");
			}
			Player target = World.getPlayer(name);
			if (target == null) {
				player.sendMessage("Couldn't find player " + name + ".");
			} else {
				target.getDefinition().setRights(Integer.valueOf(args.get(0)));
			}
		}
		if (command.equalsIgnoreCase("rcape")) {
			short[] capeColours = new short[4];
			for (int idx = 0; idx < capeColours.length; idx++) {
				capeColours[idx] = (short) Utilities.random(50000);
			}
			player.getCapeColours()[1] = capeColours;
			player.getAppearance().refresh();
		}
		if (command.equalsIgnoreCase("resethp")) {
			player.setHitpoints(player.getMaxHitpoints());
		}
        if (command.equalsIgnoreCase("hit")) {
            player.getImpactHandler().impact(100, player, HitType.REGULAR_DAMAGE);
        }
        if (command.equalsIgnoreCase("mecrit")) {
            player.getImpactHandler().impact(100, player, HitType.MELEE_CRITICAL);
        }
        if (command.equalsIgnoreCase("racrit")) {
            player.getImpactHandler().impact(100, player, HitType.RANGE_CRITICAL);
        }
        if (command.equalsIgnoreCase("macrit")) {
            player.getImpactHandler().impact(100, player, HitType.MAGE_CRITICAL);
        }
        if (command.equalsIgnoreCase("back")) {
            player.getAnimator().animate(Animation.create(18527));
            player.getAnimator().animate(null, new Graphics(3526));
            player.setNextLocation(new Location(player.getLocation().getX(), player.getLocation().getY() - 4, player.getLocation().getPlane()));
        }
		if (command.equalsIgnoreCase("inter")) {
			int interfaceId = Integer.valueOf(args.get(0));
			player.getInterfaceManager().sendInterface(interfaceId);
		}
		if (command.equalsIgnoreCase("testing")) {
			 WorldObject object = new WorldObject(70765, 10, 1, 3222, 3222,
			 0);
			 World.getWorld().spawnTimedObject(object, 50000);
			 player.getSkillManager().train(new Bonfire(player, object));
//			player.getGrandExchange().confirmOffer(new GrandExchangeOffer(new Item(1042, 1), 1, OfferType.SELL), 1);
			return;
		}
		if (command.equalsIgnoreCase("dc")) {
			player.getWriter().sendLogout(false);
		}
		if (command.equalsIgnoreCase("sobj")) {
			WorldObject obj = new WorldObject(Integer.valueOf(args.get(0)), 10, 1, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getPlane());
			World.getWorld().spawnTimedObject(obj, 5000);
		}
		if (command.equalsIgnoreCase("test")) {
			int id = Integer.valueOf(args.get(0));
			int value = Integer.valueOf(args.get(1));
			player.getWriter().sendConfigByFile(id, value);
		}
		if (command.equalsIgnoreCase("config")) {
			int id = Integer.valueOf(args.get(0));
			int value = Integer.valueOf(args.get(1));
			player.getWriter().sendConfig(id, value);
			player.getWriter().sendGameMessage("Config(" + id + ", " + value + ");");
		}
		if (command.equalsIgnoreCase("globalconfig")) {
			int id = Integer.valueOf(args.get(0));
			int value = Integer.valueOf(args.get(1));
			player.getWriter().sendGlobalConfig(id, value);
			player.getWriter().sendGameMessage("GlobalConfig(" + id + ", " + value + ");");
		}
		if (command.equalsIgnoreCase("npc")) {
			NPC npc = World.getWorld().spawnNpc(Integer.valueOf(args.get(0)), player.getLocation());
			npc.setLocked(true);
		}
		if (command.equalsIgnoreCase("npcn")) {
			World.getWorld().spawnNpc(Integer.valueOf(args.get(0)), player.getLocation());
		}
		if (command.equalsIgnoreCase("gfx")) {
			player.setGraphics(new Graphics(Integer.valueOf(args.get(0))));
		}
		if (command.equalsIgnoreCase("bank")) {
			player.getBank().initBankSession();
		}
		if (command.equalsIgnoreCase("anim")) {
			player.setAnimation(Animation.create(Integer.valueOf(args.get(0))));
		}
        if (command.equalsIgnoreCase("testanim")) {
            player.getAnimator().animate(Animation.create(21271));
        }
		if (command.equalsIgnoreCase("sync")) {
			player.setAnimation(Animation.create(Integer.valueOf(args.get(0))));
			player.setGraphics(new Graphics(Integer.valueOf(args.get(1))));
		}
		if (command.equalsIgnoreCase("sname")) {
			try {
				String skillName = args.get(0).toUpperCase();
				int skillId = Skills.class.getField(skillName).getInt(null);

				int level = Integer.valueOf(args.get(1));
				player.getSkills().setLevelXP(skillId, level);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}
		if (command.equalsIgnoreCase("master")) {
			for (int skill = 0; skill <= 25; skill++) {
				player.getSkills().addXp(skill, 120000000);
			}
			player.totalRefresh();
		}
		if (command.equalsIgnoreCase("itemn")) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < args.size(); i++) {
				builder.append(args.get(i)).append(i == args.size() - 1 ? "" : " ");
			}
			if (builder.toString().endsWith("false") || builder.toString().endsWith("true")) {
				builder.replace(builder.toString().endsWith("false") ? builder.toString().length() - 6 : builder.toString().length() - 5, builder.toString().length(), "");
			}
			boolean onlyWearItem = Boolean.parseBoolean(args.get(args.size() - 1));
			ItemDefinition def = ItemDefinition.forName(builder.toString(), onlyWearItem);
			if (def != null)
				player.getInventory().addItem(new Item(def.getId(), 1));
		}
		if (command.equalsIgnoreCase("item")) {
			int id = Integer.valueOf(args.get(0));
			int count;
			if (args.size() == 1) {
				count = 1;
			} else {
				count = Integer.valueOf(args.get(1));
			}
			player.getWriter().sendGameMessage(ItemDefinition.forId(id).getName() + " was spawned.");
			player.getInventory().addItem(id, count);
		}

		if (command.equalsIgnoreCase("tele")) {
			int z, x, y;
			if (args.get(0).contains(",")) {
				String[] cmd = args.get(0).split(",");
				z = Integer.valueOf(cmd[0]);
				x = (Integer.valueOf(cmd[1]) << 6) + Integer.valueOf(cmd[3]);
				y = (Integer.valueOf(cmd[2]) << 6) + Integer.valueOf(cmd[4]);
			} else {
				x = Integer.valueOf(args.get(0));
				y = Integer.valueOf(args.get(1));
				z = 0;
				if (args.size() >= 3) {
					z = Integer.valueOf(args.get(2));
				}
			}
			player.setNextLocation(new Location(x, y, z));
		}

		if (command.equalsIgnoreCase("myloc")) {
			player.getWriter().sendGameMessage("" + player.getLocation());
		}

		if (command.equalsIgnoreCase("empty")) {
			player.getInventory().clear();
		}
		if (command.equalsIgnoreCase("object")) {
			World.getWorld().spawnTimedObject(new WorldObject(Integer.valueOf(args.get(0)), 0, 0, player.getLocation().getX(), player.getLocation().getY(), 0), 50 * 600);
		}
		if (command.equalsIgnoreCase("ge")) {
			player.getGrandExchange().open();
		}
		if (command.equalsIgnoreCase("yell")) {
			String message = "";
			for(int i = 0; i < args.size(); i++) {
				message += args.get(i);
			}
			yell(player, message);
		}
	}

	private static void yell(Player player, String message) {
		if (message.equalsIgnoreCase("") || message.equalsIgnoreCase(" ")) {
			return;
		}
		String format = player.getRights() == 0 ? "" : "<img=" + (player.getRights() - 1) +">";
		for (Player p : World.getPlayers()) {
			p.getWriter().sendGameMessage(format + StringUtil.formatName(player.getDefinition().getUsername()) + ": " + StringUtil.formatText(message));
		}
	}
	
	private static List<String> arguments(String[] parsed) {
		List<String> newArgs = new LinkedList<>();
		for (int i = 1; i < parsed.length; i++) {
			newArgs.add(parsed[i]);
		}
		return newArgs;
	}
}
