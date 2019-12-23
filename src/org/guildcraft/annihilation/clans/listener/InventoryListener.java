package org.guildcraft.annihilation.clans.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.manager.ClansManager;

import java.util.HashMap;

/**
 * Created by Arjenpro on 11/01/2017.
 */
public class InventoryListener implements Listener {

    public static HashMap<String, String> slots = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getInventory().getTitle().equals("Clan Shop")) return;

        if (e.getCurrentItem() == null)
            return;

        e.setCancelled(true);

        if (!Clans.getInstance().getClansManager().hasClan(e.getWhoClicked().getName())) {
            e.getWhoClicked().sendMessage(ChatColor.BLUE + "Clans> " +
                    ChatColor.GRAY + "Error occured while clicking, please report this. "
                    + ChatColor.YELLOW + "Error code: 581");
            e.getWhoClicked().closeInventory();
            return;
        }

        ClansManager cm = Clans.getInstance().getClansManager();
        String clan = cm.getClan(e.getWhoClicked().getName());

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType() != Material.CHEST
                || e.getCurrentItem().getType() != Material.NAME_TAG
                || e.getCurrentItem().getType() != Material.BOOK_AND_QUILL
                || e.getCurrentItem().getType() != Material.IRON_DOOR) return;

        if (e.getCurrentItem().getType() == Material.CHEST) {
            int currentSlots = cm.getSlots(clan);
            int price;
            int nextslots;
            String update;

            if (currentSlots == 5) {
                price = 500;
                nextslots = 8;
                update = "I";
            } else if (currentSlots == 8) {
                price = 1000;
                nextslots = 11;
                update = "II";
            } else if (currentSlots == 11) {
                price = 2000;
                nextslots = 14;
                update = "III";
            } else if (currentSlots == 14) {
                price = 3500;
                nextslots = 17;
                update = "IV";
            } else if (currentSlots == 17) {
                price = 5000;
                nextslots = 20;
                update = "V";
            } else if (currentSlots == 20) {
                p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Your clan slots are already max level");
                p.closeInventory();
                return;
            } else
                return;

            if (!(cm.getClanCoins(clan) >= price)) {
                p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY
                        + "Yo do not have enough " + ChatColor.YELLOW + "Clans Coins " + ChatColor.BLUE + "to unlock this");
                p.closeInventory();
                return;
            }

            p.sendMessage(ChatColor.BLUE + "§9Clans> " + ChatColor.GRAY + "Information about slots update "
                    + ChatColor.YELLOW + update);
            p.sendMessage("");
            p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Member slots available after update: "
                    + ChatColor.YELLOW + nextslots);
            p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Price: " + ChatColor.YELLOW + price);
            p.sendMessage("");
            p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.YELLOW + "Are you sure? " +
                    ChatColor.GRAY + "Type " + ChatColor.YELLOW + "YES "
                    + ChatColor.GRAY + "in the chat within 10 seconds to confirm");
            slots.put(p.getName(), nextslots + "-" + price);
            p.closeInventory();

            Bukkit.getScheduler().scheduleSyncDelayedTask(Clans.getInstance(), () -> {
                if (slots.containsKey(p.getName())) {
                    slots.remove(p.getName());
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Time out. §eAborted purchase.");
                }
            }, 20 * 10);
        } else if (e.getCurrentItem().getType() == Material.NAME_TAG) {
            if (cm.hasTag(clan)) {
                p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Your clan tag: "
                        + ChatColor.YELLOW + cm.getTag(clan));
                p.closeInventory();
            } else {
                p.sendMessage(ChatColor.BLUE + "Clans> "
                        + ChatColor.GRAY + "To setup your clan tag you need to execute command "
                        + ChatColor.YELLOW + "/clan tag <tag>");
                p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.RED
                        + "Warning. You cannot change your tag after you created it!");
                p.closeInventory();
            }
        } else if (e.getCurrentItem().getType() == Material.BOOK_AND_QUILL) {
            if (cm.hasMOTD(clan)) {
                p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Your clan MOTD: "
                        + ChatColor.YELLOW + cm.getMembers(clan));
                p.closeInventory();
            } else {
                p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY
                        + "To setup your clan MOTD you need to execute command " + ChatColor.YELLOW + "/clan MOTD <motd>");
                p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.RED + "You can change your MOTD every day");
                p.closeInventory();
            }
        } else if (e.getCurrentItem().getType() == Material.IRON_DOOR)
            p.closeInventory();
    }
}