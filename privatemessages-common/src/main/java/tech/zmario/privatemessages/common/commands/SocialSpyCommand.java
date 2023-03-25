package tech.zmario.privatemessages.common.commands;

import lombok.RequiredArgsConstructor;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.configuration.enums.MessagesConfiguration;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;

import java.util.List;

@RequiredArgsConstructor
public class SocialSpyCommand implements Command {

    private final PrivateMessagesPlugin plugin;

    @Override
    public void execute(Sender sender, String[] args) {
        if (plugin.getSenderFactory().isConsole(sender)) {
            MessagesConfiguration.NO_CONSOLE.sendMessage(sender, plugin);
            return;
        }
        if (!sender.hasPermission(SettingsConfiguration.COMMAND_SOCIAL_SPY_PERMISSION.getString(plugin))) {
            MessagesConfiguration.NO_PERMISSION.sendMessage(sender, plugin);
            return;
        }

        if (plugin.getDataStorage().hasSocialSpy(sender.getUniqueId())) {
            plugin.getDataStorage().setSocialSpy(sender.getUniqueId(), false);

            MessagesConfiguration.SOCIAL_SPY_OFF.sendMessage(sender, plugin);
        } else {
            plugin.getDataStorage().setSocialSpy(sender.getUniqueId(), true);

            MessagesConfiguration.SOCIAL_SPY_ON.sendMessage(sender, plugin);
        }

        plugin.getSqlManager().updateSocialSpy(sender.getUniqueId(),
                plugin.getDataStorage().hasSocialSpy(sender.getUniqueId()));
    }

    @Override
    public String getName() {
        return SettingsConfiguration.COMMAND_SOCIAL_SPY_NAME.getString(plugin);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMAND_SOCIAL_SPY_PERMISSION.getString(plugin);
    }

    @Override
    public List<String> getAliases() {
        return SettingsConfiguration.COMMAND_SOCIAL_SPY_ALIASES.getStringList(plugin);
    }
}
