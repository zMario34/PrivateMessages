package tech.zmario.privatemessages.common.configuration.enums;

import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;

import java.util.List;

public enum SettingsConfiguration {

    // MySQL
    MYSQL_ENABLED("mysql.enabled"),
    MYSQL_DRIVER("mysql.driver"),
    MYSQL_HOST("mysql.host"),
    MYSQL_PORT("mysql.port"),
    MYSQL_USERNAME("mysql.username"),
    MYSQL_PASSWORD("mysql.password"),
    MYSQL_DATABASE("mysql.database"),
    MYSQL_SSL("mysql.ssl"),

    MESSAGE_SOUND_KEY("message-sound.key"),
    MESSAGE_SOUND_SOURCE("message-sound.source"),
    MESSAGE_SOUND_VOLUME("message-sound.volume"),
    MESSAGE_SOUND_PITCH("message-sound.pitch"),

    ANTI_SWEAR_ENABLE_CAPS_FILTER("anti-swear.enable-caps-filter"),
    ANTI_SWEAR_ENABLE_SWEAR_FILTER("anti-swear.enable-swear-filter"),
    ANTI_SWEAR_CAPS_FILTER_PERCENTAGE("anti-swear.caps-percent"),
    ANTI_SWEAR_SWEAR_REGEX("anti-swear.swear-regex"),

    COMMAND_RELOAD_NAME("commands.reload.name"),
    COMMAND_RELOAD_PERMISSION("commands.reload.permission"),

    COMMAND_MESSAGE_NAME("commands.message.name"),
    COMMAND_MESSAGE_PERMISSION("commands.message.permission"),
    COMMAND_MESSAGE_ALIASES("commands.message.aliases"),

    COMMAND_REPLY_NAME("commands.reply.name"),
    COMMAND_REPLY_PERMISSION("commands.reply.permission"),
    COMMAND_REPLY_ALIASES("commands.reply.aliases"),

    COMMAND_IGNORE_NAME("commands.ignore.name"),
    COMMAND_IGNORE_PERMISSION("commands.ignore.permission"),
    COMMAND_IGNORE_ALIASES("commands.ignore.aliases"),

    COMMAND_IGNORE_ADD_NAME("commands.ignore.add.name"),
    COMMAND_IGNORE_ADD_PERMISSION("commands.ignore.add.permission"),

    COMMAND_IGNORE_REMOVE_NAME("commands.ignore.remove.name"),
    COMMAND_IGNORE_REMOVE_PERMISSION("commands.ignore.remove.permission"),

    COMMAND_IGNORE_LIST_NAME("commands.ignore.list.name"),
    COMMAND_IGNORE_LIST_PERMISSION("commands.ignore.list.permission"),
    COMMAND_IGNORE_LIST_MAX_SIZE("commands.ignore.list.max-size"),

    COMMAND_SOCIAL_SPY_NAME("commands.social-spy.name"),
    COMMAND_SOCIAL_SPY_PERMISSION("commands.social-spy.permission"),
    COMMAND_SOCIAL_SPY_ALIASES("commands.social-spy.aliases"),

    COMMAND_TOGGLE_MESSAGES_NAME("commands.toggle-messages.name"),
    COMMAND_TOGGLE_MESSAGES_PERMISSION("commands.toggle-messages.permission"),
    COMMAND_TOGGLE_MESSAGES_ALIASES("commands.toggle-messages.aliases"),

    COMMAND_TOGGLE_SOUND_NAME("commands.toggle-sound.name"),
    COMMAND_TOGGLE_SOUND_PERMISSION("commands.toggle-sound.permission"),
    COMMAND_TOGGLE_SOUND_ALIASES("commands.toggle-sound.aliases"),
    ;

    private final String path;

    SettingsConfiguration(String path) {
        this.path = path;
    }

    public String getString(PrivateMessagesPlugin instance) {
        return instance.getConfigManager().getConfig().getString(path);
    }

    public int getInt(PrivateMessagesPlugin instance) {
        return instance.getConfigManager().getConfig().getInt(path);
    }

    public boolean getBoolean(PrivateMessagesPlugin instance) {
        return instance.getConfigManager().getConfig().getBoolean(path);
    }

    public List<String> getStringList(PrivateMessagesPlugin instance) {
        return instance.getConfigManager().getConfig().getStringList(path);
    }

    public float getFloat(PrivateMessagesPlugin plugin) {
        return (float) plugin.getConfigManager().getConfig().getDouble(path);
    }
}
