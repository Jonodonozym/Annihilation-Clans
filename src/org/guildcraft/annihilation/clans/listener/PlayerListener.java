package org.guildcraft.annihilation.clans.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
                        .sendMessage(ChatColor.BLUE + "Clan Info> "
                                + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                                plugin.getLocalClanManager().getLocalData(plugin.getClansManager()
                                        .getClan(e.getPlayer().getName())).getMotd()));
        }, 20 * 4);

        plugin.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                ChatColor.YELLOW + "The player " + e.getPlayer().getName() + " joined");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!e.getMessage().toLowerCase().equals("yes")) return;

        Player p = e.getPlayer();
        e.setCancelled(true);

        if (ClanCommand.disband.containsKey(p.getName())) {
            String to = ClanCommand.disband.get(p.getName());
            String toDisband = to.split("_")[0];
            String toDisband2 = to.split("_")[1];

            ClanCommand.disband.remove(p.getName());

            if (toDisband.equals("disband")) {
                p.sendMessage("§9Clans> §7Removing all members from your clan.. §eProgress: 0%");
                plugin.getClansManager().disbandClan(toDisband2);
                p.sendMessage("§9Clans> §7Disbanded your clan");
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

                p.sendMessage(ChatColor.BLUE + "Clans> "
                        + ChatColor.GRAY + "Removed " + ChatColor.YELLOW + price
                        + ChatColor.GRAY + " Clan Coins from your Clan Wallet");
                p.sendMessage(ChatColor.BLUE + "Clans> "
                        + ChatColor.GRAY + "Purchase complete. Your tag has been updated to " +
                        ChatColor.YELLOW + tag);
                Clans.log(p.getName() + " bought clan tag " + tag + " for " + price + " coins");
                return;
            }

            if (toDisband.equals("motd")) {
                String motd = toDisband2.split("-")[0];
                int price = Integer.parseInt(toDisband2.split("-")[1]);

                String clan = plugin.getClansManager().getClan(p.getName());
                plugin.getClansManager().removeClanCoins(price, clan);
                plugin.getClansManager().setMOTD(clan, motd);

                p.sendMessage(ChatColor.BLUE + "Clans> "
                        + ChatColor.GRAY + "Removed " + ChatColor.YELLOW + price +
                        ChatColor.GRAY + " Clan Coins from your Clan Wallet");
                p.sendMessage(ChatColor.BLUE + "Clans> " +
                        ChatColor.GRAY + "Purchase complete. Your MOTD has been updated to " + ChatColor.YELLOW + motd);

                if (price > 0)
                    Clans.log(p.getName() + " bought clan motd " + motd + " for " + price + " coins");
                return;
            }

            if (toDisband.equals("transfer")) {
                String to2 = toDisband2.split("-")[0];
                String clan = toDisband2.split("-")[1];

                p.sendMessage(ChatColor.BLUE + "Clans> " +
                        ChatColor.GRAY + "Transferring your clan to the player " +
                        ChatColor.YELLOW + to2);
                plugin.getClansManager().transfer(clan, to2);
                p.sendMessage(ChatColor.BLUE + "Clans> " +
                        ChatColor.GRAY + "Your clan has been transferred. The player " +
                        ChatColor.YELLOW + to2 + " has the leadership now.");
                p.sendMessage(ChatColor.BLUE + "Clans> " +
                        ChatColor.GRAY + "You are now in the " + ChatColor.YELLOW + "MEMBER " +
                        ChatColor.GRAY + "group of your clan");

                plugin.getChatManager().sendChatMessageToClan("SYSTEM", clan,
                        ChatColor.GRAY + "The leadership went from "
                                + ChatColor.YELLOW + p.getName() + ChatColor.GRAY + " to "
                                + ChatColor.YELLOW + to2);
                plugin.getChatManager().sendMessage(to2,
                        ChatColor.BLUE + "Clans> " +
                                ChatColor.GRAY + "You are now the owner of the clan "
                                + ChatColor.YELLOW + clan);
                return;
            }

            if (toDisband.equals("create")) {
                String name = toDisband2.split("-")[0];
                int price = Integer.parseInt(toDisband2.split("-")[1]);

                plugin.getClansManager().createClan(name, p);
                p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GREEN + "Created clan "
                        + ChatColor.GRAY + name + ChatColor.GREEN + ". Invite members with "
                        + ChatColor.GRAY + "/clan invite <player>");
                p.sendMessage(ChatColor.BLUE +
                        "Clans> " + ChatColor.GREEN + "You are moved in the group " +
                        ChatColor.YELLOW + "" + ChatColor.BOLD + "OWNER " +
                        ChatColor.GREEN + "of the clan " + ChatColor.GRAY + name);

                ExperienceManager.getInstance().removeXP(p, price);
                p.sendMessage(ChatColor.BLUE + "Clans> " +
                        ChatColor.GRAY + "Removed " + ChatColor.AQUA + price + "XP " +
                        ChatColor.GRAY + "from your Annihilation Wallet");

                Clans.log(p.getName() + " created clan " + name + " for " + price + " XP");
                return;
            }
        }

        if (InventoryListener.getSlots().containsKey(p.getName())) {
            int newslots = Integer.parseInt(InventoryListener.getSlots().get(p.getName()).split("-")[0]);
            int price = Integer.parseInt(InventoryListener.getSlots().get(p.getName()).split("-")[1]);

            String clan = plugin.getClansManager().getClan(p.getName());
            plugin.getClansManager().removeClanCoins(price, clan);
            plugin.getClansManager().setSlots(newslots, clan);

            p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY
                    + "Removed " + ChatColor.YELLOW + price + ChatColor.GRAY + " Clan Coins from your Clan Wallet");
            p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY +
                    "Purchase complete. Your slots has been updated to " + ChatColor.YELLOW + newslots);

            InventoryListener.getSlots().remove(p.getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!plugin.getClansManager().hasClan(e.getPlayer().getName())) return;

        String clan = plugin.getClansManager().getClan(e.getPlayer().getName());
        List<Player> local = plugin.getLocalClanManager().online.get(clan);
        local.remove(e.getPlayer());
        plugin.getLocalClanManager().online.put(clan, local);

        ClanCommand.chatMode.remove(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (!plugin.getClansManager().hasClan(e.getPlayer().getName())) return;

        String clan = plugin.getClansManager().getClan(e.getPlayer().getName());
        List<Player> local = plugin.getLocalClanManager().online.get(clan);
        local.remove(e.getPlayer());
        plugin.getLocalClanManager().online.put(clan, local);

        ClanCommand.chatMode.remove(e.getPlayer());
    }
}
