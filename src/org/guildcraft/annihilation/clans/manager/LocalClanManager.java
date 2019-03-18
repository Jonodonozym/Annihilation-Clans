package org.guildcraft.annihilation.clans.manager;

import org.guildcraft.annihilation.clans.Clan;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class LocalClanManager {
	public static boolean hasClan(String clan) {
		return Clan.getClan(clan) != null;
	}

	public static Clan getClan(String clan) {
		if (!hasClan(clan))
			return ClansDatabase.getInstance().getClan(clan);
		return Clan.getClan(clan.toLowerCase());
	}

	public static boolean isLocalDataOld(String clan) {
		if (!hasClan(clan))
			return true;
		return (System.currentTimeMillis() - Clan.getClan(clan.toLowerCase()).getUpdated()) > 600000;
	}

}
