package tech.zmario.privatemessages.bungee.commands;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import tech.zmario.privatemessages.bungee.PrivateMessagesBungee;
import tech.zmario.privatemessages.bungee.enums.MessagesConfiguration;
import tech.zmario.privatemessages.bungee.enums.SettingsConfiguration;
import tech.zmario.privatemessages.bungee.utils.Utils;
import tech.zmario.privatemessages.common.storage.DataStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageCommand extends Command implements TabExecutor {

    private final PrivateMessagesBungee plugin;

    public MessageCommand(PrivateMessagesBungee plugin) {
        super(SettingsConfiguration.COMMAND_MESSAGE_NAME.getString(), null, SettingsConfiguration.COMMAND_MESSAGE_ALIASES.getStringList().toArray(new String[0]));
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase(SettingsConfiguration.COMMAND_RELOAD_NAME.getString()) && sender.hasPermission(SettingsConfiguration.COMMAND_RELOAD_PERMISSION.getString())) {
            plugin.getConfigManager().reloadAll();
            sender.sendMessage(MessagesConfiguration.CONFIGURATIONS_RELOADED.getString());
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(MessagesConfiguration.NO_CONSOLE.getString());
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!SettingsConfiguration.COMMAND_MESSAGE_PERMISSION.getString().isEmpty() && !player.hasPermission(SettingsConfiguration.COMMAND_MESSAGE_PERMISSION.getString())) {
            player.sendMessage(MessagesConfiguration.NO_PERMISSION.getString());
            return;
        }

        if (!player.hasPermission(SettingsConfiguration.COMMAND_MESSAGE_PERMISSION.getString())) {
            player.sendMessage(MessagesConfiguration.NO_PERMISSION.getString());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessagesConfiguration.MESSAGE_USAGE.getString());
            return;
        }

        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(MessagesConfiguration.MESSAGE_PLAYER_NOT_ONLINE.getString("%target%:" + args[0]));
            return;
        }

        final DataStorage data = plugin.getStorage();

        if (data.hasIgnored(player.getUniqueId(), target.getName())) {
            player.sendMessage(MessagesConfiguration.MESSAGE_PLAYER_IGNORED.getString("%target%:" + target.getName()));
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }

        if (data.hasIgnored(target.getUniqueId(), player.getName())) {
            player.sendMessage(MessagesConfiguration.MESSAGE_TARGET_IGNORED.getString("%target%" + target.getName()));
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(player.getUniqueId())) {
            player.sendMessage(MessagesConfiguration.MESSAGE_MESSAGES_DISABLED.getString());
            data.getWaitingReply().remove(player.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(target.getUniqueId())) {
            player.sendMessage(MessagesConfiguration.MESSAGE_MESSAGES_DISABLED_TARGET.getString("%target%:" + target.getName()));
            return;
        }

        if (target == player) {
            player.sendMessage(MessagesConfiguration.MESSAGE_SELF_DISABLED.getString());
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        data.getWaitingReply().put(target.getUniqueId(), player.getUniqueId());

        player.sendMessage(MessagesConfiguration.MESSAGE_SENDER_FORMAT.getString("%target%:" + target.getName(), "%message%:" + message, "%player_server%:" + player.getServer().getInfo().getName(), "%target_server%:" + target.getServer().getInfo().getName()));
        target.sendMessage(MessagesConfiguration.MESSAGE_TARGET_FORMAT.getString("%target%:" + player.getName(), "%message%:" + message, "%player_server%:" + player.getServer().getInfo().getName(), "%target_server%:" + target.getServer().getInfo().getName()));

        Utils.sendSpyMessage(player, data, target, message, plugin);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toSet());
        }

        return ImmutableSet.of();
    }
}
