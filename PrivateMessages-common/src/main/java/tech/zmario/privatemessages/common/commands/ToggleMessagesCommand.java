package tech.zmario.privatemessages.common.commands;

import lombok.RequiredArgsConstructor;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.configuration.enums.MessagesConfiguration;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.plugin.AbstractPrivateMessagesPlugin;

import java.util.List;

@RequiredArgsConstructor
public class ToggleMessagesCommand implements Command {

    private final AbstractPrivateMessagesPlugin plugin;

    @Override
    public void execute(Sender sender, String[] args) {
        if (plugin.getSenderFactory().isConsole(sender)) {
            MessagesConfiguration.NO_CONSOLE.sendMessage(sender, plugin);
            return;
        }

        if (!SettingsConfiguration.COMMAND_TOGGLE_MESSAGES_PERMISSION.getString(plugin).isEmpty() &&
                !sender.hasPermission(SettingsConfiguration.COMMAND_TOGGLE_MESSAGES_PERMISSION.getString(plugin))) {
            MessagesConfiguration.NO_PERMISSION.sendMessage(sender, plugin);
            return;
        }

        if (!plugin.getDataStorage().hasMessagesToggled(sender.getUniqueId())) {
            plugin.getDataStorage().setMessagesToggled(sender.getUniqueId(), true);
            MessagesConfiguration.TOGGLE_MESSAGES_OFF.sendMessage(sender, plugin);
        } else {
            plugin.getDataStorage().setMessagesToggled(sender.getUniqueId(), false);
            MessagesConfiguration.TOGGLE_MESSAGES_ON.sendMessage(sender, plugin);
        }

        plugin.getSqlManager().updateMessagesToggled(sender.getUniqueId(),
                plugin.getDataStorage().hasMessagesToggled(sender.getUniqueId()));
    }

    @Override
    public String getName() {
        return SettingsConfiguration.COMMAND_TOGGLE_MESSAGES_NAME.getString(plugin);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMAND_TOGGLE_MESSAGES_PERMISSION.getString(plugin);
    }

    @Override
    public List<String> getAliases() {
        return SettingsConfiguration.COMMAND_TOGGLE_MESSAGES_ALIASES.getStringList(plugin);
    }
}
