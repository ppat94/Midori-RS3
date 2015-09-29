package com.orinate.game.core.tick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.orinate.game.content.exchange.GrandExchangeDatabase;

/**
 * @author Taylor Moon
 * @since Jan 23, 2014
 */
public class TickManager {

	/**
	 * Represents a list of ticks in the repository to be updated and processed.
	 */
	private static final Collection<Tick> ticks = Collections.synchronizedList(new ArrayList<Tick>());

	/**
	 * Loads the default ticks.
	 */
	public void loadDefaults() {
		ticks.add(GrandExchangeDatabase.getDatabase());
	}

	/**
	 * Called when the ticks in the collection should be executed and updated.
	 * Any ticks that should be destroyed or repressed are moved to seperate
	 * lists that are flagged as such category.
	 */
	public void processAllTicks() {
		synchronized (ticks) {
			try {
				for (Iterator<Tick> it = ticks.iterator(); it.hasNext();) {
					Tick tick = it.next();
					if (tick == null) {
						continue;
					}
					if (tick.decreaseDelay() > 0) {
						continue;
					}
					TickState state = tick.onTick();
					if (state == null) {
						continue;
					}
					switch (state) {
					case ALIVE:
						tick.setDelay(tick.getDuration());
						continue;
					case DESTROYED:
						ticks.remove(tick);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Registers a tick into the collection.
	 * 
	 * @param tick
	 *            The tick to be registered.
	 */
	public static void register(Tick tick) {
		synchronized (ticks) {
			ticks.add(tick);
		}
	}
}
