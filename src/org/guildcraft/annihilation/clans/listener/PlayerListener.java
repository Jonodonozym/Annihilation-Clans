package org.guildcraft.annihilation.clans.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.guildcraft.annihilation.clans.Clan;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.command.ClanCommand;
import org.guildcraft.annihilation.clans.manager.ClansDatabase;
import org.guildcraft.annihilation.gcStatsHook.ExperienceManager;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class PlayerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(Clans.getInstance(), () -> {
			final Clan clan = ClansDatabase.getInstance().getClan(e.getPlayer());
			if (clan == null)
				return;

			clan.getOnline().add(e.getPlayer());
			Bukkit.getScheduler().runTaskLater(Clans.getInstance(), () -> {
				e.getPlayer().sendMessage("§9Clan Info> §e " + clan.getMotd().replaceAll("&", "§"));
			}, 20 * 4);

			Clans.getInstance().getChatManager().sendChatMessageToClan("SYSTEM", clan,
					"§eThe player " + e.getPlayer().getName() + " joined");
		});
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();

		if (ClanCommand.disband.containsKey(player)) {
			Clan clan = ClansDatabase.getInstance().getClan(player);
			if (e.getMessage().toLowerCase().equals("yes")) {
				String commandLabel = ClanCommand.disband.get(player).split("_")[0];

				if (commandLabel.equals("disband")) {
					clan.disband();
					player.sendMessage("§9Clans> §7Disbanded your clan");
				}
				else if (commandLabel.equals("tag")) {
					String tag = ClanCommand.disband.get(player).split("_")[1].split("-")[0];
					int price = Integer.parseInt(ClanCommand.disband.get(player).split("_")[1].split("-")[1]);

					clan.setCoins(clan.getCoins() - price);
					clan.setTag(tag);
					clan.save();

					player.sendMessage("§9Clans> §7Removed §e" + price + " §7Clan Coins from your Clan Wallet");
					player.sendMessage("§9Clans> §7PYour tag has been updated to §e" + tag);
					Clans.log(player.getName() + " bought clan tag " + tag + " for " + price + " coins");
				}
				else if (commandLabel.equals("motd")) {
					String motd = ClanCommand.disband.get(player).split("_")[1].split("-")[0];
					int price = Integer.parseInt(ClanCommand.disband.get(player).split("_")[1].split("-")[1]);

					clan.setCoins(clan.getCoins() - price);
					clan.setMotd(motd);
					clan.save();

					if (price > 0) {
						player.sendMessage("§9Clans> §7Removed §e" + price + " §7Clan Coins from your Clan Wallet");
						Clans.log(player.getName() + " bought clan motd " + motd + " for " + price + " coins");
					}
					player.sendMessage("§9Clans> §7Purchase complete. Your MOTD has been updated to §e" + motd);
				}
				else if (commandLabel.equals("transfer")) {
					String to = ClanCommand.disband.get(player).split("_")[1].split("-")[0];
					clan.transfer(to);

					player.sendMessage("§9Clans> §7Transferring your clan to the player §e" + to);
					player.sendMessage("§9Clans> §7You are now in the §eMEMBER §7group of your clan");

					Clans.getInstance().getChatManager().sendChatMessageToClan("SYSTEM", clan,
							"§7The leadership went from §e" + player.getName() + "§7 to §e" + to);
					Clans.getInstance().getChatManager().sendMessage(to,
							"§9Clans> §7You are now the owner of the clan §e" + clan);
				}
				else if (commandLabel.equals("create")) {
					String name = ClanCommand.disband.get(player).split("_")[1].split("-")[0];
					int price = Integer.parseInt(ClanCommand.disband.get(player).split("_")[1].split("-")[1]);

					ClansDatabase.getInstance().createClan(name, player);
					player.sendMessage(
							"§9Clans> §aCreated clan §7" + name + "§a. Invite members with §7/clan invite <player>");
					player.sendMessage(
							"§9Clans> §aYou are moved in the group §eÂ§lOWNER §aof the clan §7" + name + "§a");

					player.sendMessage("§9Clans> §7Removed §b" + price + "XP §7from your Annihilation Wallet");

					ExperienceManager.getInstance().removeXP(player, price);

					Clans.log(player.getName() + " created clan " + name + " for " + price + " XP");
				}

				ClanCommand.disband.remove(player);
				e.setCancelled(true);
			}
		}

		else if (InventoryListener.slotsMap.containsKey(e.getPlayer().getName())) {
			if (e.getMessage().toLowerCase().equals("yes")) {
				Clan clan = ClansDatabase.getInstance().getClan(player);

				int newslots = Integer.parseInt(InventoryListener.slotsMap.get(e.getPlayer().getName()).split("-")[0]);
				int price = Integer.parseInt(InventoryListener.slotsMap.get(e.getPlayer().getName()).split("-")[1]);

				clan.setCoins(clan.getCoins() - price);
				clan.setSlots(newslots);
				clan.save();

				player.sendMessage("§9Clans> §7Removed §e" + price + " §7Clan Coins from your Clan Wallet");
				player.sendMessage("§9Clans> §7Purchase complete. Your slots has been updated to §e" + newslots);

				InventoryListener.slotsMap.remove(e.getPlayer().getName());
				e.setCancelled(true);
			}
		}

		if (e.getMessage().startsWith("@") || ClanCommand.chatMode.contains(e.getPlayer())) {
			e.setMessage(e.getMessage().replaceFirst("@+", ""));
			Clan clan = ClansDatabase.getInstance().getClan(player);
			if (clan == null) {
				ClanCommand.chatMode.remove(e.getPlayer());
				return;
			}

			Clans.getInstance().getChatManager().sendChatMessageToClan(e.getPlayer().getName(), clan, e.getMessage());

			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Clan clan = ClansDatabase.getInstance().getClan(e.getPlayer());
		if (clan != null)
			clan.getOnline().remove(e.getPlayer());

		ClanCommand.chatMode.remove(e.getPlayer());
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		Clan clan = ClansDatabase.getInstance().getClan(e.getPlayer());
		if (clan != null)
			clan.getOnline().remove(e.getPlayer());

		ClanCommand.chatMode.remove(e.getPlayer());
	}
}
