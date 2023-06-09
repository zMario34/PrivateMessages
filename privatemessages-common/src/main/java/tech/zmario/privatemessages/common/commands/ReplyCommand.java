package tech.zmario.privatemessages.common.commands;

import lombok.RequiredArgsConstructor;
import org.simpleyaml.configuration.file.FileConfiguration;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.configuration.enums.MessagesConfiguration;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.objects.Placeholder;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;
import tech.zmario.privatemessages.common.storage.DataStorage;
import tech.zmario.privatemessages.common.utils.Utils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ReplyCommand implements Command {

    private final PrivateMessagesPlugin plugin;

    @Override
    public void execute(Sender sender, String[] args) {
        if (plugin.getSenderFactory().isConsole(sender)) {
            MessagesConfiguration.NO_CONSOLE.sendMessage(sender, plugin);
            return;
        }

        if (!SettingsConfiguration.COMMAND_REPLY_PERMISSION.getString(plugin).isEmpty() &&
                !sender.hasPermission(SettingsConfiguration.COMMAND_REPLY_PERMISSION.getString(plugin))) {
            MessagesConfiguration.NO_PERMISSION.sendMessage(sender, plugin);
            return;
        }

        DataStorage data = plugin.getDataStorage();

        if (!data.getWaitingReply().containsKey(sender.getUniqueId())) {
            MessagesConfiguration.REPLY_NOT_IN_CONVERSATION.sendMessage(sender, plugin);
            return;
        }

        if (args.length == 0) {
            MessagesConfiguration.REPLY_USAGE.sendMessage(sender, plugin);
            return;
        }
        UUID targetUUID = data.getWaitingReply().get(sender.getUniqueId());
        Optional<Sender> targetOptional = plugin.getSenderFactory().wrapSender(targetUUID);

        if (!targetOptional.isPresent()) {
            MessagesConfiguration.REPLY_PLAYER_NOT_ONLINE.sendMessage(sender, plugin);
            return;
        }
        Sender target = targetOptional.get();

        if (data.hasIgnored(sender.getUniqueId(), target.getName())) {
            MessagesConfiguration.REPLY_PLAYER_IGNORED.sendMessage(sender, plugin,
                    new Placeholder("target", target.getName()));
            data.getWaitingReply().remove(sender.getUniqueId());
            return;
        }

        if (data.hasIgnored(target.getUniqueId(), sender.getName())) {
            MessagesConfiguration.REPLY_TARGET_IGNORED.sendMessage(sender, plugin,
                    new Placeholder("target", target.getName()));
            data.getWaitingReply().remove(sender.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(sender.getUniqueId())) {
            MessagesConfiguration.REPLY_MESSAGES_DISABLED.sendMessage(sender, plugin);
            data.getWaitingReply().remove(sender.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(target.getUniqueId())) {
            MessagesConfiguration.REPLY_MESSAGES_DISABLED_TARGET.sendMessage(sender, plugin,
                    new Placeholder("target", target.getName()));
            data.getWaitingReply().remove(sender.getUniqueId());
            return;
        }
        String message = String.join(" ", args);
        String senderServerName = plugin.getSenderFactory().getServerName(sender);
        String targetServerName = plugin.getSenderFactory().getServerName(target);
        FileConfiguration config = plugin.getConfigManager().getConfig();

        MessagesConfiguration.REPLY_SENDER_FORMAT.sendMessage(sender, plugin,
                new Placeholder("target", target.getName()),
                new Placeholder("message", message),
                new Placeholder("player_server", Utils.getServerDisplay(senderServerName, config)),
                new Placeholder("target_server", Utils.getServerDisplay(targetServerName, config)));

        MessagesConfiguration.REPLY_TARGET_FORMAT.sendMessage(target, plugin,
                new Placeholder("target", sender.getName()),
                new Placeholder("message", message),
                new Placeholder("player_server", Utils.getServerDisplay(targetServerName, config)),
                new Placeholder("target_server", Utils.getServerDisplay(senderServerName, config)));

        plugin.sendSpyMessage(sender, target, message);
        data.getWaitingReply().put(target.getUniqueId(), sender.getUniqueId());
    }

    @Override
    public String getName() {
        return SettingsConfiguration.COMMAND_REPLY_NAME.getString(plugin);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMAND_REPLY_PERMISSION.getString(plugin);
    }

    @Override
    public List<String> getAliases() {
        return SettingsConfiguration.COMMAND_REPLY_ALIASES.getStringList(plugin);
    }
}
