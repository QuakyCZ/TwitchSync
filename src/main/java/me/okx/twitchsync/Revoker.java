package me.okx.twitchsync;

import me.okx.twitchsync.data.CheckState;
import me.okx.twitchsync.data.StateWithId;
import me.okx.twitchsync.data.Token;
import me.okx.twitchsync.data.json.AccessToken;
import me.okx.twitchsync.events.PlayerFollowEvent;
import me.okx.twitchsync.events.PlayerResubEvent;
import me.okx.twitchsync.events.PlayerSubscriptionEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class Revoker extends BukkitRunnable {
    private TwitchSync plugin;

    public Revoker(TwitchSync plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getLogger().info("Revoking now.");
        long start = System.currentTimeMillis();

        Map<UUID, Token> tokens = plugin.getSqlHelper().getTokens().get();
        checkTokens(tokens);

        long time = System.currentTimeMillis() - start;
        plugin.getLogger().info("Finished revoking in " + time + "ms.");
    }

    private void checkTokens(Map<UUID, Token> tokens) {
        for (Map.Entry<UUID, Token> entry : tokens.entrySet()) {
            UUID uuid = entry.getKey();

            Token token = entry.getValue();

            AccessToken accessToken = refresh(token.getAccessToken());
            plugin.getSqlHelper().setToken(uuid,
                    token.getId(),
                    accessToken.getAccessToken(),
                    accessToken.getRefreshToken());

            plugin.getSqlHelper().isFollowing(uuid).ifPresent(b -> {
                Stream<StateWithId> followStates = plugin.getValidator().getFollowingState(token.getId(), accessToken);
                boolean check = check(followStates);
                if (b && check) {
                    revoke("follow", uuid);
                    plugin.getSqlHelper().setFollowing(uuid, false);
                } else if (!b && !check) {
                    //plugin.getLogger().info("Refollowed: " + uuid.toString());

                    plugin.getSqlHelper().setFollowing(uuid, true);
                    Bukkit.getScheduler().runTask(plugin, () ->
                            Bukkit.getPluginManager().callEvent(
                                    new PlayerFollowEvent(plugin.getSqlHelper().getPlayerName(uuid),
                                            plugin.getValidator().getFollowingState(
                                                    token.getId(),
                                                    accessToken)
                                            .sorted().findFirst().get().getId())));
                }
            });

            plugin.getSqlHelper().isSubscribed(uuid).ifPresent(b -> {
                Stream<StateWithId> subscribstionState = plugin.getValidator().getSubscriptionState(token.getId(), accessToken);
                boolean check = check(subscribstionState);
                // if subscribed and all states are not YES
                if (b && check) {
                    revoke("subscribe", uuid);
                    plugin.getSqlHelper().setSubscribed(uuid, false);
                } else if (!b && !check) {
                    plugin.getSqlHelper().setSubscribed(uuid, true);
                    Bukkit.getScheduler().runTask(plugin, () ->
                            Bukkit.getPluginManager().callEvent(
                                    new PlayerSubscriptionEvent(plugin.getSqlHelper().getPlayerName(uuid),
                                            plugin.getValidator().getSubscriptionState(
                                                    token.getId(),
                                                    accessToken)
                                                    .sorted().findFirst().get().getId())));
                }
            });
        }
    }

    private void revoke(String type, UUID uuid) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(type);
        String player = plugin.getSqlHelper().getPlayerName(uuid);

        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.debug(type + " - " + uuid, "revoking");

            for (String command : section.getStringList("revoke-commands")) {
                plugin.getLogger().info(command);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                        .replace("%name%", player));
            }
            for (String message : section.getStringList("revoke-messages")) {
                plugin.getSqlHelper().addMessage(message
                        .replace("%name%", player));
            }
        });
    }

    /**
     * Check if all states are not YES
     */
    public boolean check(Stream<StateWithId> states) {
        return states.allMatch(stateWithId -> stateWithId.getState() != CheckState.YES);
    }

    private AccessToken refresh(AccessToken accessToken) {
        return plugin.getValidator().refreshToken(accessToken.getRefreshToken());
    }
}
