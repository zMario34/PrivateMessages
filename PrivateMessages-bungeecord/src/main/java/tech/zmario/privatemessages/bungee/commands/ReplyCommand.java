package tech.zmario.privatemessages.bungee.commands;

import litebans.api.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import tech.zmario.privatemessages.bungee.PrivateMessagesBungee;
import tech.zmario.privatemessages.bungee.enums.MessagesConfiguration;
import tech.zmario.privatemessages.bungee.enums.SettingsConfiguration;
import tech.zmario.privatemessages.bungee.utils.Utils;
import tech.zmario.privatemessages.common.storage.DataStorage;

public class ReplyCommand extends Command {

    private final PrivateMessagesBungee plugin;

    public ReplyCommand(PrivateMessagesBungee plugin) {
        super(SettingsConfiguration.COMMAND_REPLY_NAME.getString(), SettingsConfiguration.COMMAND_REPLY_PERMISSION.getString(), SettingsConfiguration.COMMAND_REPLY_ALIASES.getStringList().toArray(new String[0]));
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!SettingsConfiguration.COMMAND_REPLY_PERMISSION.getString().isEmpty() && !player.hasPermission(SettingsConfiguration.COMMAND_REPLY_PERMISSION.getString())) {
            player.sendMessage(MessagesConfiguration.NO_PERMISSION.getString());
            return;
        }

        final DataStorage data = plugin.getStorage();

        if (!data.getWaitingReply().containsKey(player.getUniqueId())) {
            player.sendMessage(MessagesConfiguration.REPLY_NOT_IN_CONVERSATION.getString());
            return;
        }
        if (args.length == 0) {
            player.sendMessage(MessagesConfiguration.REPLY_USAGE.getString());
            return;
        }

        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(data.getWaitingReply().get(player.getUniqueId()));
        if (target == null) {
            player.sendMessage(MessagesConfiguration.REPLY_PLAYER_NOT_ONLINE.getString());
            return;
        }

        if (plugin.getProxy().getPluginManager().getPlugin("LiteBans") != null && Database.get().isPlayerMuted(player.getUniqueId(), null)) {
            player.sendMessage(MessagesConfiguration.REPLY_LITEBANS_TARGET_MUTED.getString("%target%:" + target.getName()));
            return;
        }

        if (data.hasIgnored(player.getUniqueId(), target.getName())) {
            player.sendMessage(MessagesConfiguration.REPLY_PLAYER_IGNORED.getString());
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }

        if (data.hasIgnored(target.getUniqueId(), player.getName())) {
            player.sendMessage(MessagesConfiguration.REPLY_TARGET_IGNORED.getString("%target%:" + target.getName()));
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(player.getUniqueId())) {
            player.sendMessage(MessagesConfiguration.REPLY_MESSAGES_DISABLED.getString());
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(target.getUniqueId())) {
            player.sendMessage(MessagesConfiguration.REPLY_MESSAGES_DISABLED_TARGET.getString("%target%:" + target.getName()));
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }
        String message = String.join(" ", args);
        data.getWaitingReply().put(target.getUniqueId(), player.getUniqueId());

        player.sendMessage(MessagesConfiguration.REPLY_SENDER_FORMAT.getString("%target%:" + target.getName(), "%message%:" + message, "%player_server%:" + player.getServer().getInfo().getName(), "%target_server%:" + target.getServer().getInfo().getName()));
        target.sendMessage(MessagesConfiguration.REPLY_TARGET_FORMAT.getString("%target%:" + player.getName(), "%message%:" + message, "%player_server%:" + player.getServer().getInfo().getName(), "%target_server%:" + target.getServer().getInfo().getName()));

        Utils.sendSpyMessage(player, data, target, message, plugin);
    }


}
