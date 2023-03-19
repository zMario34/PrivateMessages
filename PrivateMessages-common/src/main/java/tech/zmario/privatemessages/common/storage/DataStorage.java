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

    public void setSocialSpy(UUID uuid, boolean status) {
        users.get(uuid).setSocialSpyEnabled(status);
    }

    public void updateIgnore(UUID sender, String targetName, boolean add) {
        if (add) {
            users.get(sender).getIgnoredPlayers().add(targetName);
        } else {
            users.get(sender).getIgnoredPlayers().remove(targetName);
        }
    }
}
