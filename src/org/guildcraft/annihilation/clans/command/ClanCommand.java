package org.guildcraft.annihilation.clans.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.guildcraft.annihilation.clans.Clan;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.inv.ShopMenu;
import org.guildcraft.annihilation.clans.manager.ClansDatabase;
import org.guildcraft.annihilation.clans.manager.LocalClanManager;
import org.guildcraft.annihilation.clans.util.SQLArray;
import org.guildcraft.annihilation.gcStatsHook.ExperienceManager;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class ClanCommand implements CommandExecutor {

	private Clans pl = Clans.getInstance();

	public static HashMap<Player, String> disband = new HashMap<>();
	public static List<Player> chatMode = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (commandSender instanceof Player) {
			Player p = (Player) commandSender;
			if (strings.length == 0) {
				String clanName = "None";
				String tag = "§eNone §7(You can buy one in the /clan shop)";
				String motd = "§eNone §7(You can buy one in the /clan shop)";

				p.sendMessage("§eClans v0.1 ALPHA");
				p.sendMessage("");
				p.sendMessage("§b===================================");
				
				Clan clan = ClansDatabase.getInstance().getClan(p);
				if (clan != null) 
					clanName = clan.getName();
				
				p.sendMessage("§aYour current clan: §7" + clan==null?"None":clan.getName());
				
				if (clan == null) 
					p.sendMessage("§6You can make a clan by doing /clan create or joining an existing one!");
				else {
					if (clan.hasTag())
						tag = "§a" + clan.getTag();
					else
						tag = "§eNone §7(You can buy one in the /clan shop)";
					if (clan.hasMOTD())
						motd = "§e" + clan.getMotd().replaceAll("&", "§");
					else
						motd = "§eNone §7(You can buy one in the /clan shop)";
					p.sendMessage("§7Total members: §e" + clan.getTotalMembers() + "§7/§e"
							+ clan.getSlots());
					p.sendMessage("§7Tag: " + tag);
					p.sendMessage("§7MOTD: " + motd.replaceAll("&", "§"));
					p.sendMessage("§6Clan Coins: " + clan.getCoins());
					p.sendMessage("§dClan Points: " + clan.getPoints());

					p.sendMessage("§e------------------------------");
					p.sendMessage("§eOwner: " + clan.getOwner());
					p.sendMessage("§9Officers: " + SQLArray.convertToStringView(clan.getOfficers()));
					p.sendMessage("§7Members: " + SQLArray.convertToStringView(clan.getMembers()));
				}
				p.sendMessage("§b===================================");
			}
			else if (strings.length == 1) {
				if (strings[0].equals("help")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						helpMenu(p, false);
						return true;
					}
					helpMenu(p, true);
				}
				else if (strings[0].equals("disband")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to do this.");
						return true;
					}
					String clan = ClansDatabase.getInstance().getClan(p.getName());

					if (!ClansDatabase.getInstance().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
						p.sendMessage(
								"§9Clans> §7You have to be the rank §eÂ§lOWNER §7in order to disband to clan.");
						return true;
					}

					if (pl.gameMode) {
						p.sendMessage(
								"§9Clans> §7This command is disabled in game servers. Use it in the anni lobby!");
						return true;
					}

					p.sendMessage("");
					p.sendMessage(""); // <html><br><br></html>//
					p.sendMessage(
							"§9Clans> §eAre you sure? §7Type §eYES §7in the chat within 10 seconds to confirm."); // view
																														// PlayerListener:25
					disband.put(p.getName(), "disband_" + clan);
					Bukkit.getScheduler().scheduleSyncDelayedTask(Clans.getInstance(), new Runnable() {
						@Override
						public void run() {
							if (disband.containsKey(p.getName())) {
								disband.remove(p.getName());
								p.sendMessage("§9Clans> §7Time out. §eAborted clan disband.");
							}
						}
					}, 20 * 10);


				}
				else if (strings[0].equals("members")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to do this.");

						return true;
					}
					String clan = ClansDatabase.getInstance().getClan(p.getName());
					Clan c = LocalClanManager.getClan(clan);
					p.sendMessage("§eCreator: " + c.getOwner());
					p.sendMessage(
							"§9Officers: " + SQLArray.convertToStringView(c.getOfficers()).replaceAll(",", ", "));
					p.sendMessage("§7Members: " + SQLArray.convertToStringView(c.getMembers()).replaceAll(",", ", "));


				}
				else if (strings[0].equals("shop")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to do this.");
						return true;
					}
					if (pl.gameMode) {
						p.sendMessage(
								"§9Clans> §7This command is disabled in game servers. Use it in the anni lobby!");
						return true;
					}
					String clan = ClansDatabase.getInstance().getClan(p.getName());
					if (!ClansDatabase.getInstance().isOfficer(clan, p.getName())) {
						p.sendMessage("§9Clans> §7You have to be the rank §aOFFICER §7or higher to do this.");
						return true;
					}

					ShopMenu.open(p, clan);
				}
				else if (strings[0].equals("chat")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to do this.");
						return true;
					}
					if (chatMode.contains(p)) {
						p.sendMessage("§9Clans> §7Disabled channel §eCLANÂ§7. You are now chatting in §ePUBLIC");
						chatMode.remove(p);
					}
					else {
						p.sendMessage("§9Clans> §7Enabled channel §eCLAN");
						chatMode.add(p);
					}

					return true;
				}
				else if (strings[0].equals("leave")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to do this.");
						return true;
					}

					String clan = ClansDatabase.getInstance().getClan(p.getName());
					if (ClansDatabase.getInstance().getOwner(clan) == null) {
						ClansDatabase.getInstance().setClan(p.getName(), "null");
						p.sendMessage("§9Clans> §7You left the clan.");
						return true;
					}

					if (ClansDatabase.getInstance().getOwner(clan).toLowerCase().equalsIgnoreCase(p.getName().toLowerCase())) {
						p.sendMessage(
								"§9Clans> §7As the leader of the clan, you can't leave. Use §e/clan disband §7to disband your clan.");
						return true;
					}

					ClansDatabase.getInstance().leaveClan(clan, p.getName());
					p.sendMessage("§9Clans> §7You left the clan.");

					pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
							"§7The player §e" + p.getName() + "§7 has left the clan.");

				}
				else {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						helpMenu(p, false);
						return true;
					}
					helpMenu(p, true);
					// pl.getChatManager().sendChatMessageToClan(p.getName(),ClansDatabase.getInstance().getClan(p.getName()),getFinalArg(strings,0));


				}

			}
			else if (strings.length == 2) {

				if (strings[0].equals("join")) {
					String tojoin = strings[1];

					if (ClansDatabase.getInstance().getClanCoins(tojoin) == -1) {
						p.sendMessage("§9Clans> §7That clan doesn't exist");
						return true;
					}
					if (!ClansDatabase.getInstance().isInvited(p.getName(), tojoin)) {
						System.out.print(LocalClanManager.getClan(tojoin).toString());
						p.sendMessage("§9Clans> §7You are not invited to this clan!");
						return true;
					}
					if (ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You're already member of a clan.");
						return true;
					}

					ClansDatabase.getInstance().joinClan(tojoin, p);
					p.sendMessage("§9Clans> §7You are now part of the clan §e" + tojoin);

					pl.getChatManager().sendChatMessageToClan("SYSTEM", tojoin.toLowerCase(),
							"§7The player §e" + p.getName() + " §7joined the clan.");

				}
				else if (strings[0].equals("info")) {

					if (!ClansDatabase.getInstance().hasClan(strings[1])) {
						p.sendMessage("§9Clans> §7No clan found on your searchterm [§e" + strings[1] + "§7]");
						return true;
					}

					String clan = ClansDatabase.getInstance().getClan(strings[1]);
					Clan c = LocalClanManager.getClan(clan);

					if (c == null) {
						p.sendMessage("§9Clans> §7An error occurred. Please report this to the developers (code:18)");
						return true;
					}

					String tag;
					if (c.getTag().equals("null")) {
						tag = "§eNone";
					}
					else {
						tag = c.getTag();
					}
					p.sendMessage("§9Clans> §7Searching data for clan [§e" + ClansDatabase.getInstance().getRealName(clan)
							+ "§7]");
					p.sendMessage("§b===================================");
					p.sendMessage("§7Name: §e" + ClansDatabase.getInstance().getRealName(clan));

					p.sendMessage("§7Total members: §e" + ClansDatabase.getInstance().getTotalMembers(clan));
					p.sendMessage("§7Tag: " + tag);
					p.sendMessage("§7Slots used: §e" + ClansDatabase.getInstance().getTotalMembers(clan) + "§7/§e"
							+ ClansDatabase.getInstance().getSlots(clan));
					p.sendMessage("§dClan Points: " + c.getPoints());

					p.sendMessage("§e------------------------------");
					p.sendMessage("§eCreator: " + c.getOwner());
					p.sendMessage("§9Officers: " + SQLArray.convertToString(c.getOfficers()).replaceAll(",", ", "));
					p.sendMessage("§7Members: " + SQLArray.convertToString(c.getMembers()).replaceAll(",", ", "));

					p.sendMessage("§b===================================");



				}
				else if (strings[0].equals("invite")) {

					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to do this.");
						return true;
					}
					String clan = ClansDatabase.getInstance().getClan(p.getName());
					if (!ClansDatabase.getInstance().isOfficer(clan, p.getName())) {
						p.sendMessage("§9Clans> §7You have to be the rank §aOFFICER §7or higher to do this.");
						return true;
					}
					if (ClansDatabase.getInstance().isInvited(strings[1], clan)) {
						p.sendMessage("§9Clans> §7This player is already invited.");
						return true;
					}

					if (ClansDatabase.getInstance().hasClan(strings[1])) {
						p.sendMessage("§9Clans> §7That player is already in a clan");
						return true;
					}

					if (strings[1].length() > 16) {
						p.sendMessage("§9Clans> §7No player found for your searchterm [§e" + strings[1] + "§7]");
						return true;
					}

					if (ClansDatabase.getInstance().getSlots(clan) == ClansDatabase.getInstance().getTotalMembers(clan)) {
						p.sendMessage(
								"§9Clans> §7You cannot invite members anymore. You can buy more slots in the shop §e/clan shop");
						return true;
					}

					ClansDatabase.getInstance().invitePlayerToClan(clan, strings[1]);

					p.sendMessage("§9Clans> §7Invited player §e" + strings[1]
							+ "§7. They have to join with §e/clan join " + ClansDatabase.getInstance().getRealName(clan));

					pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
							"§7The player §e" + p.getName() + "§7 invited §e" + strings[1]);

					pl.getChatManager().sendMessage(strings[1], "claninvite_" + ClansDatabase.getInstance().getRealName(clan));


				}
				else if (strings[0].equals("chat")) {

					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to use this.");
						return true;
					}
					pl.getChatManager().sendChatMessageToClan(p.getName(),
							ClansDatabase.getInstance().getClan(p.getName()).toLowerCase(), strings[1]);
				}
				else if (strings[0].equals("promote")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to do this.");
						return true;
					}
					String clan = ClansDatabase.getInstance().getClan(p.getName());
					if (!ClansDatabase.getInstance().isOfficer(clan, p.getName())) {
						p.sendMessage("§9Clans> §7You have to be the rank §aOFFICER §7or higher to do this.");
						return true;
					}

					if (!ClansDatabase.getInstance().getMembersLS(clan).contains(strings[1].toLowerCase())) {
						p.sendMessage("§9Clans> §7That player isn't in your clan");
						return true;
					}

					ClansDatabase.getInstance().promote(clan, strings[1]);
					p.sendMessage("§9Clans> §7Promoted player §e" + strings[1] + "§7 to rank §aOFFICER");

					pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
							"§7The player " + strings[1] + " has been promoted to §aOFFICER");

				}
				else if (strings[0].equals("demote")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to do this.");
						return true;
					}
					String clan = ClansDatabase.getInstance().getClan(p.getName());
					if (!ClansDatabase.getInstance().isOfficer(clan, p.getName())) {
						p.sendMessage("§9Clans> §7You have to be the rank §aOFFICER §7or higher to do this.");
						return true;
					}

					if (!ClansDatabase.getInstance().getMembersLS(clan).contains(strings[1].toLowerCase())) {
						p.sendMessage("§9Clans> §7That player isn't in your clan");
						return true;
					}

					ClansDatabase.getInstance().promote(clan, strings[1]);
					p.sendMessage("§9Clans> §7Promoted player §e" + strings[1] + "§7 to rank §aOFFICER");

					pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
							"§7The player " + strings[1] + " has been promoted to §aOFFICER");


				}
				else if (strings[0].equals("kick")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to do this.");
						return true;
					}
					String clan = ClansDatabase.getInstance().getClan(p.getName());
					if (!ClansDatabase.getInstance().isOfficer(clan, p.getName())) {
						p.sendMessage("§9Clans> §7You have to be the rank §aOFFICER §7or higher to do this.");
						return true;
					}
					if (!ClansDatabase.getInstance().getMembersLS(clan).contains(strings[1].toLowerCase())
							&& !ClansDatabase.getInstance().getOfficersLS(clan).contains(strings[1].toLowerCase())
							&& !ClansDatabase.getInstance().getOwner(clan).toLowerCase().equals(strings[1].toLowerCase())) {
						p.sendMessage("§9Clans> §7This player isn't member of your clan.");
						return true;
					}
					if (ClansDatabase.getInstance().isOfficer(clan, strings[1])) {
						if (!ClansDatabase.getInstance().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
							p.sendMessage("§9Clans> §7You have to be the rank §eOWNERÂ§7 to kick officers.");
							return true;
						}
					}
					if (ClansDatabase.getInstance().getOwner(clan).toLowerCase().equalsIgnoreCase(strings[1].toLowerCase())) {
						p.sendMessage("§9Clans> §7You cannot kick the owner dumb ass!");
						return true;
					}

					ClansDatabase.getInstance().kickPlayer(clan, strings[1]);
					p.sendMessage("§9Clans> §7The player " + strings[1] + " has been kicked from your clan.");

					pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
							"§7The player " + strings[1] + " has been kicked.");

					pl.getChatManager().sendMessage(strings[1],
							"§4Â§l!! §eYou have been kicked from your clan §4Â§l!!");


				}
				else if (strings[0].equals("tag")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to use this.");
						return true;
					}
					if (pl.gameMode) {
						p.sendMessage(
								"§9Clans> §7This command is disabled in game servers. Use it in the anni lobby!");
						return true;
					}
					String clan = ClansDatabase.getInstance().getClan(p.getName());

					if (!ClansDatabase.getInstance().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
						p.sendMessage("§9Clans> §7You have to be the rank §eOWNERÂ§7 to purchase a tag.");
						return true;
					}

					if (ClansDatabase.getInstance().getClanCoins(clan) < 10000) {
						p.sendMessage(
								"§9Clans> §7You do not have enough Clan Coins to purchase this. \nÂ§9Clans> §7Price: §e10000 Clan Coins");
						return true;
					}


					for (String blocked : pl.blocked) {
						if (blocked.equalsIgnoreCase(strings[1])
								|| strings[1].toLowerCase().contains(blocked.toLowerCase())) {
							p.sendMessage("§9Clans> §7The word [§e" + strings[1] + "§7] is blocked");
							return true;
						}
					}

					if (strings[1].length() > 12) {
						p.sendMessage("§9Clans> §7The maximum characters for a tag is 12");
						return true;
					}

					if (strings[1].toLowerCase().contains("&".toLowerCase())) {
						p.sendMessage("§9Clans> §7You can't use colors in your tag");
						return true;
					}

					p.sendMessage("§9Clans> §7Tag purchase");
					p.sendMessage("");
					p.sendMessage("§9Clans> §7Tag: " + "§7[" + strings[1] + "§7]");
					p.sendMessage("§9Clans> §7Price: §e" + "10000");
					p.sendMessage("");
					p.sendMessage(
							"§9Clans> §eAre you sure? §7Type §eYES §7in the chat within 10 seconds to confirm");
					disband.put(p.getName(), "tag_" + strings[1] + "-" + "10000");
					Bukkit.getScheduler().scheduleSyncDelayedTask(Clans.getInstance(), new Runnable() {
						@Override
						public void run() {
							if (disband.containsKey(p.getName())) {
								disband.remove(p.getName());
								p.sendMessage("§9Clans> §7Time out. §eAborted purchase.");
							}
						}
					}, 20 * 10);

				}
				else if (strings[0].equals("transfer")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to use this.");
						return true;
					}
					if (pl.gameMode) {
						p.sendMessage(
								"§9Clans> §7This command is disabled in game servers. Use it in the anni lobby!");
						return true;
					}
					String clan = ClansDatabase.getInstance().getClan(p.getName());

					if (!ClansDatabase.getInstance().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
						p.sendMessage("§9Clans> §7You have to be the rank §eOWNERÂ§7 to transfer the leadership");
						return true;
					}

					if (!ClansDatabase.getInstance().getMembers(clan).contains(strings[1].toLowerCase())
							&& !ClansDatabase.getInstance().getOfficers(clan).contains(strings[1].toLowerCase())) {
						p.sendMessage("§9Clans> §7That player isn't in your clan");
						return true;
					}

					p.sendMessage("§9Clans> §7Clan Leadership Transfer");
					p.sendMessage("");
					p.sendMessage("§9Clans> §7Transfer to: " + "§7[" + strings[1] + "§7]");
					p.sendMessage("§9Clans> §7Price: §e" + "N/A");
					p.sendMessage("");
					p.sendMessage(
							"§9Clans> §eAre you sure? §7Type §eYES §7in the chat within 10 seconds to confirm");
					disband.put(p.getName(), "transfer_" + strings[1] + "-" + clan.toLowerCase());
					Bukkit.getScheduler().scheduleSyncDelayedTask(Clans.getInstance(), new Runnable() {
						@Override
						public void run() {
							if (disband.containsKey(p.getName())) {
								disband.remove(p.getName());
								p.sendMessage("§9Clans> §7Time out. §eAborted transfer.");
							}
						}
					}, 20 * 10);

				}
				else if (strings[0].equals("create")) {
					String create = strings[1];

					if (ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You're already member of a clan.");
						return true;
					}

					if (pl.gameMode) {
						p.sendMessage(
								"§9Clans> §7This command is disabled in game servers. Use it in the anni lobby!");
						return true;
					}


					if (ExperienceManager.getInstance().getXP(p) < 5000) {
						p.sendMessage("§9Clans> §7You do not have enough XP to purchase this. §eNEEDED: 5000XP");
						return true;
					}


					p.sendMessage("§9Clans> §7Clan create");
					p.sendMessage("");
					p.sendMessage("§9Clans> §7Name: §e" + create);
					p.sendMessage("§9Clans> §7Price: §b" + "5000XP");
					p.sendMessage("");
					p.sendMessage(
							"§9Clans> §eAre you sure? §7Type §eYES §7in the chat within 10 seconds to confirm");
					p.sendMessage("");
					disband.put(p.getName(), "create_" + strings[1] + "-" + "5000");
					Bukkit.getScheduler().scheduleSyncDelayedTask(Clans.getInstance(), new Runnable() {
						@Override
						public void run() {
							if (disband.containsKey(p.getName())) {
								disband.remove(p.getName());
								p.sendMessage("§9Clans> §7Time out. §eAborted purchase.");
							}
						}
					}, 20 * 10);


				}
				else {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to use this.");
						return true;
					}
					// pl.getChatManager().sendChatMessageToClan(p.getName(),ClansDatabase.getInstance().getClan(p.getName()),getFinalArg(strings,0));
				}
			}
			else {
				if (!ClansDatabase.getInstance().hasClan(p.getName())) {
					helpMenu(p, false);
					return true;
				}

				if (strings[0].equalsIgnoreCase("chat")) {
					if (!ClansDatabase.getInstance().hasClan(p.getName())) {
						p.sendMessage("§9Clans> §7You have to be member of a clan to use this.");
						return true;
					}
					pl.getChatManager().sendChatMessageToClan(p.getName(),
							ClansDatabase.getInstance().getClan(p.getName()).toLowerCase(), getFinalArg(strings, 1));

					return true;


				}
				else if (strings[0].equalsIgnoreCase("motd")) {
					String clan = ClansDatabase.getInstance().getClan(p.getName());
					if (pl.gameMode) {
						p.sendMessage(
								"§9Clans> §7This command is disabled in game servers. Use it in the anni lobby!");
						return true;
					}

					if (ClansDatabase.getInstance().getMOTD(clan).equals("null")) {
						if (ClansDatabase.getInstance().getClanCoins(clan) < 5000) {
							p.sendMessage(
									"§9Clans> §7You do not have enough Clan Coins to purchase this. \nÂ§9Clans> §7Price: §e5000 Clan Coins");
							return true;
						}
						p.sendMessage("§9Clans> §7MOTD purchase");
						p.sendMessage("");
						p.sendMessage("§9Clans> §7MOTD: " + "§e" + getFinalArg(strings, 1) + "");
						p.sendMessage("§9Clans> §7Price: §e" + "5000");
						p.sendMessage("");
						p.sendMessage(
								"§9Clans> §eAre you sure? §7Type §eYES §7in the chat within 10 seconds to confirm");
						disband.put(p.getName(), "motd_" + getFinalArg(strings, 1) + "-" + "5000");
						Bukkit.getScheduler().scheduleSyncDelayedTask(Clans.getInstance(), new Runnable() {
							@Override
							public void run() {
								if (disband.containsKey(p.getName())) {
									disband.remove(p.getName());
									p.sendMessage("§9Clans> §7Time out. §eAborted purchase.");
								}
							}
						}, 20 * 10);
					}
					else {
						p.sendMessage("§9Clans> §7MOTD update");
						p.sendMessage("");
						p.sendMessage("§9Clans> §7MOTD: " + "§e" + getFinalArg(strings, 1) + "");
						p.sendMessage("§9Clans> §7Price: §e" + "Free");
						p.sendMessage("");
						p.sendMessage(
								"§9Clans> §eAre you sure? §7Type §eYES §7in the chat within 10 seconds to confirm");
						disband.put(p.getName(), "motd_" + getFinalArg(strings, 1) + "-" + "0");
						Bukkit.getScheduler().scheduleSyncDelayedTask(Clans.getInstance(), new Runnable() {
							@Override
							public void run() {
								if (disband.containsKey(p.getName())) {
									disband.remove(p.getName());
									p.sendMessage("§9Clans> §7Time out. §eAborted purchase.");
								}
							}
						}, 20 * 10);
					}
				}
				else {

					helpMenu(p, true);
					// pl.getChatManager().sendChatMessageToClan(p.getName(),
					// ClansDatabase.getInstance().getClan(p.getName()), getFinalArg(strings, 0));
				}
			}
		}

		return false;
	}


	private String getFinalArg(final String[] args, final int start) {
		final StringBuilder bldr = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			if (i != start) {
				bldr.append(" ");
			}
			bldr.append(args[i]);
		}
		return bldr.toString();
	}

	public void helpMenu(Player p, boolean hasclan) {
		if (hasclan) {
			p.sendMessage("§eÂ§lClans Help");
			p.sendMessage("");
			p.sendMessage("§e/clan §7- Display clan information");
			p.sendMessage("§e/clan members §7- View the members of your clan");
			p.sendMessage("§e/clan leave §7- Leave your current clan");
			p.sendMessage("§e/clan disband §7- Disband your current clan");
			p.sendMessage("§e/clan shop §7- Open the Clan shop");
			p.sendMessage("§e/clan chat §7- Enable chat mode channel CLAN");
			p.sendMessage("");
			p.sendMessage("§e/clan join <clan> §7- Join a clan (you must be invited)");
			p.sendMessage("§e/clan invite <player> §7- Invite a player to your clan");
			p.sendMessage("§e/clan kick <player> §7- Kick a player from your clan");
			p.sendMessage("§e/clan promote <player> §7- Promote a player in your clan");
			p.sendMessage("§e/clan info <player> §7- View a player's clan information");
			p.sendMessage("§e/clan transfer <player> §7- Transfer your clan's leadership");
			p.sendMessage("§e/clan create <name> §7- Create a clan");
		}
		else {
			p.sendMessage("§eÂ§lClans help");
			p.sendMessage("");
			p.sendMessage("§e/clan §7- Display clan information");
			p.sendMessage("");
			p.sendMessage("§e/clan join <clan> §7- Join a clan (you must be invited)");
			p.sendMessage("§e/clan info <player> §7- View a player's clan information");
			p.sendMessage("§e/clan create <name> §7- Create a clan");



		}



	}
}
