package com.orinate.network.codec.buffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.orinate.io.OutBuffer;

/**
 * @author Tom
 * 
 */
public class OutBufferEncoder extends OneToOneEncoder {

	@Override
	protected Object encode(ChannelHandlerContext context, Channel channel, Object message) {
		if (message instanceof OutBuffer) {
			OutBuffer buffer = (OutBuffer) message;
			return ChannelBuffers.copiedBuffer(buffer.buffer(), 0, buffer.offset());
		} else if (message instanceof ChannelBuffer) {
			return ChannelBuffers.copiedBuffer((ChannelBuffer) message);
		}
		return message;
	}
}
