package org.guildcraft.annihilation.clans.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.guildcraft.annihilation.clans.Clan;
import org.guildcraft.annihilation.clans.ClanLevel;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.manager.ClansDatabase;

/**
 * Created by Arjenpro on 11/01/2017.
 */
public class InventoryListener implements Listener {
	public static HashMap<String, String> slotsMap = new HashMap<>();


	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory().getTitle().equals("Clan Shop")) {
			if (e.getCurrentItem() == null)
				return;

			e.setCancelled(true);

			Player p = (Player) e.getWhoClicked();
			Clan clan = ClansDatabase.getInstance().getClan(p.getName());

			if (clan == null) {
				e.getWhoClicked().sendMessage("§9Clans> §7You don't have a clan!");
				e.getWhoClicked().closeInventory();
				return;
			}

			if (e.getCurrentItem().getType().equals(Material.CHEST)) {
				int currentSlots = clan.getSlots();
				ClanLevel nextLevel = ClanLevel.getFromCurrentSlots(currentSlots);

				if (nextLevel == null) {
					p.sendMessage("§9Clans> §7Your clan slots are already max level");
					p.closeInventory();
					return;
				}

				int price = nextLevel.getPrice();
				int slots = nextLevel.getSlots();
				String levelName = nextLevel.name();

				if (clan.getCoins() < price) {
					p.sendMessage("§9Clans> §7You don't have enough §eClan Coins §7to unlock this");
					p.closeInventory();
					return;
				}

				p.sendMessage("§9Clans> §7Information about slots levelName §e" + levelName);
				p.sendMessage("");
				p.sendMessage("§9Clans> §7Member slots available after levelName: §e" + slots);
				p.sendMessage("§9Clans> §7Price: §e" + price);
				p.sendMessage("");
				p.sendMessage("§9Clans> §eAre you sure? §7Type §eYES §7in the chat within 10 seconds to confirm");
				slotsMap.put(p.getName(), slots + "-" + price);
				p.closeInventory();
				
				Bukkit.getScheduler().runTaskLater(Clans.getInstance(), () -> {
					if (slotsMap.remove(p.getName()) != null)
						p.sendMessage("§9Clans> §7Time out. §eAborted purchase.");
				}, 20 * 10);
				return;
			}
			else if (e.getCurrentItem().getType().equals(Material.NAME_TAG)) {
				if (clan.hasTag()) {
					p.sendMessage("§9Clans> §7Your clan tag: §e" + clan.getTag());
					p.closeInventory();

					return;
				}
				else {
					p.sendMessage("§9Clans> §7To setup your clan tag you need to execute command §e/clan tag <tag>");
					p.sendMessage("§9Clans> §cWarning. You cannot change your tag after you created it!");
					p.closeInventory();
				}
			}
			else if (e.getCurrentItem().getType().equals(Material.BOOK_AND_QUILL)) {
				if (clan.hasMOTD()) {
					p.sendMessage("§9Clans> §7Your clan MOTD: §e" + clan.getMotd());
					p.closeInventory();

					return;
				}
				else {
					p.sendMessage("§9Clans> §7To setup your clan MOTD you need to execute command §e/clan MOTD <motd>");
					p.sendMessage("§9Clans> §aYou can change your MOTD every day");
					p.closeInventory();
				}
			}
			else if (e.getCurrentItem().getType().equals(Material.IRON_DOOR)) {
				p.closeInventory();
				return;
			}
		}
	}
}
