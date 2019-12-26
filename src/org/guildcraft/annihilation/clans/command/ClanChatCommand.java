package org.guildcraft.annihilation.clans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.guildcraft.annihilation.clans.Clans;

public class ClanChatCommand implements CommandExecutor {

    private Clans plugin;

    public ClanChatCommand(Clans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("clanchat")
                && !cmd.getName().equalsIgnoreCase("cc")) return true;

        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't use this!");
            return true;
        }

        Player p = (Player) sender;

        if (!plugin.getClansManager().hasClan(p.getName())) {
            plugin.sendMessage(p, "You have to be a member of a clan to do this.");
            return true;
        }

        if (args.length == 0) {
            plugin.sendMessage(p, "Usage: /clanchat <message>");
            return true;
        }

        plugin.getChatManager().sendChatMessageToClan(p.getName(),
                plugin.getClansManager().getClan(p.getName()).toLowerCase(), plugin.getFinalArg(args));
        return true;
    }
}
