package org.guildcraft.annihilation.clans.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.object.Clan;

public class AClanCommand implements CommandExecutor {

    private final Clans pl;

    public AClanCommand(Clans plugin) {
        this.pl = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(cmd.getName().equalsIgnoreCase("aclan"))) return true;

        if (args.length <= 1)
            return sendHelpMessage(sender);

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                default:
                    sendHelpMessage(sender);
                    break;
            }

            String player = args[1];

            String clan = player;

            if (Clan.getClan(clan) != null)
                clan = pl.getLocalClanManager().getLocalData(clan).getName();

            if (player.equals(clan)) {
                clan = pl.getClansManager().getClan(player);

                if (pl.getClansManager().hasClan(player)) {
                    sender.sendMessage(ChatColor.RED + player + " does not have a clan.");
                    return true;
                }
            }

            switch (args[0].toLowerCase()) {
                case "resetstats":
                    pl.getClansManager().setClanScore(0, clan);
                    pl.getClansManager().setMOTD(clan, "null");
                    pl.getClansManager().setTag(clan, "null");
                    pl.getClansManager().setSlots(0, clan);
                    pl.getClansManager().setClanCoins(0, clan);

                    sender.sendMessage(ChatColor.RED + "Successfully reset stats of " + clan);
                    break;
                case "disband":
                    pl.getClansManager().disbandClan(clan);
                    sender.sendMessage(ChatColor.RED + "Successfully disbanded " + clan);
                    break;
            }
        }

        if (args.length == 3) {
            String player = args[1];

            String clan = player;

            if (Clan.getClan(clan) != null)
                clan = pl.getLocalClanManager().getLocalData(clan).getName();

            if (player.equals(clan)) {
                clan = pl.getClansManager().getClan(player);

                if (pl.getClansManager().hasClan(player)) {
                    sender.sendMessage(ChatColor.RED + player + " does not have a clan.");
                    return true;
                }
            }

            String toPut = args[2];

            if (args[0].equalsIgnoreCase("name")) {
                pl.getClansManager().setClanName(Bukkit.getOfflinePlayer(pl.getClansManager().getOwner(clan)).getPlayer(),
                        pl.getClansManager().getClan(clan), toPut);
                sender.sendMessage(ChatColor.GREEN + "Set new clan name to " + toPut);
                return true;
            } else if (args[0].equalsIgnoreCase("tag")) {
                pl.getClansManager().setTag(clan, toPut);
                sender.sendMessage(ChatColor.GREEN + "Set new tag to " + toPut);
                return true;
            } else
                return sendHelpMessage(sender);
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("score") || args[0].equalsIgnoreCase("coins")) {
                String player = args[1];

                String clan = player;

                if (Clan.getClan(clan) != null)
                    clan = pl.getLocalClanManager().getLocalData(clan).getName();

                if (player.equals(clan)) {
                    clan = pl.getClansManager().getClan(player);

                    if (pl.getClansManager().hasClan(player)) {
                        sender.sendMessage(ChatColor.RED + player + " does not have a clan.");
                        return true;
                    }
                }

                int amount;

                try {
                    amount = Integer.valueOf(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Cannot set negative amount.");
                    return true;
                }

                if (String.valueOf(amount).startsWith("-")) {
                    sender.sendMessage(ChatColor.RED + "Cannot set negative amount.");
                    return true;
                }

                if (args[0].equalsIgnoreCase("score")) {
                    switch (args[1].toLowerCase()) {
                        default:
                            break;
                        case "set":
                            pl.getClansManager().setClanScore(amount, clan);
                            sender.sendMessage(ChatColor.GREEN + "Successfully set " + amount + " clan score to " + clan);
                            break;
                        case "remove":
                            pl.getClansManager().removeClanScore(amount, clan);
                            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + amount + " clan score from " + clan);
                            break;
                        case "add":
                            pl.getClansManager().addClanScore(amount, clan);
                            sender.sendMessage(ChatColor.GREEN + "Succesfully added " + amount + " clan score to " + clan);
                            break;
                    }
                }

                if (args[0].equalsIgnoreCase("coins")) {
                    switch (args[1].toLowerCase()) {
                        default:
                            break;
                        case "set":
                            pl.getClansManager().setClanCoins(amount, clan);
                            sender.sendMessage(ChatColor.GREEN + "Successfully set " + amount + " clan coins to " + clan);
                            break;
                        case "remove":
                            pl.getClansManager().removeClanCoins(amount, clan);
                            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + amount + " clan coins from " + clan);
                            break;
                        case "add":
                            pl.getClansManager().addClanCoins(amount, clan);
                            sender.sendMessage(ChatColor.GREEN + "Succesfully added " + amount + " clan coins to " + clan);
                            break;
                    }
                }
            }

            if (args[0].equalsIgnoreCase("kit")) {
                sender.sendMessage(ChatColor.RED + "Not supported yet!");
                return true;
            }
        } else
            return sendHelpMessage(sender);

        return true;
    }

    private boolean sendHelpMessage(CommandSender sender) {
        sender.sendMessage(pl.translate(
                "\n&e&lAdmin Clans Help\n"
                        + "&b/aclan score <give/remove/set> <player/clan> <amount>\n"
                        + "&b/aclan coins <give/remove/set> <player/clan> <amount>\n"
                        + "&b/aclan kit <give/remove> <player/clan> <kit>\n"
                        + "&b/aclan name <player/clan> <name>\n"
                        + "&b/aclan tag <player>/tag> <tag>\n"
                        + "&b/aclan resetstats <player/clan>\n"
                        + "&b/aclan disband <player/clan>\n"));

        return true;
    }
}
