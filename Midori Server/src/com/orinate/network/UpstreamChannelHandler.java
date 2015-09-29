package com.orinate.network;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.orinate.Constants;
import com.orinate.game.World;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.player.Player.GameState;
import com.orinate.network.codec.handshake.HandshakeMessage;
import com.orinate.network.packet.PacketHeader;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;
import com.orinate.network.packet.impl.DefaultPacketListener;

/**
 * @author Tom
 * 
 */
public class UpstreamChannelHandler extends SimpleChannelUpstreamHandler implements ChannelHandler {

	private Player player;

	@Override
	public void channelDisconnected(ChannelHandlerContext context, ChannelStateEvent event) {
		if (player == null) {
			return;
		}
		if (!player.isOnline()) {
			World.getPlayers().remove(player);
			World.removeLobbyPlayer(player);
			return;
		}
		if (player.getGameState().equals(GameState.IN_LOBBY)) {
			if (World.getWorld().inLobby(player)) {
				World.removeLobbyPlayer(player);
				player.getFriendsChatManager().leaveChat(true);
			}
		} else {
			player.save();
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext context, MessageEvent event) {
		Object message = event.getMessage();
		if (message == null) {
			return;
		}

		if (message instanceof HandshakeMessage) {
			context.getChannel().write(((HandshakeMessage) message).buffer());
		} else {
			if (!(message instanceof PacketHeader)) {
				return;
			}
			PacketHeader header = (PacketHeader) message;
			int opcode = header.getOpcode();
			PacketListener listener = PacketRepository.get(opcode);
			if (listener == null) {
				listener = new DefaultPacketListener();
			}
			listener.handle(player, opcode, header.getBuffer());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) {
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
