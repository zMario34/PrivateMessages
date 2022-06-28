package tech.zmario.privatemessages.velocity.utils;

import com.velocitypowered.api.proxy.Player;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import tech.zmario.privatemessages.common.storage.DataStorage;
import tech.zmario.privatemessages.velocity.PrivateMessagesVelocity;
import tech.zmario.privatemessages.velocity.enums.MessagesConfiguration;
import tech.zmario.privatemessages.velocity.enums.SettingsConfiguration;

@UtilityClass
public class Utils {

    public TextComponent colorize(String string) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
    }

    public void sendSpyMessage(Player player, DataStorage data, Player target, String message, PrivateMessagesVelocity plugin) {
        plugin.getProxyServer().getAllPlayers().stream().filter(online -> online != player && online != target &&
                online.hasPermission(SettingsConfiguration.COMMAND_RELOAD_PERMISSION.getString()) &&
                data.getGamePlayers().get(online.getUniqueId()).isSocialSpyEnabled()).forEach(online ->
                online.sendMessage(MessagesConfiguration.SOCIAL_SPY_FORMAT.getString(
                        "%target%:" + target.getUsername(), "%player%:" + player.getUsername(), "%message%:" + message,
                        "%player_server%:" + player.getCurrentServer().get().getServerInfo().getName(), "%target_server%:" + target.getCurrentServer().get().getServerInfo().getName())));
    }
}
