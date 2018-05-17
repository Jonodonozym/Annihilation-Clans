package org.guildcraft.annihilation.clans.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.manager.ClansManager;

/**
 * Created by Arjenpro on 11/01/2017.
 */
public class InventoryListener implements Listener {

	public static HashMap<String, String> slots = new HashMap<>();


	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory().getTitle().equals("Clan Shop")) {
			if (e.getCurrentItem() == null) {
				return;
			}

			e.setCancelled(true);

			if (!Clans.instance.getClansManager().hasClan(e.getWhoClicked().getName())) {
				e.getWhoClicked().sendMessage(
						"§9Clans> §7Error occured while clicking, please report this. §eError code: 581");
				e.getWhoClicked().closeInventory();

				return;
			}

			ClansManager cm = Clans.instance.getClansManager();
			String clan = cm.getClan(e.getWhoClicked().getName());

			Player p = (Player) e.getWhoClicked();


			if (e.getCurrentItem().getType().equals(Material.CHEST)) {


				int currentSlots = cm.getSlots(clan);
				int price;
				int nextslots;
				String update;



				if (currentSlots == 5) {
					price = 500;
					nextslots = 8;
					update = "I";
				}
				else if (currentSlots == 8) {
					price = 1000;
					nextslots = 11;
					update = "II";
				}
				else if (currentSlots == 11) {
					price = 2000;
					nextslots = 14;
					update = "III";
				}
				else if (currentSlots == 14) {
					price = 3500;
					nextslots = 17;
					update = "IV";
				}
				else if (currentSlots == 17) {
					price = 5000;
					nextslots = 20;
					update = "V";
				}
				else if (currentSlots == 20) {

					p.sendMessage("§9Clans> §7Your clan slots are already max level");
					p.closeInventory();

					return;


				}
				else {
					return;
				}

				if (!(cm.getClanCoins(clan) >= price)) {
					p.sendMessage("§9Clans> §7Yo do not have enough §eClans Coins §7to unlock this");
					p.closeInventory();

					return;
				}

				p.sendMessage("§9Clans> §7Information about slots update §e" + update);
				p.sendMessage("");
				p.sendMessage("§9Clans> §7Member slots available after update: §e" + nextslots);
				p.sendMessage("§9Clans> §7Price: §e" + price);
				p.sendMessage("");
				p.sendMessage("§9Clans> §eAre you sure? §7Type §eYES §7in the chat within 10 seconds to confirm");
				slots.put(p.getName(), nextslots + "-" + price);
				p.closeInventory();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Clans.instance, new Runnable() {
					@Override
					public void run() {
						if (slots.containsKey(p.getName())) {
							slots.remove(p.getName());
							p.sendMessage("§9Clans> §7Time out. §eAborted purchase.");
						}
					}
				}, 20 * 10);
				return;



			}
			else if (e.getCurrentItem().getType().equals(Material.NAME_TAG)) {
				if (cm.hasTag(clan)) {
					p.sendMessage("§9Clans> §7Your clan tag: §e" + cm.getTag(clan));
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
				if (cm.hasMOTD(clan)) {
					p.sendMessage("§9Clans> §7Your clan MOTD: §e" + cm.getMembers(clan));
					p.closeInventory();

					return;
				}
				else {
					p.sendMessage(
							"§9Clans> §7To setup your clan MOTD you need to execute command §e/clan MOTD <motd>");
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
