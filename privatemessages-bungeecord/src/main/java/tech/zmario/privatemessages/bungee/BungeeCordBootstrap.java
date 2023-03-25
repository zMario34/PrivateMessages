package tech.zmario.privatemessages.bungee;

import lombok.Getter;
import net.byteflux.libby.BungeeLibraryManager;
import net.byteflux.libby.LibraryManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import tech.zmario.privatemessages.common.platform.Platform;
import tech.zmario.privatemessages.common.platform.PlatformType;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;
import tech.zmario.privatemessages.common.plugin.bootstrap.PrivateMessagesBootstrap;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

@Getter
public final class BungeeCordBootstrap extends Plugin implements PrivateMessagesBootstrap {

    private final PrivateMessagesPlugin plugin = new BungeeCordPlugin(this);
    private final Platform platform = new Platform(PlatformType.BUNGEECORD, getDescription().getVersion());

    private final ProxyServer proxyServer = ProxyServer.getInstance();
    private LibraryManager libraryManager;

    @Override
    public void onEnable() {
        libraryManager = new BungeeLibraryManager(this);
        plugin.enable();
    }

    @Override
    public void onDisable() {
        plugin.disable();
    }

    @Override
    public File getPluginFolder() {
        return getDataFolder();
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    @Override
    public Logger getPluginLogger() {
        return getLogger();
    }

    @Override
    public InputStream getResource(String name) {
        return getResourceAsStream(name);
    }
}