package tech.zmario.privatemessages.bungee.commands;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
import tech.zmario.privatemessages.common.utils.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class IgnoreCommand extends Command implements TabExecutor {

    private final PrivateMessagesBungee plugin;
    private final Map<String, SubCommand> subCommands = Maps.newHashMap();

    public IgnoreCommand(PrivateMessagesBungee plugin) {
        super(SettingsConfiguration.COMMAND_IGNORE_NAME.getString(),
                SettingsConfiguration.COMMAND_IGNORE_PERMISSION.getString(),
                SettingsConfiguration.COMMAND_IGNORE_ALIASES.getStringList().toArray(new String[0]));

        this.plugin = plugin;

        subCommands.put(SettingsConfiguration.COMMAND_IGNORE_ADD_NAME.getString(), new AddSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMAND_IGNORE_REMOVE_NAME.getString(), new RemoveSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMAND_IGNORE_LIST_NAME.getString(), new ListSubCommand(plugin));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Audience audience = plugin.getAdventure().sender(sender);

        if (!(sender instanceof ProxiedPlayer)) {
            audience.sendMessage(MessagesConfiguration.NO_CONSOLE.getString());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!SettingsConfiguration.COMMAND_IGNORE_PERMISSION.getString().isEmpty() && !player.hasPermission(SettingsConfiguration.COMMAND_IGNORE_PERMISSION.getString())) {
            audience.sendMessage(MessagesConfiguration.NO_PERMISSION.getString());
            return;
        }

        if (args.length > 0) {
            Optional<SubCommand> subCommand = Optional.ofNullable(subCommands.get(args[0].toLowerCase()));

            if (!subCommand.isPresent()) {
                for (Component component : MessagesConfiguration.IGNORE_USAGE.getStringList()) {
                    audience.sendMessage(component);
                }

                return;
            }

            if (!subCommand.get().getPermission().isEmpty() && !sender.hasPermission(subCommand.get().getPermission())) {
                audience.sendMessage(MessagesConfiguration.NO_PERMISSION.getString());
                return;
            }

            subCommand.get().execute(sender, args);
            return;
        }

        for (Component component : MessagesConfiguration.IGNORE_USAGE.getStringList())
            audience.sendMessage(component);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length == 2) {
                ProxiedPlayer player = (ProxiedPlayer) sender;

                List<String> completions = ProxyServer.getInstance().getPlayers().stream()
                        .map(ProxiedPlayer::getName)
                        .filter(name -> !name.equalsIgnoreCase(player.getName()) &&
                                !plugin.getStorage().hasIgnored(player.getUniqueId(), name))
                        .collect(Collectors.toList());

                completions.removeIf(s1 -> !StringUtil.startsWithIgnoreCase(s1, args[args.length - 1]));

                return completions;
            }

            if (args.length == 1) {
                List<String> completions = Lists.newArrayList(subCommands.keySet());
                completions.removeIf(s1 -> !StringUtil.startsWithIgnoreCase(s1, args[args.length - 1]));

                return completions;
            }
        }

        return ImmutableSet.of();
    }
}
