package me.okx.twitchsync;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RevokeCommand implements CommandExecutor {
  private TwitchSync plugin;

  public RevokeCommand(TwitchSync plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender cs, Command command, String s, String[] strings) {
    if(cs instanceof Player && ((Player)cs).hasPermission("twitch.revoke")) return false;
    new Revoker(plugin).runTaskAsynchronously(plugin);
    cs.sendMessage(ChatColor.GREEN + "Revoking those who are no longer following or subscribed.");
    return true;
  }
}
