package tech.zmario.privatemessages.common.configuration.enums;

import net.kyori.adventure.text.Component;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.objects.Placeholder;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;

import java.util.List;
import java.util.stream.Collectors;

public enum MessagesConfiguration {

    NO_CONSOLE("no-console"),

    NO_PERMISSION("no-permission"),
    CONFIGURATIONS_RELOADED("configurations-reloaded"),

    MESSAGE_USAGE("commands.message.usage"),
    MESSAGE_PLAYER_NOT_ONLINE("commands.message.player-not-online"),
    MESSAGE_MESSAGES_DISABLED("commands.message.messages-disabled"),
    MESSAGE_MESSAGES_DISABLED_TARGET("commands.message.target-messages-disabled"),
    MESSAGE_SELF_DISABLED("commands.message.self-disabled"),
    MESSAGE_PLAYER_IGNORED("commands.message.player-ignored"),
    MESSAGE_TARGET_IGNORED("commands.message.target-ignored"),
    MESSAGE_SENDER_FORMAT("commands.message.sender-format"),
    MESSAGE_TARGET_FORMAT("commands.message.target-format"),

    REPLY_USAGE("commands.reply.usage"),
    REPLY_NOT_IN_CONVERSATION("commands.reply.not-in-conversation"),
    REPLY_PLAYER_NOT_ONLINE("commands.reply.player-not-online"),
    REPLY_LITEBANS_TARGET_MUTED("commands.reply.litebans-target-muted"),
    REPLY_MESSAGES_DISABLED("commands.reply.messages-disabled"),
    REPLY_MESSAGES_DISABLED_TARGET("commands.reply.target-messages-disabled"),
    REPLY_PLAYER_IGNORED("commands.reply.player-ignored"),
    REPLY_TARGET_IGNORED("commands.reply.target-ignored"),
    REPLY_SENDER_FORMAT("commands.reply.sender-format"),
    REPLY_TARGET_FORMAT("commands.reply.target-format"),

    IGNORE_USAGE("commands.ignore.usage"),

    IGNORE_ADD_USAGE("commands.ignore.add.usage"),
    IGNORE_ADD_SELF_DISABLED("commands.ignore.add.self-disabled"),
    IGNORE_ADD_PLAYER_ALREADY_IGNORED("commands.ignore.add.player-already-ignored"),
    IGNORE_ADD_PLAYER_ADDED("commands.ignore.add.player-added"),

    IGNORE_REMOVE_USAGE("commands.ignore.remove.usage"),
    IGNORE_REMOVE_SELF_DISABLED("commands.ignore.remove.self-disabled"),
    IGNORE_REMOVE_PLAYER_NOT_IGNORED("commands.ignore.remove.player-not-ignored"),
    IGNORE_REMOVE_PLAYER_REMOVED("commands.ignore.remove.player-removed"),

    IGNORE_LIST_NOT_A_NUMBER("commands.ignore.list.not-a-number"),
    IGNORE_LIST_EMPTY("commands.ignore.list.empty"),
    IGNORE_LIST_PAGE_NOT_EXIST("commands.ignore.list.page-not-exist"),
    IGNORE_LIST_HEADER("commands.ignore.list.header"),
    IGNORE_LIST_LINE("commands.ignore.list.line"),
    IGNORE_LIST_FOOTER("commands.ignore.list.footer"),

    SOCIAL_SPY_ON("commands.social-spy.toggle-on"),
    SOCIAL_SPY_OFF("commands.social-spy.toggle-off"),
    SOCIAL_SPY_FORMAT("commands.social-spy.format"),

    TOGGLE_MESSAGES_ON("commands.toggle-messages.toggle-on"),
    TOGGLE_MESSAGES_OFF("commands.toggle-messages.toggle-off"),

    TOGGLE_SOUND_ON("commands.toggle-sound.toggle-on"),
    TOGGLE_SOUND_OFF("commands.toggle-sound.toggle-off"),
    ;


    private final String path;

    MessagesConfiguration(String path) {
        this.path = path;
    }

    public Component getString(PrivateMessagesPlugin instance, Placeholder... placeholders) {
        String message = instance.getConfigManager().getMessages().getString(path);

        for (Placeholder placeholder : placeholders) {
            message = message.replace(placeholder.getPlaceholder(), placeholder.getValue());
        }

        return instance.colorize(message);
    }

    public List<Component> getStringList(PrivateMessagesPlugin instance, Placeholder... placeholders) {
        List<String> list = instance.getConfigManager().getMessages().getStringList(path);

        for (Placeholder placeholder : placeholders) {
            list.replaceAll(s -> s.replace(placeholder.getPlaceholder(), placeholder.getValue()));
        }

        return list.stream().map(instance::colorize).collect(Collectors.toList());
    }

    public void sendMessage(Sender sender, PrivateMessagesPlugin instance, Placeholder... placeholders) {
        Object configObject = instance.getConfigManager().getMessages().get(path);

        if (configObject instanceof List) {
            getStringList(instance, placeholders).forEach(sender::sendMessage);
            return;
        }

        sender.sendMessage(getString(instance, placeholders));
    }
}
