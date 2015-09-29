package com.orinate;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.orinate.cache.Cache;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.dialogue.DialogueLoader;
import com.orinate.game.content.friends.FriendsChatManager;
import com.orinate.game.core.GameCore;
import com.orinate.game.model.npc.combat.CombatDefinitionsLoader;
import com.orinate.game.model.npc.drops.NPCDrops;
import com.orinate.network.OrinatePipelineFactory;
import com.orinate.network.packet.PacketRepository;
import com.orinate.util.entity.NPCSpawns;
import com.orinate.util.misc.LobbyConfigs;
import com.orinate.util.text.Huffman;

/**
 * @author Tyler Telis
 * @author Tom Le Godais
 * 
 */
public class Server {

	private static final Logger logger = Logger.getLogger(Server.class.getName());
	private static Server server;

	public Server() {
		logger.info("Starting " + Constants.SERVER_NAME + " v" + Constants.SERVER_VERSION + "..");
		logger.info("Server is running in verbose mode: " + Boolean.toString(Constants.VERBOSE_MODE).toUpperCase() + ".");
	}

	public static void main(String... args) {
		try {
			server = new Server();
			server.start();

			InetSocketAddress address = new InetSocketAddress(Constants.PORT);
			server.bind(address);

			logger.info("Finished.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start() throws Exception {
		if(System.getProperty("user.home").toLowerCase().contains("cody"))
			Constants.HOST = "127.0.0.1";
		GameCore core = new GameCore();
		core.start();
		Cache.init();
		Huffman.init();
		CombatDefinitionsLoader.init();
		NPCDrops.init();
		LobbyConfigs.parse();
		PacketRepository.load();
		AbilityRepository.init();
		DialogueLoader.init();
		FriendsChatManager.init();
		NPCSpawns.init();
	}

	private void bind(InetSocketAddress address) {
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.setFactory(new NioServerSocketChannelFactory());
		bootstrap.setPipelineFactory(new OrinatePipelineFactory());
		bootstrap.setOption("child.reuseAddress", true);

		bootstrap.bind(address);
		logger.info("Bound [addr=" + address + "]");
	}

	public static Server getServer() {
		return server;
	}
}