package tech.zmario.privatemessages.common.commands.subcommands;

import lombok.RequiredArgsConstructor;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.configuration.enums.MessagesConfiguration;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.objects.Placeholder;
import tech.zmario.privatemessages.common.plugin.AbstractPrivateMessagesPlugin;

import java.util.List;

@RequiredArgsConstructor
public class ListSubCommand implements Command {

    private final AbstractPrivateMessagesPlugin plugin;

    @Override
    public void execute(Sender sender, String[] args) {
        List<String> ignoredLists = plugin.getDataStorage().getUsers().get(sender.getUniqueId()).getIgnoredPlayers();

        int page = 0;

        if (args.length == 2) {
            try {

                page = Integer.parseInt(args[1]) - 1;

                if (page < 0) {
                    MessagesConfiguration.IGNORE_LIST_NOT_A_NUMBER.sendMessage(sender, plugin);
                    return;
                }
            } catch (NumberFormatException e) {
                MessagesConfiguration.IGNORE_LIST_NOT_A_NUMBER.sendMessage(sender, plugin);
                return;
            }
        }

        if (ignoredLists.isEmpty()) {
            MessagesConfiguration.IGNORE_LIST_EMPTY.sendMessage(sender, plugin);
            return;
        }

        int start = page * SettingsConfiguration.COMMAND_IGNORE_LIST_MAX_SIZE.getInt(plugin);
        int end = start + SettingsConfiguration.COMMAND_IGNORE_LIST_MAX_SIZE.getInt(plugin);

        if (end > ignoredLists.size()) {
            end = ignoredLists.size();
        }

        int maxPage = ignoredLists.size() / SettingsConfiguration.COMMAND_IGNORE_LIST_MAX_SIZE.getInt(plugin) + 1;

        Placeholder pagePlaceholder = new Placeholder("page", (page + 1) + "");
        Placeholder maxPagePlaceholder = new Placeholder("max-page", maxPage + "");

        if (page >= maxPage) {
            MessagesConfiguration.IGNORE_LIST_PAGE_NOT_EXIST.sendMessage(sender, plugin,
                    pagePlaceholder, maxPagePlaceholder);
            return;
        }

        MessagesConfiguration.IGNORE_LIST_HEADER.sendMessage(sender, plugin,
                pagePlaceholder, maxPagePlaceholder);

        for (int i = start; i < end; i++) {
            MessagesConfiguration.IGNORE_LIST_LINE.sendMessage(sender, plugin,
                    new Placeholder("index", (i + 1) + ""),
                    new Placeholder("player", ignoredLists.get(i)));
        }

        MessagesConfiguration.IGNORE_LIST_FOOTER.sendMessage(sender, plugin,
                pagePlaceholder, maxPagePlaceholder);
    }

    @Override
    public String getName() {
        return SettingsConfiguration.COMMAND_IGNORE_LIST_NAME.getString(plugin);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMAND_IGNORE_LIST_PERMISSION.getString(plugin);
    }
}
