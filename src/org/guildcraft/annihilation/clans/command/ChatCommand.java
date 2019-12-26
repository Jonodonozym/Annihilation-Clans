package org.guildcraft.annihilation.clans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.guildcraft.annihilation.clans.Clans;

public class ChatCommand implements CommandExecutor {

    private Clans plugin;

    public ChatCommand(Clans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(cmd.getName().equalsIgnoreCase("chat"))) return true;

        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't use this.");
            return true;
        }

        Player p = (Player) sender;

        if (!plugin.getClansManager().hasClan(p.getName())) {
            plugin.sendMessage(p, "You have to be a member of a clan to do this.");
            return true;
        }

        if (args.length == 0) {
            plugin.sendMessage(p, "Usage: /chat <clan/all>");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clan")) {
                if (!plugin.getChatMode().contains(p)) {
                    plugin.getChatMode().add(p);
                    plugin.sendMessage(p, "Enabled channel: &eCLAN");
                    return true;
                } else {
                    plugin.sendMessage(p, "You're already in the &eCLAN &7channel!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("all")) {
                if (plugin.getChatMode().contains(p)) {
                    plugin.getChatMode().remove(p);
                    plugin.sendMessage(p, "Disabled channel &eCLAN&7. You are now chatting in &ePUBLIC");
                    return true;
                } else {
                    plugin.sendMessage(p, "You're already in the &ePUBLIC &7channel!");
                    return true;
                }
            } else {
                plugin.sendMessage(p, "Usage: /chat <clan/all>");
                return true;
            }
        }
        return true;
    }
}
