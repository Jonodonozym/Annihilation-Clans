
package org.guildcraft.annihilation.clans;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ClanLevel {
	I(0, 5), II(500, 8), III(1000, 11), IV(2000, 14), V(3500, 17), VI(5000, 20);
	@Getter private final int price, slots;

	public static ClanLevel getFromCurrentSlots(int slots) {
		for (ClanLevel level : values())
			if (level.getSlots() <= slots)
				return level;
		return VI;
	}

	public ClanLevel next() {
		if (ordinal() == values().length)
			return null;
		return values()[ordinal() + 1];
	}
}
