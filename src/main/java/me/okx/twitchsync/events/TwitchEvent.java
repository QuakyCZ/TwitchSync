package me.okx.twitchsync.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TwitchEvent extends Event {
    private String playerName;

    public TwitchEvent(String who) {
        playerName = who;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    public String getPlayerName() {
        return playerName;
    }
}
