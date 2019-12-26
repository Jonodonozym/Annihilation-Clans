package org.guildcraft.annihilation.clans.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.guildcraft.annihilation.clans.Clans;
import org.guildcraft.annihilation.clans.inv.ShopMenu;
import org.guildcraft.annihilation.clans.object.Clan;
import org.guildcraft.annihilation.clans.util.SQLArray;
import org.guildcraft.annihilation.gcStatsHook.ExperienceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class ClanCommand implements CommandExecutor {

    public static HashMap<String, String> disband = new HashMap<>();
    public static List<Player> chatMode = new ArrayList<>();

    private Clans pl;

    public ClanCommand(Clans plugin) {
        this.pl = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;

        Player p = (Player) commandSender;

        if (strings.length == 0) {
            String clan = "None";
            String clanName = "None";
            Clan c = null;
            String tag = ChatColor.YELLOW + "None " + ChatColor.GRAY + "(You can buy one in the /clan shop)";
            String motd = tag;

            if (pl.getClansManager().hasClan(p.getName())) {
                clan = pl.getClansManager().getClan(p.getName());
                c = pl.getLocalClanManager().getLocalData(clan);
                clanName = pl.getClansManager().getRealName(clan);

                System.out.println(c.getTag());

                if (!c.getTag().equals("null")) {
                    System.out.print("Setting tag");
                    tag = ChatColor.GREEN + c.getTag();
                }

                if (!c.getMotd().equals("null")) {
                    System.out.print("Setting motd");
                    motd = ChatColor.YELLOW + c.getMotd().replaceAll("&", "§");
                }
            }

            p.sendMessage(ChatColor.YELLOW + "Clans v0.1 ALPHA");
            p.sendMessage("");
            p.sendMessage(ChatColor.GREEN + "Your current clan: " + ChatColor.GRAY + clanName);

            if (!clan.equals("None"))
                sendClan(p, c, tag, motd, clan, false);
            else
                p.sendMessage(ChatColor.BLUE + "You can make a clan by doing /clan create or joining an existing one!");
            return true;
        }

        if (strings.length == 1) {
            switch (strings[0].toLowerCase()) {
                default:
                    helpMenu(p, pl.getClansManager().hasClan(p.getName()));
                    return true;

                case "help":
                    helpMenu(p, pl.getClansManager().hasClan(p.getName()));
                    return true;

                case "disband":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    String clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " +
                                ChatColor.GRAY + "You have to be the rank "
                                + ChatColor.YELLOW + "" + ChatColor.BOLD + "OWNER " + ChatColor.GRAY
                                + "in order to disband to clan.");
                        return true;
                    }

                    if (pl.gameMode) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "This command is disabled in game servers. Use it in the anni lobby!");
                        return true;
                    }

                    p.sendMessage("");
                    disband.put(p.getName(), "disband_" + clan);
                    setTimer(p);
                    return true;

                case "members":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());
                    Clan c = pl.getLocalClanManager().getLocalData(clan);

                    p.sendMessage(ChatColor.YELLOW + "Creator: " + ChatColor.RED + c.getOwner());
                    p.sendMessage(ChatColor.BLUE + "Officers: "
                            + ChatColor.GOLD
                            + SQLArray.convertToStringView(c.getOfficers()).replaceAll(",", ", "));
                    p.sendMessage(ChatColor.GRAY + "Members: "
                            + ChatColor.GREEN
                            + SQLArray.convertToStringView(c.getMembers()).replaceAll(",", ", "));
                    return true;

                case "shop":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    if (pl.gameMode) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "This command is disabled in game servers. Use it in the anni lobby!");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be the rank " + ChatColor.GREEN + "OFFICER " +
                                ChatColor.GRAY + "or higher to do this.");
                        return true;
                    }

                    ShopMenu.open(p, clan);
                    return true;

                case "chat":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    if (chatMode.contains(p)) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " +
                                ChatColor.GRAY + "Disabled channel " + ChatColor.YELLOW + "CLAN"
                                + ChatColor.GRAY + ". You are now chatting in " + ChatColor.YELLOW + "PUBLIC");
                        chatMode.remove(p);
                        return true;
                    } else {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Enabled channel "
                                + ChatColor.YELLOW + "CLAN");
                        chatMode.add(p);
                        return true;
                    }

                case "leave":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());
                    if (pl.getClansManager().getOwner(clan) == null) {
                        pl.getClansManager().setClan(p.getName(), "null");
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You left the clan.");
                        return true;
                    }

                    if (pl.getClansManager().getOwner(clan).toLowerCase().equalsIgnoreCase(p.getName().toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "As the leader of the clan, you can't leave. Use "
                                + ChatColor.YELLOW + "/clan disband " + ChatColor.GRAY + "to disband your clan.");
                        return true;
                    }

                    pl.getClansManager().leaveClan(clan, p.getName());
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You left the clan.");
                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            ChatColor.GRAY + "The player " + ChatColor.YELLOW + p.getName()
                                    + ChatColor.GRAY + " §7 has left the clan.");
                    return true;
            }
        } else if (strings.length == 2) {
            switch (strings[0].toLowerCase()) {
                default:
                    break;

                case "join":
                    String tojoin = strings[1];

                    if (pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You're already member of a clan.");
                        return true;
                    }

                    if (pl.getClansManager().getClanCoins(tojoin) == -1) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "That clan doesn't exist");
                        return true;
                    }

                    if (!pl.getClansManager().isInvited(p.getName(), tojoin)) {
                        System.out.print(pl.getLocalClanManager().getLocalData(tojoin).toString());
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You are not invited to this clan!");
                        return true;
                    }

                    pl.getClansManager().joinClan(tojoin, p);
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You are now part of the clan "
                            + ChatColor.YELLOW + tojoin);
                    pl.getChatManager().sendChatMessageToClan("SYSTEM", tojoin.toLowerCase(),
                            ChatColor.GRAY + "The player " + ChatColor.YELLOW + p.getName()
                                    + ChatColor.GRAY + " joined the clan.");
                    return true;

                case "info":
                    if (!pl.getClansManager().hasClan(strings[1])) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + strings[1] + " is not a valid clan!");
                        return true;
                    }

                    String clan = pl.getClansManager().getClan(strings[1]);
                    Clan c = pl.getLocalClanManager().getLocalData(clan);

                    if (c == null) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "NPE error occurred. Please report this to the developers (code:18)");
                        return true;
                    }

                    String tag = c.getTag().equals("null") ? ChatColor.YELLOW + "None" : c.getTag();
                    p.sendMessage(ChatColor.BLUE + "Clans> "
                            + ChatColor.GRAY + "Searching data for clan ["
                            + ChatColor.YELLOW + pl.getClansManager().getRealName(clan) + ChatColor.GRAY + "]");

                    sendClan(p, c, tag, "", clan, true);
                    return true;

                case "invite":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be the rank " + ChatColor.GREEN + "OFFICER " +
                                ChatColor.GRAY + "or higher to do this.");
                        return true;
                    }

                    if (pl.getClansManager().isInvited(strings[1], clan)) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "This player is already invited.");
                        return true;
                    }

                    if (pl.getClansManager().hasClan(strings[1])) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "That player is already in a clan");
                        return true;
                    }

                    if (strings[1].length() > 16) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + strings[1] + " is not a valid clan!");
                        return true;
                    }

                    if (pl.getClansManager().getSlots(clan) == pl.getClansManager().getTotalMembers(clan)) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You cannot invite members anymore. " +
                                "You can buy more slots in the shop " + ChatColor.YELLOW + "/clan shop");
                        return true;
                    }

                    pl.getClansManager().invitePlayerToClan(clan, strings[1]);
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Invited player " +
                            "" + ChatColor.YELLOW + strings[1] + ChatColor.GRAY + ". They have to join with "
                            + ChatColor.YELLOW + "/clan join " + pl.getClansManager().getRealName(clan));
                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            ChatColor.GRAY + "The player " + ChatColor.YELLOW + p.getName()
                                    + ChatColor.GRAY + " invited" + ChatColor.YELLOW + strings[1]);

                    pl.getChatManager().sendMessage(strings[1], "claninvite_"
                            + pl.getClansManager().getRealName(clan));
                    return true;

                case "chat":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    pl.getChatManager().sendChatMessageToClan(p.getName(),
                            pl.getClansManager().getClan(p.getName()).toLowerCase(), strings[1]);
                    return true;

                case "promote":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be the rank " + ChatColor.GREEN + "OFFICER " +
                                ChatColor.GRAY + "or higher to do this.");
                        return true;
                    }

                    if (!pl.getClansManager().getMembersLS(clan).contains(strings[1].toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "That player isn't in your clan");
                        return true;
                    }

                    pl.getClansManager().promote(clan, strings[1]);
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Promoted player " +
                            ChatColor.YELLOW + strings[1] + ChatColor.GRAY + " to rank " + ChatColor.GREEN + "OFFICER");

                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            ChatColor.GRAY + "The player " + strings[1] + " has been promoted to " +
                                    ChatColor.GREEN + "OFFICER");
                    return true;

                case "demote":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be the rank " + ChatColor.GREEN + "OFFICER " +
                                ChatColor.GRAY + "or higher to do this.");
                        return true;
                    }

                    if (!pl.getClansManager().getMembersLS(clan).contains(strings[1].toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "That player isn't in your clan");
                        return true;
                    }

                    pl.getClansManager().demote(clan, strings[1]);
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Demoted player "
                            + ChatColor.YELLOW + strings[1] + ChatColor.GRAY + " to rank " + ChatColor.GOLD + "MEMBER");

                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            ChatColor.GRAY + "The player " + strings[1] + " has been demoted to "
                                    + ChatColor.GOLD + "MEMBER");
                    return true;

                case "kick":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be the rank " + ChatColor.GREEN + "OFFICER " +
                                ChatColor.GRAY + "or higher to do this.");
                        return true;
                    }

                    if (!pl.getClansManager().getMembersLS(clan).contains(strings[1].toLowerCase())
                            && !pl.getClansManager().getOfficersLS(clan).contains(strings[1].toLowerCase())
                            && !pl.getClansManager().getOwner(clan).toLowerCase().equals(strings[1].toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "This player isn't member of your clan.");
                        return true;
                    }

                    if (pl.getClansManager().isOfficer(clan, strings[1])) {
                        if (!pl.getClansManager().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
                            p.sendMessage(ChatColor.BLUE + "Clans> " +
                                    ChatColor.GRAY + "You have to be the rank " + ChatColor.YELLOW + "OWNER"
                                    + ChatColor.GRAY + " to kick officers.");
                            return true;
                        }
                    }

                    if (pl.getClansManager().getOwner(clan).toLowerCase().equalsIgnoreCase(strings[1].toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You cannot kick the owner dumb ass!");
                        return true;
                    }

                    pl.getClansManager().kickPlayer(clan, strings[1]);
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "The player " + strings[1]
                            + " has been kicked from your clan.");

                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            ChatColor.GRAY + "The player " + strings[1] + " has been kicked.");

                    pl.getChatManager().sendMessage(strings[1],
                            ChatColor.DARK_RED + "!! " + ChatColor.YELLOW
                                    + "You have been kicked from your clan " + ChatColor.DARK_RED + "!!");
                    return true;

                case "tag":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    if (pl.gameMode) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "This command is disabled in game servers. Use it in the anni lobby!");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You have to be the rank " +
                                ChatColor.YELLOW + "OWNER" + ChatColor.GRAY + " to purchase a tag.");
                        return true;
                    }

                    if (pl.getClansManager().getClanCoins(clan) < 10000) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You do not have enough Clan Coins to purchase this. \n" +
                                ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "10000 Clan Coins");
                        return true;
                    }

                    for (String blocked : pl.blocked) {
                        if (blocked.equalsIgnoreCase(strings[1])
                                || strings[1].toLowerCase().contains(blocked.toLowerCase())) {
                            p.sendMessage(ChatColor.BLUE + "Clans> "
                                    + ChatColor.GRAY + "The word `" + ChatColor.YELLOW + strings[1]
                                    + ChatColor.GRAY + "` is blocked");
                            return true;
                        }
                    }

                    if (strings[1].length() > 12) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "The maximum characters for a tag is 12");
                        return true;
                    }

                    if (strings[1].toLowerCase().contains("&".toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You can't use colors in your tag");
                        return true;
                    }

                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Tag purchase");
                    p.sendMessage("");
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Tag: [" + strings[1] + "]");
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "10000");
                    disband.put(p.getName(), "tag_" + strings[1] + "-" + "10000");
                    setTimer(p);
                    return true;

                case "transfer":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You have to be member of a clan to do this.");
                        return true;
                    }

                    if (pl.gameMode) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "This command is disabled in game servers. Use it in the anni lobby!");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You have to be the rank "
                                + ChatColor.YELLOW + "OWNER" + ChatColor.GRAY + " §7 to transfer the leadership");
                        return true;
                    }

                    if (!pl.getClansManager().getMembers(clan).contains(strings[1].toLowerCase())
                            && !pl.getClansManager().getOfficers(clan).contains(strings[1].toLowerCase())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "That player isn't in your clan");
                        return true;
                    }

                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Clan Leadership Transfer");
                    p.sendMessage("");
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Transfer to: [" + strings[1] + "]");
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "N/A");
                    disband.put(p.getName(), "transfer_" + strings[1] + "-" + clan.toLowerCase());
                    setTimer(p);
                    return true;

                case "create":
                    if (pl.getClansManager().hasClan(p.getName())) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "You're already member of a clan.");
                        return true;
                    }

                    String create = strings[1];

                    if (pl.gameMode) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "This command is disabled in game servers. Use it in the anni lobby!");
                        return true;
                    }

                    if (pl.getLocalClanManager().hasLocalData(create)) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + create + " is already a clan!");
                        return true;
                    }

                    if (ExperienceManager.getInstance().getXP(p) < 5000) {
                        p.sendMessage(ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "You do not have enough XP to purchase this. " +
                                ChatColor.YELLOW + "NEEDED: 5000XP");
                        return true;
                    }

                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Clan create");
                    p.sendMessage("");
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Name: " + ChatColor.YELLOW + create);
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Price: " + ChatColor.AQUA + "5000XP");
                    disband.put(p.getName(), "create_" + strings[1] + "-" + "5000");
                    setTimer(p);
                    return true;
            }
        } else {
            if (!pl.getClansManager().hasClan(p.getName())) {
                helpMenu(p, false);
                return true;
            }

            if (strings[0].equalsIgnoreCase("chat")) {
                if (!pl.getClansManager().hasClan(p.getName())) {
                    p.sendMessage(ChatColor.BLUE + "Clans> "
                            + ChatColor.GRAY + "You have to be member of a clan to use this.");
                    return true;
                }

                pl.getChatManager().sendChatMessageToClan(p.getName(),
                        pl.getClansManager().getClan(p.getName()).toLowerCase(), getFinalArg(strings));
                return true;
            } else if (strings[0].equalsIgnoreCase("motd")) {
                String clan = pl.getClansManager().getClan(p.getName());

                if (pl.gameMode) {
                    p.sendMessage(ChatColor.BLUE + "Clans> "
                            + ChatColor.GRAY + "This command is disabled in game servers. Use it in the anni lobby!");
                    return true;
                }

                if (pl.getClansManager().getMOTD(clan).equals("null")) {
                    if (pl.getClansManager().getClanCoins(clan) < 5000) {
                        p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY
                                + "You do not have enough Clan Coins to purchase this. " + ChatColor.BLUE + "Clans> "
                                + ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "5000 Clan Coins");
                        return true;
                    }

                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "MOTD purchase");
                    p.sendMessage("");
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "MOTD: "
                            + ChatColor.YELLOW + getFinalArg(strings) + "");
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "5000");
                    disband.put(p.getName(), "motd_" + getFinalArg(strings) + "-" + "5000");
                    setTimer(p);
                    return true;
                } else {
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "MOTD Update");
                    p.sendMessage("");
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "MOTD: "
                            + ChatColor.YELLOW + getFinalArg(strings) + "");
                    p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Price: " + ChatColor.YELLOW + "Free");
                    disband.put(p.getName(), "motd_" + getFinalArg(strings) + "-" + "0");
                    setTimer(p);
                    return true;
                }
            } else {
                helpMenu(p, true);
                return true;
            }
        }
        return true;
    }


    private String getFinalArg(final String[] args) {
        final StringBuilder bldr = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            if (i != 1)
                bldr.append(" ");
            bldr.append(args[i]);
        }

        return bldr.toString();
    }

    private void helpMenu(Player p, boolean hasClan) {
        if (hasClan) {
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Clans Help");
            p.sendMessage("");
            p.sendMessage(ChatColor.YELLOW + "/clan " + ChatColor.GRAY + "- Display clan information");
            p.sendMessage(ChatColor.YELLOW + "/clan members " + ChatColor.GRAY + "- View the members of your clan");
            p.sendMessage(ChatColor.YELLOW + "/clan leave " + ChatColor.GRAY + " Leave your current clan");
            p.sendMessage(ChatColor.YELLOW + "/clan disband " + ChatColor.GRAY + "- Disband your current clan");
            p.sendMessage(ChatColor.YELLOW + "/clan shop " + ChatColor.GRAY + "- Open the Clan shop");
            p.sendMessage(ChatColor.YELLOW + "/clan chat " + ChatColor.GRAY + "- Enable chat mode channel CLAN");
            p.sendMessage("");
            p.sendMessage(ChatColor.YELLOW + "/clan join <clan> " + ChatColor.GRAY + "- Join a clan (you must be invited)");
            p.sendMessage(ChatColor.YELLOW + "/clan invite <player> " + ChatColor.GRAY + "- Invite a player to your clan");
            p.sendMessage(ChatColor.YELLOW + "/clan kick <player> " + ChatColor.GRAY + "- Kick a player from your clan");
            p.sendMessage(ChatColor.YELLOW + "/clan promote <player> " + ChatColor.GRAY + "- Promote a player in your clan");
            p.sendMessage(ChatColor.YELLOW + "/clan info <player> " + ChatColor.GRAY + "- View a player's clan information");
            p.sendMessage(ChatColor.YELLOW + "/clan transfer <player> " + ChatColor.GRAY + "- Transfer your clan's leadership");
            p.sendMessage(ChatColor.YELLOW + "/clan create <name> " + ChatColor.GRAY + "- Create a clan");
        } else {
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Clans help");
            p.sendMessage("");
            p.sendMessage(ChatColor.YELLOW + "/clan " + ChatColor.GRAY + "- Display clan information");
            p.sendMessage("");
            p.sendMessage(ChatColor.YELLOW + "/clan join <clan> " + ChatColor.GRAY + "- Join a clan (you must be invited)");
            p.sendMessage(ChatColor.YELLOW + "/clan info <player> " + ChatColor.GRAY + "- View a player's clan information");
            p.sendMessage(ChatColor.YELLOW + "/clan create <name> " + ChatColor.GRAY + "- Create a clan");
        }
    }

    private void sendClan(Player p, Clan c, String tag, String motd, String clan, boolean info) {
        p.sendMessage(ChatColor.AQUA + "===================================");
        p.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.YELLOW + pl.getClansManager().getRealName(clan));
        p.sendMessage(ChatColor.GRAY + "Total members: " + ChatColor.YELLOW
                + pl.getClansManager().getTotalMembers(clan));
        p.sendMessage(ChatColor.GRAY + "Tag: " + tag);

        if (!info)
            p.sendMessage(ChatColor.GRAY + "MOTD: " + ChatColor.translateAlternateColorCodes('&', motd));

        p.sendMessage(ChatColor.GRAY + "Slots used: " + ChatColor.YELLOW
                + pl.getClansManager().getTotalMembers(clan) + ChatColor.GRAY + "/" + ChatColor.YELLOW
                + pl.getClansManager().getSlots(clan));
        p.sendMessage(ChatColor.GOLD + "Clan Coins: " + c.getCoins());
        p.sendMessage(ChatColor.LIGHT_PURPLE + "Clan Points: " + c.getPoints());

        p.sendMessage(ChatColor.YELLOW + "------------------------------");
        p.sendMessage(ChatColor.YELLOW + "Creator: " + ChatColor.RED + c.getOwner());
        p.sendMessage(ChatColor.BLUE + "Officers: "
                + ChatColor.GOLD + SQLArray.convertToStringView(c.getOfficers()));
        p.sendMessage(ChatColor.GRAY + "Members: "
                + ChatColor.GREEN + SQLArray.convertToStringView(c.getMembers()));
        p.sendMessage(ChatColor.AQUA + "===================================");
    }

    private void setTimer(Player p) {
        p.sendMessage("");
        p.sendMessage(ChatColor.BLUE + "Clans> " +
                ChatColor.YELLOW + "Are you sure? " + ChatColor.GRAY + "Type " + ChatColor.YELLOW + "YES " +
                ChatColor.GRAY + "in the chat within 10 seconds to confirm.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> {
            if (disband.containsKey(p.getName())) {
                disband.remove(p.getName());
                p.sendMessage(ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Time out. §eAborted purchase.");
            }
        }, 20 * 10);
    }
}
