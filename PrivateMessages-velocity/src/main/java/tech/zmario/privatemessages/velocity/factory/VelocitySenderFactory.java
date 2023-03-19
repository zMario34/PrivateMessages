package tech.zmario.privatemessages.velocity.factory;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import tech.zmario.privatemessages.common.factory.SenderFactory;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.factory.user.impl.BaseSender;
import tech.zmario.privatemessages.velocity.VelocityPlugin;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class VelocitySenderFactory implements SenderFactory<CommandSource> {

    private final VelocityPlugin plugin;

    @Override
    public Optional<Sender> getSender(String name) {
        return getProxyUser(name).map(this::getSender);
    }

    @Override
    public Optional<Sender> getSender(UUID uuid) {
        return plugin.getBootstrap().getProxyServer().getPlayer(uuid).map(this::getSender);
    }

    @Override
    public Sender getSender(CommandSource source) {
        if (source instanceof Player) {
            Player player = (Player) source;
            return new BaseSender<>(player.getUniqueId(), player, this);
        }

        return new BaseSender<>(UUID.randomUUID(), source, this);
    }

    private Optional<CommandSource> getProxyUser(String name) {
        Optional<Player> player = plugin.getBootstrap().getProxyServer().getPlayer(name);

        if (player.isPresent()) {
            return player.map(CommandSource.class::cast);
        }
        return player.map(CommandSource.class::cast).or(Optional::empty);
    }

    @Override
    public CommandSource getConsoleSender() {
        return plugin.getBootstrap().getProxyServer().getConsoleCommandSource();
    }

    @Override
    public String getName(CommandSource CommandSource) {
        if (CommandSource instanceof Player) {
            return ((Player) CommandSource).getUsername();
        }

        return "Console";
    }

    @Override
    public String getServerName(Sender sender) {
        return plugin.getBootstrap().getProxyServer().getServer(sender.getName())
                .map(server -> server.getServerInfo().getName())
                .orElse("Unknown");
    }

    @Override
    public void sendMessage(CommandSource CommandSource, Component component) {
        CommandSource.sendMessage(component);
    }

    @Override
    public boolean hasPermission(CommandSource sender, String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isPlayerOnline(String name) {
        return plugin.getBootstrap().getProxyServer().getPlayer(name).isPresent();
    }

    @Override
    public boolean isConsole(Sender sender) {
        return getProxyUser(sender.getName()).map(CommandSource -> CommandSource instanceof ConsoleCommandSource).orElse(false);
    }
}
