package tech.zmario.privatemessages.bungee;

import lombok.Getter;
import net.byteflux.libby.BungeeLibraryManager;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import tech.zmario.privatemessages.bungee.commands.*;
import tech.zmario.privatemessages.bungee.configuration.ConfigManager;
import tech.zmario.privatemessages.bungee.database.DatabaseManager;
import tech.zmario.privatemessages.bungee.listeners.BaseListeners;
import tech.zmario.privatemessages.common.configuration.Configuration;
import tech.zmario.privatemessages.common.storage.DataStorage;

@Getter
public final class PrivateMessagesBungee extends Plugin {

    @Getter
    private static PrivateMessagesBungee instance;
    private ConfigManager configManager;
    private BungeeLibraryManager libraryManager;
    private DataStorage storage;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        setupConfigurations();
        setupInstances();
        registerListeners(new BaseListeners(this));
        registerCommands(new MessageCommand(this), new ReplyCommand(this), new IgnoreCommand(this),
                new SocialSpyCommand(this), new ToggleMessagesCommand(this));
        getLogger().info("Enabled in " + (System.currentTimeMillis() - startTime) + "ms!");
    }

    private void registerCommands(Command... commands) {
        for (Command command : commands) {
            getProxy().getPluginManager().registerCommand(this, command);
        }
    }

    private void setupConfigurations() {
        configManager = new ConfigManager(this);
        configManager.create("config.yml");
        configManager.create("messages.yml");
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners)
            getProxy().getPluginManager().registerListener(this, listener);
    }

    private void setupInstances() {
        instance = this;
        storage = new DataStorage();
        libraryManager = new BungeeLibraryManager(this);
        libraryManager.addMavenCentral();
        //databaseManager = new DatabaseManager(this, SettingsConfiguration.MYSQL_ENABLED.getBoolean());
    }

    @Override
    public void onDisable() {
        configManager = null;
        storage = null;
        databaseManager.close();
        databaseManager = null;
        instance = null;
    }

    public Configuration getConfig() {
        return configManager.get("config.yml");
    }

    public Configuration getMessages() {
        return configManager.get("messages.yml");
    }
}