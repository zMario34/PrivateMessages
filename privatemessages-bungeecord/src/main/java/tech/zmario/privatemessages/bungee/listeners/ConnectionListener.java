package tech.zmario.privatemessages.bungee.listeners;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tech.zmario.privatemessages.bungee.BungeeCordPlugin;
import tech.zmario.privatemessages.common.listeners.AbstractConnectionListener;

@RequiredArgsConstructor
public class ConnectionListener extends AbstractConnectionListener implements Listener {

    private final BungeeCordPlugin plugin;

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        handleJoinEvent(plugin.getSenderFactory().getSender(player), plugin);
    }
}
