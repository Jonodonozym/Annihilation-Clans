package org.guildcraft.annihilation.clans;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.guildcraft.annihilation.clans.bungee.ChatManager;
import org.guildcraft.annihilation.clans.command.ClanCommand;
import org.guildcraft.annihilation.clans.listener.ClanChatListener;
import org.guildcraft.annihilation.clans.listener.InventoryListener;
import org.guildcraft.annihilation.clans.listener.PlayerListener;
import org.guildcraft.annihilation.clans.manager.ClansManager;
import org.guildcraft.annihilation.clans.manager.DatabaseManager;
import org.guildcraft.annihilation.clans.manager.LocalClanManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by arjen on 20/04/2016.
 */
public class Clans extends JavaPlugin {
    public static File log;
    @Getter
    private static Clans instance;

    public boolean gameMode;

    public List<String> blocked;

    @Getter
    private DatabaseManager databaseManager;

    @Getter
    private ClansManager clansManager;

    @Getter
    private LocalClanManager localClanManager;

    @Getter
    private ChatManager chatManager;

    public static void log(String string) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(log, true), true);
            if (string.equals("")) {
                writer.write(System.getProperty("line.separator"));
            } else {
                Date dt = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = df.format(dt);
                writer.write(time + " [MS] " + string);
                writer.write(System.getProperty("line.separator"));
            }
            writer.close();
        } catch (IOException e) {
            Bukkit.getServer().getLogger().warning("[ANNI] An error occurred while writing to the log! IOException");
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        System.out.println("[Clans] Starting Clans™ ALPHA by Arjenpro");
        instance = this;
        registerListeners();
        registerCommands();

        log = new File(getDataFolder(), "bought.log");

        if (!log.exists()) {
            try {
                getDataFolder().mkdirs();
                log.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().warning("[Clans] Failed to create the bought.log! IOException");
            }
        }

        String host = getConfig().getString("sql.host");
        int port = getConfig().getInt("sql.port");
        String name = getConfig().getString("sql.database");
        String user = getConfig().getString("sql.user");
        String pass = getConfig().getString("sql.pass");
        System.out.println("[Clans] Loading database..");
        System.out.println("[Clans] Connecting to database IP " + host + " with username " + user + "..");
        databaseManager = new DatabaseManager(host, port, name, user, pass, this);
        databaseManager.open();

        System.out.print("[Clans] Connected to database successfully.");

        clansManager = new ClansManager(this);
        localClanManager = new LocalClanManager(this);
        chatManager = new ChatManager(this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", chatManager);

        if (getConfig().getBoolean("lobbyMode")) {
            gameMode = false;
            System.out.print("[Clans] Lobbymode has been disabled. Cache will be used instead of live database.");
        } else {
            gameMode = true;
            System.out.print(
                    "[Clans] Lobbymode has been enabled. The plugin will use real life data from the SQL database.");
        }

        blocked = getConfig().getStringList("blockedwords");

        System.out.println("[Clans] Clans is now ready to use!");
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&',
                "&7" + text);
    }

    public void sendMessage(Player player, String msg) {
        player.sendMessage(translate("&f[&bClans&f] &7" + msg));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClanChatListener(this), this);
    }

    private void registerCommands() {
        getCommand("clan").setExecutor(new ClanCommand(this));
    }
}
