package com.orinate.game.content.exchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.orinate.game.core.tick.Tick;
import com.orinate.game.core.tick.TickState;

/**
 * @author Taylor Moon
 */
public class GrandExchangeDatabase extends Tick {

	private static final GrandExchangeDatabase DATABASE = new GrandExchangeDatabase(0);

	private static final Collection<GrandExchange> exchanges = Collections.synchronizedList(new ArrayList<GrandExchange>());

	public GrandExchangeDatabase(int delay) {
		super(delay);
	}

	public static void register(GrandExchange grandExchange) {
		if (grandExchange != null) {
			synchronized (grandExchange) {
				exchanges.add(grandExchange);
			}
		}
	}

	public static void unRegister(GrandExchange grandExchange) {
		if (grandExchange != null) {
			synchronized (grandExchange) {
				exchanges.remove(grandExchange);
			}
		}
	}

	@Override
	public TickState onTick() {
		synchronized (exchanges) {
			for (GrandExchange exchange : exchanges) {
				exchange.update();
			}
		}
		return TickState.ALIVE;
	}

	/**
	 * @return the database
	 */
	public static GrandExchangeDatabase getDatabase() {
		return DATABASE;
	}
}
