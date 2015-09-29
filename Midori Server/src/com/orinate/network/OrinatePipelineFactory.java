package com.orinate.network;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import com.orinate.network.codec.buffer.OutBufferEncoder;
import com.orinate.network.codec.handshake.HandshakeDecoder;

/**
 * @author Tyler Telis
 * @author Tom Le Godais
 */
public class OrinatePipelineFactory implements ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("encoder", new OutBufferEncoder());
		pipeline.addLast("decoder", new HandshakeDecoder());
		pipeline.addLast("upHandler", new UpstreamChannelHandler());
		return pipeline;
	}
}