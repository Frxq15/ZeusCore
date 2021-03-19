package me.Frxq15.ZeusCore.SQLManager;

import me.Frxq15.ZeusCore.Main;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private final static Map<UUID, PlayerData> players = new HashMap<>();

    private final UUID uuid;
    private int streak = 0;
    private String rank = "";
    private int kills = 0;
    private int rankid = 0;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        players.put(uuid, this);
    }
    public UUID getUuid() { return uuid; }
    public void setKills(int kills) { this.kills = kills; }
    public void setStreak(int streak) { this.streak = streak; }
    public void setRank(String rank) {  this.rank = rank; }
    public void setRankID(int rankid) {  this.rankid = rankid; }
    public int getRankID() { return rankid; }
    public int getStreak() { return streak; }
    public String getRank() { return rank; }
    public int getKills() { return kills; }
    public static void removePlayerData(UUID uuid) { players.remove(uuid); }

    public void uploadPlayerData(Main Main) {
        Bukkit.getScheduler().runTaskAsynchronously(Main, () -> Main.getSqlHelper().setStreak(uuid, streak));
        Bukkit.getScheduler().runTaskAsynchronously(Main, () -> Main.getSqlHelper().setRank(uuid, rank));
        Bukkit.getScheduler().runTaskAsynchronously(Main, () -> Main.getSqlHelper().setKills(uuid, kills));
        Bukkit.getScheduler().runTaskAsynchronously(Main, () -> Main.getSqlHelper().setRankID(uuid, rankid));
    }

    public static PlayerData getPlayerData(Main Main, UUID uuid) {
        if (!players.containsKey(uuid)) {
            PlayerData playerData = new PlayerData(uuid);
            playerData.setStreak(Main.getSqlHelper().getStreak(uuid));
            playerData.setRank(Main.getSqlHelper().getRank(uuid));
            playerData.setKills(Main.getSqlHelper().getKills(uuid));
            playerData.setRankID(Main.getSqlHelper().getRankID(uuid));
        }
        return players.get(uuid);
    }

    public static Map<UUID, PlayerData> getAllPlayerData() {
        return players;
    }

}
