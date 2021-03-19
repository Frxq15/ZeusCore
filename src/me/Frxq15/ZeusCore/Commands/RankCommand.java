package me.Frxq15.ZeusCore.Commands;

import me.Frxq15.ZeusCore.Main;
import me.Frxq15.ZeusCore.SQLManager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.AQUA+"[ZeusKillStreaks] This command cannot be executed from console.");
            return true;
        }
        String dr = Main.getRanksFile().getString("DEFAULT" + ".NAME");
        Player p = (Player) commandSender;
        if(!p.hasPermission("zeuskillstreaks.rank")) {
            p.sendMessage(Main.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(strings.length == 0) {
            if(PlayerData.getPlayerData(Main.getInstance(), p.getUniqueId()).getRank().equals(dr)) {
                p.sendMessage(Main.formatMsg("DEFAULT_RANK"));
                return true;
            }
            p.sendMessage(Main.formatMsg("CURRENT_RANK")
                    .replace("%rank%", PlayerData.getPlayerData(Main.getInstance(), p.getUniqueId()).getRank()));
            return true;
        }
        if(strings.length == 1) {
            if(!p.hasPermission("zeuskillstreaks.rank.others")) {
                p.sendMessage(Main.formatMsg("NO_PERMISSION"));
                return true;
            }
            Player target = Bukkit.getPlayer(strings[0]);

            if(target == null) {
                p.sendMessage(Main.formatMsg("PLAYER_NOT_FOUND"));
                return true;
            }

            if(PlayerData.getPlayerData(Main.getInstance(), target.getUniqueId()).getStreak() == 0) {
                p.sendMessage(Main.formatMsg("DEFAULT_RANK_OTHER").replace("%player%", target.getName()));
                if(!target.isOnline()) {
                    PlayerData.removePlayerData(target.getUniqueId());
                }
                return true;
            }
            p.sendMessage(Main.formatMsg("CURRENT_RANK_OTHER")
                    .replace("%rank%", PlayerData.getPlayerData(Main.getInstance(), target.getUniqueId()).getRank()).replace("%player%", target.getName()));
            if(!target.isOnline()) {
                PlayerData.removePlayerData(target.getUniqueId());
            }
            return true;
        }
        p.sendMessage(Main.colourize("&cUsage: &c/rank <player>"));
        return true;
    }
}
