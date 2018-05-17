package org.guildcraft.annihilation.clans.inv;

import org.bukkit.Bukkit;
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

	public static ClansManager cm = Clans.instance.getClansManager();

	public static void open(Player p, String clan) {
		Inventory inv = Bukkit.createInventory(null, 54, "Clan Shop");



		// slots
		int currentSlots = cm.getSlots(clan);
		int price;
		int nextslots;
		String update;



		if (currentSlots == 5) {
			price = 500;
			nextslots = 8;
			update = "I";
			inv.setItem(13,
					ItemUtil.getItem(Material.CHEST, 1,
							new String[] { "§7Upgrade your slots to", "§7level §e" + update, "",
									"§9Max members on level " + update + ": " + nextslots, "", "§7Price: §e" + price,
									"§aClick to upgrade to level " + update },
							"§9Level " + update + " - " + nextslots + " members"));

		}
		else if (currentSlots == 8) {
			price = 1000;
			nextslots = 11;
			update = "II";
			inv.setItem(13,
					ItemUtil.getItem(Material.CHEST, 2,
							new String[] { "§7Upgrade your slots to", "§7level §e" + update, "",
									"§9Max members on level " + update + ": " + nextslots, "", "§7Price: §e" + price,
									"§aClick to upgrade to level " + update },
							"§9Level " + update + " - " + nextslots + " members"));

		}
		else if (currentSlots == 11) {
			price = 2000;
			nextslots = 14;
			update = "III";
			inv.setItem(13,
					ItemUtil.getItem(Material.CHEST, 3,
							new String[] { "§7Upgrade your slots to", "§7level §e" + update, "",
									"§9Max members on level " + update + ": " + nextslots, "", "§7Price: §e" + price,
									"§aClick to upgrade to level " + update },
							"§9Level " + update + " - " + nextslots + " members"));

		}
		else if (currentSlots == 14) {
			price = 3500;
			nextslots = 17;
			update = "IV";
			inv.setItem(13,
					ItemUtil.getItem(Material.CHEST, 4,
							new String[] { "§7Upgrade your slots to", "§7level §e" + update, "",
									"§9Max members on level " + update + ": " + nextslots, "", "§7Price: §e" + price,
									"§aClick to upgrade to level " + update },
							"§9Level " + update + " - " + nextslots + " members"));

		}
		else if (currentSlots == 17) {
			price = 5000;
			nextslots = 20;
			update = "V";
			inv.setItem(15,
					ItemUtil.getItem(Material.CHEST, 5,
							new String[] { "§7Upgrade your slots to", "§7level §e" + update, "",
									"§9Max members on level " + update + ": " + nextslots, "", "§7Price: §e" + price,
									"§aClick to upgrade to level " + update },
							"§9Level " + update + " - " + nextslots + " members"));

		}
		else if (currentSlots == 20) {

			inv.setItem(13, ItemUtil.getItem(Material.CHEST, 5, new String[] { "§7You upgraded your slots",
					"§7to §eMAX", "", "§7Price: §eN/A", "§cFully upgraded!" }, "§9Fully upgraded!"));


		}
		else {
			inv.setItem(13,
					ItemUtil.getItem(Material.CHEST, 0,
							new String[] { "§cError while getting your",
									"§cslots. Please report this to the developers", "", "Error Details:",
									"Error: error_clans_319" },
							"§9Error!"));

		}


		// tag
		if (cm.getTag(clan).equals("null")) {
			inv.setItem(29, ItemUtil.getItem(Material.NAME_TAG, 1,
					new String[] { "§7Click to buy a §aClan Tag", "§7Others players will see",
							"§7a tag in tab and as a prefix", "", "§7Price: §e10000", "§aClick to buy a tag!", "",
							"§cWarning: you cannot change your tag", "§cafter you created it." },
					"§aClan Tag"));

		}
		else {
			inv.setItem(29, ItemUtil.getItem(Material.NAME_TAG, 1,
					new String[] { "§7You already own a clan tag", "§7Others players will see",
							"§7a tag in tab and as a prefix", "", "§7Price: §eN/A", "§cYou already have a clan tag",
							"", "§cWarning: you cannot change your tag", "§cafter you created it." },
					"§aClan Tag: §7" + cm.getTag(clan)));

		}

		// motd

		if (cm.getMOTD(clan).equals("null")) {
			inv.setItem(33, ItemUtil.getItem(Material.BOOK_AND_QUILL, 1,
					new String[] { "§7Click to buy a §eClan MOTD", "§7Your clan members will see",
							"§7a message of the day when they join", "", "§7Price: §e1000",
							"§aClick to buy an MOTD!", "", "§cWarning: you can change", "your MOTD every day." },
					"§eClan MOTD"));

		}
		else {
			inv.setItem(33,
					ItemUtil.getItem(Material.BOOK_AND_QUILL, 1,
							new String[] { "§7Click to buy a §eClan MOTD", "§7Your clan members will see",
									"§7a message of the day when they join", "", "§7Price: §eN/A",
									"§cYou already have a clan tag", "", "§cWarning: you can change",
									"your MOTD every day." },
							"§eClan MOTD: §7" + "Click to see"));

		}


		// info
		ItemStack exit = ItemUtil.getItem(Material.IRON_DOOR, 1, new String[] { "§7Exit the shop" }, "§cClose");
		ItemStack info = ItemUtil.getItem(Material.FEATHER, 1,
				new String[] { "§8Clan information", "",
						"§7Clan Coins: §9" + Clans.instance.getClansManager().getClanCoins(clan) },
				"§e" + Clans.instance.getClansManager().getRealName(clan) + " Clan");
		ItemStack alpha = ItemUtil.getItem(Material.PAPER, 1,
				new String[] { "§7Bugs can occur", "§7in this state of Clans", "", "§8------------",
						"§eClans §7v0.1 ALPHA", "§cReport bugs at the forums", "§7Made for Annihilation" },
				"§5Annihilation Clans ALPHA");

		inv.setItem(48, alpha);
		inv.setItem(49, info);
		inv.setItem(50, exit);

		p.openInventory(inv);
	}
}
