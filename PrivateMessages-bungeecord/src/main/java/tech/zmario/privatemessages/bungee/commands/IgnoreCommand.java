package tech.zmario.privatemessages.bungee.commands;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import tech.zmario.privatemessages.bungee.PrivateMessagesBungee;
import tech.zmario.privatemessages.bungee.commands.interfaces.SubCommand;
import tech.zmario.privatemessages.bungee.commands.subcommands.AddSubCommand;
import tech.zmario.privatemessages.bungee.commands.subcommands.ListSubCommand;
import tech.zmario.privatemessages.bungee.commands.subcommands.RemoveSubCommand;
import tech.zmario.privatemessages.bungee.enums.MessagesConfiguration;
import tech.zmario.privatemessages.bungee.enums.SettingsConfiguration;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class IgnoreCommand extends Command implements TabExecutor {

    private final Map<String, SubCommand> subCommands;

    public IgnoreCommand(PrivateMessagesBungee plugin) {
        super(SettingsConfiguration.COMMAND_IGNORE_NAME.getString(), SettingsConfiguration.COMMAND_IGNORE_PERMISSION.getString(), SettingsConfiguration.COMMAND_IGNORE_ALIASES.getStringList().toArray(new String[0]));
        subCommands = Maps.newHashMap();
        subCommands.put(SettingsConfiguration.COMMAND_IGNORE_ADD_NAME.getString(), new AddSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMAND_IGNORE_REMOVE_NAME.getString(), new RemoveSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMAND_IGNORE_LIST_NAME.getString(), new ListSubCommand(plugin));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(MessagesConfiguration.NO_CONSOLE.getString());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!SettingsConfiguration.COMMAND_IGNORE_PERMISSION.getString().isEmpty() && !player.hasPermission(SettingsConfiguration.COMMAND_IGNORE_PERMISSION.getString())) {
            player.sendMessage(MessagesConfiguration.NO_PERMISSION.getString());
            return;
        }

        if (args.length > 0) {
            Optional<SubCommand> subCommand = Optional.ofNullable(subCommands.get(args[0].toLowerCase()));

            if (!subCommand.isPresent()) {
                for (String string : MessagesConfiguration.IGNORE_USAGE.getStringList()) {
                    player.sendMessage(TextComponent.fromLegacyText(string));
                }

                return;
            }

            if (!subCommand.get().getPermission().isEmpty() && !sender.hasPermission(subCommand.get().getPermission())) {
                player.sendMessage(MessagesConfiguration.NO_PERMISSION.getString());
                return;
            }

            subCommand.get().execute(sender, args);
            return;
        }

        for (String string : MessagesConfiguration.IGNORE_USAGE.getStringList())
            player.sendMessage(TextComponent.fromLegacyText(string));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toSet());
        }
        return ImmutableSet.of();
    }
}
