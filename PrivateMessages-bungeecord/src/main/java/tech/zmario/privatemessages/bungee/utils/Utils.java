package tech.zmario.privatemessages.bungee.utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tech.zmario.privatemessages.bungee.PrivateMessagesBungee;
import tech.zmario.privatemessages.bungee.enums.MessagesConfiguration;
import tech.zmario.privatemessages.bungee.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.storage.DataStorage;

import java.util.List;
import java.util.Objects;

@UtilityClass
public class Utils {

    public String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public void sendSpyMessage(ProxiedPlayer player, DataStorage data, ProxiedPlayer target, String message, PrivateMessagesBungee plugin) {
        plugin.getProxy().getPlayers().stream().filter(online -> online != player && online != target &&
                online.hasPermission(SettingsConfiguration.COMMAND_RELOAD_PERMISSION.getString()) &&
                data.getGamePlayers().get(online.getUniqueId()).isSocialSpyEnabled()).forEach(online ->
                online.sendMessage(MessagesConfiguration.SOCIAL_SPY_FORMAT.getString(
                        "%target%:" + player.getName(), "%player%:" + player.getName(), "%message%:" + message,
                        "%player_server%:" + player.getServer().getInfo().getName(), "%target_server%:" + target.getServer().getInfo().getName())));
    }

    public String listToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String string : list) {
            builder.append(string);
            if (!Objects.equals(list.get(list.size() - 1), string)) {
                builder.append(";");
            }
        }
        return builder.toString();
    }
}
