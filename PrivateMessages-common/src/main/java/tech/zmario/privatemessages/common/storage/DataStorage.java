package tech.zmario.privatemessages.common.storage;

import com.google.common.collect.Maps;
import lombok.Getter;
import tech.zmario.privatemessages.common.objects.User;

import java.util.Map;
import java.util.UUID;

@Getter
public class DataStorage {

    public Map<UUID, UUID> waitingReply = Maps.newHashMap();;
    public Map<UUID, User> users = Maps.newHashMap();

    public boolean hasIgnored(UUID sender, String targetName) {
        return users.get(sender).getIgnoredPlayers().contains(targetName.toLowerCase());
    }

    public boolean hasMessagesToggled(UUID uuid) {
        return users.get(uuid).isToggleEnabled();
    }

    public boolean hasSocialSpy(UUID uuid) {
        return users.get(uuid).isSocialSpyEnabled();
    }

    public void setMessagesToggled(UUID uuid, boolean status) {
        users.get(uuid).setToggleEnabled(status);
    }

    public void setSoundToggled(UUID uuid, boolean status) {
        users.get(uuid).setSoundEnabled(status);
    }

    public void setSocialSpy(UUID uuid, boolean status) {
        users.get(uuid).setSocialSpyEnabled(status);
    }

    public void updateIgnore(UUID uuid, String targetName, boolean add) {
        User user = users.get(uuid);

        if (add) {
            user.getIgnoredPlayers().add(targetName);
        } else {
            user.getIgnoredPlayers().remove(targetName);
        }
    }

    public boolean hasSoundToggled(UUID uuid) {
        return users.get(uuid).isSoundEnabled();
    }
}
