package org.guildcraft.annihilation.clans.inv;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.manager.ClansManager;
import org.guildcraft.annihilation.clans.util.ItemUtil;

/**
 * Created by Arjenpro on 11/01/2017.
 */
public class ShopMenu {

    public static void open(Player p, String clan) {
        Inventory inv = Bukkit.createInventory(null, 54, "Clan Shop");

        // slots
        ClansManager cm = Clans.getInstance().getClansManager();
        int currentSlots = cm.getSlots(clan);
        int price;
        int nextSlots;
        String update;

        if (currentSlots == 5) {
            price = 500;
            nextSlots = 8;
            update = "I";
            setMemberItem(inv, 1, update, nextSlots, price);
        } else if (currentSlots == 8) {
            price = 1000;
            nextSlots = 11;
            update = "II";
            setMemberItem(inv, 2, update, nextSlots, price);
        } else if (currentSlots == 11) {
            price = 2000;
            nextSlots = 14;
            update = "III";
            setMemberItem(inv, 3, update, nextSlots, price);
        } else if (currentSlots == 14) {
            price = 3500;
            nextSlots = 17;
            update = "IV";
            setMemberItem(inv, 4, update, nextSlots, price);
        } else if (currentSlots == 17) {
            price = 5000;
            nextSlots = 20;
            update = "V";
            setMemberItem(inv, 5, update, nextSlots, price);
        } else if (currentSlots == 20) {
            inv.setItem(13, ItemUtil.getItem(Material.CHEST, 5, new String[]{
                    ChatColor.GRAY + "You upgraded your slots",
                    ChatColor.GRAY + "to " + ChatColor.YELLOW + "MAX",
                    "",
                    ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "N/A",
                    ChatColor.RED + "Fully upgraded!"}, ChatColor.BLUE + "Fully upgraded!"));
        } else {
            inv.setItem(13,
                    ItemUtil.getItem(Material.CHEST, 0,
                            new String[]{ChatColor.RED + "Error while getting your",
                                    ChatColor.RED + "slots. Please report this to the developers",
                                    "",
                                    "Error Details:",
                                    "Error: error_clans_319"},
                            ChatColor.BLUE + "Error!"));
        }

        // tag
        if (cm.getTag(clan).equals("null")) {
            inv.setItem(29, ItemUtil.getItem(Material.NAME_TAG, 1,
                    new String[]{ChatColor.GRAY + "Click to buy a " + ChatColor.GREEN + "Clan Tag",
                            ChatColor.GRAY + "Others players will see",
                            ChatColor.GRAY + "a tag in tab and as a prefix",
                            "",
                            ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "10000",
                            ChatColor.GREEN + "Click to buy a tag!",
                            "",
                            ChatColor.RED + "Warning: you cannot change your tag",
                            ChatColor.RED + "after you created it."},
                    ChatColor.GREEN + "Clan Tag"));

        } else {
            inv.setItem(29, ItemUtil.getItem(Material.NAME_TAG, 1,
                    new String[]{ChatColor.GRAY + "You already own a clan tag",
                            ChatColor.GRAY + "Others players will see",
                            ChatColor.GRAY + "a tag in tab and as a prefix",
                            "",
                            ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "N/A",
                            ChatColor.RED + "You already have a clan tag",
                            "",
                            ChatColor.RED + "Warning: you cannot change your tag",
                            ChatColor.RED + "after you created it."},
                    ChatColor.GREEN + "Clan Tag: " + ChatColor.GRAY
                            + cm.getTag(clan)));
        }

        // motd

        if (cm.getMOTD(clan).equals("null")) {
            inv.setItem(33, ItemUtil.getItem(Material.BOOK_AND_QUILL, 1,
                    new String[]{ChatColor.GRAY + "Click to buy a " + ChatColor.YELLOW + "Clan MOTD",
                            ChatColor.GRAY + "Your clan members will see",
                            ChatColor.GRAY + "a message of the day when they join",
                            "",
                            ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "1000",
                            ChatColor.GREEN + "Click to buy an MOTD!",
                            "",
                            ChatColor.RED + "Warning: you can change",
                            ChatColor.RED + "your MOTD every day."},
                    ChatColor.YELLOW + "Clan MOTD"));

        } else {
            inv.setItem(33,
                    ItemUtil.getItem(Material.BOOK_AND_QUILL, 1,
                            new String[]{ChatColor.GRAY + "Click to buy a " + ChatColor.YELLOW + "Clan MOTD",
                                    ChatColor.GRAY + " clan members will see",
                                    ChatColor.GRAY + "a message of the day when they join",
                                    "",
                                    ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "N/A",
                                    ChatColor.RED + "You already have a clan tag",
                                    "",
                                    ChatColor.RED + "Warning: you can change",
                                    ChatColor.RED + "your MOTD every day."},
                            ChatColor.YELLOW + "Clan MOTD: " + ChatColor.GRAY + "Click to see"));

        }

        // info
        ItemStack exit = ItemUtil.getItem(Material.IRON_DOOR, 1, new String[]{
                ChatColor.GRAY + "Exit the shop"}, ChatColor.RED + "Close");
        ItemStack info = ItemUtil.getItem(Material.FEATHER, 1,
                new String[]{ChatColor.DARK_GRAY + "Clan information", "",
                        ChatColor.GRAY + "Clan Coins: " + ChatColor.BLUE + cm.getClanCoins(clan)},
                ChatColor.YELLOW + cm.getRealName(clan) + " Clan");
        ItemStack alpha = ItemUtil.getItem(Material.PAPER, 1,
                new String[]{ChatColor.GRAY + "Bugs can occur",
                        ChatColor.GRAY + "in this state of Clans", "", ChatColor.DARK_GRAY + "------------",
                        ChatColor.YELLOW + "Clans " + ChatColor.GRAY + "v0.1 ALPHA",
                        ChatColor.RED + "Report bugs at the forums",
                        ChatColor.GRAY + "Made for Annihilation "},
                ChatColor.DARK_PURPLE + "Annihilation Clans ALPHA");

        inv.setItem(48, alpha);
        inv.setItem(49, info);
        inv.setItem(50, exit);

        p.openInventory(inv);
    }

    private static void setMemberItem(Inventory inv, int amount, String update, int nextSlots, int price) {
        inv.setItem(13,
                ItemUtil.getItem(Material.CHEST, amount,
                        new String[]{ChatColor.GRAY + "Upgrade your slots to", ChatColor.GRAY + "level "
                                + ChatColor.YELLOW + update, "",
                                ChatColor.BLUE + "Max members on level " + update + ": " + nextSlots, "",
                                ChatColor.GRAY + "Price: " + ChatColor.YELLOW + price,
                                ChatColor.GREEN + "Click to upgrade to level " + update},
                        ChatColor.BLUE + "Level " + update + " - " + nextSlots + " members"));
    }
}
