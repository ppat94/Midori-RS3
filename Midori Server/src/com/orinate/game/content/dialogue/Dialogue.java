package com.orinate.game.content.dialogue;

import java.util.List;

import com.orinate.cache.parsers.NPCDefinitions;
import com.orinate.game.model.player.Player;

/**
 * @author Tom
 * 
 */
public abstract class Dialogue {

	public enum Options {
		FIRST, SECOND, THIRD, FOURTH, FIFTH;

		public static Options forComponent(int componentId) {
			for (Options opt : Options.values()) {
				if ((opt.ordinal() + 3) == componentId) {
					return opt;
				}
			}
			return null;
		}
	}

	public static int NO_EXPRESSION = 9760, SAD = 9764, SAD_TWO = 9768, NO_EXPRESSION_TWO = 9772, WHY = 9776;
	public static int SCARED = 9780, MIDLY_ANGRY = 9784, ANGRY = 9788, VERY_ANGRY = 9792, ANGRY_TWO = 9796;
	public static int MANIC_FACE = 9800, JUST_LISTEN = 9804, PLAIN_TALKING = 9808, LOOK_DOWN = 9812;
	public static int WHAT_THE = 9816, WHAT_THE_TWO = 9820, EYES_WIDE = 9824, CROOKED_HEAD = 9828;
	public static int GLANCE_DOWN = 9832, UNSURE = 9836, LISTEN_LAUGH = 9840, TALK_SWING = 9844, NORMAL = 9847;
	public static int GOOFY_LAUGH = 9851, NORMAL_STILL = 9855, THINKING_STILL = 9859, LOOKING_UP = 9862;

	private Player player;
	private List<Object> parameters;
	protected int stage;

	public abstract int npcId();

	public abstract void onStart();

	public abstract void process(Options option);

	public void sendPlayerDialogue(int animationId, String... lines) {
		StringBuffer buffer = new StringBuffer();
		for (int curIdx = 0; curIdx < lines.length; curIdx++) {
			lines[curIdx] = lines[curIdx].replaceAll("@name@", player.getDefinition().getDisplayName());
			buffer.append(" " + lines[curIdx]);
		}
		String text = buffer.toString();
		player.getInterfaceManager().sendChatBoxInterface(1191);
		player.getWriter().sendIComponentText(1191, 2, player.getDefinition().getUsername());
		player.getWriter().sendIComponentText(1191, 10, text);
		player.getWriter().sendPlayerOnIComponent(1191, 8);
		player.getWriter().sendIComponentAnimation(animationId, 1191, 8);
		player.getWriter().sendCS2Script(8178);
	}

	public void sendNPCDialogue(int animationId, int npcId, String... lines) {
		StringBuffer buffer = new StringBuffer();
		for (int curIdx = 0; curIdx < lines.length; curIdx++) {
			lines[curIdx] = lines[curIdx].replaceAll("@name@", player.getDefinition().getDisplayName());
			buffer.append(" " + lines[curIdx]);
		}
		String title = "";
		if (!NPCDefinitions.forId(npcId).name.equalsIgnoreCase("null")) {
			title = NPCDefinitions.forId(npcId).name;
		}
		String text = buffer.toString();
		player.getInterfaceManager().sendChatBoxInterface(1184);
		player.getWriter().sendIComponentText(1184, 10, title);
		player.getWriter().sendIComponentText(1184, 9, text);
		player.getWriter().sendNPCOnIComponent(1184, 7, npcId);
		player.getWriter().sendIComponentAnimation(animationId, 1184, 7);
		player.getWriter().sendCS2Script(8178);
	}

	public void sendOptionDialogue(String title, String... options) {
		if (options.length > 5) {
			return;
		}
		String[] newOptions = new String[5];
		for (int idx = 0; idx < newOptions.length; idx++) {
			newOptions[idx] = "";
		}
		int curIdx = 0;
		for (String option : options) {
			if (option == null) {
				continue;
			}
			newOptions[curIdx++] = option;
		}
		player.getInterfaceManager().sendChatBoxInterface(1188);
		player.getWriter().sendIComponentText(1188, 14, title);
		player.getWriter().sendCS2Script(5589, newOptions[4], newOptions[3], newOptions[2], newOptions[1], newOptions[0], options.length);
		player.getWriter().sendCS2Script(8178);
	}

	public void finish() {
		player.getDialogueManager().finishDialogue();
	}

	public Player getPlayer() {
		return player;
	}

	public Object params(int idx) {
		return parameters.get(idx);
	}

	public void setParameters(List<Object> toSet) {
		this.parameters = toSet;
	}

	public void setPlayer(Player toSet) {
		this.player = toSet;
	}
}
