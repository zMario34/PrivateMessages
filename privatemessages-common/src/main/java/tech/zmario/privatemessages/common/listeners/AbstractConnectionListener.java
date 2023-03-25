package tech.zmario.privatemessages.common.listeners;

import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.objects.User;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;

import java.util.UUID;

public abstract class AbstractConnectionListener {

    public void handleJoinEvent(Sender sender, PrivateMessagesPlugin plugin) {
        UUID uuid = sender.getUniqueId();

        plugin.getSqlManager().isPresent(uuid).thenAccept(present -> {
            if (!present) plugin.getSqlManager().createPlayer(uuid);

            plugin.getSqlManager().getIgnoredPlayers(uuid).thenAccept(list -> {
                User user = new User(uuid, list);

                plugin.getSqlManager().getToggledStatus(uuid).thenAccept(user::setToggleEnabled);
                plugin.getSqlManager().getSocialSpyStatus(uuid).thenAccept(user::setSocialSpyEnabled);
                plugin.getSqlManager().getSoundStatus(uuid).thenAccept(user::setSoundEnabled);

                plugin.getDataStorage().getUsers().put(uuid, user);
            });
        });
    }
}
