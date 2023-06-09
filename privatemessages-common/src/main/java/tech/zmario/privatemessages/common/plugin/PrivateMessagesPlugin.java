package tech.zmario.privatemessages.common.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import tech.zmario.privatemessages.common.configuration.ConfigManager;
import tech.zmario.privatemessages.common.configuration.enums.MessagesConfiguration;
import tech.zmario.privatemessages.common.factory.SenderFactory;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.objects.Placeholder;
import tech.zmario.privatemessages.common.plugin.bootstrap.PrivateMessagesBootstrap;
import tech.zmario.privatemessages.common.sql.SQLManager;
import tech.zmario.privatemessages.common.storage.DataStorage;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class PrivateMessagesPlugin {

    private final PrivateMessagesBootstrap bootstrap;
    
    private ConfigManager configManager;
    private DataStorage dataStorage;
    private SQLManager sqlManager;

    private boolean legacyMessaging;

    public void enable() {
        long start = System.currentTimeMillis();

        configManager = new ConfigManager(bootstrap);
        configManager.load();

        loadLibraries();

        bootstrap.getPluginLogger().info(String.format("Loading PrivateMessages (v%s - Platform: %s)",
                bootstrap.getPlatform().getVersion(),
                bootstrap.getPlatform().getPlatformType().getName())
        );

        dataStorage = new DataStorage();
        sqlManager = new SQLManager(this);

        registerCommands();
        registerPlayers();
        finishLoad();

        String messagingType = configManager.getMessages().getString("messages-type");
        legacyMessaging = messagingType != null && messagingType.equalsIgnoreCase("ampersand");

        long end = System.currentTimeMillis();
        bootstrap.getPluginLogger().info("PrivateMessages has been loaded in " + (end - start) + "ms");
    }

    public void disable() {
        bootstrap.getPluginLogger().info("PrivateMessages is shutting down...");
        sqlManager.disable();
    }

    public PrivateMessagesBootstrap getBootstrap() {
        return bootstrap;
    }

    public Component colorize(String message) {
        return legacyMessaging ?
                LegacyComponentSerializer.legacyAmpersand().deserialize(message) :
                MiniMessage.miniMessage().deserialize(message);
    }

    public void sendSpyMessage(Sender player, Sender receiver, String message) {
        getOnlineUsers().stream()
                .filter(sender -> sender.hasPermission("privatemessages.spy"))
                .forEach(sender -> MessagesConfiguration.SOCIAL_SPY_FORMAT.sendMessage(sender,
                        this,
                        new Placeholder("player", player.getName()),
                        new Placeholder("target", receiver.getName()),
                        new Placeholder("message", message))
                );
    }

    protected abstract void finishLoad();

    protected abstract void registerCommands();

    protected abstract void registerPlayers();

    protected abstract void loadLibraries();

    public abstract SenderFactory<?> getSenderFactory();

    public abstract List<Sender> getOnlineUsers();
}