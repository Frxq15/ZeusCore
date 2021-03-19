package me.Frxq15.ZeusCore;

import me.Frxq15.ZeusCore.SQLManager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Manager {
    public static void giveReward(Player p) {
        PlayerData playerData = PlayerData.getPlayerData(Main.getInstance(), p.getUniqueId());
        int current = playerData.getStreak();

        if (current == 0) {
            return;
        }
        Main.getRewardFile().getKeys(false).forEach(reward -> {
            if (current == Main.getRewardFile().getInt(reward + ".REQUIRED_KILLSTREAK")) {
                //reward commands
                for (String command : Main.getRewardFile().getStringList(reward + ".COMMANDS")) {
                    command = command.replace("%player%", p.getName()).replace("%streak%", current + "");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
                if (Main.getRewardFile().getBoolean(reward + ".BROADCAST_MESSAGE")) {
                    Bukkit.broadcastMessage(Main.colourize(Main.getRewardFile().getString(reward + ".MESSAGE").replace("%player%", p.getName()).replace("%streak%", current + "")));
                }
            }
        });
    }

    public static void rankCheck(Player p) {
        PlayerData playerData = PlayerData.getPlayerData(Main.getInstance(), p.getUniqueId());
        int current = playerData.getKills();

        if (current < Main.getRanksFile().getInt("1." + "REQUIRED_KILLS")) {
            return;
        }
        Main.getRanksFile().getKeys(false).forEach(rank -> {
            if (current == Main.getRanksFile().getInt(rank + ".REQUIRED_KILLS")) {
                    playerData.setRankID(Main.getRanksFile().getInt(rank + ".ID"));
                    playerData.setRank(Main.getRanksFile().getString(rank + ".NAME"));
                    p.sendMessage(Main.formatRankMsg(rank + ".PLAYER_MESSAGE"));
                    if(Main.getRanksFile().getBoolean(rank + ".BROADCAST_MESSAGE")) {
                        Bukkit.broadcastMessage(Main.colourize(Main.getRanksFile().getString(rank + ".MESSAGE").replace("%player%", p.getName())));
                }
                for (String command : Main.getRanksFile().getStringList(rank + ".COMMANDS")) {
                    command = command.replace("%player%", p.getName()).replace("%player%", p.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }); //end of foreach
    }
}
