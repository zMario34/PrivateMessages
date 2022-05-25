package tech.zmario.privatemessages.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import tech.zmario.privatemessages.velocity.PrivateMessagesVelocity;
import tech.zmario.privatemessages.velocity.enums.MessagesConfiguration;

public class ToggleMessagesCommand implements SimpleCommand {

    private final PrivateMessagesVelocity plugin;

    public ToggleMessagesCommand(PrivateMessagesVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player)) {
            source.sendMessage(MessagesConfiguration.NO_CONSOLE.getString());
            return;
        }
        final Player player = (Player) source;
        if (!plugin.getStorage().hasMessagesToggled(player.getUniqueId())) {
            plugin.getStorage().setMessagesToggled(player.getUniqueId(), true);
            player.sendMessage(MessagesConfiguration.TOGGLE_MESSAGES_OFF.getString());
        } else {
            plugin.getStorage().setMessagesToggled(player.getUniqueId(), false);
            player.sendMessage(MessagesConfiguration.TOGGLE_MESSAGES_ON.getString());
        }
        plugin.getDatabaseManager().updateMessagesToggled(player, plugin.getStorage().hasMessagesToggled(player.getUniqueId()));
    }
}
