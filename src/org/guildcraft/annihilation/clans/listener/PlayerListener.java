package org.guildcraft.annihilation.clans.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.command.ClanCommand;
import org.guildcraft.annihilation.gcStatsHook.ExperienceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class PlayerListener implements Listener {

    private Clans plugin;

    public PlayerListener(Clans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!plugin.getClansManager().hasClan(e.getPlayer().getName())) return;

        String clan = plugin.getClansManager().getClan(e.getPlayer().getName());

        if (!plugin.getLocalClanManager().online.containsKey(clan))
            plugin.getLocalClanManager().online.put(clan.toLowerCase(), new ArrayList<>());

        List<Player> local = plugin.getLocalClanManager().online.get(clan.toLowerCase());
        local.add(e.getPlayer());
        plugin.getLocalClanManager().online.put(clan.toLowerCase(), local);

        if (plugin.getLocalClanManager().getLocalData(clan) == null)
            plugin.getLocalClanManager().createLocalData(clan);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!plugin.getLocalClanManager()
                    .getLocalData(plugin.getClansManager().getClan(e.getPlayer().getName())).getMotd()
                    .equals("null"))
                e.getPlayer()
                        .sendMessage(ChatColor.BLUE + "Clan MOTD> "
                                + ChatColor.DARK_AQUA + plugin.getLocalClanManager().getLocalData(plugin.getClansManager()
                                .getClan(e.getPlayer().getName())).getMotd());
        }, 20 * 4);

        plugin.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                ChatColor.YELLOW + "The player " + e.getPlayer().getName() + " joined");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        if (ClanCommand.disband.containsKey(p.getName())) {
            if (!e.getMessage().toLowerCase().equals("yes")) return;
            e.setCancelled(true);

            String to = ClanCommand.disband.get(p.getName());
            String toDisband = to.split("_")[0];
            String toDisband2 = to.split("_")[1];

            ClanCommand.disband.remove(p.getName());

            if (toDisband.equals("disband")) {
                plugin.sendMessage(p, "Removing all members from your clan...");
                plugin.getClansManager().disbandClan(toDisband2);
                plugin.sendMessage(p, "Disbanded your clan!");
                return;
            }

            if (toDisband.equals("tag")) {
                System.out.println("[AsyncPlayerChatEvent]: Tag[" + to + "\n"
                        + " Tag:" + toDisband2.split("-")[0] + " Price:" + toDisband2.split("-")[1] + "]");

                String tag = toDisband2.split("-")[0];
                String price = toDisband2.split("-")[1];

                System.out.print(tag);
                System.out.print(price);

                String clan = plugin.getClansManager().getClan(p.getName());
                plugin.getClansManager().removeClanCoins(Integer.parseInt(price), clan);
                plugin.getClansManager().setTag(clan, tag);

                plugin.sendMessage(p, "Removed &e" + price + " &7Clan Coins from your Clan Wallet");
                plugin.sendMessage(p, "Purchase complete. Your tag has been updated to &e" + tag);
                Clans.log(p.getName() + " bought clan tag " + tag + " for " + price + " coins");
                return;
            }

            if (toDisband.equals("motd")) {
                String motd = toDisband2.split("-")[0];
                int price = Integer.parseInt(toDisband2.split("-")[1]);

                String clan = plugin.getClansManager().getClan(p.getName());
                plugin.getClansManager().removeClanCoins(price, clan);
                plugin.getClansManager().setMOTD(clan, motd);

                plugin.sendMessage(p, "Removed &e" + price + " &7Clan Coins from your Clan Wallet");
                plugin.sendMessage(p, "Purchase complete. Your MOTD has been updated to &e" + motd);

                if (price > 0)
                    Clans.log(p.getName() + " bought clan motd " + motd + " for " + price + " coins");
                return;
            }

            if (toDisband.equals("transfer")) {
                String to2 = toDisband2.split("-")[0];
                String clan = toDisband2.split("-")[1];

                plugin.sendMessage(p, "Transferring your clan to the player &e" + to2);
                plugin.getClansManager().transfer(clan, to2);
                plugin.sendMessage(p, "Your clan has been transferred. The player &e" + to2
                        + " &7has the leadership now.");
                plugin.sendMessage(p, "You are now in the &eMEMBER &7group of your clan.");

                plugin.getChatManager().sendChatMessageToClan("SYSTEM", clan,
                        plugin.translate("&7The leadership went from &e" + p.getName() + " &7to &e" + to2));
                plugin.getChatManager().sendMessage(to2,
                        plugin.translate("&f[&bClans&f] &7You are now the owner of the clan &e" + clan));
                return;
            }

            if (toDisband.equals("create")) {
                String name = toDisband2.split("-")[0];
                int price = Integer.parseInt(toDisband2.split("-")[1]);

                plugin.getClansManager().createClan(name, p);
                plugin.sendMessage(p, "&aCreated clan &7" + name
                        + "&a. &7Invite players with &e/clan invite <player>");
                plugin.sendMessage(p, "&aYou are moved to the group &e&lOWNER &aof the clan &7" + name);
                ExperienceManager.getInstance().removeXP(p, price);
                plugin.sendMessage(p, "Removed &b" + price + "XP &7from your Annihilation Wallet.");
                Clans.log(p.getName() + " created clan " + name + " for " + price + " XP");
                return;
            }
        }

        if (InventoryListener.getSlots().containsKey(p.getName())) {
            if (!e.getMessage().toLowerCase().equals("yes")) return;

            e.setCancelled(true);
            int newslots = Integer.parseInt(InventoryListener.getSlots().get(p.getName()).split("-")[0]);
            int price = Integer.parseInt(InventoryListener.getSlots().get(p.getName()).split("-")[1]);

            String clan = plugin.getClansManager().getClan(p.getName());
            plugin.getClansManager().removeClanCoins(price, clan);
            plugin.getClansManager().setSlots(newslots, clan);

            plugin.sendMessage(p, "Removed &e" + price + " &7Clan Coins from your Clan Wallet");
            plugin.sendMessage(p, "Purchase complete. Your slots has been updated to &e" + newslots);
            InventoryListener.getSlots().remove(p.getName());
        }
    }
}
