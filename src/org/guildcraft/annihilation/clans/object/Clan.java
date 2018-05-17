package org.guildcraft.annihilation.clans.object;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arjen on 20/04/2016.
 */
public class Clan {


	private String name;
	private String owner;
	private ArrayList<String> officers;
	private ArrayList<String> members;
	private String tag;
	private String motd;
	private int coins;
	private int points;
	private ArrayList<String> invited;
	private Long updated;

	public static HashMap<String, Clan> clans = new HashMap<>();


	public static void addClan(String name, String owner, ArrayList<String> officers, ArrayList<String> members,
			String tag, String motd, int coins, int points, ArrayList<String> invited, Long updated) {
		clans.put(name, new Clan(name, owner, officers, members, tag, motd, coins, points, invited, updated));



	}

	public static Clan getClan(String name) {
		if (!clans.containsKey(name)) {
			return null;
		}
		else {
			return clans.get(name);
		}
	}


	public Clan(String name, String owner, ArrayList<String> officers, ArrayList<String> members, String tag,
			String motd, int coins, int points, ArrayList<String> invited, Long updated) {
		this.name = name;
		this.owner = owner;
		this.officers = officers;
		this.members = members;
		this.tag = tag;
		this.motd = motd;
		this.coins = coins;
		this.points = points;
		this.invited = invited;
		this.updated = updated;


	}

	public String getName() {
		return name;
	}

	public int getCoins() {
		return coins;
	}

	public ArrayList<String> getOfficers() {
		return officers;
	}

	public String getMotd() {
		return motd;
	}

	public String getOwner() {
		return owner;
	}

	public ArrayList<String> getMembers() {
		return members;
	}

	public String getTag() {
		return tag;
	}

	public int getPoints() {
		return points;
	}

	public Long getUpdated() {
		return updated;
	}

	public ArrayList<String> getInvited() {
		return invited;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}


	public void setPoints(int points) {
		this.points = points;
	}


}
