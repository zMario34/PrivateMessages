package tech.zmario.privatemessages.common.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.commands.subcommands.AddSubCommand;
import tech.zmario.privatemessages.common.commands.subcommands.ListSubCommand;
import tech.zmario.privatemessages.common.commands.subcommands.RemoveSubCommand;
import tech.zmario.privatemessages.common.configuration.enums.MessagesConfiguration;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.plugin.AbstractPrivateMessagesPlugin;
import tech.zmario.privatemessages.common.utils.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class IgnoreCommand implements Command {

    private final AbstractPrivateMessagesPlugin plugin;
    private final Map<String, Command> subCommands = Maps.newHashMap();

    public IgnoreCommand(AbstractPrivateMessagesPlugin plugin) {
        this.plugin = plugin;

        subCommands.put(SettingsConfiguration.COMMAND_IGNORE_ADD_NAME.getString(plugin), new AddSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMAND_IGNORE_REMOVE_NAME.getString(plugin), new RemoveSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMAND_IGNORE_LIST_NAME.getString(plugin), new ListSubCommand(plugin));
    }

    @Override
    public void execute(Sender sender, String[] args) {
        if (plugin.getSenderFactory().isConsole(sender)) {
            MessagesConfiguration.NO_CONSOLE.sendMessage(sender, plugin);
            return;
        }

        if (!SettingsConfiguration.COMMAND_IGNORE_PERMISSION.getString(plugin).isEmpty() &&
                !sender.hasPermission(SettingsConfiguration.COMMAND_IGNORE_PERMISSION.getString(plugin))) {
            MessagesConfiguration.NO_PERMISSION.sendMessage(sender, plugin);
            return;
        }

        if (args.length > 0) {
            Optional<Command> subCommand = Optional.ofNullable(subCommands.get(args[0].toLowerCase()));

            if (!subCommand.isPresent()) {
                MessagesConfiguration.IGNORE_USAGE.sendMessage(sender, plugin);
                return;
            }

            if (!subCommand.get().getPermission().isEmpty() && !sender.hasPermission(subCommand.get().getPermission())) {
                MessagesConfiguration.NO_PERMISSION.sendMessage(sender, plugin);
                return;
            }

            subCommand.get().execute(sender, args);
            return;
        }

        MessagesConfiguration.IGNORE_USAGE.sendMessage(sender, plugin);
    }

    @Override
    public List<String> suggest(Sender sender, String[] args) {
        if (!plugin.getSenderFactory().isConsole(sender)) {
            if (args.length == 2) {
                List<String> completions = plugin.getOnlineUsers().stream()
                        .map(Sender::getName)
                        .filter(name -> !name.equalsIgnoreCase(sender.getName()) &&
                                !plugin.getDataStorage().hasIgnored(sender.getUniqueId(), name))
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

        return ImmutableList.of();
    }

    @Override
    public String getName() {
        return SettingsConfiguration.COMMAND_IGNORE_NAME.getString(plugin);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMAND_IGNORE_PERMISSION.getString(plugin);
    }

    @Override
    public List<String> getAliases() {
        return SettingsConfiguration.COMMAND_IGNORE_ALIASES.getStringList(plugin);
    }
}
