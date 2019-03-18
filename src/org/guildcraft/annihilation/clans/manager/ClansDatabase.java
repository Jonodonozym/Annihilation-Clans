package org.guildcraft.annihilation.clans.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.guildcraft.annihilation.clans.Clan;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.bungee.ChatManager;
import org.guildcraft.annihilation.clans.util.SQLArray;

import jdz.bukkitUtils.sql.SQLConfig;
import jdz.bukkitUtils.sql.SQLRow;
import jdz.bukkitUtils.sql.SqlDatabase;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClansDatabase extends SqlDatabase {
	@Getter private static ClansDatabase instance;

	public static void init(SQLConfig config) {
		instance = new ClansDatabase(config);
	}

	public ClansDatabase(SQLConfig config) {
		super(Clans.getInstance());

		setConfig(config);

		update("CREATE TABLE IF NOT EXISTS clans_clan (name varchar(16) NOT NULL, "
				+ "realname varchar(16) NOT NULL, members varchar(512) NOT NULL, officers varchar(128) NOT NULL, invited varchar(256) NOT NULL, coins varchar(32) NOT NULL, points varchar(32) NOT NULL, owner varchar(32) NOT NULL,tag varchar(32) NOT NULL,motd varchar(256) NOT NULL, slots varchar(16) NOT NULL, "
				+ "UNIQUE KEY name (name) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
		update("CREATE TABLE IF NOT EXISTS " + "clans_players" + " ( username varchar(16) NOT NULL, "
				+ "clan varchar(32) NOT NULL, realname varchar(32) NOT NULL,"
				+ "UNIQUE KEY username (username) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
	}

	public void createClan(String name, Player owner) {
		Bukkit.getScheduler().runTaskAsynchronously(Clans.getInstance(), () -> {
			update("INSERT IGNORE INTO " + "clans_clan "
					+ "(name, realname, members,officers,invited,coins,points,owner,tag,motd,slots) VALUES " + "('"
					+ name.toLowerCase() + "','" + name + "', '', '', '','0','0','" + owner.getName()
					+ "','null','null','5');");

			Clan clan = getClan(name);
			setClan(owner, clan);
			clan.getOnline().add(owner);
		});
	}

	public Clan getClan(String name) {
		SQLRow row = queryFirst(
				"SELECT realname, owner, members, officers, invited, coins, points, tag, motd, slots FROM clans_clan WHERE name = '"
						+ name + "';");
		if (row == null)
			return null;
		
		String realname = row.get(0);
		String owner = row.get(1);

		List<String> members = new ArrayList<String>(Arrays.asList(row.get(2).split(", ")));
		List<String> officers = new ArrayList<String>(Arrays.asList(row.get(3).split(", ")));
		List<String> invited = new ArrayList<String>(Arrays.asList(row.get(4).split(", ")));

		int coins = Integer.parseInt(row.get(5));
		int points = Integer.parseInt(row.get(6));

		String tag = row.get(7);
		String motd = row.get(8);

		int slots = Integer.parseInt(row.get(9));

		return Clan.addClan(realname, owner, officers, members, tag, motd, coins, points, invited, slots,
				System.currentTimeMillis());
	}

	public void save(Clan clan) {
		String members = SQLArray.convertToString(clan.getMembers());
		String officers = SQLArray.convertToString(clan.getOfficers());
		String invited = SQLArray.convertToString(clan.getInvited());

		String update = String.format(
				"UPDATE clans_clan SET owner='%s' members='%s', officers='%s', invited='%s', coins=%s, points=%s, tag='%s', motd='%s', slots=%s"
						+ "WHERE realname='%s'",
				clan.getOwner(), members, officers, invited, clan.getCoins(), clan.getPoints(),
				clan.getTag().replaceAll(";", ""), clan.getMotd().replaceAll(";", ""), clan.getSlots(), clan.getName().toLowerCase());

		updateAsync(update);
	}

	public void disband(Clan clan) {
		updateAsync("DELETE  FROM " + "clans_clan" + " WHERE name='" + clan.getName().toLowerCase() + "'");
		updateAsync("update clans_players set clan = 'null' WHERE clan = " + clan.getName().toLowerCase() + ";");
		ChatManager.getInstance().sendChatMessageToClan("SYSTEM", clan, "§eThe clan has been disbanded.");
	}

	public boolean hasClan(OfflinePlayer player) {
		return getClan(player) != null;
	}
	
	public Clan getClan(OfflinePlayer player) {
		SQLRow row = queryFirst("SELECT clan FROM clans_players WHERE username = '" + player.getName().toLowerCase() + "';");
		if (row == null || row.get("clan").equals("null"))
			return null;
		return LocalClanManager.getClan(row.get("clan"));
	}

	public void setClan(OfflinePlayer player, Clan clan) {
		updateAsync("REPLACE INTO clans_players (username, clan, realname) VALUES('" + player.getName().toLowerCase()
				+ "','" + (clan == null ? "null" : clan.getName().toLowerCase()) + "','" + player.getName() + "')");
	}

	public String getRealPlayerName(String player) {
		SQLRow row = queryFirst("SELECT realname FROM clans_players WHERE username ='" + player.toLowerCase() + "';");
		return row.get("realname");
	}
}
