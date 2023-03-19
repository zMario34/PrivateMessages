package tech.zmario.privatemessages.bungee;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import tech.zmario.privatemessages.bungee.commands.BungeeCommand;
import tech.zmario.privatemessages.bungee.factory.BungeeSenderFactory;
import tech.zmario.privatemessages.bungee.listeners.ConnectionListener;
import tech.zmario.privatemessages.common.commands.*;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.plugin.AbstractPrivateMessagesPlugin;
import tech.zmario.privatemessages.common.plugin.bootstrap.PrivateMessagesBootstrap;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BungeeCordPlugin extends AbstractPrivateMessagesPlugin {

    private final BungeeCordBootstrap bootstrap;
    private BungeeSenderFactory senderFactory;

    public BungeeCordPlugin(PrivateMessagesBootstrap bootstrap) {
        super(bootstrap);
        this.bootstrap = (BungeeCordBootstrap) bootstrap;
    }

    @Override
    protected void finishLoad() {
        senderFactory = new BungeeSenderFactory(this);

        bootstrap.getProxyServer().getPluginManager().registerListener(bootstrap, new ConnectionListener(this));
    }

    @Override
    public BungeeSenderFactory getSenderFactory() {
        return senderFactory;
    }

    @Override
    public List<Sender> getOnlineUsers() {
        return bootstrap.getProxyServer().getPlayers()
                .stream()
                .map(senderFactory::getSender)
                .collect(Collectors.toList());
    }

    @Override
    public LibraryManager getLibraryManager() {
        return bootstrap.getLibraryManager();
    }

    @Override
    protected void registerCommands() {
        List<Command> commands = Lists.newArrayList(new MessageCommand(this),
                new ReplyCommand(this),
                new IgnoreCommand(this),
                new SocialSpyCommand(this),
                new ToggleMessagesCommand(this));

        commands.forEach(command -> bootstrap.getProxyServer().getPluginManager().registerCommand(bootstrap,
                new BungeeCommand(this, command)));
    }

    @Override
    protected void loadLibraries() {
        LibraryManager libraryManager = getLibraryManager();
        Library hikariCp = Library.builder().groupId("com{}zaxxer").artifactId("HikariCP").version("4.0.3").build();
        Library miniMessage = Library.builder().groupId("net{}kyori").artifactId("adventure-text-minimessage").version("4.13.0").build();
        Library textSerializer = Library.builder().groupId("net{}kyori").artifactId("adventure-text-serializer-legacy").version("4.13.0").build();

        libraryManager.addMavenCentral();

        if (SettingsConfiguration.MYSQL_ENABLED.getBoolean(this)) {
            Library mysqlConnector = Library.builder()
                    .groupId("mysql")
                    .artifactId("mysql-connector-java")
                    .version("8.0.19")
                    .build();

            libraryManager.loadLibrary(mysqlConnector);
        } else {
            Library sqLite = Library.builder()
                    .groupId("org{}xerial")
                    .artifactId("sqlite-jdbc")
                    .version("3.40.1.0")
                    .build();

            libraryManager.loadLibrary(sqLite);
        }

        libraryManager.loadLibrary(hikariCp);
        libraryManager.loadLibrary(miniMessage);
        libraryManager.loadLibrary(textSerializer);
    }
}
