package tech.zmario.privatemessages.common.commands;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.simpleyaml.configuration.file.FileConfiguration;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.configuration.enums.MessagesConfiguration;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.objects.Placeholder;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;
import tech.zmario.privatemessages.common.storage.DataStorage;
import tech.zmario.privatemessages.common.utils.StringUtil;
import tech.zmario.privatemessages.common.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MessageCommand implements Command {

    private final PrivateMessagesPlugin plugin;

    @Override
    public void execute(Sender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase(SettingsConfiguration.COMMAND_RELOAD_NAME.getString(plugin))
                && sender.hasPermission(SettingsConfiguration.COMMAND_RELOAD_PERMISSION.getString(plugin))) {
            plugin.getConfigManager().reload();

            MessagesConfiguration.CONFIGURATIONS_RELOADED.sendMessage(sender, plugin);
            return;
        }

        if (plugin.getSenderFactory().isConsole(sender)) {
            MessagesConfiguration.NO_CONSOLE.sendMessage(sender, plugin);
            return;
        }

        if (!SettingsConfiguration.COMMAND_MESSAGE_PERMISSION.getString(plugin).isEmpty()
                && !sender.hasPermission(SettingsConfiguration.COMMAND_MESSAGE_PERMISSION.getString(plugin))) {
            MessagesConfiguration.NO_PERMISSION.sendMessage(sender, plugin);
            return;
        }

        if (args.length < 2) {
            MessagesConfiguration.MESSAGE_USAGE.sendMessage(sender, plugin);
            return;
        }
        Optional<Sender> targetOptional = plugin.getSenderFactory().wrapSender(args[0]);

        if (!targetOptional.isPresent()) {
            MessagesConfiguration.MESSAGE_PLAYER_NOT_ONLINE.sendMessage(sender, plugin,
                    new Placeholder("target", args[0]));
            return;
        }
        Sender target = targetOptional.get();
        DataStorage data = plugin.getDataStorage();

        if (data.hasIgnored(sender.getUniqueId(), target.getName())) {
            MessagesConfiguration.MESSAGE_PLAYER_IGNORED.sendMessage(sender, plugin,
                    new Placeholder("target", target.getName()));
            data.getWaitingReply().remove(sender.getUniqueId());
            return;
        }

        if (data.hasIgnored(target.getUniqueId(), sender.getName())) {
            MessagesConfiguration.MESSAGE_TARGET_IGNORED.sendMessage(sender, plugin,
                    new Placeholder("target", target.getName()));
            data.getWaitingReply().remove(sender.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(sender.getUniqueId())) {
            MessagesConfiguration.MESSAGE_MESSAGES_DISABLED.sendMessage(sender, plugin);
            data.getWaitingReply().remove(sender.getUniqueId());
            return;
        }

        if (data.hasMessagesToggled(target.getUniqueId())) {
            MessagesConfiguration.MESSAGE_MESSAGES_DISABLED_TARGET.sendMessage(sender, plugin,
                    new Placeholder("target", target.getName()));
            return;
        }

        if (target.equals(sender)) {
            MessagesConfiguration.MESSAGE_SELF_DISABLED.sendMessage(sender, plugin);
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String senderServerName = plugin.getSenderFactory().getServerName(sender);
        String targetServerName = plugin.getSenderFactory().getServerName(target);
        FileConfiguration config = plugin.getConfigManager().getConfig();

        if (SettingsConfiguration.ANTI_SWEAR_ENABLE_CAPS_FILTER.getBoolean(plugin)
                && Utils.isCaps(message, SettingsConfiguration.ANTI_SWEAR_CAPS_FILTER_PERCENTAGE.getInt(plugin))) {
            message = message.toLowerCase();
        }

        if (SettingsConfiguration.ANTI_SWEAR_ENABLE_SWEAR_FILTER.getBoolean(plugin)) {
            message = Utils.filterSwear(message, SettingsConfiguration.ANTI_SWEAR_SWEAR_REGEX.getString(plugin));
        }

        MessagesConfiguration.MESSAGE_SENDER_FORMAT.sendMessage(sender, plugin,
                new Placeholder("target", target.getName()),
                new Placeholder("message", message),
                new Placeholder("player_server", Utils.getServerDisplay(senderServerName, config)),
                new Placeholder("target_server", Utils.getServerDisplay(targetServerName, config)));

        MessagesConfiguration.MESSAGE_TARGET_FORMAT.sendMessage(target, plugin,
                new Placeholder("target", sender.getName()),
                new Placeholder("message", message),
                new Placeholder("player_server", Utils.getServerDisplay(targetServerName, config)),
                new Placeholder("target_server", Utils.getServerDisplay(senderServerName, config)));

        if (plugin.getDataStorage().hasSoundToggled(target.getUniqueId()))
            target.playSound(Sound.sound(Key.key(SettingsConfiguration.MESSAGE_SOUND_KEY.getString(plugin)),
                    Sound.Source.valueOf(SettingsConfiguration.MESSAGE_SOUND_SOURCE.getString(plugin)),
                    SettingsConfiguration.MESSAGE_SOUND_VOLUME.getFloat(plugin),
                    SettingsConfiguration.MESSAGE_SOUND_PITCH.getFloat(plugin)));

        data.getWaitingReply().put(target.getUniqueId(), sender.getUniqueId());
        plugin.sendSpyMessage(sender, target, message);
    }

    @Override
    public List<String> suggest(Sender sender, String[] args) {
        if (args.length == 1 && !plugin.getSenderFactory().isConsole(sender)) {
            List<String> completions = plugin.getOnlineUsers().stream()
                    .map(Sender::getName)
                    .filter(name -> !name.equals(sender.getName()) &&
                            !plugin.getDataStorage().hasIgnored(sender.getUniqueId(), name))
                    .collect(Collectors.toList());

            completions.removeIf(s1 -> !StringUtil.startsWithIgnoreCase(s1, args[args.length - 1]));

            return completions;
        }

        return ImmutableList.of();
    }

    @Override
    public String getName() {
        return SettingsConfiguration.COMMAND_MESSAGE_NAME.getString(plugin);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMAND_MESSAGE_PERMISSION.getString(plugin);
    }

    @Override
    public List<String> getAliases() {
        return SettingsConfiguration.COMMAND_MESSAGE_ALIASES.getStringList(plugin);
    }
}
