package me.okx.twitchsync;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TwitchSyncCommand implements CommandExecutor {
  private TwitchSync plugin;

  public TwitchSyncCommand(TwitchSync plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender cs, Command command, String s, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(ChatColor.RED + "You must be a player to do this");
      return true;
    }

    Player player = (Player) cs;

    plugin.getSqlHelper().addPlayer(player.getUniqueId(),player.getName());

    String url = plugin.getValidator().createAuthenticationUrl(player.getUniqueId());
    if(url == null) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("messages.error")));
      return true;
    }

    TextComponent textComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("messages.link-message")));

     textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
     textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to sync to Twitch").create()));
     player.spigot().sendMessage(textComponent);

    return true;
  }
}
