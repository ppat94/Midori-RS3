package com.orinate.network.codec.buffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.orinate.Constants;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketHeader;

/**
 * @author Tom
 * 
 */
public class InBufferDecoder extends FrameDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		if (buffer.readable()) {
			int opcode = buffer.readByte() & 0xFF;
			if (opcode < 0) {
				buffer.discardReadBytes();
				return null;
			}
			int length = Constants.PACKET_LENGTHS[opcode];
			if (length < 0) {
				switch (length) {
				case -1:
					if (buffer.readable()) {
						length = buffer.readByte() & 0xff;
					}
					break;
				case -2:
					if (buffer.readableBytes() >= 2) {
						length = buffer.readShort() & 0xffff;
					}
					break;
				default:
					length = buffer.readableBytes();
					break;
				}
			}
			if (buffer.readableBytes() >= length) {
				if (length < 0) {
					return null;
				}
				byte[] payload = new byte[length];
				buffer.readBytes(payload, 0, length);
				return new PacketHeader(new InBuffer(payload), opcode);
			}
		}
		return null;
	}
}
