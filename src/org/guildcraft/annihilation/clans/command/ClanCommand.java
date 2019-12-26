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

import java.util.HashMap;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class ClanCommand implements CommandExecutor {

    public static HashMap<String, String> disband = new HashMap<>();

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
            String tag = pl.translate("&eNone &7(You can buy one in the /clan shop)");
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
                    motd = ChatColor.DARK_AQUA + c.getMotd();
                }
            }

            p.sendMessage(ChatColor.YELLOW + "Clans v1.0 BETA");
            p.sendMessage("");
            pl.sendMessage(p, "&aYour current clan:");
            p.sendMessage(ChatColor.GREEN + "Your current clan: " + ChatColor.GRAY + clanName);

            if (!clan.equals("None"))
                sendClan(p, c, tag, motd, clan, false);
            else
                pl.sendMessage(p, "&9You can make a clan by doing /clan create or joining an existing one!");
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
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    String clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
                        pl.sendMessage(p, "You have to be the rank &4OWNER &7in order to disband the clan");
                        return true;
                    }

                    if (pl.gameMode) {
                        pl.sendMessage(p, "This command is disabled on in-game servers. Use it in the anni lobby!");
                        return true;
                    }

                    p.sendMessage("");
                    disband.put(p.getName(), "disband_" + clan);
                    setTimer(p);
                    return true;

                case "members":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());
                    Clan c = pl.getLocalClanManager().getLocalData(clan);

                    p.sendMessage(pl.translate("&eCreator: &c" + c.getOwner()));
                    p.sendMessage(pl.translate("&9Officers: &6"
                            + SQLArray.convertToStringView(c.getOfficers()).replaceAll(",", ", ")));
                    p.sendMessage(pl.translate("&7Members: &a"
                            + SQLArray.convertToStringView(c.getMembers()).replaceAll(",", ", ")));
                    return true;

                case "shop":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    if (pl.gameMode) {
                        pl.sendMessage(p, "This command is disabled on in-game servers. Use it in the anni lobby!");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        pl.sendMessage(p, "You have to be the rank &aOFFICER &7or higher to do this.");
                        return true;
                    }

                    ShopMenu.open(p, clan);
                    return true;

                case "leave":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());
                    if (pl.getClansManager().getOwner(clan) == null) {
                        pl.getClansManager().setClan(p.getName(), "null");
                        pl.sendMessage(p, "You left the clan.");
                        return true;
                    }

                    if (pl.getClansManager().getOwner(clan).toLowerCase().equalsIgnoreCase(p.getName().toLowerCase())) {
                        pl.sendMessage(p, "As the leader of the clan, you can't leave. " +
                                "Use &e/clan disband &7to disband your clan.");
                        return true;
                    }

                    pl.getClansManager().leaveClan(clan, p.getName());
                    pl.sendMessage(p, "You left the clan.");
                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            pl.translate("&7The player &e" + p.getName() + " &7has left the clan."));
                    return true;
            }
        } else if (strings.length == 2) {
            switch (strings[0].toLowerCase()) {
                default:
                    break;

                case "score":
                    String player = strings[1];

                    if (!pl.getClansManager().hasClan(player)) {
                        pl.sendMessage(p, player + " does not have a clan!");
                        return true;
                    }

                    pl.sendMessage(p, "&dClan Score of &7" + player + "s clan: &d"
                            + pl.getClansManager().getClanScore(pl.getClansManager().getClan(player)));
                    return true;

                case "coins":
                    player = strings[1];

                    if (!pl.getClansManager().hasClan(player)) {
                        pl.sendMessage(p, player + " does not have a clan!");
                        return true;
                    }

                    pl.sendMessage(p, "&6Clan Coins of &7" + player + "s clan: &6"
                            + pl.getClansManager().getClanCoins(pl.getClansManager().getClan(player)));
                    return true;

                case "join":
                    String tojoin = strings[1];

                    if (pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You're already a member of a clan.");
                        return true;
                    }

                    if (pl.getClansManager().getClanCoins(tojoin) == -1) {
                        pl.sendMessage(p, "That clan doesn't exist!");
                        return true;
                    }

                    if (!pl.getClansManager().isInvited(p.getName(), tojoin)) {
                        System.out.print(pl.getLocalClanManager().getLocalData(tojoin).toString());
                        pl.sendMessage(p, "You are not invited to this clan!");
                        return true;
                    }

                    pl.getClansManager().joinClan(tojoin, p);
                    pl.sendMessage(p, "You are now part of the clan &e" + tojoin);
                    pl.getChatManager().sendChatMessageToClan("SYSTEM", tojoin.toLowerCase(),
                            pl.translate("&7The player &e" + p.getName() + " &7joined the clan."));
                    return true;

                case "info":
                    if (!pl.getClansManager().hasClan(strings[1])) {
                        pl.sendMessage(p, strings[1] + " is not a valid clan!");
                        return true;
                    }

                    String clan = pl.getClansManager().getClan(strings[1]);
                    Clan c = pl.getLocalClanManager().getLocalData(clan);

                    if (c == null) {
                        pl.sendMessage(p, "NPE error occured. Please report this to the developers (code:18)");
                        return true;
                    }

                    pl.sendMessage(p, "Searching data for clan [&e" + pl.getClansManager().getRealName(clan) + "&7]");
                    String tag = c.getTag().equals("null") ? ChatColor.YELLOW + "None" : c.getTag();
                    sendClan(p, c, tag, "", clan, true);
                    return true;

                case "invite":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        pl.sendMessage(p, "You have to be the rank &aOFFICER &7or higher to do this.");
                        return true;
                    }

                    if (pl.getClansManager().isInvited(strings[1], clan)) {
                        pl.sendMessage(p, "This player is already invited.");
                        return true;
                    }

                    if (pl.getClansManager().hasClan(strings[1])) {
                        pl.sendMessage(p, "That player is already in a clan!");
                        return true;
                    }

                    if (strings[1].length() > 16) {
                        pl.sendMessage(p, strings[1] + " is not a valid clan!");
                        return true;
                    }

                    if (pl.getClansManager().getSlots(clan) == pl.getClansManager().getTotalMembers(clan)) {
                        pl.sendMessage(p, "You cannot invite members anymore. " +
                                "You can buy more slots in the shop &e/clan shop");
                        return true;
                    }

                    pl.getClansManager().invitePlayerToClan(clan, strings[1]);
                    pl.sendMessage(p, "Invited player &e" + strings[1] + " &7. " +
                            "They have to join with &e/clan join" + pl.getClansManager().getRealName(clan));
                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            pl.translate("&7The player &e" + p.getName() + " &7invited &e" + strings[1]));

                    pl.getChatManager().sendMessage(strings[1], "claninvite_" + pl.getClansManager().getRealName(clan));
                    return true;

                case "promote":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        pl.sendMessage(p, "You have to be the rank &aOFFICER &7or higher to do this.");
                        return true;
                    }

                    if (!pl.getClansManager().getMembersLS(clan).contains(strings[1].toLowerCase())) {
                        pl.sendMessage(p, "That player isn't in your clan!");
                        return true;
                    }

                    pl.getClansManager().promote(clan, strings[1]);
                    pl.sendMessage(p, "Promoted player &e" + strings[1] + " &7to rank &aOFFICER");
                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            pl.translate("&7The player " + strings[1] + " has been promoted to &aOFFICER"));
                    return true;

                case "demote":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        pl.sendMessage(p, "You have to be the rank &aOFFICER &7or higher to do this.");
                        return true;
                    }

                    if (!pl.getClansManager().getMembersLS(clan).contains(strings[1].toLowerCase())) {
                        pl.sendMessage(p, "That player isn't in your clan1");
                        return true;
                    }

                    pl.getClansManager().demote(clan, strings[1]);
                    pl.sendMessage(p, "Demoted player &e" + strings[1] + " &7to rank &6MEMBER");
                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            pl.translate("&7The player " + strings[1] + " has been demoted to &6MEMBER"));
                    return true;

                case "kick":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().isOfficer(clan, p.getName())) {
                        pl.sendMessage(p, "You have to be the rank &aOFFICER &7or higher to do this.");
                        return true;
                    }

                    if (!pl.getClansManager().getMembersLS(clan).contains(strings[1].toLowerCase())
                            && !pl.getClansManager().getOfficersLS(clan).contains(strings[1].toLowerCase())
                            && !pl.getClansManager().getOwner(clan).toLowerCase().equals(strings[1].toLowerCase())) {
                        pl.sendMessage(p, "This player isn't a member of your clan!");
                        return true;
                    }

                    if (pl.getClansManager().isOfficer(clan, strings[1])) {
                        if (!pl.getClansManager().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
                            pl.sendMessage(p, "You have to be the rank &4OWNER &7in order to kick officers.");
                            return true;
                        }
                    }

                    if (pl.getClansManager().getOwner(clan).toLowerCase().equalsIgnoreCase(strings[1].toLowerCase())) {
                        pl.sendMessage(p, "You cannot kick the owner noob!");
                        return true;
                    }

                    pl.getClansManager().kickPlayer(clan, strings[1]);
                    pl.sendMessage(p, "The player " + strings[1] + " has been kicked from your clan.");

                    pl.getChatManager().sendChatMessageToClan("SYSTEM", clan.toLowerCase(),
                            pl.translate("&7The player " + strings[1] + " has been kicked."));

                    pl.getChatManager().sendMessage(strings[1],
                            pl.translate("&4!! &eYou have been kicked from your clan &4!!"));
                    return true;

                case "tag":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    if (pl.gameMode) {
                        pl.sendMessage(p, "This command is disabled on in-game servers. Use it in the anni lobby!");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
                        pl.sendMessage(p, "You have to be the rank &eOWNER &7to purchase a tag.");
                        return true;
                    }

                    if (pl.getClansManager().getClanCoins(clan) < 10000) {
                        pl.sendMessage(p, "You do not have enough Clan Coins to purchase this.");
                        pl.sendMessage(p, "Price: &e10000 Clan Coins");
                        return true;
                    }

                    for (String blocked : pl.blocked) {
                        if (blocked.equalsIgnoreCase(strings[1])
                                || strings[1].toLowerCase().contains(blocked.toLowerCase())) {
                            pl.sendMessage(p, "The word `&e" + strings[1] + "&7` is blocked!");
                            return true;
                        }
                    }

                    if (strings[1].length() > 12) {
                        pl.sendMessage(p, "The maximum chracters for a tag is 12!");
                        return true;
                    }

                    if (strings[1].toLowerCase().contains("&")) {
                        pl.sendMessage(p, "You can't use color codes in your tag!");
                        return true;
                    }

                    pl.sendMessage(p, "Tag Purchase");
                    p.sendMessage("");
                    pl.sendMessage(p, "Tag: [" + strings[1] + "]");
                    pl.sendMessage(p, "Price: &e10000");
                    disband.put(p.getName(), "tag_" + strings[1] + "-" + "10000");
                    setTimer(p);
                    return true;

                case "transfer":
                    if (!pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You have to be a member of a clan to do this.");
                        return true;
                    }

                    if (pl.gameMode) {
                        pl.sendMessage(p, "This command is disabled on in-game servers. Use it in the anni lobby!");
                        return true;
                    }

                    clan = pl.getClansManager().getClan(p.getName());

                    if (!pl.getClansManager().getOwner(clan).toLowerCase().equals(p.getName().toLowerCase())) {
                        pl.sendMessage(p, "You have to be the rank &eOWNER &7to transfer the leadership.");
                        return true;
                    }

                    if (!pl.getClansManager().getMembers(clan).contains(strings[1].toLowerCase())
                            && !pl.getClansManager().getOfficers(clan).contains(strings[1].toLowerCase())) {
                        pl.sendMessage(p, "That player isn't in your clan!");
                        return true;
                    }

                    pl.sendMessage(p, "Clan LeaderShip Transfer");
                    p.sendMessage("");
                    pl.sendMessage(p, "Transfer to: [" + strings[1] + "]");
                    disband.put(p.getName(), "transfer_" + strings[1] + "-" + clan.toLowerCase());
                    setTimer(p);
                    return true;

                case "create":
                    if (pl.getClansManager().hasClan(p.getName())) {
                        pl.sendMessage(p, "You're already a member of a clan!");
                        return true;
                    }

                    String create = strings[1];

                    if (pl.gameMode) {
                        pl.sendMessage(p, "This command is disabled on in-game servers. Use it in the anni lobby!");
                        return true;
                    }

                    if (pl.getLocalClanManager().hasLocalData(create)) {
                        pl.sendMessage(p, create + " is already a clan!");
                        return true;
                    }

                    if (ExperienceManager.getInstance().getXP(p) < 5000) {
                        pl.sendMessage(p, "You do not have enough XP to purchase this. &eNEEDED: 5000XP");
                        return true;
                    }

                    pl.sendMessage(p, "Clan Create");
                    p.sendMessage("");
                    pl.sendMessage(p, "Name: &e" + create);
                    pl.sendMessage(p, "Price: &b5000XP");
                    disband.put(p.getName(), "create_" + strings[1] + "-" + "5000");
                    setTimer(p);
                    return true;
            }
        } else {
            if (!pl.getClansManager().hasClan(p.getName())) {
                helpMenu(p, false);
                return true;
            }

            if (strings[0].equalsIgnoreCase("motd")) {
                String clan = pl.getClansManager().getClan(p.getName());

                if (pl.gameMode) {
                    pl.sendMessage(p, "This command is disbaled on in-game severs. Use it in the anni lobby!");
                    return true;
                }

                if (pl.getClansManager().getMOTD(clan).equals("null")) {
                    if (pl.getClansManager().getClanCoins(clan) < 5000) {
                        pl.sendMessage(p, "You do not have enough Clan Coins to purchase this.");
                        pl.sendMessage(p, "Price: &e5000 Clan Coins");
                        return true;
                    }

                    pl.sendMessage(p, "MOTD Purchase");
                    p.sendMessage("");
                    pl.sendMessage(p, "MOTD: &e" + pl.getFinalArg(strings));
                    pl.sendMessage(p, "Price: &e5000");
                    disband.put(p.getName(), "motd_" + pl.getFinalArg(strings) + "-" + "5000");
                    setTimer(p);
                    return true;
                } else {
                    pl.sendMessage(p, "MOTD Update");
                    p.sendMessage("");
                    pl.sendMessage(p, "MOTD: &e" + pl.getFinalArg(strings));
                    pl.sendMessage(p, "Price: Free");
                    disband.put(p.getName(), "motd_" + pl.getFinalArg(strings) + "-" + "0");
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


    private void helpMenu(Player p, boolean hasClan) {
        if (hasClan) {
            p.sendMessage(pl.translate(
                    "&e&lClans Help\n"
                            + "\n"
                            + "&e/clan &7- Display clan information\n"
                            + "&e/clan members &7- View the members of your clan\n"
                            + "&e/clan leave &7- Leave your current clan\n"
                            + "&e/clan disband &7- Disband your current clan\n"
                            + "&e/clan shop &7- Open the Clan Shop\n"
                            + "&e/clan chat &7- Enable chat mode channel: CLAN\n"
                            + "\n"
                            + "&e/clan join <clan> &7- Join a clan (you must be invited)\n"
                            + "&e/clan invite <player> &7- Invite a player to your clan\n"
                            + "&e/clan kick <player> &7- Kick a player from your clan\n"
                            + "&e/clan promote <player> &7- Promote a player in your clan\n"
                            + "&e/clan demote <player> &7- Demote a player in your clan\n"
                            + "&e/clan info <player> &7- View a player's clan information\n"
                            + "&e/clan transfer <player> &7- Transfer your clan's leadership\n"
                            + "&e/clan create <name> &7- Create a clan"));
        } else {
            p.sendMessage(pl.translate(
                    "&e&lClans Help\n"
                            + "\n"
                            + "&e/clan &7- Display clan information\n"
                            + "\n"
                            + "&e/clan join <clan> &7- Join a clan (you must be invited)\n"
                            + "&e/clan info <player> &7- View a player's clan information\n"
                            + "&e/clan create <name> &7- Create a clan."));
        }
    }

    private void sendClan(Player p, Clan c, String tag, String motd, String clan, boolean info) {
        p.sendMessage(ChatColor.AQUA + "===================================");
        p.sendMessage(pl.translate("&7Name: &e" + pl.getClansManager().getRealName(clan) + "\n" +
                "&7Total Members: &e" + pl.getClansManager().getTotalMembers(clan) + "\n" +
                "&7Tag: " + tag));

        if (!info)
            p.sendMessage(ChatColor.GRAY + "MOTD: " + motd);

        p.sendMessage(pl.translate(
                "&7Slots used: &e" + pl.getClansManager().getTotalMembers(clan) + "&7/&e"
                        + pl.getClansManager().getSlots(clan)) + "\n" +
                "&6Clan Coins: " + c.getCoins() + "\n" +
                "&dClan Score: " + c.getScore());
        p.sendMessage(ChatColor.YELLOW + "------------------------------");
        p.sendMessage(pl.translate("&eCreator: &c" + c.getOwner() + "\n"
                + "&9Officers: &6" + SQLArray.convertToStringView(c.getOfficers())) + "\n" +
                "&7Members: &a" + SQLArray.convertToStringView(c.getMembers()));
        p.sendMessage(ChatColor.AQUA + "===================================");
    }

    private void setTimer(Player p) {
        p.sendMessage("");
        pl.sendMessage(p, "&eAre you sure? &7Type &eYES &7in the chat within 10 seconds to confirm.");

        Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> {
            if (disband.containsKey(p.getName())) {
                disband.remove(p.getName());
                pl.sendMessage(p, "Time out. &eAborted purchase.");
            }
        }, 20 * 10);
    }
}
