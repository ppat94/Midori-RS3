package com.orinate.network.codec.handshake;

import com.orinate.io.OutBuffer;

/**
 * @author Tom
 * 
 */
public class HandshakeMessage {

	private OutBuffer buffer;

	public HandshakeMessage(OutBuffer buffer) {
		this.buffer = buffer;
	}

	public OutBuffer buffer() {
		return buffer;
	}
}
