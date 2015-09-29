package com.orinate.network.packet;

import com.orinate.io.InBuffer;

/**
 * @author Tom
 * 
 */
public class PacketHeader {

	private int opcode;
	private InBuffer buffer;

	public PacketHeader(InBuffer buffer, int opcode) {
		this.opcode = opcode;
		this.buffer = buffer;
	}

	public int getOpcode() {
		return opcode;
	}

	public InBuffer getBuffer() {
		return buffer;
	}
}
