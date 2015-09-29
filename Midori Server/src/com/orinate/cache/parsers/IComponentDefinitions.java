package com.orinate.cache.parsers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

import com.alex.io.InputStream;
import com.orinate.cache.Cache;

@SuppressWarnings("unused")
public class IComponentDefinitions {

	private static IComponentDefinitions[][] icomponentsdefs;
	private static IComponentSettings GLOBAL_SETTINGS = new IComponentSettings(0, -1);

	public int anInt537;
	public boolean hidden;
	public Object[] anObjectArray540;
	public String aString542;
	public int renderY;
	public int anInt548;
	public boolean aBool551;
	public int[] anIntArray554;
	// public static Class126 aClass126_555;
	// static Class126 aClass126_556;
	// public Class416 aClass416_557;
	// public static Class126 aClass126_558;
	public static boolean aBool559;
	public String aString560;
	public String aString561;
	public int interfaceSet = -1586287441;
	public boolean aBool564;
	public byte aByte566;
	public int anInt567;
	public byte aByte568;
	public int anInt569;
	public int anInt570;
	public int anInt571;
	// Class396 aClass396_572;
	public int anInt573;
	public int renderX;
	public int parentId;
	public static int anInt576;
	// static Class126 aClass126_577;
	public boolean aBool578;
	public int anInt579;
	public IComponentDefinitions aClass46_580;
	public int anInt581;
	public int[] anIntArray582;
	public int anInt583;
	public int anInt584;
	public Object[] anObjectArray585;
	public int anInt586;
	public int anInt587;
	public int anInt588;
	public int anInt589;
	public int anInt590;
	public boolean aBool591;
	public int anInt592;
	public int anInt593;
	public boolean aBool594;
	public int anInt595;
	public int anInt596;
	public Object[] anObjectArray597;
	public int anInt598;
	public int anInt599;
	public boolean aBool600;
	public Object[] anObjectArray602;
	public boolean aBool603;
	public int anInt605;
	public int anInt606;
	public boolean aBool607;
	public int anInt608;
	public int anInt609;
	public int anInt610;
	public boolean aBool611;
	public int anInt612;
	public int anInt613;
	public int anInt614;
	public int anInt615;
	public int anInt616;
	public int anInt617;
	public int anInt618;
	public int anInt619;
	public int anInt620;
	public int anInt621;
	public int[] anIntArray622;
	short[] aShortArray624;
	short[] aShortArray625;
	public int anInt626;
	public int interfaceIndex = 168085009;
	public int anInt628;
	public Object[] anObjectArray629;
	@SuppressWarnings("rawtypes")
	public Hashtable<Number, Comparable> aHashTable4823;
	public int anInt630;
	public boolean aBool631;
	public String aString632;
	public int anInt633;
	public int anInt634;
	public Object[] anObjectArray635;
	public boolean aBool636;
	public boolean aBool637;
	public boolean aBool638;
	public IComponentSettings aClass592_Sub33_639;
	public boolean aBool640;
	public byte[][] aByteArrayArray641;
	public int anInt642;
	public int[] anIntArray643;
	public byte aByte644;
	public int anInt645;
	public static int anInt646 = 0;
	public String aString649;
	public String[] aStringArray650;
	public int anInt651;
	public int anInt652;
	public int anInt653;
	public int anInt654;
	public int anInt656;
	public int anInt657;
	public String aString658;
	public boolean hasScripts;
	public Object[] anObjectArray660;
	public int type;
	public Object[] anObjectArray662;
	public Object[] anObjectArray663;
	public int anInt664;
	public int anInt665;
	public Object[] anObjectArray666;
	public Object[] anObjectArray667;
	public Object[] anObjectArray668;
	public int[] anIntArray669;
	public Object[] anObjectArray670;
	public Object[] anObjectArray671;
	public Object[] anObjectArray672;
	public int[] anIntArray673;
	public Object[] anObjectArray674;
	short[] aShortArray676;
	public int anInt677;
	public Object[] anObjectArray678;
	public int[] anIntArray679;
	public Object[] anObjectArray680;
	public static int anInt681;
	public Object[] anObjectArray682;
	public int[] anIntArray683;
	public Object[] anObjectArray684;
	public Object[] anObjectArray685;
	public Object[] anObjectArray686;
	public Object[] anObjectArray687;
	public Object[] anObjectArray688;
	public Object[] anObjectArray689;
	public int[] anIntArray690;
	public Object[] anObjectArray691;
	public int anInt692;
	public Object[] anObjectArray693;
	public Object[] anObjectArray694;
	public Object[] anObjectArray695;
	short[] aShortArray696;
	public int[] anIntArray697;
	public Object[] anObjectArray698;
	public int anInt699;
	public byte[][] aByteArrayArray700;
	public int extraData;
	public int anInt702;
	public static final int anInt703 = 5;
	public boolean aBool704;
	public int anInt705;
	public int anInt706 = 0;
	public int anInt707;
	public int anInt708;
	public int anInt709;
	public int anInt710;
	public Object[] anObjectArray711;
	public int anInt712;
	// public Class434 aClass434_713;
	// public IcomponentDefinitions[] aClass46Array714;
	public int anInt715;
	public Object[] anObjectArray716;
	public boolean aBool717;
	public int anInt718;
	public boolean aBool719;
	public int anInt720;
	public Object[] anObjectArray721;
	public int anInt722;
	public int anInt723;
	public int anInt724;
	public int anInt725;
	public Object[] anObjectArray726;
	public byte aByte727;
	// public Class509 aClass509_728;
	public boolean aBool729;

	public Object[] decodedCS2Scripts;

	static {
		// icomponentsdefs = new
		// IComponentDefinitions[getInterfaceDefinitionsSize()][];
	}

	public static void main(String[] args) throws IOException {
		Cache.init();
		icomponentsdefs = new IComponentDefinitions[getInterfaceDefinitionsSize()][];
		BufferedWriter writer = new BufferedWriter(new FileWriter("protocol-information/Dumps/interfaces.txt"));
		for (int i = 0; i < IComponentDefinitions.getInterfaceDefinitionsSize(); i++) {
			IComponentDefinitions[] defs = IComponentDefinitions.getInterface(i);
			writer.write("INTERFACE: " + i + "");
			for (IComponentDefinitions def : defs) {
				if (def != null) {
					writer.write(def.aString542 == null ? "" : def.aString542);
					writer.newLine();
					writer.write(def.aString560 == null ? "" : def.aString560);
					writer.newLine();
					writer.write(def.aString561 == null ? "" : def.aString561);
					writer.newLine();
					writer.write(def.aString632 == null ? "" : def.aString632);
					writer.newLine();
					writer.write(def.aString649 == null ? "" : def.aString649);
					writer.newLine();
					writer.write(def.aString658 == null ? "" : def.aString658);
					writer.newLine();
				}
				writer.newLine();
			}
		}
		writer.close();
		// Cache.init();
		// icomponentsdefs = new
		// IComponentDefinitions[getInterfaceDefinitionsSize()][];
		// IComponentDefinitions[] defs = getInterface(451);
		// loop2: for (int i2 = 0; i2 < defs.length; i2++) {
		// int parent = defs[i2].parentId;
		// if (parent != -1)
		// parent &= 0xffff;
		// if (!defs[i2].hasScripts)
		// continue loop2;
		// System.out.println("player.getWriter().sendCS2Config(" +
		// defs[i2].decodedCS2Scripts[0] + ", " + Arrays.toString(defs[i2 +
		// 1].decodedCS2Scripts) + ");");
		// }
	}

	public static IComponentDefinitions getInterfaceComponent(int id, int component) {
		IComponentDefinitions[] inter = getInterface(id);
		if (inter == null || component >= inter.length)
			return null;
		return inter[component];
	}

	public static IComponentDefinitions[] getInterface(int id) {
		if (id >= icomponentsdefs.length)
			return null;
		if (icomponentsdefs[id] == null) {
			icomponentsdefs[id] = new IComponentDefinitions[getInterfaceDefinitionsComponentsSize(id)];
			for (int i = 0; i < icomponentsdefs[id].length; i++) {
				byte[] data = Cache.STORE.getIndexes()[3].getFile(id, i);
				if (data != null) {
					IComponentDefinitions defs = icomponentsdefs[id][i] = new IComponentDefinitions();
					defs.interfaceSet = (id + (i << 16));
					defs.decode(new InputStream(data));
				}
			}
		}
		return icomponentsdefs[id];
	}

	public final void decode(InputStream stream) {
		int newInt = stream.readUnsignedByte();
		if (255 == newInt)
			newInt = -1;
		type = stream.readUnsignedByte();
		if (0 != (type & 0x80)) {
			type = (type & 0x7f);
			aString560 = stream.readString();
		}
		anInt706 = stream.readUnsignedShort();
		anInt570 = stream.readShort();
		anInt571 = stream.readShort();
		anInt619 = stream.readUnsignedShort();
		anInt573 = stream.readUnsignedShort();
		aByte568 = (byte) stream.readByte();
		aByte644 = (byte) stream.readByte();
		aByte566 = (byte) stream.readByte();
		aByte727 = (byte) stream.readByte();
		parentId = stream.readUnsignedShort();
		if (65535 == parentId)
			parentId = -1;
		else
			parentId = ((interfaceSet & ~0xffff) + parentId);
		int i_2_ = stream.readUnsignedByte();
		hidden = 0 != (i_2_ & 0x1);
		if (newInt >= 0)
			aBool607 = (i_2_ & 0x2) != 0;
		if (0 == type) {
			anInt588 = stream.readUnsignedShort();
			anInt677 = stream.readUnsignedShort();
			if (newInt < 0)
				aBool607 = stream.readUnsignedByte() == 1;
		}
		if (5 == type) {
			anInt595 = stream.readInt();
			anInt596 = stream.readUnsignedShort();
			int i_3_ = stream.readUnsignedByte();
			aBool729 = 0 != (i_3_ & 0x1);
			aBool578 = (i_3_ & 0x2) != 0;
			anInt567 = stream.readUnsignedByte();
			anInt598 = stream.readUnsignedByte();
			anInt599 = stream.readInt();
			aBool600 = stream.readUnsignedByte() == 1;
			aBool611 = stream.readUnsignedByte() == 1;
			anInt590 = stream.readInt();
			if (newInt >= 3)
				aBool603 = stream.readUnsignedByte() == 1;
		}
		if (6 == type) {
			anInt616 = 1;
			anInt605 = stream.readBigSmart();
			int i_4_ = stream.readUnsignedByte();
			boolean bool = (i_4_ & 0x1) == 1;
			aBool551 = 2 == (i_4_ & 0x2);
			aBool564 = 4 == (i_4_ & 0x4);
			aBool637 = (i_4_ & 0x8) == 8;
			if (bool) {
				anInt613 = stream.readShort();
				anInt614 = stream.readShort();
				anInt610 = stream.readUnsignedShort();
				anInt589 = stream.readUnsignedShort();
				anInt708 = stream.readUnsignedShort();
				anInt548 = stream.readUnsignedShort();
			} else if (aBool551) {
				anInt613 = stream.readShort();
				anInt614 = stream.readShort();
				anInt615 = stream.readShort();
				anInt610 = stream.readUnsignedShort();
				anInt589 = stream.readUnsignedShort();
				anInt708 = stream.readUnsignedShort();
				anInt548 = stream.readShort();
			}
			anInt712 = stream.readBigSmart();
			if (aByte568 != 0)
				anInt617 = stream.readUnsignedShort();
			if (aByte644 != 0)
				anInt715 = stream.readUnsignedShort();
		}
		if (4 == type) {
			anInt630 = stream.readBigSmart();
			if (newInt >= 2)
				aBool631 = stream.readUnsignedByte() == 1;
			aString632 = stream.readString();
			anInt633 = stream.readUnsignedByte();
			anInt654 = stream.readUnsignedByte();
			anInt665 = stream.readUnsignedByte();
			aBool636 = stream.readUnsignedByte() == 1;
			anInt590 = stream.readInt();
			anInt567 = stream.readUnsignedByte();
			if (newInt >= 0)
				anInt652 = stream.readUnsignedByte();
		}
		if (3 == type) {
			anInt590 = stream.readInt();
			aBool591 = stream.readUnsignedByte() == 1;
			anInt567 = stream.readUnsignedByte();
		}
		if (type == 9) {
			anInt593 = stream.readUnsignedByte();
			anInt590 = stream.readInt();
			aBool594 = stream.readUnsignedByte() == 1;
		}
		int i_5_ = stream.read24BitInt();
		int i_6_ = stream.readUnsignedByte();
		if (0 != i_6_) {
			aByteArrayArray641 = new byte[11][];
			aByteArrayArray700 = new byte[11][];
			anIntArray643 = new int[11];
			anIntArray622 = new int[11];
			for (/**/; 0 != i_6_; i_6_ = stream.readUnsignedByte()) {
				int i_7_ = (i_6_ >> 4) - 1;
				i_6_ = i_6_ << 8 | stream.readUnsignedByte();
				i_6_ &= 0xfff;
				if (4095 == i_6_)
					i_6_ = -1;
				byte i_8_ = (byte) stream.readByte();
				if (i_8_ != 0)
					aBool640 = true;
				byte i_9_ = (byte) stream.readByte();
				anIntArray643[i_7_] = i_6_;
				aByteArrayArray641[i_7_] = new byte[] { i_8_ };
				aByteArrayArray700[i_7_] = new byte[] { i_9_ };
			}
		}
		aString649 = stream.readString();
		int i_10_ = stream.readUnsignedByte();
		int i_11_ = i_10_ & 0xf;
		int i_12_ = i_10_ >> 4;
		if (i_11_ > 0) {
			aStringArray650 = new String[i_11_];
			for (int i_13_ = 0; i_13_ < i_11_; i_13_++)
				aStringArray650[i_13_] = stream.readString();
		}
		if (i_12_ > 0) {
			int i_14_ = stream.readUnsignedByte();
			anIntArray697 = new int[i_14_ + 1];
			for (int i_15_ = 0; i_15_ < anIntArray697.length; i_15_++)
				anIntArray697[i_15_] = -1;
			anIntArray697[i_14_] = stream.readUnsignedShort();
		}
		if (i_12_ > 1) {
			int i_16_ = stream.readUnsignedByte();
			anIntArray697[i_16_] = stream.readUnsignedShort();
		}
		aString542 = stream.readString();
		if (aString542.equals(""))
			aString542 = null;
		anInt692 = stream.readUnsignedByte();
		anInt656 = stream.readUnsignedByte();
		anInt657 = stream.readUnsignedByte();
		aString658 = stream.readString();
		int i_17_ = -1;
		if (method11759(i_5_, -2130534533) != 0) {
			i_17_ = stream.readUnsignedShort();
			if (65535 == i_17_)
				i_17_ = -1;
			anInt699 = stream.readUnsignedShort();
			if (anInt699 == 65535)
				anInt699 = -1;
			anInt583 = stream.readUnsignedShort();
			if (65535 == anInt583)
				anInt583 = -1;
		}
		if (newInt >= 0) {
			anInt584 = stream.readUnsignedShort();
			if (65535 == anInt584)
				anInt584 = -1;
		}
		aClass592_Sub33_639 = new IComponentSettings(i_5_, i_17_);
		if (newInt >= 0) {
			int i_18_ = stream.readUnsignedByte();
			for (int i_19_ = 0; i_19_ < i_18_; i_19_++) {
				int i_20_ = stream.read24BitInt();
				int i_21_ = stream.readInt();
				aHashTable4823.put(i_20_, (long) i_21_);
			}
			int i_22_ = stream.readUnsignedByte();
			for (int i_23_ = 0; i_23_ < i_22_; i_23_++) {
				int i_24_ = stream.read24BitInt();
				String string = stream.readJagString();
				aHashTable4823.put((long) i_24_, string);
			}
		}
		anObjectArray660 = decodeCS2Script(stream);
		anObjectArray726 = decodeCS2Script(stream);
		anObjectArray667 = decodeCS2Script(stream);
		anObjectArray671 = decodeCS2Script(stream);
		anObjectArray670 = decodeCS2Script(stream);
		anObjectArray672 = decodeCS2Script(stream);
		anObjectArray674 = decodeCS2Script(stream);
		anObjectArray635 = decodeCS2Script(stream);
		anObjectArray684 = decodeCS2Script(stream);
		anObjectArray685 = decodeCS2Script(stream);
		if (newInt >= 0)
			anObjectArray686 = decodeCS2Script(stream);
		anObjectArray666 = decodeCS2Script(stream);
		anObjectArray721 = decodeCS2Script(stream);
		anObjectArray662 = decodeCS2Script(stream);
		anObjectArray663 = decodeCS2Script(stream);
		anObjectArray716 = decodeCS2Script(stream);
		anObjectArray668 = decodeCS2Script(stream);
		anObjectArray540 = decodeCS2Script(stream);
		anObjectArray687 = decodeCS2Script(stream);
		anObjectArray678 = decodeCS2Script(stream);
		anObjectArray680 = decodeCS2Script(stream);
		anIntArray673 = method1247(stream, -387563529);
		anIntArray582 = method1247(stream, -705367249);
		anIntArray669 = method1247(stream, -1306190153);
		anIntArray679 = method1247(stream, -680259643);
		anIntArray690 = method1247(stream, -824435393);
	}

	int[] method1247(InputStream class592_sub19, int i) {
		int i_26_ = class592_sub19.readUnsignedByte();
		if (i_26_ == 0)
			return null;
		int[] is = new int[i_26_];
		for (int i_27_ = 0; i_27_ < i_26_; i_27_++)
			is[i_27_] = class592_sub19.readInt();
		return is;
	}

	public final Object[] decodeScript(InputStream buffer, byte i) {
		int i_83_ = buffer.readUnsignedByte();
		if (i_83_ == 0)
			return null;
		Object[] objects = new Object[i_83_];
		for (int i_84_ = 0; i_84_ < i_83_; i_84_++) {
			int i_85_ = buffer.readUnsignedByte();
			if (i_85_ == 0)
				objects[i_84_] = new Integer(buffer.readInt());
			else if (1 == i_85_)
				objects[i_84_] = buffer.readString();
		}
		hasScripts = true;
		return objects;
	}

	private final int[] method4150(InputStream buffer) {
		int i = buffer.readUnsignedByte();
		if (i == 0) {
			return null;
		}
		int[] is = new int[i];
		for (int i_60_ = 0; i_60_ < i; i_60_++)
			is[i_60_] = buffer.readInt();
		return is;
	}

	static final int method925(int i) {
		return (i & 0x3fda8) >> 11;
	}

	@SuppressWarnings("rawtypes")
	public IComponentDefinitions() {
		aByte566 = (byte) 0;
		aByte727 = (byte) 0;
		aByte568 = (byte) 0;
		aByte644 = (byte) 0;
		anInt570 = 0;
		anInt571 = 0;
		anInt619 = 0;
		anInt573 = 0;
		renderX = 0;
		renderY = 0;
		anInt645 = 0;
		anInt620 = 0;
		anInt651 = 1;
		anInt579 = 1;
		parentId = -1;
		hidden = false;
		anInt699 = -1;
		anInt583 = -1;
		anInt584 = 1;
		aBool607 = false;
		anInt586 = 0;
		anInt618 = 0;
		anInt588 = 0;
		anInt677 = 0;
		anInt590 = 0;
		aBool591 = false;
		anInt567 = 0;
		anInt593 = 1;
		aBool594 = false;
		anInt595 = 1;
		anInt596 = 0;
		aBool729 = false;
		anInt598 = 0;
		anInt599 = 0;
		aBool578 = false;
		aBool603 = true;
		anInt616 = 1;
		anInt606 = 1;
		anInt608 = 0;
		anInt609 = 0;
		anInt610 = 0;
		anInt589 = 0;
		anInt708 = 0;
		anInt613 = 0;
		anInt614 = 0;
		anInt615 = 0;
		anInt548 = -1;
		anInt617 = 0;
		anInt715 = 0;
		aBool564 = false;
		aBool637 = false;
		anInt621 = -1;
		anInt630 = -1;
		aBool631 = true;
		aString632 = "";
		anInt633 = 0;
		anInt654 = 0;
		anInt665 = 0;
		aBool636 = false;
		anInt652 = 0;
		aBool638 = false;
		aClass592_Sub33_639 = IComponentSettings.GLOBAL_SETTINGS;
		aBool640 = false;
		aString649 = "";
		anInt653 = -1;
		aClass46_580 = null;
		anInt692 = 0;
		anInt656 = 0;
		anInt657 = -1 * anInt646;
		aString658 = "";
		hasScripts = false;
		extraData = -1;
		anInt702 = 0;
		aString561 = null;
		aBool704 = false;
		anInt705 = -1;
		anInt592 = -1;
		anInt712 = -1;
		aBool719 = false;
		aBool717 = false;
		anInt718 = 1;
		anInt587 = 0;
		anInt720 = 0;
		anInt642 = 0;
		anInt722 = 0;
		anInt723 = 0;
		anInt724 = 0;
		anInt725 = -1;
		anInt581 = -1;
		aHashTable4823 = new Hashtable<Number, Comparable>();
	}

	public static final int getInterfaceDefinitionsSize() {
		return Cache.STORE.getIndexes()[3].getLastArchiveId() + 1;
	}

	static final int method11759(int i, int i_20_) {
		return i >> 11 & 0x7f;
	}

	public Object[] decodeCS2Script(InputStream buffer) {
		int i_83_ = buffer.readUnsignedByte();
		if (i_83_ == 0)
			return null;
		Object[] objects = new Object[i_83_];
		for (int i_84_ = 0; i_84_ < i_83_; i_84_++) {
			int i_85_ = buffer.readUnsignedByte();
			if (i_85_ == 0)
				objects[i_84_] = new Integer(buffer.readInt());
			else if (1 == i_85_)
				objects[i_84_] = buffer.readString();
		}
		decodedCS2Scripts = objects;
		hasScripts = true;
		return objects;
	}

	public static final int getInterfaceDefinitionsComponentsSize(int interfaceId) {
		return Cache.STORE.getIndexes()[3].getLastFileId(interfaceId) + 1;
	}
}
