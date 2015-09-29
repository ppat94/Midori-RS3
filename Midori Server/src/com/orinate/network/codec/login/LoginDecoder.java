package com.orinate.network.codec.login;

import java.math.BigInteger;
import java.net.ProtocolException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.orinate.Constants;
import com.orinate.game.World;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.player.PlayerDefinition;
import com.orinate.network.UpstreamChannelHandler;
import com.orinate.network.codec.buffer.InBufferDecoder;
import com.orinate.util.base37.Base37Utils;
import com.orinate.util.crypto.XTEA;
import com.orinate.util.entity.FileManager;
import com.orinate.util.net.BufferUtils;
import com.orinate.util.text.StringUtil;

/**
 * @author Tyler
 * @author Tom
 */
public class LoginDecoder extends FrameDecoder implements ChannelHandler {

	public enum Stage {
		CONNECTION_TYPE, CLIENT_DETAILS, LOBBY_PAYLOAD, GAME_PAYLOAD;
	}

	public enum LoginType {
		LOBBY, GAME;
	}

	private Stage loginStage = Stage.CONNECTION_TYPE;
	private int loginSize;
	private LoginType currentLoginType;

	@Override
	public Object decode(ChannelHandlerContext context, Channel channel, ChannelBuffer buffer) {
		try {
			switch (loginStage) {
			case CONNECTION_TYPE:
				return decodeConnectionType(buffer);
			case CLIENT_DETAILS:
				return decodeClientDetails(buffer);
			case LOBBY_PAYLOAD:
				return decodeLobbyPayload(context, channel, buffer);
			case GAME_PAYLOAD:
				return decodeGamePayload(context, channel, buffer);
			}
		} catch (ProtocolException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private Object decodeGamePayload(ChannelHandlerContext context, Channel channel, ChannelBuffer buffer) throws ProtocolException {
		int secureBufferSize = buffer.readShort() & 0xFFFF;
		if (buffer.readableBytes() < secureBufferSize) {
			throw new ProtocolException("Invalid secure buffer size.");
		}

		byte[] secureBytes = new byte[secureBufferSize];
		buffer.readBytes(secureBytes);

		ChannelBuffer secureBuffer = ChannelBuffers.wrappedBuffer(new BigInteger(secureBytes).modPow(Constants.PUBLIC_EXPONENT, Constants.PUBLIC_MODULUS).toByteArray());
		int blockOpcode = secureBuffer.readUnsignedByte();

		if (blockOpcode != 10) {
			throw new ProtocolException("Invalid block opcode.");
		}

		int[] xteaKey = new int[4];
		for (int key = 0; key < xteaKey.length; key++) {
			xteaKey[key] = secureBuffer.readInt();
		}

		long vHash = secureBuffer.readLong();
		if (vHash != 0L) {
			throw new ProtocolException("Invalid login virtual hash.");
		}

		String password = BufferUtils.readString(secureBuffer);

		long[] loginSeeds = new long[2];
		for (int seed = 0; seed < loginSeeds.length; seed++) {
			loginSeeds[seed] = secureBuffer.readLong();
		}

		byte[] xteaBlock = new byte[buffer.readableBytes()];
		buffer.readBytes(xteaBlock);

		XTEA xtea = new XTEA(xteaKey);
		xtea.decrypt(xteaBlock, 0, xteaBlock.length);

		ChannelBuffer xteaBuffer = ChannelBuffers.wrappedBuffer(xteaBlock);

		boolean decodeAsString = xteaBuffer.readByte() == 1;
		String username = StringUtil.formatProtocol(decodeAsString ? BufferUtils.readString(xteaBuffer) : Base37Utils.decodeBase37(xteaBuffer.readLong()));

		@SuppressWarnings("unused")
		int gameType = xteaBuffer.readUnsignedByte();
		@SuppressWarnings("unused")
		int screenWidth = xteaBuffer.readUnsignedShort();
		@SuppressWarnings("unused")
		int screenHeight = xteaBuffer.readUnsignedShort();
		xteaBuffer.readUnsignedByte();

		int[] randomData = new int[24];
		for (int data = 0; data < randomData.length; data++) {
			randomData[data] = xteaBuffer.readUnsignedByte();
		}

		@SuppressWarnings("unused")
		String clientSettings = BufferUtils.readString(xteaBuffer);
		@SuppressWarnings("unused")
		int affiliateID = xteaBuffer.readInt();

		int length = xteaBuffer.readUnsignedByte();

		byte[] machineData = new byte[length];
		for (int data = 0; data < machineData.length; data++) {
			machineData[data] = (byte) xteaBuffer.readUnsignedByte();
		}

		xteaBuffer.readInt();
		xteaBuffer.readInt();
		xteaBuffer.readInt();

		BufferUtils.readString(xteaBuffer);
		xteaBuffer.readUnsignedByte();
		BufferUtils.readString(xteaBuffer);

		xteaBuffer.readUnsignedByte();
		xteaBuffer.readUnsignedByte();
		xteaBuffer.readUnsignedByte();
		xteaBuffer.readUnsignedByte();

		xteaBuffer.readInt();
		String worldToken = BufferUtils.readString(xteaBuffer);
		if (!worldToken.equals(Constants.SERVER_TOKEN)) {
			throw new ProtocolException("Invalid server token.");
		}
		xteaBuffer.readUnsignedByte();

		Player player = new Player(new PlayerDefinition(username, password));

		int returnCode = 2;
		if (FileManager.contains(username)) {
			player = (Player) FileManager.load(username);
			if (player == null) {
				returnCode = 24;
			} else if (!password.equals(player.getDefinition().getPassword()) && !Constants.isOwnerIP(getIP(context.getChannel()))) {
				returnCode = 3;
			}
		} else {
			player = new Player(new PlayerDefinition(username, password));
		}

		player.init(channel, currentLoginType);
		World.getWorld().register(player, returnCode, currentLoginType);

		UpstreamChannelHandler handler = (UpstreamChannelHandler) channel.getPipeline().get("upHandler");
		handler.setPlayer(player);

		context.getChannel().setAttachment(player);

		channel.getPipeline().replace("decoder", "decoder", new InBufferDecoder());
		return null;
	}

	private Object decodeLobbyPayload(ChannelHandlerContext context, Channel channel, ChannelBuffer buffer) throws ProtocolException {
		int secureBufferSize = buffer.readShort() & 0xFFFF;
		if (buffer.readableBytes() < secureBufferSize) {
			throw new ProtocolException("Invalid secure buffer size.");
		}

		byte[] secureBytes = new byte[secureBufferSize];
		buffer.readBytes(secureBytes);

		ChannelBuffer secureBuffer = ChannelBuffers.wrappedBuffer(new BigInteger(secureBytes).modPow(Constants.PUBLIC_EXPONENT, Constants.PUBLIC_MODULUS).toByteArray());
		int blockOpcode = secureBuffer.readUnsignedByte();

		if (blockOpcode != 10) {
			throw new ProtocolException("Invalid block opcode.");
		}

		int[] xteaKey = new int[4];
		for (int key = 0; key < xteaKey.length; key++) {
			xteaKey[key] = secureBuffer.readInt();
		}

		long vHash = secureBuffer.readLong();
		if (vHash != 0L) {
			throw new ProtocolException("Invalid login virtual hash.");
		}

		String password = BufferUtils.readString(secureBuffer);

		long[] loginSeeds = new long[2];
		for (int seed = 0; seed < loginSeeds.length; seed++) {
			loginSeeds[seed] = secureBuffer.readLong();
		}

		byte[] xteaBlock = new byte[buffer.readableBytes()];
		buffer.readBytes(xteaBlock);

		XTEA xtea = new XTEA(xteaKey);
		xtea.decrypt(xteaBlock, 0, xteaBlock.length);

		ChannelBuffer xteaBuffer = ChannelBuffers.wrappedBuffer(xteaBlock);

		boolean decodeAsString = xteaBuffer.readByte() == 1;
		String username = StringUtil.formatProtocol(decodeAsString ? BufferUtils.readString(xteaBuffer) : Base37Utils.decodeBase37(xteaBuffer.readLong()));

		@SuppressWarnings("unused")
		int gameType = xteaBuffer.readUnsignedByte();
		@SuppressWarnings("unused")
		int languageID = xteaBuffer.readUnsignedByte();
		xteaBuffer.skipBytes(24);
		@SuppressWarnings("unused")
		String clientSettings = BufferUtils.readString(xteaBuffer);
		int length = xteaBuffer.readUnsignedByte();

		byte[] machineData = new byte[length];
		xteaBuffer.readBytes(machineData);

		xteaBuffer.readInt();
		BufferUtils.readString(xteaBuffer);
		xteaBuffer.readInt();
		xteaBuffer.readInt();

		String serverToken = BufferUtils.readString(xteaBuffer);
		if (!serverToken.equals(Constants.SERVER_TOKEN)) {
			throw new ProtocolException("Invalid server token.");
		}

		Player player;

		int returnCode = 2;
		if (FileManager.contains(username)) {
			player = (Player) FileManager.load(username);
			if (player == null) {
				returnCode = 24;
			} else if (!password.equals(player.getDefinition().getPassword()) && !Constants.isOwnerIP(getIP(context.getChannel()))) {
				returnCode = 3;
			}
		} else {
			player = new Player(new PlayerDefinition(username, password));
		}

		player.init(channel, currentLoginType);
		World.getWorld().register(player, returnCode, currentLoginType);

		UpstreamChannelHandler handler = (UpstreamChannelHandler) channel.getPipeline().get("upHandler");
		handler.setPlayer(player);

		context.getChannel().setAttachment(player);

		channel.getPipeline().replace("decoder", "decoder", new InBufferDecoder());
		return null;
	}
	
	public String getIP(Channel channel) {
		return channel == null ? "" : channel.getRemoteAddress().toString().split(":")[0].replace("/", "");

	}

	private Object decodeClientDetails(ChannelBuffer buffer) throws ProtocolException {
		if (buffer.readableBytes() < loginSize) {
			throw new ProtocolException("Invalid login size.");
		}

		int version = buffer.readInt();
		int subVersion = buffer.readInt();

		if (version != Constants.CLIENT_VERSION && subVersion != Constants.SUB_VERSION) {
			throw new ProtocolException("Invalid client version/sub-version.");
		}

		if (currentLoginType.equals(LoginType.GAME)) {
			buffer.readByte();
		}

		loginStage = currentLoginType.equals(LoginType.LOBBY) ? Stage.LOBBY_PAYLOAD : Stage.GAME_PAYLOAD;
		return null;
	}

	private Object decodeConnectionType(ChannelBuffer buffer) throws ProtocolException {
		int loginType = buffer.readUnsignedByte();
		if (loginType != 16 && loginType != 18 && loginType != 19) {
			throw new ProtocolException("Invalid login opcode: " + loginType);
		}

		currentLoginType = loginType == 19 ? LoginType.LOBBY : LoginType.GAME;
		loginSize = buffer.readShort() & 0xFFFF;

		loginStage = Stage.CLIENT_DETAILS;
		return null;
	}
}