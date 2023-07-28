package tech.zmario.privatemessages.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.velocity.VelocityPlugin;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class VelocityCommand implements SimpleCommand {

    private final VelocityPlugin plugin;
    private final Command command;

    @Override
    public void execute(Invocation invocation) {
        command.execute(plugin.getSenderFactory().wrapSender(invocation.source()), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {

        if (!(invocation.source() instanceof Player)) {
            return Collections.emptyList();
        }

        return command.suggest(plugin.getSenderFactory().wrapSender(invocation.source()), invocation.arguments());
    }
}
