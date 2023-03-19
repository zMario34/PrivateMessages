package tech.zmario.privatemessages.common.plugin;

import net.byteflux.libby.LibraryManager;
import net.kyori.adventure.text.Component;
import tech.zmario.privatemessages.common.configuration.ConfigManager;
import tech.zmario.privatemessages.common.factory.SenderFactory;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.hooks.RedisBungeeHook;
import tech.zmario.privatemessages.common.plugin.bootstrap.PrivateMessagesBootstrap;
import tech.zmario.privatemessages.common.storage.DataStorage;

import java.util.List;

public interface PrivateMessagesPlugin {

    PrivateMessagesBootstrap getBootstrap();

    DataStorage getDataStorage();

    LibraryManager getLibraryManager();

    ConfigManager getConfigManager();

    SenderFactory<?> getSenderFactory();

    List<Sender> getOnlineUsers();

    Component colorize(String message);

    void sendSpyMessage(Sender player, Sender receiver, String message);

}
