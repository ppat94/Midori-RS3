package com.orinate.game.content;

import java.io.Serializable;
import java.util.ArrayList;

import com.orinate.cache.parsers.ClientScriptMap;
import com.orinate.game.World;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.region.Region;
import com.orinate.util.Utilities;

/**
 * @author Taylor
 */
public class MusicHandler implements Serializable {

	private static final long serialVersionUID = 6663193773961510125L;
	private static final int[] SETTINGS = new int[] { 20, 21, 22, 23, 24, 25, 298, 311, 346, 414, 464, 598, 662, 721, 906, 1009, 1104, 1136, 1180, 1202, 1381, 1394, 1434, 1596, 1618, 1619, 1620, 1865, 1864, 2246, 2019, -1, 2430, 2559 };
	private static final int[] PLAY_LIST_SETTINGS = new int[] { 1621, 1622, 1623, 1624, 1625, 1626 };

	private Player player;
	private transient int music;
	private transient long musicDelay;
	private transient boolean settedMusic;
	private ArrayList<Integer> unlockedMusics;
	private ArrayList<Integer> playList;

	private transient boolean playListOn;
	private transient int nextPlayListMusic;
	private transient boolean shuffleOn;

	public MusicHandler() {
		unlockedMusics = new ArrayList<Integer>();
		playList = new ArrayList<Integer>(12);
		// auto unlocked musics
		unlockedMusics.add(62);
		unlockedMusics.add(400);
		unlockedMusics.add(16);
		unlockedMusics.add(466);
		unlockedMusics.add(321);
		unlockedMusics.add(547);
		unlockedMusics.add(621);
		unlockedMusics.add(207);
		unlockedMusics.add(401);
		unlockedMusics.add(147);
		unlockedMusics.add(457);
		unlockedMusics.add(552);
		unlockedMusics.add(858);
	}

	public void passMusics(Player p) {
		for (int musicId : p.getMusicHandler().unlockedMusics) {
			if (!unlockedMusics.contains(musicId))
				unlockedMusics.add(musicId);
		}
	}

	public boolean hasMusic(int id) {
		return unlockedMusics.contains(id);
	}

	public void setPlayer(Player player) {
		this.player = player;
		music = World.getWorld().getRegion(player.getLocation().getRegionId()).getMusicId();
	}

	public void switchShuffleOn() {
		if (shuffleOn) {
			playListOn = false;
			refreshPlayListConfigs();
		}
		shuffleOn = !shuffleOn;
	}

	public void switchPlayListOn() {
		if (playListOn) {
			playListOn = false;
			shuffleOn = false;
			refreshPlayListConfigs();
		} else {
			playListOn = true;
			nextPlayListMusic = 0;
			replayMusic();
		}
	}

	public void clearPlayList() {
		if (playList.isEmpty())
			return;
		playList.clear();
		refreshPlayListConfigs();
	}

	public void addPlayingMusicToPlayList() {
		addToPlayList((int) ClientScriptMap.getMap(1351).getKeyForValue(music));
	}

	public void addToPlayList(int musicIndex) {
		if (playList.size() == 12)
			return;
		int musicId = ClientScriptMap.getMap(1351).getIntValue(musicIndex);
		if (musicId != -1 && unlockedMusics.contains(musicId) && !playList.contains(musicId)) {
			playList.add(musicId);
			if (playListOn)
				switchPlayListOn();
			else
				refreshPlayListConfigs();
		}
	}

	public void removeFromPlayList(int musicIndex) {
		Integer musicId = ClientScriptMap.getMap(1351).getIntValue(musicIndex);
		if (musicId != -1 && unlockedMusics.contains(musicId) && playList.contains(musicId)) {
			playList.remove(musicId);
			if (playListOn)
				switchPlayListOn();
			else
				refreshPlayListConfigs();
		}
	}

	public void refreshPlayListConfigs() {
		int[] configValues = new int[PLAY_LIST_SETTINGS.length];
		for (int i = 0; i < configValues.length; i++)
			configValues[i] = -1;
		for (int i = 0; i < playList.size(); i += 2) {
			Integer musicId1 = playList.get(i);
			Integer musicId2 = (i + 1) >= playList.size() ? null : playList.get(i + 1);
			if (musicId1 == null && musicId2 == null)
				break;
			int musicIndex = (int) ClientScriptMap.getMap(1351).getKeyForValue(musicId1);
			int configValue;
			if (musicId2 != null) {
				int musicIndex2 = (int) ClientScriptMap.getMap(1351).getKeyForValue(musicId2);
				configValue = musicIndex | musicIndex2 << 15;
			} else
				configValue = musicIndex | -1 << 15;
			configValues[i / 2] = configValue;
		}
		for (int i = 0; i < PLAY_LIST_SETTINGS.length; i++)
			if (PLAY_LIST_SETTINGS[i] == -1)
				player.getWriter().sendConfig(PLAY_LIST_SETTINGS[i], configValues[i]);
	}

	public void refreshListConfigs() {
		int[] configValues = new int[SETTINGS.length];
		for (int musicId : unlockedMusics) {
			int musicIndex = (int) ClientScriptMap.getMap(1351).getKeyForValue(musicId);
			if (musicIndex == -1)
				continue;
			int index = getConfigIndex(musicIndex);
			if (index >= SETTINGS.length)
				continue;
			configValues[index] |= 1 << (musicIndex - (index * 32));
		}
		for (int i = 0; i < SETTINGS.length; i++) {
			if (SETTINGS[i] != -1 && configValues[i] != 0)
				player.getWriter().sendConfig(SETTINGS[i], configValues[i]);
		}
	}

	public void addMusic(int musicId) {
		unlockedMusics.add(musicId);
		refreshListConfigs();
	}

	public int getConfigIndex(int musicId) {
		return (musicId + 1) / 32;
	}

	public void init() {
		// unlock music inter all options
		if (music >= 0)
			playMusic(music);
		refreshListConfigs();
		refreshPlayListConfigs();
	}

	public boolean musicEnded() {
		return music != -2 && musicDelay + (180000) < System.currentTimeMillis();
	}

	public void replayMusic() {
		if (playListOn && playList.size() > 0) {
			if (shuffleOn)
				music = playList.get(Utilities.getRandom(playList.size() - 1));
			else {
				if (nextPlayListMusic >= playList.size())
					nextPlayListMusic = 0;
				music = playList.get(nextPlayListMusic++);
			}
		} else if (unlockedMusics.size() > 0) // random music
			music = unlockedMusics.get(Utilities.getRandom(unlockedMusics.size() - 1));
		playMusic(music);
	}

	public void checkMusic(int requestMusicId) {
		if (playListOn || settedMusic && musicDelay + (180000) >= System.currentTimeMillis())
			return;
		settedMusic = false;
		if (music != requestMusicId)
			playMusic(requestMusicId);
	}

	public void forcePlayMusic(int musicId) {
		settedMusic = true;
		playMusic(musicId);
	}

	public void reset() {
		settedMusic = false;
		player.getMusicHandler().checkMusic(World.getWorld().getRegion(player.getLocation().getRegionId()).getMusicId());
	}

	public void playAnotherMusic(int musicIndex) {
		int musicId = ClientScriptMap.getMap(1351).getIntValue(musicIndex);
		if (musicId != -1 && unlockedMusics.contains(musicId)) {
			settedMusic = true;
			if (playListOn)
				switchPlayListOn();
			playMusic(musicId);
		}

	}

	public void playMusic(int musicId) {
		musicDelay = System.currentTimeMillis();
		if (musicId == -2) {
			music = musicId;
			player.getWriter().sendMusic(-1);
			player.getWriter().sendIComponentText(187, 4, "");
			return;
		}
		player.getWriter().sendMusic(musicId);
		music = musicId;
		int musicIndex = (int) ClientScriptMap.getMap(1351).getKeyForValue(musicId);
		if (musicIndex != -1) {
			String musicName = ClientScriptMap.getMap(1345).getStringValue(musicIndex);
			if (musicName.equals(" "))
				musicName = Region.getMusicName1(player.getLocation().getRegionId());
			player.getWriter().sendIComponentText(187, 4, musicName != null ? musicName : "");
			if (!unlockedMusics.contains(musicId)) {
				addMusic(musicId);
				if (musicName != null)
					player.getWriter().sendGameMessage("<col=ff0000>You have unlocked a new music track: " + musicName + ".");
			}
		}
	}

	public void process() {
		if (musicEnded()) {
			replayMusic();
		}
	}
}
