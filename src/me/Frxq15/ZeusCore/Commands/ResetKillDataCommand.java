package me.Frxq15.ZeusCore.Commands;

import me.Frxq15.ZeusCore.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ResetKillDataCommand implements CommandExecutor {
    static Main plugin = Main.getPlugin(Main.class);
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.AQUA+"[ZeusCore] This command cannot be executed from console.");
            return true;
        }
        Player p = (Player) commandSender;
        if(!p.hasPermission("zeuscore.resetkilldata")) {
            p.sendMessage(Main.formatMsg("NO_PERMISSION"));
            return true;
        }

        if(!Main.getInstance().rplayers.contains(p.getName())) {
            p.sendMessage(Main.formatMsg("RESET_NOT_WHITELISTED"));
            return true;
        }
        if(strings.length == 0) {
            if(Main.getInstance().confirming.contains(p.getName())) {
                p.sendMessage(Main.formatMsg("RESET_ANSWER_NEEDED"));
                return true;
            }
            runConfirm(p, 60);
            return true;
        }
        if(strings.length == 1) {
            String answer = strings[0];

            if(!Main.getInstance().confirming.contains(p.getName())) {
                p.sendMessage(Main.colourize("&cUsage: /resetkilldata"));
                return true;
            }

            switch (answer.toLowerCase()) {
                case "confirm":
                    p.sendMessage(Main.formatMsg("RESET_CONFIRMED"));
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        all.kickPlayer("A data reset is being processed, please wait patiently.");
                    }
                    Main.getInstance().confirmingstatus = true;
                    Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] A data reset is now being processed.");
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> Main.getInstance().getSqlHelper().deleteTable(), 20L * 5);
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> createTable(plugin.table), 20L * 5);
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> Main.getInstance().confirmingstatus = false, 20L * 30);
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[ZeusCore] Data reset completed"), 20L * 30);
                    return true;

                case "deny":
                    p.sendMessage(Main.formatMsg("RESET_DENIED"));
                    Main.getInstance().confirming.remove(p.getName());
                    return true;
            }
        }
        p.sendMessage(Main.colourize("&cUsage: /resetkilldata <confirm|deny>"));
        return true;
    }
    public void runConfirm(Player p, Integer time) {
        final int[] count = {time};
        Main.getInstance().confirming.add(p.getName());
        p.sendMessage(Main.formatMsg("RESET_ANSWER_NEEDED"));
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] == 0) {
                    // Starting
                    Main.getInstance().confirming.remove(p.getName());
                    p.sendMessage(Main.formatMsg("RESET_CONFIRM_FAILED"));
                    cancel();

                } else {
                    count[0]--;
                }
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("ZeusCore")), 20L, 20L);
    }
    public void createTable(String table) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement statement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (uuid VARCHAR(36) PRIMARY KEY, player VARCHAR(16), streak INT(11), rank VARCHAR(36), kills INT(11), rankid INT(11));");
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
