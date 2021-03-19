package me.Frxq15.ZeusCore.SQLManager;

import me.Frxq15.ZeusCore.Main;
import me.Frxq15.ZeusCore.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class DataHandlingListener implements Listener {

    private final Main plugin;

    public DataHandlingListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();

        plugin.getSqlHelper().createPlayer(uuid, name);

        PlayerData playerData = PlayerData.getPlayerData(plugin, uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSqlHelper().updatePlayerName(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if(Main.getInstance().confirmingstatus = false) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID uuid = event.getPlayer().getUniqueId();

            PlayerData playerData = PlayerData.getPlayerData(plugin, uuid);
                plugin.getSqlHelper().setStreak(uuid, playerData.getStreak());
                plugin.getSqlHelper().setRank(uuid, playerData.getRank());
                plugin.getSqlHelper().setKills(uuid, playerData.getKills());
                plugin.getSqlHelper().setRankID(uuid, playerData.getRankID());

                PlayerData.removePlayerData(uuid);
            });
        }
    }
    @EventHandler
    public void addStreak(PlayerDeathEvent e) {
        Player p = e.getEntity().getKiller();
        PlayerData playerData = PlayerData.getPlayerData(plugin, p.getUniqueId());
        if(e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        int current = playerData.getStreak();
        playerData.setStreak((current+1));
        Manager.giveReward(p);
    }
    @EventHandler
    public void clearStreak(PlayerDeathEvent e) {
        Player p = e.getEntity();
        PlayerData playerData = PlayerData.getPlayerData(plugin, p.getUniqueId());
        if(e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        if(playerData.getStreak() == 0) {
            return;
        }
        p.sendMessage(Main.formatMsg("KILLSTREAK_OVER").replace("%streak%", playerData.getStreak()+""));
        playerData.setStreak(0);
    }
    @EventHandler
    public void addRank(PlayerDeathEvent e) {
        Player p = e.getEntity().getKiller();
        PlayerData playerData = PlayerData.getPlayerData(plugin, p.getUniqueId());
        playerData.setKills((playerData.getKills()+1));
        if(e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        if(e.getEntity().getKiller().getType() != EntityType.PLAYER) {
            return;
        }
        Manager.rankCheck(p.getPlayer());
    }
    @EventHandler
    public void onJoin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if(Main.getInstance().confirmingstatus) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Main.colourize(("&cA data reset is currently in progress, please be patient.")));
        }
    }
}
