package tech.zmario.privatemessages.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import tech.zmario.privatemessages.common.storage.DataStorage;
import tech.zmario.privatemessages.velocity.PrivateMessagesVelocity;
import tech.zmario.privatemessages.velocity.enums.MessagesConfiguration;
import tech.zmario.privatemessages.velocity.enums.SettingsConfiguration;
import tech.zmario.privatemessages.velocity.utils.Utils;

import java.util.Arrays;
import java.util.Optional;

public class MessageCommand implements SimpleCommand {

    private final PrivateMessagesVelocity plugin;

    public MessageCommand(PrivateMessagesVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length == 1 && args[0].equalsIgnoreCase(SettingsConfiguration.COMMAND_RELOAD_NAME.getString()) && source.hasPermission(SettingsConfiguration.COMMAND_RELOAD_PERMISSION.getString())) {
            plugin.getConfigManager().reloadAll();
            source.sendMessage(MessagesConfiguration.CONFIGURATIONS_RELOADED.getString());
            return;
        }

        if (!(source instanceof Player)) {
            source.sendMessage(MessagesConfiguration.NO_CONSOLE.getString());
            return;
        }
        final Player player = (Player) source;

        if (!SettingsConfiguration.COMMAND_MESSAGE_PERMISSION.getString().isEmpty() && !player.hasPermission(SettingsConfiguration.COMMAND_MESSAGE_PERMISSION.getString())) {
            player.sendMessage(MessagesConfiguration.NO_PERMISSION.getString());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessagesConfiguration.MESSAGE_USAGE.getString());
            return;
        }

        final Optional<Player> targetOptional = plugin.getProxyServer().getPlayer(args[0]);

        if (!targetOptional.isPresent()) {
            player.sendMessage(MessagesConfiguration.MESSAGE_PLAYER_NOT_ONLINE.getString("%target%:" + args[0]));
            return;
        }

        final Player target = targetOptional.get();
        final DataStorage data = plugin.getStorage();

        if (data.hasIgnored(player.getUniqueId(), target.getUsername())) {
            player.sendMessage(MessagesConfiguration.MESSAGE_PLAYER_IGNORED.getString("%target%:" + target.getUsername()));
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }

        if (data.hasIgnored(target.getUniqueId(), player.getUsername())) {
            player.sendMessage(MessagesConfiguration.MESSAGE_TARGET_IGNORED.getString("%target%" + target.getUsername()));
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(player.getUniqueId())) {
            player.sendMessage(MessagesConfiguration.MESSAGE_MESSAGES_DISABLED.getString());
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(target.getUniqueId())) {
            player.sendMessage(MessagesConfiguration.MESSAGE_MESSAGES_DISABLED_TARGET.getString("%target%:" + target.getUsername()));
            return;
        }

        if (target == player) {
            player.sendMessage(MessagesConfiguration.MESSAGE_SELF_DISABLED.getString());
            return;
        }
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        data.getWaitingReply().put(target.getUniqueId(), player.getUniqueId());

        player.sendMessage(MessagesConfiguration.MESSAGE_SENDER_FORMAT.getString("%target%:" + target.getUsername(), "%message%:" + message, "%player_server%:" + player.getCurrentServer().get().getServerInfo().getName(), "%target_server%:" + target.getCurrentServer().get().getServerInfo().getName()));
        target.sendMessage(MessagesConfiguration.MESSAGE_TARGET_FORMAT.getString("%target%:" + player.getUsername(), "%message%:" + message, "%player_server%:" + player.getCurrentServer().get().getServerInfo().getName(), "%target_server%:" + target.getCurrentServer().get().getServerInfo().getName()));

        Utils.sendSpyMessage(player, data, target, message, plugin);
    }
}
