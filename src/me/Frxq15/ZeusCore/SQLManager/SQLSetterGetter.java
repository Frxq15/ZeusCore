package me.Frxq15.ZeusCore.SQLManager;

import me.Frxq15.ZeusCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLSetterGetter {

    static Main plugin = Main.getPlugin(Main.class);

    public static boolean playerExists(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void createPlayer(final UUID uuid, String name) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            if (!playerExists(uuid)) {
                PreparedStatement insert = plugin.getConnection()
                        .prepareStatement("INSERT INTO " + plugin.table + "(uuid,player,streak,rank,kills,rankid) VALUES (?,?,?,?,?,?)");
                insert.setString(1, uuid.toString());
                insert.setString(2, name);
                insert.setInt(3, 0);
                insert.setString(4, Main.getRanksFile().getString("DEFAULT" + ".NAME"));
                insert.setInt(5, 0);
                insert.setInt(6, 0);
                insert.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerName(Player player) {
        try {
            PreparedStatement selectPlayer = plugin.getConnection().prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE uuid = ?;");
            selectPlayer.setString(1, player.getUniqueId().toString());
            ResultSet playerResult = selectPlayer.executeQuery();

            if (playerResult.next() && !playerResult.getString("player").equals(player.getName())) {
                PreparedStatement updateName = plugin.getConnection().prepareStatement("UPDATE `"+plugin.table + "` SET player = ? WHERE uuid = ?;");
                updateName.setString(1, player.getName());
                updateName.setString(2, player.getUniqueId().toString());
                updateName.executeUpdate();
            }

            playerResult.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(String table) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (uuid VARCHAR(36) PRIMARY KEY, player VARCHAR(16), streak INT(11), rank VARCHAR(36), kills INT(11), rankid INT(11));");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            }
        });
    }

    public void setStreak(UUID uuid, int streak) {
        if(!playerExists(uuid)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Error whilst setting streak for uuid "+uuid+", please contact the developer about this error.");
            return;
        }
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET streak=? WHERE UUID=?");
            statement.setInt(1, streak);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setRank(UUID uuid, String rank) {
        if(!playerExists(uuid)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Error whilst setting rank for uuid "+uuid+", please contact the developer about this error.");
            return;
        }
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET rank=? WHERE UUID=?");
            statement.setString(1, rank);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setKills(UUID uuid, int kills) {
        if(!playerExists(uuid)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Error whilst setting kills for uuid "+uuid+", please contact the developer about this error.");
            return;
        }
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET kills=? WHERE UUID=?");
            statement.setInt(1, kills);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setRankID(UUID uuid, int rankID) {
        if(!playerExists(uuid)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Error whilst setting rankID for uuid "+uuid+", please contact the developer about this error.");
            return;
        }
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET rankid=? WHERE UUID=?");
            statement.setInt(1, rankID);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getKills(UUID uuid) {
        if(!playerExists(uuid)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Error whilst getting kills for uuid "+uuid+", please contact the developer about this error.");
            return 0;
        }

        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getInt("kills");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getStreak(UUID uuid) {
        if(!playerExists(uuid)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Error whilst getting streak for uuid "+uuid+", please contact the developer about this error.");
            return 0;
        }

        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getInt("streak");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getRankID(UUID uuid) {
        if(!playerExists(uuid)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Error whilst getting rankID for uuid "+uuid+", please contact the developer about this error.");
            return 0;
        }

        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getInt("rankid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public String getRank(UUID uuid) {
        if(!playerExists(uuid)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Error whilst getting rank for uuid "+uuid+", please contact the developer about this error.");
            return "None";
        }

        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getString("rank");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "None";
    }
    public void deleteTable() {
        try {
            plugin.getConnection().prepareStatement("DROP TABLE IF EXISTS " + plugin.table).executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
