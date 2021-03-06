package com.orinate.io;

/**
 * @author Tom
 * @author Emperor
 * 
 */
public class InBuffer extends Buffer {

	public InBuffer(int capacity) {
		this.buffer = new byte[capacity];
	}

	public InBuffer(byte[] buffer) {
		this.buffer = buffer;
		this.length = buffer.length;
	}

	public void checkPosition(int position) {
		if ((offset + position) >= buffer.length) {
			byte[] newBuffer = new byte[(offset + length) * 2];
			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
			this.buffer = newBuffer;
		}
	}

	public int getByte() {
		return buffer[offset++];
	}

	public int getByteC() {
		return -getByte();
	}

	public int getByteA() {
		return (getByte() & 0xFF) - 128;
	}

	public int getByteS() {
		return 128 - getByte();
	}

	public int getUnsignedByte() {
		return getByte() & 0xFF;
	}
	
	public int getUnsignedByte128() {
		return getUnsignedByte() - 128 & 0xff;
	}

	public int getInt() {
		int value = 0;
		value |= getUnsignedByte() << 24;
		value |= getUnsignedByte() << 16;
		value |= getUnsignedByte() << 8;
		value |= getUnsignedByte();
		return value;
	}

	public int getLEInt() {
		int value = getUnsignedByte();
		value |= getUnsignedByte() << 8;
		value |= getUnsignedByte() << 16;
		value |= getUnsignedByte() << 24;
		return value;
	}

	public String getString() {
		StringBuilder builder = new StringBuilder();
		int value;
		while ((value = getByte()) != 0) {
			builder.append((char) value);
		}
		return builder.toString();
	}

	public String getJagString() {
		getByte();
		return getString();
	}

	public int getTriByte() {
		int value = 0;
		value |= getByte() << 16;
		value |= getByte() << 8;
		value |= getUnsignedByte();
		return value;
	}

	public int getShort() {
		int value = 0;
		value |= getUnsignedByte() << 8;
		value |= getUnsignedByte();

		if (value > 32767) {
			value -= 0x10000;
		}
		return value;
	}

	public int getLEShort() {
		return getUnsignedByte() + (getUnsignedByte() << 8);
	}

	public int getShortA() {
		int value = (getUnsignedByte() << 8) + (getByte() - 128 & 0xff);
		if (value > 32767) {
			value -= 0x10000;
		}
		return value;
	}

	public int getLEShortA() {
		int value = (getByte() - 128 & 0xff) + (getUnsignedByte() << 8);
		if (value > 32767) {
			value -= 0x10000;
		}
		return value;
	}

	public int read128ShortLE() {
		int i = getUnsignedByte() + (getUnsignedByte() << 8);
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public long getLong() {
		long value = getInt() & 0xffffffffL;
		long value_2 = getInt() & 0xffffffffL;
		return (value << 32) + value_2;
	}

	public int getIntA() {
		return ((getByte() & 0xFF) << 8) + (getByte() & 0xFF) + ((getByte() & 0xFF) << 24) + ((getByte() & 0xFF) << 16);
	}

	public int getIntB() {
		return ((getByte() & 0xFF) << 16) + ((getByte() & 0xFF) << 24) + (getByte() & 0xFF) + ((getByte() & 0xFF) << 8);
	}

	public long getLongL() {
		long first = getIntB();
		long second = getIntB();
		if (second < 0) {
			second = second & 0xffffffffL;
		}
		return (first << -41780448) + second;
	}

	public int getUnsignedSmart() {
		int i = 0xff & buffer[offset];
		if (i >= 128) {
			return -32768 + getShort();
		}
		return getUnsignedByte();
	}

	public int getLargeSmart() {
		int peek = buffer[offset];
		if (peek < 0) {
			return getInt() & 0x7fffffff;
		}
		int val = getShort();
		if (val == 32767) {
			return -1;
		}
		return val;
	}

	public int available() {
		return offset < length ? length - offset : 0;
	}

	public boolean hasAvailable() {
		return available() > 0;
	}

	public void skipBytes(int bytesCount) {
		offset += bytesCount;
	}

	public int getSmart2() {
		int offset = 0;
		int value = getUnsignedSmart();
		while (value == 32767) {
			value = getUnsignedSmart();
			offset += 32767;
		}
		offset += value;
		return offset;
	}
}
