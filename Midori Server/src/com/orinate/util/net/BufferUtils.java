package com.orinate.util.net;

import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @author Tom
 * 
 */
public class BufferUtils {

	public static String readString(ChannelBuffer buffer) {
		StringBuilder builder = new StringBuilder();
		int value;
		while ((value = buffer.readByte()) != 0) {
			builder.append((char) value);
		}
		return builder.toString();
	}

	public static String readString(ByteBuffer in) {
		StringBuilder sb = new StringBuilder();
		byte b;
		while (in.hasRemaining() && (b = in.get()) != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	public static int readLargeSmart(ByteBuffer buffer) {
		if (buffer.get(buffer.position()) > 0) {
			int value = buffer.getShort() & 0xFFFF;
			if (value == 32767) {
				return -1;
			}
			return value;
		}
		return buffer.getInt() & 0x7fffffff;
	}

	public static void writeInt(int val, int index, byte[] buffer) {
		buffer[index++] = (byte) (val >> 24);
		buffer[index++] = (byte) (val >> 16);
		buffer[index++] = (byte) (val >> 8);
		buffer[index++] = (byte) val;
	}

	public static int readInt(int index, byte[] buffer) {
		return ((buffer[index++] & 0xff) << 24) | ((buffer[index++] & 0xff) << 16) | ((buffer[index++] & 0xff) << 8) | (buffer[index++] & 0xff);
	}

	public static int readTriByte(ChannelBuffer buffer) {
		return (buffer.readByte() & 0xff) << 16 | (buffer.readByte() & 0xff) << 8 | (buffer.readByte() & 0xff);
	}
}
