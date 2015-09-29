package com.orinate.network.codec.handshake;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.orinate.Constants;
import com.orinate.io.InBuffer;
import com.orinate.io.OutBuffer;
import com.orinate.network.codec.login.LoginDecoder;
import com.orinate.network.codec.ondemand.OndemandDecoder;
import com.orinate.network.util.InBasedDecoder;

/**
 * @author Tom
 * 
 */
public class HandshakeDecoder extends InBasedDecoder implements ChannelHandler {

	@Override
	public Object decode(ChannelHandlerContext context, Channel channel, InBuffer buffer) {
		if (context.getPipeline().get(HandshakeDecoder.class) != null) {
			context.getPipeline().remove(this);
		}
		int opcode = buffer.getByte() & 0xFF;
		switch (opcode) {
		case 15:
			int size = buffer.getByte() & 0xFF;
			if (buffer.available() < size) {
				channel.close();
				return null;
			}

			int clientVersion = buffer.getInt();
			int subVersion = buffer.getInt();
			if (clientVersion != Constants.CLIENT_VERSION || subVersion != Constants.SUB_VERSION) {
				channel.close();
				return null;
			}
			String token = buffer.getString();
			if (!token.equals(Constants.SERVER_TOKEN)) {
				channel.close();
				return null;
			}

			OutBuffer out = new OutBuffer();

			out.putByte(0);
			for (int element : Constants.ELEMENT_KEYS) {
				out.putInt(element);
			}

			channel.getPipeline().addBefore("upHandler", "decoder", new OndemandDecoder());
			return new HandshakeMessage(out);
		case 14:
			out = new OutBuffer();
			out.putByte(0);
			channel.getPipeline().addBefore("upHandler", "decoder", new LoginDecoder()); // new
																							// OldLoginDecoder();
			return new HandshakeMessage(out);
		}
		return null;
	}
}
