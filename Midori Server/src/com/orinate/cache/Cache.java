package com.orinate.cache;

import java.math.BigInteger;
import java.util.logging.Logger;

import com.alex.store.Store;
import com.alex.util.whirlpool.Whirlpool;
import com.orinate.Constants;
import com.orinate.io.OutBuffer;

public class Cache {

	private static final Logger logger = Logger.getLogger(Cache.class.getName());
	public static Store STORE;

	public static void init() {
		try {
			Cache.STORE = new Store(Constants.CACHE_PATH);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		logger.info("Loaded " + STORE.getIndexes().length + " cache indicies.");
	}

	public static final byte[] getArchiveData() {
		OutBuffer buffer = new OutBuffer();
		buffer.putByte(STORE.getIndexes().length);
		for (int idx = 0; idx < STORE.getIndexes().length; idx++) {
			if (STORE.getIndexes()[idx] == null) {
				buffer.putInt(0);
				buffer.putInt(0);
				buffer.putInt(0);
				buffer.putInt(0);
				buffer.putBytes(new byte[64]);
				continue;
			}
			buffer.putInt(STORE.getIndexes()[idx].getCRC());
			buffer.putInt(STORE.getIndexes()[idx].getTable().getRevision());
			buffer.putInt(0);
			buffer.putInt(0);
			buffer.putBytes(STORE.getIndexes()[idx].getWhirlpool());
		}
		byte[] archive = new byte[buffer.offset()];
		buffer.offset(0);
		buffer.getBytes(archive, 0, archive.length);
		OutBuffer footer = new OutBuffer(65);
		footer.putByte(0);
		footer.putBytes(Whirlpool.getHash(archive, 0, archive.length));
		byte[] hash = new byte[footer.offset()];
		footer.offset(0);
		footer.getBytes(hash, 0, hash.length);
		hash = new BigInteger(hash).modPow(Constants.ONDEMAND_KEY, Constants.ONDEMAND_MODULUS).toByteArray();
		buffer.putBytes(hash);
		archive = new byte[buffer.offset()];
		buffer.offset(0);
		buffer.getBytes(archive, 0, archive.length);
		return archive;
	}

	public static Store getStore() {
		return STORE;
	}
}
