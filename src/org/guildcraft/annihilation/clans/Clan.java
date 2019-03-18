package org.guildcraft.annihilation.clans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.guildcraft.annihilation.clans.manager.ClansDatabase;

import jdz.bukkitUtils.sql.ORM.PrimaryKey;
import jdz.bukkitUtils.sql.ORM.SQLDataClass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "name", callSuper = false)
public class Clan extends SQLDataClass {
	private static HashMap<String, Clan> clans = new HashMap<>();

	public static Clan addClan(String name, String owner, List<String> officers, List<String> members, String tag,
			String motd, int coins, int points, List<String> invited, int slots, long updated) {
		Clan c = new Clan(name, owner, tag, motd, coins, points, slots, updated, officers, members, invited);
		clans.put(name.toLowerCase(), c);
		return c;
	}

	public static Clan getClan(String name) {
		return clans.get(name.toLowerCase());
	}

	@PrimaryKey private String name;

	private String owner;
	private String tag;
	private String motd;
	private int coins;
	private int points;
	private int slots;
	private long updated;

	private final List<String> officers;
	private final List<String> members;
	private final List<String> invited;

	private final Set<Player> online = new HashSet<Player>();

	public void join(Player player) {
		if (!invited.remove(player.getName().toLowerCase()))
			return;

		ClansDatabase.getInstance().setClan(player, this);
		members.add(player.getName().toLowerCase());
	}

	public void invite(Player player) {
		invited.add(player.getName().toLowerCase());
	}

	public boolean isInvited(OfflinePlayer player) {
		return invited.contains(player.getName().toLowerCase());
	}

	public void kick(OfflinePlayer player) {
		leave(player);
	}

	public void leave(OfflinePlayer player) {
		ClansDatabase.getInstance().setClan(player, null);
		officers.remove(player.getName().toLowerCase());
		members.remove(player.getName().toLowerCase());
		if (player.isOnline())
			online.remove(player);
	}

	public void transfer(String name) {
		if (officers.remove(name) || members.remove(name)) {
			members.add(owner);
			owner = name;
		}
	}

	public boolean isOfficer(OfflinePlayer player) {
		String name = player.getName().toLowerCase();
		return owner.equals(name) || officers.contains(name);
	}

	public void promote(OfflinePlayer player) {
		String name = player.getName().toLowerCase();
		if (members.remove(name))
			officers.add(name);
	}

	public void demote(OfflinePlayer player) {
		String name = player.getName().toLowerCase();
		if (officers.remove(name))
			members.add(name);
	}

	public void disband() {
		ClansDatabase.getInstance().disband(this);
		clans.remove(name.toLowerCase());
	}

	public int getTotalMembers() {
		return 1 + officers.size() + members.size();
	}

	public boolean hasTag() {
		return tag.equals("null");
	}

	public boolean hasMOTD() {
		return motd.equals("null");
	}

	public void save() {
		ClansDatabase.getInstance().save(this);
	}
}
