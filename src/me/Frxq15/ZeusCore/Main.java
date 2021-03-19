package me.Frxq15.ZeusCore;

import me.Frxq15.ZeusCore.Commands.KillStreakCommand;
import me.Frxq15.ZeusCore.Commands.KillsCommand;
import me.Frxq15.ZeusCore.Commands.RankCommand;
import me.Frxq15.ZeusCore.Commands.ResetKillDataCommand;
import me.Frxq15.ZeusCore.SQLManager.DataHandlingListener;
import me.Frxq15.ZeusCore.SQLManager.PlayerData;
import me.Frxq15.ZeusCore.SQLManager.SQLSetterGetter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin {

    private static Main instance;
    private SQLSetterGetter sqlHelper;
    private Connection connection;
    public String host, database, username, password, table;
    public int port;
    public List<String> confirming = new ArrayList<String>();
    public boolean confirmingstatus = false;

    public void onEnable() {
        instance = this;
        sqlHelper = new SQLSetterGetter();
        saveDefaultConfig();
        createRewardFile();
        createRanksFile();
        mysqlSetup();
        getServer().getPluginManager().registerEvents(new DataHandlingListener(this), this);
        getCommand("killstreak").setExecutor(new KillStreakCommand());
        getCommand("rank").setExecutor(new RankCommand());
        getCommand("kills").setExecutor(new KillsCommand());
        getCommand("resetkilldata").setExecutor(new ResetKillDataCommand());
        SQLSetterGetter.createTable(table);
        setResetPlayers();
        startSavingTask();
    }
    public void onDisable() {
        PlayerData.getAllPlayerData().forEach((uuid, playerData) -> sqlHelper.setStreak(uuid, playerData.getStreak()));
    }
    public static Main getInstance() {
        return instance;
    }
    public void mysqlSetup() {
        host = Main.getInstance().getConfig().getString("DATABASE." + "HOST");
        port = Main.getInstance().getConfig().getInt("DATABASE." + "PORT");
        database = Main.getInstance().getConfig().getString("DATABASE." + "DATABASE");
        username = Main.getInstance().getConfig().getString("DATABASE." + "USERNAME");
        password = Main.getInstance().getConfig().getString("DATABASE." + "PASSWORD");
        table = Main.getInstance().getConfig().getString("DATABASE." + "TABLE");

        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password="
                        + password));
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] MySQL Connected successfully.");

            }

        }catch(SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Please setup your MySQL database in the config.yml.");
        }

    }

    private void startSavingTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> PlayerData.getAllPlayerData().forEach((uuid, playerData) -> sqlHelper.setStreak(uuid, playerData.getStreak())), 20L * 60L * 5L, 20L * 60L * 5L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> PlayerData.getAllPlayerData().forEach((uuid, playerData) -> sqlHelper.setRank(uuid, playerData.getRank())), 20L * 60L * 5L, 20L * 60L * 5L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> PlayerData.getAllPlayerData().forEach((uuid, playerData) -> sqlHelper.setKills(uuid, playerData.getKills())), 20L * 60L * 5L, 20L * 60L * 5L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> PlayerData.getAllPlayerData().forEach((uuid, playerData) -> sqlHelper.setRankID(uuid, playerData.getRankID())), 20L * 60L * 5L, 20L * 60L * 5L);
    }
    public List<String> rplayers = new ArrayList<String>();
    public void setResetPlayers() {
        for(String player : Main.getInstance().getConfig().getStringList("RESET_DATA_WHITELIST")) {
            rplayers.add(player);
        }
    }

    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public SQLSetterGetter getSqlHelper() {
        return sqlHelper;
    }

    public static String formatMsg(String input) {
        return ChatColor.translateAlternateColorCodes('&', getInstance().getConfig().getString(input));
    }
    public static String formatRankMsg(String input) {
        return ChatColor.translateAlternateColorCodes('&', getRanksFile().getString(input));
    }

    public static String colourize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String RankManager(String input) { return Main.getRewardFile().getString(input); }

    public static File StorageFile;
    public static FileConfiguration StorageConfig;

    public static FileConfiguration getRewardFile() {
        return StorageConfig;
    }

    public static void reloadRewardFile() {
        StorageConfig = YamlConfiguration.loadConfiguration(StorageFile);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ZeusCore] rewards.yml was reloaded successfully");
    }

    public static void saveRewardFile() {
        try {
            StorageConfig.save(StorageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createRewardFile() {
        StorageFile = new File(Main.getInstance().getDataFolder(), "rewards.yml");
        if (!StorageFile.exists()) {
            StorageFile.getParentFile().mkdirs();
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ZeusCore] rewards.yml was created successfully");
            Main.getInstance().saveResource("rewards.yml", false);
        }

        StorageConfig = new YamlConfiguration();
        try {
            StorageConfig.load(StorageFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public static File RanksFile;
    public static FileConfiguration RanksConfig;

    public static FileConfiguration getRanksFile() {
        return RanksConfig;
    }

    public static void reloadRanksFile() {
        RanksConfig = YamlConfiguration.loadConfiguration(RanksFile);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ZeusCore] ranks.yml was reloaded successfully");
    }

    public static void saveRanksFile() {
        try {
            RanksConfig.save(RanksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createRanksFile() {
        RanksFile = new File(Main.getInstance().getDataFolder(), "ranks.yml");
        if (!RanksFile.exists()) {
            RanksFile.getParentFile().mkdirs();
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ZeusCore] ranks.yml was created successfully");
            Main.getInstance().saveResource("ranks.yml", false);
        }

        RanksConfig = new YamlConfiguration();
        try {
            RanksConfig.load(RanksFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
