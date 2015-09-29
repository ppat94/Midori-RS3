package com.orinate.network.codec.ondemand;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.orinate.cache.Cache;
import com.orinate.io.InBuffer;
import com.orinate.io.OutBuffer;
import com.orinate.network.util.InBasedDecoder;

/**
 * @author Tom
 * @author Tyler
 */
public class OndemandDecoder extends InBasedDecoder implements ChannelHandler {

	private int encryptionValue;
	private byte[] ARCHIVE_DATA;

	@Override
	public Object decode(ChannelHandlerContext context, Channel channel, InBuffer buffer) {
		while (buffer.hasAvailable() && channel.isConnected()) {
			int priority = buffer.getUnsignedByte();
			if (priority == 0 || priority == 1) {
				decodeContainer(channel, buffer, priority == 1);
			} else {
				decodeRequest(channel, buffer, priority);
			}
		}
		return null;
	}

	private void decodeRequest(Channel channel, InBuffer buffer, int requestId) {
		if (requestId == 7) {
			channel.close();
			return;
		}
		if (requestId == 4) {
			encryptionValue = buffer.getUnsignedByte();
			if (buffer.getInt() != 0) {
				channel.close();
			}
		} else {
			buffer.skipBytes(5);
		}
	}

	private void decodeContainer(Channel channel, InBuffer buffer, boolean priority) {
		int indexId = buffer.getUnsignedByte();
		int archiveId = buffer.getInt();
		if (archiveId < 0) {
			return;
		}
		if (indexId != 255) {
			if (Cache.getStore().getIndexes().length <= indexId || Cache.getStore().getIndexes()[indexId] == null || !Cache.getStore().getIndexes()[indexId].archiveExists(archiveId)) {
				System.out.println(indexId + " : "+archiveId);
				return;
			}
		} else if (archiveId != 255) {
			if (Cache.getStore().getIndexes().length <= archiveId || Cache.getStore().getIndexes()[archiveId] == null) {
				System.out.println(indexId + " : "+archiveId);
				return;
			}
		}
		sendCacheArchive(channel, indexId, archiveId, priority);
	}

	private void sendCacheArchive(Channel channel, int indexId, int containerId, boolean priority) {
		if (indexId == 255 && containerId == 255) {
			channel.write(getArchiveData());
		} else {
			ChannelBuffer message = getArchiveData(indexId, containerId, priority);
			if (message == null) {
				return;
			}
			channel.write(message);
		}
	}

	private ChannelBuffer getArchiveData(int indexId, int archiveId, boolean priority) {
		byte[] archive = indexId == 255 ? Cache.getStore().getIndex255().getArchiveData(archiveId) : Cache.getStore().getIndexes()[indexId].getMainFile().getArchiveData(archiveId);
		if (archive == null) {
			return null;
		}
		int compression = archive[0] & 0xff;
		int length = ((archive[1] & 0xff) << 24) + ((archive[2] & 0xff) << 16) + ((archive[3] & 0xff) << 8) + (archive[4] & 0xff);
		if (!priority)
			archiveId |= ~0x7FFFFFFF;
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeByte(indexId);
		buffer.writeInt(archiveId);
		buffer.writeByte(compression);
		buffer.writeInt(length);
		int realLength = compression != 0 ? length + 4 : length;
		for (int index = 5; index < realLength + 5; index++) {
			if (buffer.writerIndex() % 512 == 0) {
				buffer.writeByte(indexId);
				buffer.writeInt(archiveId);
			}
			buffer.writeByte(archive[index]);
		}
		int v = encryptionValue;
		if (v != 0) {
			for (int i = 0; i < buffer.arrayOffset(); i++)
				buffer.setByte(i, buffer.getByte(i) ^ v);
		}
		return buffer;
	}

	private OutBuffer getArchiveData() {
		if (ARCHIVE_DATA == null) {
			ARCHIVE_DATA = Cache.getArchiveData();
		}
		return getArchiveData(255, 255, ARCHIVE_DATA);
	}

	private OutBuffer getArchiveData(int indexFileId, int containerId, byte[] archive) {
		OutBuffer buffer = new OutBuffer(archive.length * 4);
		buffer.putByte(indexFileId);
		buffer.putInt(containerId);
		buffer.putByte(0);
		buffer.putInt(archive.length);
		int offset = 10;
		for (int index = 0; index < archive.length; index++) {
			if (offset == 512) {
				buffer.putByte(indexFileId);
				buffer.putInt(containerId);
				offset = 5;
			}
			buffer.putByte(archive[index]);
			offset++;
		}
		return buffer;
	}
}
