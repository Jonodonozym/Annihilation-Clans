package org.guildcraft.annihilation.clans.inv;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.guildcraft.annihilation.clans.Clan;
import org.guildcraft.annihilation.clans.ClanLevel;
import org.guildcraft.annihilation.clans.manager.ClansDatabase;
import org.guildcraft.annihilation.clans.util.ItemUtil;

/**
 * Created by Arjenpro on 11/01/2017.
 */
public class ShopMenu {
	public static ClansDatabase cm = ClansDatabase.getInstance();

	public static void open(Player p, Clan clan) {
		Inventory inv = Bukkit.createInventory(null, 54, "Clan Shop");

		int currentSlots = clan.getSlots();
		ClanLevel nextLevel = ClanLevel.getFromCurrentSlots(currentSlots);

		ItemStack item;
		if (nextLevel == null) {
			item = ItemUtil.getItem(Material.CHEST, 5, "§9Fully upgraded!", "§7You upgraded your slots", "§7to §eMAX",
					"", "§7Price: §eN/A", "§cFully upgraded!");
		}
		else {
			int price = nextLevel.getPrice();
			int slots = nextLevel.getSlots();
			String levelName = nextLevel.name();
			item = ItemUtil.getItem(Material.CHEST, 1, "§9Level " + levelName + " - " + slots + " members",
					"§7Upgrade your slots to", "§7level §e" + levelName, "",
					"§9Max members on level " + levelName + ": " + slots, "", "§7Price: §e" + price,
					"§aClick to upgrade to level " + levelName);
		}
		inv.setItem(13, item);

		if (!clan.hasTag()) {
			inv.setItem(29, ItemUtil.getItem(Material.NAME_TAG, 1, "§aClan Tag", "§7Click to buy a §aClan Tag",
					"§7Others players will see", "§7a tag in tab and as a prefix", "", "§7Price: §e10000",
					"§aClick to buy a tag!", "", "§cWarning: you cannot change your tag", "§cafter you created it."));
		}
		else {
			inv.setItem(29,
					ItemUtil.getItem(Material.NAME_TAG, 1, "§aClan Tag: §7" + clan.getTag(),
							"§7You already own a clan tag", "§7Others players will see",
							"§7a tag in tab and as a prefix", "", "§7Price: §eN/A", "§cYou already have a clan tag", "",
							"§cWarning: you cannot change your tag", "§cafter you created it."));
		}

		if (!clan.hasMOTD()) {
			inv.setItem(33, ItemUtil.getItem(Material.BOOK_AND_QUILL, 1, "§eClan MOTD", "§7Click to buy a §eClan MOTD",
					"§7Your clan members will see", "§7a message of the day when they join", "", "§7Price: §e1000",
					"§aClick to buy an MOTD!", "", "§cWarning: you can change", "your MOTD every day."));

		}
		else {
			inv.setItem(33, ItemUtil.getItem(Material.BOOK_AND_QUILL, 1, "§eClan MOTD: §7" + clan.getMotd(),
					"§7Click to buy a §eClan MOTD", "§7Your clan members will see",
					"§7a message of the day when they join", "", "§7Price: §eFree", "§cYou already have a clan tag", "",
					"§cWarning: you can change", "your MOTD ONCE per day."));
		}

		ItemStack exit = ItemUtil.getItem(Material.IRON_DOOR, 1, "§cClose");
		ItemStack info = ItemUtil.getItem(Material.FEATHER, 1, "§e" + clan.getName() + " Clan", "§8Clan information",
				"", "§7Clan Coins: §9" + clan.getCoins());
		ItemStack alpha = ItemUtil.getItem(Material.PAPER, 1, "§5Annihilation Clans ALPHA", "§7Bugs can occur",
				"§7in this state of Clans", "", "§8------------", "§eClans §7v0.2 ALPHA", "§cReport bugs at the forums",
				"§7Made for Annihilation");

		inv.setItem(48, alpha);
		inv.setItem(49, info);
		inv.setItem(50, exit);

		p.openInventory(inv);
	}
}
