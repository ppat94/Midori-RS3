package com.orinate.game.content.dialogue.impl;

import com.orinate.game.content.dialogue.Dialogue;

public class OrlaFairweather extends Dialogue {

	@Override
	public void onStart() {
		this.sendPlayerDialogue(NORMAL, "What's going on here?");
		this.stage = 1;
	}

	@Override
	public void process(Options option) {
		if (stage == 1) {
			this.sendNPCDialogue(NORMAL, npcId(), "So glad you asked! I'm Orla Fairweather, and these are my<br>colleagues. We're pioneers, researcing the art of<br>divination!");
			this.stage = 2;
		} else if (stage == 2) {
			this.sendPlayerDialogue(CROOKED_HEAD, "Divination? You mean like reading tea leaves, casting<br>bones, that sort of thing?");
			this.stage = 3;
		} else if (stage == 3) {
			this.sendNPCDialogue(NORMAL, npcId(), "Oh no, this is much larger! Here, we are dealing with the<br>energy of the world! The fate of Gielinor<br> may be deterimined by our actions!");
			this.stage = 4;
		} else if (this.stage == 4) {
			this.sendPlayerDialogue(EYES_WIDE, "Wow - that does sound interesting.");
			this.stage = 5;
		} else if (stage == 5) {
			this.sendNPCDialogue(NORMAL, npcId(), "Oh yes. It's early days, but we've had exciting results<br> already.");
			this.stage = 6;
		} else if (stage == 6) {
			this.sendPlayerDialogue(NO_EXPRESSION, "Can I start training divination myself?");
			this.stage = 7;
		} else if (stage == 7) {
			this.sendNPCDialogue(NORMAL, npcId(), "Of course! We could use all the help we get!");
			this.stage = 8;
		} else if (stage == 8) {
			this.sendNPCDialogue(NORMAL, npcId(), "The first step is to gather a memory. The wisps to the<br>south will do - see how you get on and come back to me if<br>you need help.");
			this.stage = 9;
		} else if (stage == 9) {
			this.finish();
		}
	}

	@Override
	public int npcId() {
		return 18196;
	}
}
