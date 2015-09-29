package com.orinate.cache.parsers;

public final class IComponentSettings {
	public int anInt9986;
	public int anInt9991;
	static IComponentSettings GLOBAL_SETTINGS = new IComponentSettings(0, -1);

	public final boolean method16215() {
		return (anInt9991 * 1458128309 >> 22 & 0x1) != 0;
	}

	public final boolean method16216(int i) {
		return 0 != (anInt9991 * 1458128309 & 0x1);
	}

	public final boolean method16217(int i, int i_0_) {
		return (anInt9991 * 1458128309 >> i + 1 & 0x1) != 0;
	}

	public final boolean method16218() {
		return 0 != (anInt9991 * 1458128309 & 0x1);
	}

	public final boolean method16219(byte i) {
		return 0 != (1458128309 * anInt9991 >> 23 & 0x1);
	}

	public final boolean method16220(int i) {
		return (1458128309 * anInt9991 >> 21 & 0x1) != 0;
	}

	public final boolean method16221(byte i) {
		return (anInt9991 * 1458128309 >> 22 & 0x1) != 0;
	}

	public final boolean method16223() {
		return 0 != (anInt9991 * 1458128309 & 0x1);
	}

	public final int method16224(int i) {
		return anInt9991 * 1458128309 >> 18 & 0x7;
	}

	public final boolean method16225() {
		return 0 != (anInt9991 * 1458128309 & 0x1);
	}

	public final boolean method16226(int i) {
		return (anInt9991 * 1458128309 >> i + 1 & 0x1) != 0;
	}

	public IComponentSettings(int i, int i_1_) {
		anInt9991 = i * -1923661667;
		anInt9986 = i_1_ * 1422968475;
	}

	public final int method16229() {
		return anInt9991 * 1458128309 >> 18 & 0x7;
	}

	public final boolean method16230() {
		return (1458128309 * anInt9991 >> 21 & 0x1) != 0;
	}

	public final boolean method16232() {
		return 0 != (1458128309 * anInt9991 >> 23 & 0x1);
	}

	public final boolean method16233() {
		return 0 != (1458128309 * anInt9991 >> 23 & 0x1);
	}

	public final boolean method16234() {
		return 0 != (1458128309 * anInt9991 >> 23 & 0x1);
	}

	public final boolean method16235() {
		return 0 != (1458128309 * anInt9991 >> 23 & 0x1);
	}

}
