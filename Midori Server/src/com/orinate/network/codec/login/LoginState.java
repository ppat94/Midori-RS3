package com.orinate.network.codec.login;

/**
 * Represents a LoginState for the {@link OldLoginDecoder}
 * @author SonicForce41
 */
public enum LoginState {
	
	/**
	 * The values
	 */
	CONNECTION_TYPE,
	CLIENT_DETAILS,
	LOBBY_PAYLOAD,
	GAME_PAYLOAD;
}