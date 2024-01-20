package de.bushnaq.abdalla.mercator.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TimeStatisticManager {
	Map<String, TimeStatistic> statisticMap = new HashMap<String, TimeStatistic>();

	public Set<String> getSet() {
		return statisticMap.keySet();
	}

	public TimeStatistic getStatistic(final String statisticName) {
		return statisticMap.get(statisticName);
	}

	public void pause(final String statisticName) throws Exception {
		final TimeStatistic planetStatistic = statisticMap.get(statisticName);
		if (planetStatistic != null) {
			planetStatistic.pause();
		} else {
			throw new Exception("Cannot pause measurment. TimeStatistic not in measuring mode.");
		}
	}

	public void resume(final String statisticName) throws Exception {
		TimeStatistic planetStatistic = statisticMap.get(statisticName);
		if (planetStatistic != null) {
			planetStatistic.resume();
		} else {
			planetStatistic = new TimeStatistic();
		}
		statisticMap.put(statisticName, planetStatistic);
	}

	public void start(final String statisticName) throws Exception {
		TimeStatistic planetStatistic = statisticMap.get(statisticName);
		if (planetStatistic != null) {
			planetStatistic.start();
		} else {
			planetStatistic = new TimeStatistic();
		}
		statisticMap.put(statisticName, planetStatistic);
	}

	public void stop(final String statisticName) throws Exception {
		final TimeStatistic planetStatistic = statisticMap.get(statisticName);
		if (planetStatistic != null) {
			planetStatistic.stop();
		} else {
			throw new Exception("Cannot stop measurment. TimeStatistic not in measuring mode.");
		}
	}
}
