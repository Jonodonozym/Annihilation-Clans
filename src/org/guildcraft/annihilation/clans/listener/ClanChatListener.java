package org.guildcraft.annihilation.clans.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.guildcraft.annihilation.clans.Clans;

import java.util.List;

public class ClanChatListener implements Listener {

    private Clans plugin;

    public ClanChatListener(Clans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!plugin.getClansManager().hasClan(e.getPlayer().getName())) return;
        if (!plugin.getChatMode().contains(e.getPlayer())) return;

        Player p = e.getPlayer();
        String clan = plugin.getClansManager().getClan(p.getName());
        plugin.getChatManager().sendChatMessageToClan(p.getName(), clan, e.getMessage());
        e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!plugin.getClansManager().hasClan(e.getPlayer().getName())) return;

        String clan = plugin.getClansManager().getClan(e.getPlayer().getName());
        List<Player> local = plugin.getLocalClanManager().online.get(clan);
        local.remove(e.getPlayer());
        plugin.getLocalClanManager().online.put(clan, local);

        plugin.getChatMode().remove(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (!plugin.getClansManager().hasClan(e.getPlayer().getName())) return;

        String clan = plugin.getClansManager().getClan(e.getPlayer().getName());
        List<Player> local = plugin.getLocalClanManager().online.get(clan);
        local.remove(e.getPlayer());
        plugin.getLocalClanManager().online.put(clan, local);

        plugin.getChatMode().remove(e.getPlayer());
    }
}
