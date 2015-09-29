package com.orinate.game.model.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import com.orinate.Constants;
import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.World;
import com.orinate.game.content.MusicHandler;
import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.combat.ability.ActionBar;
import com.orinate.game.content.dialogue.DialogueManager;
import com.orinate.game.content.exchange.GrandExchange;
import com.orinate.game.content.friends.FriendListManager;
import com.orinate.game.content.friends.FriendsChatManager;
import com.orinate.game.content.misc.LodestoneNetwork;
import com.orinate.game.content.skills.SkillManager;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.bank.Bank;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.route.RouteEvent;
import com.orinate.game.model.update.NPCUpdate;
import com.orinate.game.model.update.PlayerUpdate;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator.Priority;
import com.orinate.io.OutBuffer;
import com.orinate.network.codec.login.LoginDecoder.LoginType;
import com.orinate.network.packet.PacketWriter;
import com.orinate.util.entity.FileManager;

/**
 * @author Tom
 * 
 */
public class Player extends Entity {

	public static enum GameState {
		IN_LOBBY, IN_GAME;
	}

	private static final Logger logger = Logger.getLogger(Player.class.getName());
	private static final long serialVersionUID = 1L;

	private transient boolean isClientFocused;
	private transient Channel channel;
	private transient PacketWriter writer;
	private transient PlayerUpdate update;
	private transient NPCUpdate npcUpdate;
	private transient boolean online;
	private transient GameState gameState;
	private transient boolean loginDataSent;
	private transient InterfaceManager interfaceManager;
	private transient boolean logoutToLobby;
	private transient DialogueManager dialogueManager;
	private transient SkillManager skillManager;
	private transient RouteEvent routeEvent;
	private transient AbilityRepository abilities;
	private transient int fps;
	private transient int chatType;
	private transient int offheap;
	private transient int gameServerPing;
	private transient long foodDelay;
	private transient long lastBonfire;
	private transient MusicHandler musicHandler;

	private int customRenderAnim = -1;
	private FriendListManager friendsListManager;
	private FriendsChatManager friendsChatManager;
	private PlayerDefinition definition;
	private Appearance appearance;
	private Inventory inventory;
	private Equipment equipment;
	private HashMap<Integer, Object> interfaceSettings;
	private boolean interfacesLocked;
	private ActionBar actionBar;
	private Bank bank;
	private short[][] capeColours;
	private boolean[] boons;
	private LodestoneNetwork lodestoneNetwork;
	private GrandExchange exchange;

	public Player(PlayerDefinition definition) {
		this.definition = definition;
		this.inventory = new Inventory(this);
		this.equipment = new Equipment(this);
		this.interfaceSettings = new HashMap<Integer, Object>();
		this.appearance = new Appearance(this);
		this.setSkills(new Skills(this));
		this.actionBar = new ActionBar(this);
		this.bank = new Bank(this);
		this.capeColours = new short[2][];
		this.capeColours[0] = Arrays.copyOf(ItemDefinition.forId(20767).originalModelColors, 4);
		this.capeColours[1] = Arrays.copyOf(ItemDefinition.forId(20769).originalModelColors, 4);
		this.boons = new boolean[12];
		this.lodestoneNetwork = new LodestoneNetwork(this);
		this.friendsListManager = new FriendListManager(this);
		this.friendsChatManager = new FriendsChatManager();
		musicHandler = new MusicHandler();
		this.setExchange(new GrandExchange(this));
	}

	@Override
	public void initEntity() {
		super.initEntity();
		this.setSize(1);
	}

	@Override
	public CombatStyle getCombatStyle(boolean offhand) {
		Item weapon = equipment.get(offhand ? Equipment.SLOT_OFFHAND : Equipment.SLOT_ONHAND);
		// TODO: if (getProperties().getMagicSpell()[offhand ? 1 : 0] != null) {
		// return CombatStyle.MAGIC;
		// }
		if (weapon != null) {
			CombatStyle style = weapon.getDefinitions().getCombatStyle();
			if (style != null) {
				return style;
			}
		}
		return offhand ? null : CombatStyle.MELEE;
	}

	@Override
	public int getMaxHitpoints() {
		return getSkills().getLevel(Skills.HITPOINTS) * 40 + getProperties().getBonuses().getLifepoints();
	}

	public void init(Channel channel, LoginType type) {
		if (friendsListManager == null)
			friendsListManager = new FriendListManager(this);
		if (inventory == null)
			inventory = new Inventory(this);
		if (equipment == null)
			equipment = new Equipment(this);
		if (appearance == null)
			appearance = new Appearance(this);
		if (interfaceSettings == null) {
			interfaceSettings = new HashMap<Integer, Object>();
			setDefaultInterfaceLayout();
		}
		if (musicHandler == null) {
			musicHandler = new MusicHandler();
		}
		if (getSkills() == null)
			setSkills(new Skills(this));
		if (actionBar == null)
			actionBar = new ActionBar(this);
		if (bank == null)
			bank = new Bank(this);
		if (capeColours == null) {
			capeColours = new short[2][];
			capeColours[0] = Arrays.copyOf(ItemDefinition.forId(20767).originalModelColors, 4);
			capeColours[1] = Arrays.copyOf(ItemDefinition.forId(20769).originalModelColors, 4);
		}
		if (boons == null)
			boons = new boolean[12];
		if (lodestoneNetwork == null)
			lodestoneNetwork = new LodestoneNetwork(this);
		if (getGrandExchange() == null)
			setExchange(new GrandExchange(this));
		if (friendsChatManager == null)
			friendsChatManager = new FriendsChatManager();
		if (type.equals(LoginType.GAME)) {
			abilities = new AbilityRepository();
			abilities.load();
			update = new PlayerUpdate(this);
			npcUpdate = new NPCUpdate(this);
			interfaceManager = new InterfaceManager(this);
			dialogueManager = new DialogueManager(this);
			skillManager = new SkillManager(this);
			inventory.setPlayer(this);
			equipment.setPlayer(this);
			appearance.setPlayer(this);
			getSkills().setEntity(this);
			actionBar.setPlayer(this);
			bank.setPlayer(this);
			lodestoneNetwork.setPlayer(this);
			friendsListManager.setPlayer(this);
			friendsChatManager.setPlayer(this);
			getGrandExchange().setPlayer(this);
		} else {
			friendsListManager.setPlayer(this);
			friendsChatManager.setPlayer(this);
		}
		this.initEntity();
		this.channel = channel;
		this.writer = new PacketWriter(this);
//		musicHandler.setPlayer(this);
//		musicHandler.init();
	}

	public void save() {
		//friendsChatManager.leaveChat(true);
		setFinished(true);
		World.updateEntityRegion(this);

		World.getPlayers().remove(this);
		if (isLogoutToLobby()) {
			World.addLobbyPlayer(this);
		}

		loginDataSent = false;

		if (!isLogoutToLobby()) {
			online = false;
		}

		FileManager.savePlayer(this);
		logger.info("Logout request complete [username=" + definition.getUsername() + ", toLobby=" + isLogoutToLobby() + "]");
	}

	@Override
	public Animation getCombatAnimation(int index) {
		int slot = index == 0 ? Equipment.SLOT_ONHAND : Equipment.SLOT_OFFHAND;
		int animId = -1;
		Item item;
		if (index < 2) {
			if ((item = equipment.get(slot)) == null) {
				animId = 18226;
			} else {
				animId = item.getDefinitions().getMainhandEmote();
				if (index == 1) {
					animId = item.getDefinitions().getOffhandEmote();
				}
			}
		}
		return Animation.create(animId, index == 1 ? Priority.LOW : Priority.MID);
	}

	@Override
	public void process() {
		super.process();
		if (routeEvent != null && routeEvent.processEvent(this)) {
			routeEvent = null;
		}
		skillManager.process();
//		musicHandler.process();
	}

	@Override
	public void resetUpdate() {
		super.resetUpdate();
		setFirstStep(false);
		update.reset();
	}

	@Override
	public void setRunning(boolean run) {
		super.setRunning(run);
		sendRunButtonConfig();
	}

	public void sendRunButtonConfig() {
		getWriter().sendConfig(463, isRunning() ? 1 : 0);
	}

	@Override
	public String toString() {
		return "[id=" + getIndex() + ", name=" + definition.getUsername() + ", rights= + " + getRights() + "]";
	}

	public void saveInterfaceSettingsKey(int key, Object settings) {
		if (interfaceSettings.containsKey(key)) {
			interfaceSettings.remove(key);
		}
		interfaceSettings.put(key, settings);
	}

	@Override
	public void processDeath() {

	}

	public void sendLoginData(LoginType loginType) {
		if (loginType.equals(LoginType.LOBBY)) {
			writer.sendLobbyInformation();
			gameState = GameState.IN_LOBBY;
			//friendsListManager.loadFriendsList();
			//friendsChatManager.load();
		} else {
			loadMapRegions();
			writer.sendWorldInformation();
			writer.writeInterfaceInfoReset();
			inventory.init();
			equipment.init();
			appearance.refresh();
			getSkills().sendSkills();
			actionBar.refresh();
			lodestoneNetwork.onLogin();
			sendRunButtonConfig();
			friendsListManager.loadFriendsList();
			friendsChatManager.load();
			loginDataSent = true;
			World.updateEntityRegion(this);
			gameState = GameState.IN_GAME;
			customRenderAnim = -1;
		}
		online = true;
		logger.info("Login request complete [username=" + definition.getUsername() + ", index=" + getIndex() + ", type=" + loginType + "]");
	}

	@Override
	public void finish() {

	}

	@Override
	public void loadMapRegions() {
		super.loadMapRegions();
		writer.sendBuildSceneGraph(!loginDataSent);
	}

	public GameState getGameState() {
		return gameState;
	}

	public ChannelFuture write(OutBuffer buffer) {
		if (!channel.isConnected()) {
			return null;
		}
		return channel.write(buffer);
	}

	public Channel getChannel() {
		return channel;
	}

	public PlayerDefinition getDefinition() {
		return definition;
	}

	public PacketWriter getWriter() {
		return writer;
	}

	public PlayerUpdate getUpdate() {
		return update;
	}

	public Appearance getAppearance() {
		return appearance;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public HashMap<Integer, Object> getInterfaceSettings() {
		return interfaceSettings;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public void totalRefresh() {
		appearance.refresh();
		equipment.init();
		inventory.init();
		getSkills().refresh();
		actionBar.refresh();
	}

	public void setDefaultInterfaceLayout() {
		saveInterfaceSettingsKey(3825, -2146827988);
		saveInterfaceSettingsKey(3834, -2146827988);
		saveInterfaceSettingsKey(3295, 32);
		saveInterfaceSettingsKey(3836, -2146827988);
		saveInterfaceSettingsKey(3318, -2146885110);
		saveInterfaceSettingsKey(3319, 16773120);
		saveInterfaceSettingsKey(3316, 1638625);
		saveInterfaceSettingsKey(3317, 16777215);
		saveInterfaceSettingsKey(3314, 352321536);
		saveInterfaceSettingsKey(3315, 385875968);
		saveInterfaceSettingsKey(3312, 335544320);
		saveInterfaceSettingsKey(3313, 369098752);
		saveInterfaceSettingsKey(3326, 287005);
		saveInterfaceSettingsKey(3327, 8372224);
		saveInterfaceSettingsKey(3324, 1638625);
		saveInterfaceSettingsKey(3325, 16777215);
		saveInterfaceSettingsKey(3322, 1638625);
		saveInterfaceSettingsKey(3323, 16777215);
		saveInterfaceSettingsKey(3320, -2146041344);
		saveInterfaceSettingsKey(3321, 8374271);
		saveInterfaceSettingsKey(3303, 28479488);
		saveInterfaceSettingsKey(3302, -2130394560);
		saveInterfaceSettingsKey(3301, 4095);
		saveInterfaceSettingsKey(3779, 167772160);
		saveInterfaceSettingsKey(3300, -2146565954);
		saveInterfaceSettingsKey(3778, 385875968);
		saveInterfaceSettingsKey(3781, 167772160);
		saveInterfaceSettingsKey(3299, 33554431);
		saveInterfaceSettingsKey(3780, 385875968);
		saveInterfaceSettingsKey(3298, -2130509472);
		saveInterfaceSettingsKey(3296, -2130706433);
		saveInterfaceSettingsKey(3311, 352321536);
		saveInterfaceSettingsKey(3310, 318767104);
		saveInterfaceSettingsKey(3309, 2047);
		saveInterfaceSettingsKey(3308, -2130709888);
		saveInterfaceSettingsKey(3307, 16777215);
		saveInterfaceSettingsKey(3306, 1638625);
		saveInterfaceSettingsKey(3305, 352317440);
		saveInterfaceSettingsKey(3304, -2146766272);
		saveInterfaceSettingsKey(3770, 167772160);
		saveInterfaceSettingsKey(3769, 385875968);
		saveInterfaceSettingsKey(3721, 100992003);
		saveInterfaceSettingsKey(3722, 7);
		saveInterfaceSettingsKey(2989, -2147344282);
		saveInterfaceSettingsKey(2988, 3255);
		saveInterfaceSettingsKey(2991, -2145025348);
		saveInterfaceSettingsKey(2985, 1638625);
		saveInterfaceSettingsKey(2984, 3073);
		saveInterfaceSettingsKey(2987, -2147176268);
		saveInterfaceSettingsKey(2986, 16777215);
		saveInterfaceSettingsKey(2981, -2146303776);
		saveInterfaceSettingsKey(2980, 4196352);
		saveInterfaceSettingsKey(2983, -2147348360);
		saveInterfaceSettingsKey(2982, 8392703);
		saveInterfaceSettingsKey(2977, -2146115221);
		saveInterfaceSettingsKey(2976, 5607423);
		saveInterfaceSettingsKey(2979, -2146664148);
		saveInterfaceSettingsKey(2978, 2048);
		saveInterfaceSettingsKey(2996, 8325113);
		saveInterfaceSettingsKey(2992, 8390656);
		saveInterfaceSettingsKey(2993, -2146848419);
		saveInterfaceSettingsKey(2994, 4196352);
		saveInterfaceSettingsKey(2995, 2360096);
		saveInterfaceSettingsKey(2959, 1638625);
		saveInterfaceSettingsKey(2957, 402653184);
		saveInterfaceSettingsKey(2956, 16777215);
		saveInterfaceSettingsKey(2955, 1102022);
		saveInterfaceSettingsKey(2954, 16777215);
		saveInterfaceSettingsKey(2953, 1638625);
		saveInterfaceSettingsKey(2952, 16777215);
		saveInterfaceSettingsKey(2951, 1638625);
		saveInterfaceSettingsKey(2950, 16777215);
		saveInterfaceSettingsKey(2949, 1638625);
		saveInterfaceSettingsKey(2948, 16777215);
		saveInterfaceSettingsKey(2947, 1638625);
		saveInterfaceSettingsKey(2946, 16777215);
		saveInterfaceSettingsKey(2945, 1638625);
		saveInterfaceSettingsKey(2944, 8388608);
		saveInterfaceSettingsKey(2974, 10187906);
		saveInterfaceSettingsKey(2975, -2147348352);
		saveInterfaceSettingsKey(2972, 16777215);
		saveInterfaceSettingsKey(2973, -2147221254);
		saveInterfaceSettingsKey(2970, 16777215);
		saveInterfaceSettingsKey(2971, 1638625);
		saveInterfaceSettingsKey(2968, 16777215);
		saveInterfaceSettingsKey(2969, 1638625);
		saveInterfaceSettingsKey(2966, 16777215);
		saveInterfaceSettingsKey(2967, 1638625);
		saveInterfaceSettingsKey(2964, 402653184);
		saveInterfaceSettingsKey(2965, 1638625);
		saveInterfaceSettingsKey(2962, 16777215);
		saveInterfaceSettingsKey(2963, 369098752);
		saveInterfaceSettingsKey(2960, 16777215);
		saveInterfaceSettingsKey(2961, 1638625);
		saveInterfaceSettingsKey(3459, -2145025348);
		saveInterfaceSettingsKey(3458, 4186112);
		saveInterfaceSettingsKey(3457, -2147344282);
		saveInterfaceSettingsKey(3456, 1797000);
		saveInterfaceSettingsKey(3463, 2360096);
		saveInterfaceSettingsKey(3462, 4175872);
		saveInterfaceSettingsKey(3461, -2146848419);
		saveInterfaceSettingsKey(3460, 8374271);
		saveInterfaceSettingsKey(3465, 126);
		saveInterfaceSettingsKey(3464, 8374271);
		saveInterfaceSettingsKey(3406, 8374271);
		saveInterfaceSettingsKey(2852, 589439264);
		saveInterfaceSettingsKey(3407, 1638625);
		saveInterfaceSettingsKey(2853, 336794129);
		saveInterfaceSettingsKey(3404, 16773120);
		saveInterfaceSettingsKey(2854, 454698005);
		saveInterfaceSettingsKey(3405, -2146041344);
		saveInterfaceSettingsKey(2855, 572588031);
		saveInterfaceSettingsKey(3402, 16777215);
		saveInterfaceSettingsKey(3403, -2146885110);
		saveInterfaceSettingsKey(3400, 385875968);
		saveInterfaceSettingsKey(3401, 1638625);
		saveInterfaceSettingsKey(3398, 369098752);
		saveInterfaceSettingsKey(2860, 1023);
		saveInterfaceSettingsKey(3399, 352321536);
		saveInterfaceSettingsKey(3396, 352321536);
		saveInterfaceSettingsKey(2862, -1);
		saveInterfaceSettingsKey(3397, 335544320);
		saveInterfaceSettingsKey(2863, 691470335);
		saveInterfaceSettingsKey(3394, 2047);
		saveInterfaceSettingsKey(2856, 319951120);
		saveInterfaceSettingsKey(3395, 318767104);
		saveInterfaceSettingsKey(2857, -236);
		saveInterfaceSettingsKey(3392, 16777215);
		saveInterfaceSettingsKey(2858, 23039);
		saveInterfaceSettingsKey(3393, -2130709888);
		saveInterfaceSettingsKey(2859, -268435456);
		saveInterfaceSettingsKey(3423, 1102022);
		saveInterfaceSettingsKey(2869, 33646952);
		saveInterfaceSettingsKey(3422, 16777215);
		saveInterfaceSettingsKey(2868, 842019327);
		saveInterfaceSettingsKey(3421, 1638625);
		saveInterfaceSettingsKey(3420, 16777215);
		saveInterfaceSettingsKey(3419, 1638625);
		saveInterfaceSettingsKey(2865, -13162457);
		saveInterfaceSettingsKey(3418, 16777215);
		saveInterfaceSettingsKey(2864, 642008373);
		saveInterfaceSettingsKey(3417, 1638625);
		saveInterfaceSettingsKey(2867, -47560);
		saveInterfaceSettingsKey(3416, 16777215);
		saveInterfaceSettingsKey(2866, -1);
		saveInterfaceSettingsKey(3415, 1638625);
		saveInterfaceSettingsKey(3414, 16777215);
		saveInterfaceSettingsKey(3413, 1638625);
		saveInterfaceSettingsKey(3412, 8372224);
		saveInterfaceSettingsKey(3411, 287005);
		saveInterfaceSettingsKey(3410, 16777215);
		saveInterfaceSettingsKey(3409, 1638625);
		saveInterfaceSettingsKey(3408, 16777215);
		saveInterfaceSettingsKey(3436, 16777215);
		saveInterfaceSettingsKey(3437, 1638625);
		saveInterfaceSettingsKey(3438, 16777215);
		saveInterfaceSettingsKey(3439, 1638625);
		saveInterfaceSettingsKey(3432, 402653184);
		saveInterfaceSettingsKey(3433, 1638625);
		saveInterfaceSettingsKey(3434, 16777215);
		saveInterfaceSettingsKey(3435, 1638625);
		saveInterfaceSettingsKey(3428, 16777215);
		saveInterfaceSettingsKey(3429, 1638625);
		saveInterfaceSettingsKey(3430, 16777215);
		saveInterfaceSettingsKey(3431, 369098752);
		saveInterfaceSettingsKey(3424, 16777215);
		saveInterfaceSettingsKey(3425, 402653184);
		saveInterfaceSettingsKey(3427, 1638625);
		saveInterfaceSettingsKey(3453, 1638625);
		saveInterfaceSettingsKey(3452, 3072);
		saveInterfaceSettingsKey(3455, -2147176268);
		saveInterfaceSettingsKey(3454, 16777215);
		saveInterfaceSettingsKey(3449, -2146303776);
		saveInterfaceSettingsKey(3448, 4188159);
		saveInterfaceSettingsKey(3451, -2147348360);
		saveInterfaceSettingsKey(3450, 8376319);
		saveInterfaceSettingsKey(3445, -2146115221);
		saveInterfaceSettingsKey(3444, 5603327);
		saveInterfaceSettingsKey(3447, -2146664148);
		saveInterfaceSettingsKey(3446, 2048);
		saveInterfaceSettingsKey(3441, -2147221254);
		saveInterfaceSettingsKey(3440, 16777215);
		saveInterfaceSettingsKey(3443, -2147348352);
		saveInterfaceSettingsKey(3442, 10183807);
		saveInterfaceSettingsKey(3338, 1102022);
		saveInterfaceSettingsKey(2912, 32);
		saveInterfaceSettingsKey(3339, 16777215);
		saveInterfaceSettingsKey(2913, -2130706433);
		saveInterfaceSettingsKey(3336, 1638625);
		saveInterfaceSettingsKey(2914, 8390656);
		saveInterfaceSettingsKey(2915, -2130509472);
		saveInterfaceSettingsKey(3337, 16777215);
		saveInterfaceSettingsKey(2916, 33554431);
		saveInterfaceSettingsKey(3342, 1638625);
		saveInterfaceSettingsKey(2917, -2146565954);
		saveInterfaceSettingsKey(3343, 16777215);
		saveInterfaceSettingsKey(2918, 4095);
		saveInterfaceSettingsKey(3340, 402653184);
		saveInterfaceSettingsKey(2919, -2130394560);
		saveInterfaceSettingsKey(2920, 28491776);
		saveInterfaceSettingsKey(3330, 1638625);
		saveInterfaceSettingsKey(2921, -2146520512);
		saveInterfaceSettingsKey(3331, 16777215);
		saveInterfaceSettingsKey(2922, 352317440);
		saveInterfaceSettingsKey(3328, 1638625);
		saveInterfaceSettingsKey(2923, 1638625);
		saveInterfaceSettingsKey(3329, 16777215);
		saveInterfaceSettingsKey(2924, 16777215);
		saveInterfaceSettingsKey(3334, 1638625);
		saveInterfaceSettingsKey(2925, -2144451968);
		saveInterfaceSettingsKey(3335, 16777215);
		saveInterfaceSettingsKey(2926, 2048);
		saveInterfaceSettingsKey(3332, 1638625);
		saveInterfaceSettingsKey(2927, 318767104);
		saveInterfaceSettingsKey(3333, 16777215);
		saveInterfaceSettingsKey(2929, 335544320);
		saveInterfaceSettingsKey(3355, 16777215);
		saveInterfaceSettingsKey(2928, 352321536);
		saveInterfaceSettingsKey(3354, 1638625);
		saveInterfaceSettingsKey(2931, 352321536);
		saveInterfaceSettingsKey(3353, 16777215);
		saveInterfaceSettingsKey(2930, 369098752);
		saveInterfaceSettingsKey(3352, 1638625);
		saveInterfaceSettingsKey(2933, 1638625);
		saveInterfaceSettingsKey(3359, 5603327);
		saveInterfaceSettingsKey(2932, 385875968);
		saveInterfaceSettingsKey(3358, -2147348352);
		saveInterfaceSettingsKey(2935, -2146885110);
		saveInterfaceSettingsKey(3357, 10183807);
		saveInterfaceSettingsKey(2934, 16777215);
		saveInterfaceSettingsKey(3356, -2147221254);
		saveInterfaceSettingsKey(2937, -2146041344);
		saveInterfaceSettingsKey(3347, 402653184);
		saveInterfaceSettingsKey(2936, 15155700);
		saveInterfaceSettingsKey(3346, 369098752);
		saveInterfaceSettingsKey(2939, 1638625);
		saveInterfaceSettingsKey(3345, 16777215);
		saveInterfaceSettingsKey(2938, 8390656);
		saveInterfaceSettingsKey(3344, 1638625);
		saveInterfaceSettingsKey(2941, 1638625);
		saveInterfaceSettingsKey(3351, 16777215);
		saveInterfaceSettingsKey(2940, 16777215);
		saveInterfaceSettingsKey(3350, 1638625);
		saveInterfaceSettingsKey(2943, 287005);
		saveInterfaceSettingsKey(3349, 16777215);
		saveInterfaceSettingsKey(2942, 16777215);
		saveInterfaceSettingsKey(3348, 1638625);
		saveInterfaceSettingsKey(3368, 1638625);
		saveInterfaceSettingsKey(3369, 16777215);
		saveInterfaceSettingsKey(3370, -2147176268);
		saveInterfaceSettingsKey(3371, 1797000);
		saveInterfaceSettingsKey(3372, -2147344282);
		saveInterfaceSettingsKey(3373, 4186112);
		saveInterfaceSettingsKey(3374, -2145025348);
		saveInterfaceSettingsKey(3375, 8374271);
		saveInterfaceSettingsKey(3360, -2146115221);
		saveInterfaceSettingsKey(3361, 2048);
		saveInterfaceSettingsKey(3362, -2146664148);
		saveInterfaceSettingsKey(3363, 4188159);
		saveInterfaceSettingsKey(3364, -2146303776);
		saveInterfaceSettingsKey(3365, 8376319);
		saveInterfaceSettingsKey(3366, -2147348360);
		saveInterfaceSettingsKey(3367, 3072);
		saveInterfaceSettingsKey(3385, -2146565954);
		saveInterfaceSettingsKey(3384, 33554431);
		saveInterfaceSettingsKey(3387, -2130394560);
		saveInterfaceSettingsKey(3386, 4095);
		saveInterfaceSettingsKey(3389, -2146766272);
		saveInterfaceSettingsKey(3388, 28479488);
		saveInterfaceSettingsKey(3391, 1638625);
		saveInterfaceSettingsKey(3390, 352317440);
		saveInterfaceSettingsKey(3377, 4175872);
		saveInterfaceSettingsKey(3376, -2146848419);
		saveInterfaceSettingsKey(3379, 8374271);
		saveInterfaceSettingsKey(3378, 2360096);
		saveInterfaceSettingsKey(3381, -2130706433);
		saveInterfaceSettingsKey(3380, 32);
		saveInterfaceSettingsKey(3383, -2130509472);
	}

	public int getRights() {
		return 2;
	}

	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
	}

	public void setInterfacesLocked(boolean interfacesLocked) {
		this.interfacesLocked = interfacesLocked;
	}

	public boolean isInterfacesLocked() {
		return interfacesLocked;
	}

	public boolean isLogoutToLobby() {
		return logoutToLobby;
	}

	public ActionBar getActionBar() {
		return actionBar;
	}

	public NPCUpdate getNpcUpdate() {
		return npcUpdate;
	}

	public void logout(boolean lobby) {
		if (lobby) {
			logoutToLobby = true;
		}
		writer.sendLogout(lobby);
	}

	public SkillManager getSkillManager() {
		return skillManager;
	}

	public DialogueManager getDialogueManager() {
		return dialogueManager;
	}

	public Bank getBank() {
		return bank;
	}

	public void resetAll() {
		resetAll(true);
	}

	private void resetAll(boolean stopWalk) {
		resetAll(stopWalk, true);
	}

	public void resetAll(boolean stopWalk, boolean stopInterfaces) {
		if (stopInterfaces) {
			closeInterfaces();
		}
		if (stopWalk) {
			resetWalkSteps();
		}
		this.foodDelay = 0;
		getCombatSchedule().end();
		routeEvent = null;
		skillManager.stopTraining();
		resetFaceEntity();
	}

	private void closeInterfaces() {
		if (interfaceManager.containsChatboxInterface()) {
			interfaceManager.closeChatboxInterface();
		}
		if (interfaceManager.containsScreenInterface()) {
			interfaceManager.closeScreenInterface();
		}
		if (interfaceManager.containsInventoryInterface()) {
			interfaceManager.closeInventoryInterface();
		}
		dialogueManager.finishDialogue();
	}

	public RouteEvent getRouteEvent() {
		return routeEvent;
	}

	public void setRouteEvent(RouteEvent routeEvent) {
		this.routeEvent = routeEvent;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public int getOffheap() {
		return offheap;
	}

	public void setOffheap(int offheap) {
		this.offheap = offheap;
	}

	public int getGameServerPing() {
		return gameServerPing;
	}

	public void setGameServerPing(int gameServerPing) {
		this.gameServerPing = gameServerPing;
	}

	public short[][] getCapeColours() {
		return capeColours;
	}

	public boolean getBoon(int index) {
		return boons[index];
	}

	public LodestoneNetwork getLodestoneNetwork() {
		return lodestoneNetwork;
	}

	@Override
	public int getRenderAnim() {
		int renderId = customRenderAnim == -1 ? 2699 : customRenderAnim;
		if (equipment.get(Equipment.SLOT_ONHAND) != null) {
			ItemDefinition def = equipment.get(Equipment.SLOT_ONHAND).getDefinitions();
			renderId = getCombatSchedule().isCombatStance() ? def.getAggressiveAnimation() : def.getPassiveAnimation();
		}
		return renderId;
	}

	public long getFoodDelay() {
		return foodDelay;
	}

	public void setFoodDelay(long foodDelay) {
		this.foodDelay = foodDelay;
	}

	/**
	 * @return the abilities
	 */
	public AbilityRepository getAbilities() {
		return abilities;
	}

	/**
	 * @param abilities
	 *            the abilities to set
	 */
	public void setAbilities(AbilityRepository abilities) {
		this.abilities = abilities;
	}

	public FriendListManager getFriendsListManager() {
		return friendsListManager;
	}

	public void setFriendsListManager(FriendListManager friendsListManager) {
		this.friendsListManager = friendsListManager;
	}

	public boolean isClientFocused() {
		return isClientFocused;
	}

	public void setClientFocused(boolean isClientFocused) {
		this.isClientFocused = isClientFocused;
	}

	public void sendPublicChat(PublicChatMessage publicMessage) {
		for (Player localPlayer : World.getPlayers()) {
			if (localPlayer == null)
				continue;
			localPlayer.getWriter().sendPublicChatMessage(this, publicMessage);
		}
	}

	public void sendMessage(String string) {
		writer.sendGameMessage(string);
	}

	/**
	 * @return the exchange
	 */
	public GrandExchange getGrandExchange() {
		return exchange;
	}

	/**
	 * @param exchange
	 *            the exchange to set
	 */
	public void setExchange(GrandExchange exchange) {
		this.exchange = exchange;
	}

	/**
	 * @return the customRenderAnim
	 */
	public int getCustomRenderAnim() {
		return customRenderAnim;
	}

	/**
	 * @param customRenderAnim
	 *            the customRenderAnim to set
	 */
	public void setCustomRenderAnim(int customRenderAnim) {
		this.customRenderAnim = customRenderAnim;
		appearance.refresh();
	}

	/**
	 * @return the lastBonfire
	 */
	public long getLastBonfire() {
		return lastBonfire;
	}

	/**
	 * @param lastBonfire
	 *            the lastBonfire to set
	 */
	public void setLastBonfire(long lastBonfire) {
		this.lastBonfire = lastBonfire;
	}

	public int getAvailableMoney() {
		// TODO money pouch.
		return inventory.getNumberOf(995);
	}
	
	public int getChatType() {
		return chatType;
	}
	
	public void setChatType(int chatType) {
		this.chatType = chatType;
	}

	public FriendsChatManager getFriendsChatManager() {
		return friendsChatManager;
	}
	
	public String getIP() {
		return channel == null ? "" : channel.getRemoteAddress().toString().split(":")[0].replace("/", "");
	}
	
	public boolean isOwner() {
		return Constants.isOwnerIP(getIP());
	}

	/**
	 * @return the musicHandler
	 */
	public MusicHandler getMusicHandler() {
		return musicHandler;
	}

	/**
	 * @param musicHandler the musicHandler to set
	 */
	public void setMusicHandler(MusicHandler musicHandler) {
		this.musicHandler = musicHandler;
	}
}