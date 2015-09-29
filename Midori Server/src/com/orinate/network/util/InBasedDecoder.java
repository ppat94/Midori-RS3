package com.orinate.network.util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.orinate.io.InBuffer;

/**
 * @author Tom
 * 
 */
public abstract class InBasedDecoder extends FrameDecoder {

	public InBasedDecoder() {
		super();
	}

	public InBasedDecoder(boolean unfold) {
		super(unfold);
	}

	@Override
	public Object decode(ChannelHandlerContext context, Channel channel, ChannelBuffer buffer) {
		buffer.markReaderIndex();
		int available = buffer.readableBytes();
		if (available < 1 || available > 7500) {
			return null;
		}
		byte[] data = new byte[buffer.readableBytes()];
		buffer.readBytes(data);
		return decode(context, channel, new InBuffer(data));
	}

	public abstract Object decode(ChannelHandlerContext context, Channel channel, InBuffer buffer);
}
