package com.abdalla.bushnaq.mercator.util;

import java.util.ArrayList;
import java.util.List;

import com.abdalla.bushnaq.mercator.universe.good.GoodType;

public class HistoryManager {
	public static final int CREDIT_HISTORY_SIZE = 200;
	List<HistoryItem> items = new ArrayList<HistoryItem>();
	private int[] lastAnualTradingGoodVolume;
	long lastQuery = Long.MIN_VALUE;
	int startTime = 0;

	public HistoryManager() {
	}

	public HistoryItem get(final long currentTime) {
		HistoryItem item = null;
		if (currentTime >= items.size() + startTime) {
			item = new HistoryItem();
			items.add(item);
			while (items.size() > CREDIT_HISTORY_SIZE) {
				items.remove(0);
				startTime++;
			}
		} else {
			item = items.get((int) currentTime - startTime);
		}
		return item;
	}

	public int getAnualExportAmountOfGoods() {
		int amount = 0;
		final int lastYear = ((startTime + 99) / 100) * 100;
		for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
			final HistoryItem item = items.get(i);
			amount += item.exportedAmountOfGoods;
		}
		return amount;
	}

	public float getAnualExportCredits() {
		float amount = 0;
		final int lastYear = ((startTime + 99) / 100) * 100;
		for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
			final HistoryItem item = items.get(i);
			amount += item.exportedCredits;
		}
		return amount;
	}

	public int getAnualImportAmountOfGoods() {
		int amount = 0;
		final int lastYear = ((startTime + 99) / 100) * 100;
		for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
			final HistoryItem item = items.get(i);
			amount += item.importedAmountOfGoods;
		}
		return amount;
	}

	public float getAnualImportedCredits() {
		float amount = 0;
		final int lastYear = ((startTime + 99) / 100) * 100;
		for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
			final HistoryItem item = items.get(i);
			amount += item.importedCredits;
		}
		return amount;
	}

	public float getAnualLocalCreditsEarned() {
		float amount = 0;
		final int lastYear = ((startTime + 99) / 100) * 100;
		for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
			final HistoryItem item = items.get(i);
			amount += item.localCreditsEarned;
		}
		return amount;
	}

	public float getAnualLocalCreditsSpent() {
		float amount = 0;
		final int lastYear = ((startTime + 99) / 100) * 100;
		for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
			final HistoryItem item = items.get(i);
			amount += item.localCreditsSpent;
		}
		return amount;
	}

	public int[] getAnualTradingGoodVolume() {
		if (lastQuery != startTime + items.size()) {
			final int[] amount = new int[GoodType.values().length];
			final int lastYear = ((startTime + 99) / 100) * 100;
			for (int i = lastYear - startTime; i < Math.min(lastYear - startTime + 100, items.size()); i++) {
				final HistoryItem item = items.get(i);
				for (int index = 0; index < GoodType.values().length; index++) {
					amount[index] += item.tradedGoodVolume[index];
				}
			}
			lastQuery = startTime + items.size();
			lastAnualTradingGoodVolume = amount;
			return amount;
		} else {
			return lastAnualTradingGoodVolume;
		}
	}
}
