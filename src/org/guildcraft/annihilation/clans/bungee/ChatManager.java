package org.guildcraft.annihilation.clans.bungee;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.guildcraft.annihilation.clans.Clans;

import java.io.*;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class ChatManager implements PluginMessageListener {

    private Clans plugin;

    public ChatManager(Clans pl) {
        this.plugin = pl;
    }

    public void sendChatMessageToClan(String sender, String clan, String message) {
        if (plugin.getLocalClanManager().online.get(clan) == null)
            return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("Clans");

        ByteArrayOutputStream msgbyte = new ByteArrayOutputStream();
        DataOutputStream msg = new DataOutputStream(msgbyte);

        try {
            msg.writeUTF(sender);
            msg.writeUTF(clan);
            msg.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgbyte.toByteArray().length);
        out.write(msgbyte.toByteArray());

        Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

        if (p != null)
            p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

        for (Player s : plugin.getLocalClanManager().online.get(clan)) {
            if (sender.equals("SYSTEM"))
                s.sendMessage(ChatColor.GRAY + "[ " +
                        ChatColor.GREEN + "Clan" + ChatColor.GRAY + "] " +
                        ChatColor.YELLOW + message);
            else
                s.sendMessage(ChatColor.GRAY + "[ " +
                        ChatColor.GREEN + "Clan" + ChatColor.GRAY + "] " + ChatColor.BLUE + sender + ": "
                        + ChatColor.WHITE + message);
        }
    }

    public void sendMessage(String to, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ForwardToPlayer");
        out.writeUTF(to);
        out.writeUTF("ClansInvite");

        ByteArrayOutputStream msgbyte = new ByteArrayOutputStream();
        DataOutputStream msg = new DataOutputStream(msgbyte);

        try {
            msg.writeUTF(to);
            msg.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgbyte.toByteArray().length);
        out.write(msgbyte.toByteArray());

        Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

        if (p != null)
            p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (!s.equals("BungeeCord"))
            return;

        try {
            ByteArrayDataInput msgin = ByteStreams.newDataInput(bytes);
            String sub = msgin.readUTF();

            if (sub.equals("Clans")) {
                short len = msgin.readShort();
                byte[] msgbytes = new byte[len];
                msgin.readFully(msgbytes);

                DataInputStream in = new DataInputStream(new ByteArrayInputStream(msgbytes));

                String sender = in.readUTF();
                String clan = in.readUTF();
                String message = in.readUTF();

                if (plugin.getLocalClanManager().online.get(clan) == null)
                    return;

                for (Player p : plugin.getLocalClanManager().online.get(clan.toLowerCase())) {
                    if (sender.equals("SYSTEM"))
                        p.sendMessage("§7[§aClan§7] §e" + message);
                    else
                        p.sendMessage("§7[§aClan§7] §a" + sender + ": §f" + message);
                }
            } else if (sub.equals("ClansInvite")) {
                short len = msgin.readShort();
                byte[] msgbytes = new byte[len];
                msgin.readFully(msgbytes);

                DataInputStream in = new DataInputStream(new ByteArrayInputStream(msgbytes));

                String to = in.readUTF();
                String msg = in.readUTF();

                if (Bukkit.getPlayer(to) != null) {// just to be sure
                    if (msg.split("_")[0].equals("claninvite")) {
                        String clan = msg.split("_")[1];
                        TextComponent accept = new TextComponent(ChatColor.YELLOW + "HERE");
                        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(ChatColor.YELLOW + "Click to join the clan " + clan).create()));
                        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan join " + clan));

                        TextComponent format = new TextComponent(ChatColor.BLUE + "Clans> " +
                                ChatColor.GRAY + "You have been invited by the clan " + ChatColor.YELLOW
                                + clan + ChatColor.GRAY + " \n" + ChatColor.BLUE + "Clans> " + ChatColor.GRAY + "Click");
                        TextComponent end = new TextComponent(ChatColor.GRAY + "to join the clan");
                        format.addExtra(accept);
                        format.addExtra(end);

                        Bukkit.getPlayer(to).spigot().sendMessage(format);
                    } else
                        Bukkit.getPlayer(to).sendMessage(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
