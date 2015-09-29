package com.orinate.game.content.misc;

import com.orinate.game.model.player.Player;

/**
 * 
 * @author Tyler
 * 
 */
public class SkillingInterface {

	public static void sendInterface(Player player) {
		player.getWriter().sendConfig(1105, 4);
		player.getWriter().sendConfig(1168, 6939);
		player.getWriter().sendConfig(1169, 6947);
		player.getWriter().sendConfig(1170, 52);// Item id
		player.getWriter().sendConfig(312, 3145728);
		player.getWriter().sendConfig(312, 204472320);
		player.getWriter().sendConfig(1174, 0);
		player.getWriter().sendConfig(1174, 0);
		player.getWriter().sendConfig(1174, 0);
		player.getWriter().sendGlobalConfig(1703, 590);
		player.getWriter().sendGlobalConfig(1704, 946);
		player.getWriter().sendGlobalConfig(1705, -1);
		player.getWriter().sendGlobalConfig(1706, 24291);
		player.getWriter().sendGlobalConfig(1707, -1);
		player.getWriter().sendGlobalConfig(1708, -1);
		player.getWriter().sendGlobalConfig(1709, -1);
		player.getWriter().sendGlobalConfig(1710, -1);
		player.getWriter().sendGlobalConfig(1711, -1);
		player.getWriter().sendGlobalConfig(1712, -1);
		player.getWriter().sendGlobalConfig(1713, -1);
		player.getWriter().sendGlobalConfig(1714, -1);
		player.getWriter().sendGlobalConfig(1715, -1);
		player.getWriter().sendGlobalConfig(1716, -1);
		player.getWriter().sendGlobalConfig(1717, -1);
		player.getWriter().sendGlobalConfig(199, -1);
		player.getWriter().sendGlobalConfig(3678, -1);
		player.getWriter().sendCS2Script(8178);
		player.getWriter().sendInterface(false, 1477, 238, 1179);
		player.getWriter().sendGlobalConfig(2222, 6940);
		player.getWriter().sendGlobalConfig(2225, 0);
		player.getWriter().sendGlobalConfig(2689, 0);
		player.getWriter().sendGlobalConfig(2690, 0);
		player.getWriter().sendGlobalConfig(2223, 1);
		player.getWriter().sendGlobalConfig(2224, 33);
		player.getWriter().sendGlobalConfig(199, -1);
		player.getWriter().sendGlobalConfig(3678, -1);
		player.getWriter().sendCS2Script(8178);
		player.getWriter().sendInterface(false, 1477, 238, 1370);
		player.getWriter().sendInterface(true, 1370, 62, 1371);
		player.getWriter().sendIComponentSettings(1371, 44, 0, 16, 2);
		player.getWriter().sendIComponentSettings(1371, 62, 0, 12, 2);
		player.getWriter().sendIComponentSettings(1371, 36, 0, 2, 2359296);
		player.getWriter().sendIComponentSettings(1371, 143, 0, 2, 2);
	}
}
