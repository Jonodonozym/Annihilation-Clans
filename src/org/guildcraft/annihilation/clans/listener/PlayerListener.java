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
        Player p = e.getPlayer();
        /*
        TODO: CLEAN THE CRAP OUT OF THIS
         */

        if (ClanCommand.disband.containsKey(p.getName())) {
            if (e.getMessage().toLowerCase().equals("yes")) {
                if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("disband")) {
                    Player p = e.getPlayer();
                    String clan = ClanCommand.disband.get(p.getName()).split("_")[1];
                    p.sendMessage("§9Clans> §7Removing all members from your clan.. §eProgress: 0%");
                    plugin.getClansManager().disbandClan(clan);
                    p.sendMessage("§9Clans> §7Disbanded your clan");
                    ClanCommand.disband.remove(p.getName());
                    e.setCancelled(true);

                } else if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("tag")) {
                    System.out.print(ClanCommand.disband.get(e.getPlayer().getName()));
                    System.out.print((ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1]).split("-")[0]);
                    System.out.print((ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1]).split("-")[1]);

                    String tag = (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1]).split("-")[0];
                    String price = (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1]).split("-")[1];

                    System.out.print(tag);
                    System.out.print(price);


                    String clan = plugin.getClansManager().getClan(p.getName());
                    plugin.getClansManager().removeClanCoins(Integer.parseInt(price), clan);
                    plugin.getClansManager().setTag(clan, tag);

                    p.sendMessage("§9Clans> §7Removed §e" + price + " §7Clan Coins from your Clan Wallet");
                    p.sendMessage("§9Clans> §7Purchase complete. Your tag has been updated to §e" + tag);
                    Clans.log(p.getName() + " bought clan tag " + tag + " for " + price + " coins");

                    ClanCommand.disband.remove(p.getName());
                    e.setCancelled(true);
                } else if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("motd")) {

                    Player p = e.getPlayer();

                    String motd = ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[0];
                    int price = Integer
                            .parseInt(ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[1]);

                    String clan = plugin.getClansManager().getClan(p.getName());
                    plugin.getClansManager().removeClanCoins(price, clan);
                    plugin.getClansManager().setMOTD(clan, motd);

                    p.sendMessage("§9Clans> §7Removed §e" + price + " §7Clan Coins from your Clan Wallet");
                    p.sendMessage("§9Clans> §7Purchase complete. Your MOTD has been updated to §e" + motd);

                    if (price > 0) {
                        Clans.log(p.getName() + " bought clan motd " + motd + " for " + price + " coins");

                    }

                    ClanCommand.disband.remove(p.getName());
                    e.setCancelled(true);

                } else if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("transfer")) {
                    Player p = e.getPlayer();


                    String to = ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[0];
                    String clan = ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[1];


                    p.sendMessage("§9Clans> §7Transferring your clan to the player §e" + to);
                    p.sendMessage("§9Clans> §7Your clan has been transferred. The player §e" + to
                            + " has the leadership now.");
                    p.sendMessage("§9Clans> §7You are now in the §eMEMBER §7group of your clan");

                    plugin.getChatManager().sendChatMessageToClan("SYSTEM", clan,
                            "§7The leadership went from §e" + p.getName() + "§7 to §e" + to);
                    plugin.getChatManager().sendMessage(to,
                            "§9Clans> §7You are now the owner of the clan §e" + clan);


                    plugin.getClansManager().transfer(clan, to);

                    ClanCommand.disband.remove(p.getName());
                    e.setCancelled(true);


                } else if (ClanCommand.disband.get(e.getPlayer().getName()).split("_")[0].equals("create")) {

                    Player p = e.getPlayer();

                    String name = ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[0];
                    int price = Integer
                            .parseInt(ClanCommand.disband.get(e.getPlayer().getName()).split("_")[1].split("-")[1]);

                    plugin.getClansManager().createClan(name, p);
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
        } else if (InventoryListener.slots.containsKey(e.getPlayer().getName())) {
            if (e.getMessage().toLowerCase().equals("yes")) {

                Player p = e.getPlayer();

                int newslots = Integer.parseInt(InventoryListener.slots.get(e.getPlayer().getName()).split("-")[0]);
                int price = Integer.parseInt(InventoryListener.slots.get(e.getPlayer().getName()).split("-")[1]);

                String clan = plugin.getClansManager().getClan(p.getName());
                plugin.getClansManager().removeClanCoins(price, clan);
                plugin.getClansManager().setSlots(newslots, clan);

                p.sendMessage("§9Clans> §7Removed §e" + price + " §7Clan Coins from your Clan Wallet");
                p.sendMessage("§9Clans> §7Purchase complete. Your slots has been updated to §e" + newslots);

                InventoryListener.slots.remove(e.getPlayer().getName());
                e.setCancelled(true);
            }
        }

        if (ClanCommand.chatMode.contains(e.getPlayer())) {
            if (!plugin.getClansManager().hasClan(e.getPlayer().getName().toLowerCase())) {
                ClanCommand.chatMode.remove(e.getPlayer());
                return;
            }

            String clan = plugin.getClansManager().getClan(e.getPlayer().getName());
            plugin.getChatManager().sendChatMessageToClan(e.getPlayer().getName(), clan, e.getMessage());
            e.setCancelled(true);
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
