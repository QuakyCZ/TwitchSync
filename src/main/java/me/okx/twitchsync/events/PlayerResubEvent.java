package me.okx.twitchsync.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerResubEvent extends Event {
    private String name;
    private int channelId;
    public String getName() {return name;}

    public PlayerResubEvent(String name, int channelId) {
        this.name = name;
        this.channelId = channelId;
    }

    public int getChannelId() {
        return channelId;
    }
    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
