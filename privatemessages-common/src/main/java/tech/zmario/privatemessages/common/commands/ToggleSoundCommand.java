package tech.zmario.privatemessages.common.commands;

import lombok.RequiredArgsConstructor;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.configuration.enums.MessagesConfiguration;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.plugin.AbstractPrivateMessagesPlugin;

import java.util.List;

@RequiredArgsConstructor
public class ToggleSoundCommand implements Command {

    private final AbstractPrivateMessagesPlugin plugin;

    @Override
    public void execute(Sender sender, String[] args) {
        if (plugin.getSenderFactory().isConsole(sender)) {
            MessagesConfiguration.NO_CONSOLE.sendMessage(sender, plugin);
            return;
        }

        if (!SettingsConfiguration.COMMAND_TOGGLE_SOUND_PERMISSION.getString(plugin).isEmpty() &&
                !sender.hasPermission(SettingsConfiguration.COMMAND_TOGGLE_SOUND_PERMISSION.getString(plugin))) {
            MessagesConfiguration.NO_PERMISSION.sendMessage(sender, plugin);
            return;
        }

        if (!plugin.getDataStorage().hasSoundToggled(sender.getUniqueId())) {
            plugin.getDataStorage().setSoundToggled(sender.getUniqueId(), true);
            MessagesConfiguration.TOGGLE_SOUND_OFF.sendMessage(sender, plugin);
        } else {
            plugin.getDataStorage().setSoundToggled(sender.getUniqueId(), false);
            MessagesConfiguration.TOGGLE_SOUND_ON.sendMessage(sender, plugin);
        }

        plugin.getSqlManager().updateSoundToggled(sender.getUniqueId(),
                plugin.getDataStorage().hasSoundToggled(sender.getUniqueId()));
    }

    @Override
    public String getName() {
        return SettingsConfiguration.COMMAND_TOGGLE_SOUND_NAME.getString(plugin);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMAND_TOGGLE_SOUND_PERMISSION.getString(plugin);
    }

    @Override
    public List<String> getAliases() {
        return SettingsConfiguration.COMMAND_TOGGLE_SOUND_ALIASES.getStringList(plugin);
    }
}
