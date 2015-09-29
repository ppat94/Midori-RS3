package com.orinate.util.text;

/**
 * @author Tom
 * 
 */
public class StringUtil {

	public static char[] aCharArray6385 = { '\u20ac', '\0', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017d', '\0', '\0', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\0', '\u017e', '\u0178' };

	public static String formatProtocol(String string) {
		return string.replaceAll(" ", "_").toLowerCase();
	}

	public static String formatText(String string) {
		boolean afterPeriod = false;
		string = string.replaceAll("_", " ");
		StringBuilder b = new StringBuilder(string.length());
		for (int i = 0; i < string.length(); i++) {
			if (i == 0 || afterPeriod) {
				b.append(Character.toUpperCase(string.charAt(i)));
				afterPeriod = false;
			} else
				b.append(string.charAt(i));
			if (string.charAt(i) == '.')
				afterPeriod = true;
		}
		return b.toString();
	}

	public static String formatName(String string) {
		boolean afterSpace = false;
		string = string.replaceAll("_", " ");
		StringBuilder b = new StringBuilder(string.length());
		for (int i = 0; i < string.length(); i++) {
			if (i == 0 || afterSpace) {
				b.append(Character.toUpperCase(string.charAt(i)));
				afterSpace = false;
			} else
				b.append(string.charAt(i));
			if (string.charAt(i) == ' ')
				afterSpace = true;
		}
		return b.toString();
	}
	
	public static final long stringToLong(String s) {
		long l = 0L;
		for (int i = 0; i < s.length() && i < 12; i++) {
			char c = s.charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L) {
			l /= 37L;
		}
		return l;
	}

	public static final String getUnformatedMessage(int messageDataLength, int messageDataOffset, byte[] messageData) {
		char[] cs = new char[messageDataLength];
		int i = 0;
		for (int i_6_ = 0; i_6_ < messageDataLength; i_6_++) {
			int i_7_ = 0xff & messageData[i_6_ + messageDataOffset];
			if ((i_7_ ^ 0xffffffff) != -1) {
				if ((i_7_ ^ 0xffffffff) <= -129 && (i_7_ ^ 0xffffffff) > -161) {
					int i_8_ = aCharArray6385[i_7_ - 128];
					if (i_8_ == 0)
						i_8_ = 63;
					i_7_ = i_8_;
				}
				cs[i++] = (char) i_7_;
			}
		}
		return new String(cs, 0, i);
	}
}
