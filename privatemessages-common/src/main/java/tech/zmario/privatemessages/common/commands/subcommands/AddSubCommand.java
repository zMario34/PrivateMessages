package tech.zmario.privatemessages.common.commands.subcommands;

import lombok.RequiredArgsConstructor;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.configuration.enums.MessagesConfiguration;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.objects.Placeholder;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;

@RequiredArgsConstructor
public class AddSubCommand implements Command {

    private final PrivateMessagesPlugin plugin;

    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length < 2) {
            MessagesConfiguration.IGNORE_ADD_USAGE.sendMessage(sender, plugin);
            return;
        }

        if (args[1].equalsIgnoreCase(sender.getName())) {
            MessagesConfiguration.IGNORE_ADD_SELF_DISABLED.sendMessage(sender, plugin);
            return;
        }

        if (plugin.getDataStorage().hasIgnored(sender.getUniqueId(), args[1].toLowerCase())) {
            MessagesConfiguration.IGNORE_ADD_PLAYER_ALREADY_IGNORED.sendMessage(sender, plugin,
                    new Placeholder("target", args[1]));
            return;
        }

        plugin.getDataStorage().updateIgnore(sender.getUniqueId(), args[1].toLowerCase(), true);
        plugin.getSqlManager().addIgnore(sender.getUniqueId(), args[1].toLowerCase());

        MessagesConfiguration.IGNORE_ADD_PLAYER_ADDED.sendMessage(sender, plugin,
                new Placeholder("target", args[1]));
    }

    @Override
    public String getName() {
        return SettingsConfiguration.COMMAND_IGNORE_ADD_NAME.getString(plugin);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMAND_IGNORE_ADD_PERMISSION.getString(plugin);
    }
}
