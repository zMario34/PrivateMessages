package tech.zmario.privatemessages.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import tech.zmario.privatemessages.common.listeners.AbstractConnectionListener;
import tech.zmario.privatemessages.velocity.VelocityPlugin;

@RequiredArgsConstructor
public class ConnectionListener extends AbstractConnectionListener {

    private final VelocityPlugin plugin;

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        Player player = event.getPlayer();

        if (!player.isActive()) return;
        handleJoinEvent(plugin.getSenderFactory().getSender(player), plugin);
    }
}
