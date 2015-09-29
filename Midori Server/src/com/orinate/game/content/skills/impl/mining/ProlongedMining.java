package com.orinate.game.content.skills.impl.mining;

import com.orinate.game.content.skills.Skill;
import com.orinate.game.model.player.Player;

/**
 * 
 * @author Trenton
 * 
 */
public class ProlongedMining extends Skill {

	public ProlongedMining(Player player, Object[] args) {
		super(player, args);
	}

	@Override
	public boolean onSkillStart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onProcess() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSkillEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getSkillDelay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canProcess() {
		// TODO Auto-generated method stub
		return false;
	}
}
