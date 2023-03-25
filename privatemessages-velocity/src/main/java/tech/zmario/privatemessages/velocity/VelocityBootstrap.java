package tech.zmario.privatemessages.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.byteflux.libby.LibraryManager;
import net.byteflux.libby.VelocityLibraryManager;
import org.slf4j.LoggerFactory;
import tech.zmario.privatemessages.common.platform.Platform;
import tech.zmario.privatemessages.common.platform.PlatformType;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;
import tech.zmario.privatemessages.common.plugin.bootstrap.PrivateMessagesBootstrap;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.Logger;

@Plugin(
        id = "privatemessages",
        name = "PrivateMessages",
        version = "2.1.1",
        authors = {"zMario"},
        description = "A plugin that allows you to send private messages to other players."
)
@Getter
public class VelocityBootstrap implements PrivateMessagesBootstrap {

    private final PrivateMessagesPlugin plugin;
    private final ProxyServer proxyServer;
    private final Logger logger;

    private final Platform platform = new Platform(PlatformType.VELOCITY, "2.1.1");
    private LibraryManager libraryManager;
    private File dataFolder;

    @Inject
    public VelocityBootstrap(ProxyServer proxyServer, Logger logger) {
        this.plugin = new VelocityPlugin(this);
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        PluginDescription pluginDescription = proxyServer.getPluginManager().getPlugin("privatemessages").get().getDescription();
        File serverJar;
        try {
            serverJar = new File(Plugin.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        File folder = new File(serverJar.getParentFile() + "/plugins/", pluginDescription.getId());

        if (!folder.exists()) folder.mkdirs();

        dataFolder = folder;

        libraryManager = new VelocityLibraryManager<>(LoggerFactory.getLogger("PrivateMessages"),
                dataFolder.toPath(),
                getProxyServer().getPluginManager(),
                this);

        plugin.enable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        plugin.disable();
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public File getPluginFolder() {
        return dataFolder;
    }

    @Override
    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    @Override
    public InputStream getResource(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    @Override
    public Logger getPluginLogger() {
        return logger;
    }
}
