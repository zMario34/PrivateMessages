package tech.zmario.privatemessages.common.plugin.bootstrap;

import net.byteflux.libby.LibraryManager;
import tech.zmario.privatemessages.common.platform.Platform;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public interface PrivateMessagesBootstrap {

    Platform getPlatform();

    File getPluginFolder();

    LibraryManager getLibraryManager();

    Logger getPluginLogger();

    default InputStream getResource(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

}
