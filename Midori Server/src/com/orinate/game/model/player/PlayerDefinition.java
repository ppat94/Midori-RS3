package com.orinate.game.model.player;

import java.io.Serializable;

import com.orinate.util.text.StringUtil;

/**
 * @author Tom
 * @author Emperor
 * 
 */
public class PlayerDefinition implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	private String displayName;

	private int rights;

	private int runEnergy;

	public PlayerDefinition(String username, String password) {
		this.username = StringUtil.formatProtocol(username);
		this.password = password;
		this.rights = 0;
		this.runEnergy = 100;
	}
	
	public String getDisplayName() {
		if(displayName != null)
			return displayName;
		return StringUtil.formatName(username);
	}

	public String getUsername() {
		return StringUtil.formatProtocol(username);
	}

	public String getPassword() {
		return password;
	}

	public boolean hasDisplayName() {
		return displayName != null;
	}

	public int getRights() {
		return rights;
	}

	public void setRights(int rights) {
		this.rights = rights;
	}

	public int getRunEnergy() {
		return runEnergy;
	}

	public void setRunEnergy(int runEnergy) {
		this.runEnergy = runEnergy;
	}
}