package tech.zmario.privatemessages.bungee.factory;

import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tech.zmario.privatemessages.bungee.BungeeCordPlugin;
import tech.zmario.privatemessages.common.factory.SenderFactory;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.factory.user.impl.BaseSender;

import java.util.Optional;
import java.util.UUID;

public class BungeeSenderFactory implements SenderFactory<CommandSender> {

    private final BungeeCordPlugin plugin;
    private final BungeeAudiences audiences;

    public BungeeSenderFactory(BungeeCordPlugin plugin) {
        this.plugin = plugin;
        audiences = BungeeAudiences.create(plugin.getBootstrap());
    }

    @Override
    public Optional<Sender> getSender(String name) {
        return getProxyUser(name).map(this::getSender);
    }

    @Override
    public Optional<Sender> getSender(UUID uuid) {
        return Optional.ofNullable(getSender(plugin.getBootstrap().getProxyServer().getPlayer(uuid)));
    }

    @Override
    public Sender getSender(CommandSender source) {
        if (source instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) source;
            return new BaseSender<>(player.getUniqueId(), player, this);
        }

        return new BaseSender<>(UUID.randomUUID(), source, this);
    }

    private Optional<ProxiedPlayer> getProxyUser(String name) {
        return Optional.ofNullable(plugin.getBootstrap().getProxyServer().getPlayer(name));
    }

    @Override
    public CommandSender getConsoleSender() {
        return plugin.getBootstrap().getProxyServer().getConsole();
    }

    @Override
    public String getName(CommandSender sender) {
        return sender.getName();
    }

    @Override
    public String getServerName(Sender sender) {
        return getProxyUser(sender.getName())
                .map(player -> player.getServer().getInfo().getName())
                .orElse("Unknown");
    }

    @Override
    public void sendMessage(CommandSender sender, Component component) {
        audiences.sender(sender).sendMessage(component);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isPlayerOnline(String name) {
        return plugin.getBootstrap().getProxyServer().getPlayer(name) != null;
    }

    @Override
    public boolean isConsole(Sender sender) {
        return getProxyUser(sender.getName())
                .map(commandSender -> commandSender.equals(plugin.getBootstrap().getProxyServer().getConsole()))
                .orElse(false);
    }
}
