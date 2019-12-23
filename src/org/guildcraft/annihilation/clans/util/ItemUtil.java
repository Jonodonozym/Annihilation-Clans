package org.guildcraft.annihilation.clans.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Arjenpro on 11/01/2017.
 */
public class ItemUtil {

    public static ItemStack getItem(Material m, int amount, String[] lore, String display) {
        ItemStack is = new ItemStack(m, amount);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(display);
        List<String> lorz = new ArrayList<>(Arrays.asList(lore));
        meta.setLore(lorz);
        is.setItemMeta(meta);
        return is;
    }
}
