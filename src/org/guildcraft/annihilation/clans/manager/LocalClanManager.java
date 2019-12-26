package org.guildcraft.annihilation.clans.manager;

import org.bukkit.entity.Player;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.object.Clan;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class LocalClanManager {

	private Clans pl;

	public HashMap<String, List<Player>> online = new HashMap<>();

	public LocalClanManager(Clans pl) {
		this.pl = pl;
	}

	public boolean hasLocalData(String clan) {
	    return Clan.clans.containsKey(clan.toLowerCase());
	}

	public void createLocalData(String name) { // get real life data from SQL
		System.out.print("Getting SQL data for " + name);
		Clan.addClan(name.toLowerCase(), pl.getClansManager().getOwner(name), pl.getClansManager().getOfficers(name),
				pl.getClansManager().getMembers(name), pl.getClansManager().getTag(name),
				pl.getClansManager().getMOTD(name), pl.getClansManager().getClanCoins(name),
				pl.getClansManager().getClanPoints(name), pl.getClansManager().getInvited(name),
				System.currentTimeMillis());
	}

	/*
	 * 
	 * //maybe soon
	 * public Clan getLocalData(String clan){
	 * if (Clans.instance.gameMode) { //cache only in game servers: sql lag avoid
	 * if (hasLocalData(clan.toLowerCase())) {
	 * if (isLocalDataOld(clan.toLowerCase())) {
	 * createLocalData(clan.toLowerCase());
	 * return Clan.clans.get(clan);
	 * } else {
	 * return Clan.clans.get(clan);
	 * 
	 * }
	 * } else {
	 * createLocalData(clan);
	 * 
	 * return Clan.clans.get(clan);
	 * }
	 * } else {
	 * createLocalData(clan); //lazy
	 * return Clan.getClan(clan.toLowerCase());
	 * 
	 * }
	 * 
	 * return
	 * }
	 * 
	 */

	public Clan getLocalData(String clan) {
		createLocalData(clan);
		return Clan.getClan(clan.toLowerCase());
	}

	public boolean isLocalDataOld(String clan) {
		if (!hasLocalData(clan))
			return true;
		return ((System.currentTimeMillis() - Clan.clans.get(clan.toLowerCase()).getUpdated()) > 600000); // 10 min
																											// update;
																											// changeable
	}
}