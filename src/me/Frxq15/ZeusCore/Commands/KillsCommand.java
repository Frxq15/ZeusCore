package me.Frxq15.ZeusCore.Commands;

import me.Frxq15.ZeusCore.Main;
import me.Frxq15.ZeusCore.SQLManager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.AQUA+"[ZeusCore] This command cannot be executed from console.");
            return true;
        }
        Player p = (Player) commandSender;
        if(!p.hasPermission("zeuscore.kills")) {
            p.sendMessage(Main.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(strings.length == 0) {
            p.sendMessage(Main.formatMsg("PLAYER_KILLS")
                    .replace("%kills%", PlayerData.getPlayerData(Main.getInstance(), p.getUniqueId()).getKills()+""));
            return true;
        }
        if(strings.length == 1) {
            if(!p.hasPermission("zeuscore.kills.others")) {
                p.sendMessage(Main.formatMsg("NO_PERMISSION"));
                return true;
            }
            Player target = Bukkit.getPlayer(strings[0]);

            if(target == null) {
                p.sendMessage(Main.formatMsg("PLAYER_NOT_FOUND"));
                return true;
            }
            p.sendMessage(Main.formatMsg("PLAYER_KILLS_OTHER")
                    .replace("%kills%", PlayerData.getPlayerData(Main.getInstance(), target.getUniqueId()).getKills()+"").replace("%player%", target.getName()));
            if(!target.isOnline()) {
                PlayerData.removePlayerData(target.getUniqueId());
            }
            return true;
        }
        p.sendMessage(Main.colourize("&cUsage: &c/kills <player>"));
        return true;
    }
}
