package org.guildcraft.annihilation.clans.listener;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.command.ClanCommand;
import org.guildcraft.annihilation.gcStatsHook.ExperienceManager;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class PlayerListener implements Listener {


	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		if (Clans.instance.getClansManager().hasClan(e.getPlayer().getName())) {
			String clan = Clans.instance.getClansManager().getClan(e.getPlayer().getName());

			if (!Clans.instance.getLocalClanManager().online.containsKey(clan)) {
				Clans.instance.getLocalClanManager().online.put(clan.toLowerCase(), new ArrayList<Player>());
			}
			ArrayList<Player> local = Clans.instance.getLocalClanManager().online.get(clan.toLowerCase());
			local.add(e.getPlayer());
			Clans.instance.getLocalClanManager().online.put(clan.toLowerCase(), local);
			if (Clans.instance.getLocalClanManager().getLocalData(clan) == null) {
				Clans.instance.getLocalClanManager().createLocalData(clan);
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(Clans.instance, new Runnable() {
				@Override
				public void run() {
					if (!Clans.instance.getLocalClanManager()
							.getLocalData(Clans.instance.getClansManager().getClan(e.getPlayer().getName())).getMotd()
							.equals("null"))
						e.getPlayer()
								.sendMessage("§9Clan Info> §e " + Clans.instance.getLocalClanManager()
										.getLocalData(Clans.instance.getClansManager().getClan(e.getPlayer().getName()))
										.getMotd().replaceAll("&", "§"));

				}
			}, 20 * 4);

			Clans.instance.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
					"§eThe player " + e.getPlayer().getName() + " joined");
		}

	}



	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {

		if (ClanCommand.disband.containsKey(e.getPlayer().getName())) {
			if (e.getMessage().toLowerCase().equals("yes")) {

				if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("disband")) {
					Player p = e.getPlayer();
					String clan = ClanCommand.disband.get(p.getName()).split("_")[1];
					p.sendMessage("§9Clans> §7Removing all members from your clan.. §eProgress: 0%");
					Clans.instance.getClansManager().disbandClan(clan);
					p.sendMessage("§9Clans> §7Disbanded your clan");
					ClanCommand.disband.remove(p.getName());
					e.setCancelled(true);

				}
				else if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("tag")) {

					Player p = e.getPlayer();
					System.out.print(ClanCommand.disband.get(e.getPlayer().getName()));
					System.out.print((ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1]).split("-")[0]);
					System.out.print((ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1]).split("-")[1]);

					String tag = (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1]).split("-")[0];
					String price = (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1]).split("-")[1];

					System.out.print(tag);
					System.out.print(price);


					String clan = Clans.instance.getClansManager().getClan(p.getName());
					Clans.instance.getClansManager().removeClanCoins(Integer.parseInt(price), clan);
					Clans.instance.getClansManager().setTag(clan, tag);

					p.sendMessage("§9Clans> §7Removed §e" + price + " §7Clan Coins from your Clan Wallet");
					p.sendMessage("§9Clans> §7Purchase complete. Your tag has been updated to §e" + tag);
					Clans.log(p.getName() + " bought clan tag " + tag + " for " + price + " coins");

					ClanCommand.disband.remove(p.getName());
					e.setCancelled(true);
				}
				else if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("motd")) {

					Player p = e.getPlayer();

					String motd = ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[0];
					int price = Integer
							.parseInt(ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[1]);

					String clan = Clans.instance.getClansManager().getClan(p.getName());
					Clans.instance.getClansManager().removeClanCoins(price, clan);
					Clans.instance.getClansManager().setMOTD(clan, motd);

					p.sendMessage("§9Clans> §7Removed §e" + price + " §7Clan Coins from your Clan Wallet");
					p.sendMessage("§9Clans> §7Purchase complete. Your MOTD has been updated to §e" + motd);

					if (price > 0) {
						Clans.log(p.getName() + " bought clan motd " + motd + " for " + price + " coins");

					}

					ClanCommand.disband.remove(p.getName());
					e.setCancelled(true);

				}
				else if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("transfer")) {
					Player p = e.getPlayer();


					String to = ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[0];
					String clan = ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[1];


					p.sendMessage("§9Clans> §7Transferring your clan to the player §e" + to);
					p.sendMessage("§9Clans> §7Your clan has been transferred. The player §e" + to
							+ " has the leadership now.");
					p.sendMessage("§9Clans> §7You are now in the §eMEMBER §7group of your clan");

					Clans.instance.getChatManager().sendChatMessageToClan("SYSTEM", clan,
							"§7The leadership went from §e" + p.getName() + "§7 to §e" + to);
					Clans.instance.getChatManager().sendMessage(to,
							"§9Clans> §7You are now the owner of the clan §e" + clan);


					Clans.instance.getClansManager().transfer(clan, to);

					ClanCommand.disband.remove(p.getName());
					e.setCancelled(true);


				}
				else if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("create")) {

					Player p = e.getPlayer();

					String name = ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[0];
					int price = Integer
							.parseInt(ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[1]);

					Clans.instance.getClansManager().createClan(name, p);
					p.sendMessage("§9Clans> §aCreated clan §7" + name
							+ "§a. Invite members with §7/clan invite <player>");
					p.sendMessage(
							"§9Clans> §aYou are moved in the group §e§lOWNER §aof the clan §7" + name + "§a");

					p.sendMessage("§9Clans> §7Removed §b" + price + "XP §7from your Annihilation Wallet");

					ExperienceManager.getInstance().removeXP(p, price);

					Clans.log(p.getName() + " created clan " + name + " for " + price + " XP");

					ClanCommand.disband.remove(p.getName());
					e.setCancelled(true);


				}
			}
		}
		else if (InventoryListener.slots.containsKey(e.getPlayer().getName())) {
			if (e.getMessage().toLowerCase().equals("yes")) {

				Player p = e.getPlayer();

				int newslots = Integer.parseInt(InventoryListener.slots.get(e.getPlayer().getName()).split("-")[0]);
				int price = Integer.parseInt(InventoryListener.slots.get(e.getPlayer().getName()).split("-")[1]);

				String clan = Clans.instance.getClansManager().getClan(p.getName());
				Clans.instance.getClansManager().removeClanCoins(price, clan);
				Clans.instance.getClansManager().setSlots(newslots, clan);

				p.sendMessage("§9Clans> §7Removed §e" + price + " §7Clan Coins from your Clan Wallet");
				p.sendMessage("§9Clans> §7Purchase complete. Your slots has been updated to §e" + newslots);

				InventoryListener.slots.remove(e.getPlayer().getName());
				e.setCancelled(true);

			}


		}



		if (ClanCommand.chatMode.contains(e.getPlayer())) {
			if (!Clans.instance.getClansManager().hasClan(e.getPlayer().getName().toLowerCase())) {
				ClanCommand.chatMode.remove(e.getPlayer());
				return;
			}
			String clan = Clans.instance.getClansManager().getClan(e.getPlayer().getName());

			Clans.instance.getChatManager().sendChatMessageToClan(e.getPlayer().getName(), clan, e.getMessage());

			e.setCancelled(true);
		}
	}


	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (Clans.instance.getClansManager().hasClan(e.getPlayer().getName())) {
			String clan = Clans.instance.getClansManager().getClan(e.getPlayer().getName());
			ArrayList<Player> local = Clans.instance.getLocalClanManager().online.get(clan);
			local.remove(e.getPlayer());
			Clans.instance.getLocalClanManager().online.put(clan, local);

			if (ClanCommand.chatMode.contains(e.getPlayer())) {
				ClanCommand.chatMode.remove(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		if (Clans.instance.getClansManager().hasClan(e.getPlayer().getName())) {
			String clan = Clans.instance.getClansManager().getClan(e.getPlayer().getName());
			ArrayList<Player> local = Clans.instance.getLocalClanManager().online.get(clan);
			local.remove(e.getPlayer());
			Clans.instance.getLocalClanManager().online.put(clan, local);

			if (ClanCommand.chatMode.contains(e.getPlayer())) {
				ClanCommand.chatMode.remove(e.getPlayer());
			}
		}
	}
}
