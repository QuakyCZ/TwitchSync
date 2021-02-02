package me.okx.twitchsync.events;

import me.okx.twitchsync.TwitchSync;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener {
    private TwitchSync plugin;

    public PlayerListener(TwitchSync plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerSubscriptionEvent e) {
        handle("subscribe", e.getPlayerName(), e.getChannelId());
    }

    /* @EventHandler
     public void on(PlayerResubEvent e) {
       handle("subscribe",e.getName(), e.getChannelId());
     }*/
    @EventHandler
    public void on(PlayerFollowEvent e) {
        handle("follow", e.getPlayerName(), e.getChannelId());
    }

    private void handle(String path, String player, int channelId) {
        //plugin.getLogger().info(player + " " + path);
        ConfigurationSection config = plugin.getConfig().getConfigurationSection(path);
        if (!config.getBoolean("enabled")) {
            return;
        }

        String channel = plugin.getValidator().getChannelName(channelId);


        for (String command : config.getStringList("commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replace("%name%", player)
                    .replace("%channel%", channel)
                    .replace("%channelid%", channelId + ""));

        }

        for (String message : config.getStringList("messages")) {
            plugin.getSqlHelper().addMessage(message
                    .replace("%name%", player)
                    .replace("%channel%", channel)
                    .replace("%channelid%", channelId + "")
            );
        }
    }

  /*private void handle(String path, String name, int channelId) {
    ConfigurationSection config = plugin.getConfig().getConfigurationSection(path);
    if(!config.getBoolean("enabled")) {
      return;
    }

    String channel = plugin.getValidator().getChannelName(channelId);

    for (String command : config.getStringList("commands")) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
              .replace("%name%", name)
              .replace("%channel%", channel)
              .replace("%channelid%", channelId + ""));
    }
  }*/
}
