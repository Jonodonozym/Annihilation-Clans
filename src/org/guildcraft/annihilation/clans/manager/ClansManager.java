package org.guildcraft.annihilation.clans.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.util.SQLArray;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by arjen on 20/04/2016.
 */
public class ClansManager {

	private Clans pl;

	public ClansManager(Clans pl) {
		this.pl = pl;

		setup();
	}

	// create datbases and tables

	public void setup() {
		System.out.print("[Clans] Updating information...");
		System.out.print("[Clans] Starting ClansManager™ setup..");

		pl.getDatabaseManager().query("CREATE TABLE IF NOT EXISTS `" + "clans_clan"
				+ "` ( `name` varchar(16) NOT NULL, "
				+ "`realname` varchar(16) NOT NULL,`members` varchar(512) NOT NULL, `officers` varchar(128) NOT NULL, `invited` varchar(256) NOT NULL, `coins` varchar(32) NOT NULL, `points` varchar(32) NOT NULL, `owner` varchar(32) NOT NULL,`tag` varchar(32) NOT NULL,`motd` varchar(256) NOT NULL, `slots` varchar(16) NOT NULL, "
				+ "UNIQUE KEY `name` (`name`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
		pl.getDatabaseManager()
				.query("CREATE TABLE IF NOT EXISTS `" + "clans_players" + "` ( `username` varchar(16) NOT NULL, "
						+ "`clan` varchar(32) NOT NULL, `realname` varchar(32) NOT NULL,"
						+ "UNIQUE KEY `username` (`username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;");

		System.out.print("[Clans] ClansManager™ finished starting up.");
	}



	// basic


	public void createClan(String name, Player owner) {

		pl.getDatabaseManager().query("INSERT IGNORE INTO " + "clans_clan"
				+ " (`name`, `realname`, `members`,`officers`,`invited`,`coins`,`points`,`owner`,`tag`,`motd`,`slots`) VALUES "
				+ "('" + name.toLowerCase() + "','" + name + "', '', '', '','0','0','" + owner.getName()
				+ "','null','null','5');");

		System.out.print("created clan");
		setClan(owner.getName(), name, owner);

		String clan = name.toLowerCase();
		if (!Clans.instance.getLocalClanManager().online.containsKey(clan)) {
			Clans.instance.getLocalClanManager().online.put(clan, new ArrayList<Player>());
		}
		ArrayList<Player> local = Clans.instance.getLocalClanManager().online.get(clan);
		local.add(owner);
		Clans.instance.getLocalClanManager().online.put(clan, local);


	}

	public void joinClan(String name, Player joiner) {

		// add to member list
		ArrayList<String> newArrayMembers = getMembers(name);
		if (!newArrayMembers.contains(joiner.getName()))
			newArrayMembers.add(joiner.getName().toLowerCase());

		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "members" + "`='"
				+ SQLArray.convertToString(newArrayMembers) + "' WHERE `name`='" + name.toLowerCase() + "';");


		// remove from invite list
		ArrayList<String> newArrayInvited = getInvited(name);
		newArrayInvited.remove(joiner.getName().toLowerCase());

		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "invited" + "`='"
				+ SQLArray.convertToString(newArrayInvited) + "' WHERE `name`='" + name.toLowerCase() + "';");


		// set clan in player data
		setClan(joiner.getName(), name, joiner);

		String clan = name.toLowerCase();
		if (!Clans.instance.getLocalClanManager().online.containsKey(clan)) {
			Clans.instance.getLocalClanManager().online.put(clan, new ArrayList<Player>());
		}
		ArrayList<Player> local = Clans.instance.getLocalClanManager().online.get(clan);
		local.add(joiner);
		Clans.instance.getLocalClanManager().online.put(clan, local);

	}

	public void invitePlayerToClan(String clan, String invited) {
		ArrayList<String> newArray = getInvited(clan);
		if (!newArray.contains(invited.toLowerCase()))
			newArray.add(invited.toLowerCase());

		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "invited" + "`='"
				+ SQLArray.convertToString(newArray) + "' WHERE `name`='" + clan.toLowerCase() + "';");


	}

	public boolean isInvited(String isinvited, String clan) {

		return (getInvited(clan).contains(isinvited.toLowerCase()));

	}

	public void leaveClan(String clan, String leaver) {
		setClan(leaver, "null");

		if (isOfficer(clan, leaver)) {
			ArrayList<String> newArrayOfficers = getOfficers(clan);
			newArrayOfficers.remove(leaver.toLowerCase());

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "officers" + "`='"
					+ SQLArray.convertToString(newArrayOfficers) + "' WHERE `name`='" + clan.toLowerCase() + "';");
		}
		else {
			ArrayList<String> newArrayMembers = getMembers(clan);
			newArrayMembers.remove(leaver.toLowerCase());

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "members" + "`='"
					+ SQLArray.convertToString(newArrayMembers) + "' WHERE `name`='" + clan.toLowerCase() + "';");
		}

		if (Bukkit.getPlayer(leaver) != null) {
			ArrayList<Player> local = Clans.instance.getLocalClanManager().online.get(clan.toLowerCase());
			local.remove(Bukkit.getPlayer(leaver));
			Clans.instance.getLocalClanManager().online.put(clan.toLowerCase(), local);
		}

	}

	public void kickPlayer(String clan, String leaver) {
		setClan(leaver, "null");
		if (isOfficer(clan, leaver)) {
			ArrayList<String> newArrayOfficers = getOfficers(clan);
			newArrayOfficers.remove(leaver.toLowerCase());

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "officers" + "`='"
					+ SQLArray.convertToString(newArrayOfficers) + "' WHERE `name`='" + clan.toLowerCase() + "';");
		}
		else {
			ArrayList<String> newArrayMembers = getMembers(clan);
			newArrayMembers.remove(leaver.toLowerCase());

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "members" + "`='"
					+ SQLArray.convertToString(newArrayMembers) + "' WHERE `name`='" + clan.toLowerCase() + "';");
		}

		if (Bukkit.getPlayer(leaver) != null) {

			ArrayList<Player> local = Clans.instance.getLocalClanManager().online.get(clan.toLowerCase());
			local.remove(Bukkit.getPlayer(leaver));
			Clans.instance.getLocalClanManager().online.put(clan.toLowerCase(), local);
		}
	}

	public void disbandClan(String clan) {
		setClan(getOwner(clan), "null");
		for (String officers : getOfficers(clan)) {
			setClan(officers, "null");
		}
		for (String members : getMembers(clan)) {
			setClan(members, "null");
		}


		pl.getDatabaseManager() // sql delete
				.query("DELETE  FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'");


		pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(), "§eThe clan has been disbanded.");

	}

	public void transfer(String clan, String to) {
		final String owner = getOwner(clan);

		if (isOfficer(clan, to)) {
			ArrayList<String> newArrayMembers = getMembers(clan);
			newArrayMembers.add(owner.toLowerCase());

			ArrayList<String> newArrayOfficers = getOfficers(clan);
			newArrayOfficers.remove(to.toLowerCase());



			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "members" + "`='"
					+ SQLArray.convertToString(newArrayMembers) + "' WHERE `name`='" + clan.toLowerCase() + "';");

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "officers" + "`='"
					+ SQLArray.convertToString(newArrayOfficers) + "' WHERE `name`='" + clan.toLowerCase() + "';");
		}
		else {

			ArrayList<String> newArrayMembers = getMembers(clan);
			newArrayMembers.add(owner.toLowerCase());
			newArrayMembers.remove(to.toLowerCase());

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "members" + "`='"
					+ SQLArray.convertToString(newArrayMembers) + "' WHERE `name`='" + clan.toLowerCase() + "';");


		}

		setOwner(clan, to);
	}



	// player data

	public String getClan(String player) {


		try {
			ResultSet rs = pl.getDatabaseManager().query("SELECT " + "clan" + " FROM `" + "clans_players"
					+ "` WHERE `username`='" + player.toLowerCase() + "'").getResultSet();

			while (rs.next())
				return rs.getString("clan");


		}
		catch (SQLException ex) {
			return null;
		}


		return null;
	}

	public boolean hasClan(String player) {

		try {
			ResultSet rs = pl.getDatabaseManager().query("SELECT " + "clan" + " FROM `" + "clans_players"
					+ "` WHERE `username`='" + player.toLowerCase() + "'").getResultSet();

			while (rs.next()) {
				if (rs.getString("clan").equals("null")) {
					System.out.print("Clan equals 'null'");
					return false;
				}
				else {

					return true;
				}
			}



		}
		catch (SQLException ex) {
			System.out.print("SQLexc");
			return false;
		}

		return false;
	}

	public void setClan(String name, String clan) {
		System.out.print("Setting clan");

		pl.getDatabaseManager().query("UPDATE `" + "clans_players" + "` SET `" + "clan" + "`='" + clan.toLowerCase()
				+ "' WHERE `username`='" + name.toLowerCase() + "';");


		System.out.print("updated clan: " + getClan(name));
	}

	public void setClan(String name, String clan, Player p) {
		System.out.print("Setting clan");
		pl.getDatabaseManager()
				.query("INSERT IGNORE INTO " + "clans_players" + " (`username`, `clan`, `realname`) VALUES " + "('"
						+ name.toLowerCase() + "', '" + clan.toLowerCase() + "', '" + p.getName() + "');");

		pl.getDatabaseManager().query("UPDATE `" + "clans_players" + "` SET `" + "clan" + "`='" + clan.toLowerCase()
				+ "' WHERE `username`='" + name.toLowerCase() + "';");


		System.out.print("updated clan: " + getClan(name));
	}

	public String getRealPlayerName(String player) {
		try {
			ResultSet rs = pl.getDatabaseManager().query("SELECT " + "realname" + " FROM `" + "clans_players"
					+ "` WHERE `username`='" + player.toLowerCase() + "'").getResultSet();

			while (rs.next())
				return rs.getString("realname");


		}
		catch (SQLException ex) {
			return null;
		}


		return null;
	}



	// currency

	public int getClanCoins(String clan) {


		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "coins" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return rs.getInt("coins");


		}
		catch (SQLException ex) {
			return -1;
		}


		return -1;



	}

	public void addClanCoins(int add, String clan) {

		int set = getClanCoins(clan) + add;
		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "coins" + "`='" + set + "' WHERE `name`='"
				+ clan.toLowerCase() + "';");
	}

	public void removeClanCoins(int remove, String clan) {

		int set = getClanCoins(clan) - remove;
		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "coins" + "`='" + set + "' WHERE `name`='"
				+ clan.toLowerCase() + "';");
	}

	public int getClanPoints(String clan) {

		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "points" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return rs.getInt("points");


		}
		catch (SQLException ex) {
			return -1;
		}


		return -1;
	}

	public void addClanPoints(int add, String clan) {

		int set = getClanPoints(clan) + add;
		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "points" + "`='" + set
				+ "' WHERE `name`='" + clan.toLowerCase() + "';");
	}



	// info

	public String getTag(String clan) {

		try {
			ResultSet rs = pl.getDatabaseManager()
					.query("SELECT " + "tag" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return rs.getString("tag");


		}
		catch (SQLException ex) {
			return null;
		}


		return null;

	}

	public boolean hasTag(String clan) {
		return !getTag(clan).equals("null");

	}

	public void setTag(String clan, String tag) {

		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "tag" + "`='" + tag + "' WHERE `name`='"
				+ clan.toLowerCase() + "';");
	}

	public String getMOTD(String clan) {
		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "motd" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return rs.getString("motd");


		}
		catch (SQLException ex) {
			return null;
		}


		return null;
	}

	public boolean hasMOTD(String clan) {
		return !getMOTD(clan).equals("null");

	}

	public void setMOTD(String clan, String motd) {
		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "motd" + "`='" + motd + "' WHERE `name`='"
				+ clan.toLowerCase() + "';");
	}

	public String getOwner(String clan) {
		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "owner" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return rs.getString("owner");


		}
		catch (SQLException ex) {
			return null;
		}


		return null;

	}

	public void setOwner(String clan, String owner) {
		String newowner = getRealPlayerName(owner.toLowerCase());
		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "owner" + "`='" + newowner
				+ "' WHERE `name`='" + clan.toLowerCase() + "';");
	}

	public String getRealName(String clan) {
		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "realname" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return rs.getString("realname");


		}
		catch (SQLException ex) {
			return null;
		}


		return null;
	}

	public int getSlots(String clan) {
		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "slots" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return rs.getInt("slots");


		}
		catch (SQLException ex) {
			return -1;
		}


		return -1;
	}

	public void setSlots(int set, String clan) {
		pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "slots" + "`='" + set + "' WHERE `name`='"
				+ clan.toLowerCase() + "';");
	}

	public ArrayList<String> getOfficers(String clan) {
		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "officers" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return SQLArray.getFromString(rs.getString("officers"));


		}
		catch (SQLException ex) {
			return null;
		}


		return null;
	}

	public ArrayList<String> getOfficersLS(String clan) {
		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "officers" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return SQLArray.getFromStringLS(rs.getString("officers"));


		}
		catch (SQLException ex) {
			return null;
		}


		return null;
	}

	public ArrayList<String> getMembers(String clan) {
		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "members" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return SQLArray.getFromString(rs.getString("members"));


		}
		catch (SQLException ex) {
			return null;
		}


		return null;
	}

	public ArrayList<String> getMembersLS(String clan) {
		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "members" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return SQLArray.getFromStringLS(rs.getString("members"));


		}
		catch (SQLException ex) {
			return null;
		}


		return null;
	}

	public ArrayList<String> getInvited(String clan) {
		try {
			ResultSet rs = pl.getDatabaseManager().query(
					"SELECT " + "invited" + " FROM `" + "clans_clan" + "` WHERE `name`='" + clan.toLowerCase() + "'")
					.getResultSet();

			while (rs.next())
				return SQLArray.getFromString(rs.getString("invited"));


		}
		catch (SQLException ex) {
			return null;
		}


		return null;
	}


	// player data

	public boolean isOfficer(String clan, String player) {
		return (getOfficers(clan).contains(player.toLowerCase())
				|| getOwner(clan).toLowerCase().equals(player.toLowerCase()));
	}

	public void promote(String clan, String player) {
		if (!isOfficer(clan, player)) {

			// remove from members
			ArrayList<String> newArrayMembers = getMembers(clan);
			newArrayMembers.remove(player.toLowerCase());

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "members" + "`='"
					+ SQLArray.convertToString(newArrayMembers) + "' WHERE `name`='" + clan.toLowerCase() + "';");

			// remove from members
			ArrayList<String> newArrayOfficers = getOfficers(clan);
			newArrayOfficers.add(player.toLowerCase());

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "officers" + "`='"
					+ SQLArray.convertToString(newArrayOfficers) + "' WHERE `name`='" + clan.toLowerCase() + "';");
		}
	}

	public void demote(String clan, String player) {
		if (isOfficer(clan, player)) {

			// remove from members
			ArrayList<String> newArrayMembers = getOfficers(clan);
			newArrayMembers.remove(player.toLowerCase());

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "officers" + "`='"
					+ SQLArray.convertToString(newArrayMembers) + "' WHERE `name`='" + clan.toLowerCase() + "';");

			// remove from members
			ArrayList<String> newArrayOfficers = getMembers(clan);
			newArrayOfficers.add(player.toLowerCase());

			pl.getDatabaseManager().query("UPDATE `" + "clans_clan" + "` SET `" + "members" + "`='"
					+ SQLArray.convertToString(newArrayOfficers) + "' WHERE `name`='" + clan.toLowerCase() + "';");
		}
	}


	// util

	public int getTotalMembers(String clan) {
		int members = 1;
		members += getMembers(clan).size();
		members += getOfficers(clan).size();

		return members;
	}



}
