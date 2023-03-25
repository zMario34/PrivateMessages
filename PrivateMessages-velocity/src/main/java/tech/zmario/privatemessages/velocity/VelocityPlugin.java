package tech.zmario.privatemessages.velocity;

import com.google.common.collect.Lists;
import com.velocitypowered.api.command.CommandMeta;
import lombok.Getter;
import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import tech.zmario.privatemessages.common.commands.*;
import tech.zmario.privatemessages.common.commands.interfaces.Command;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.factory.user.Sender;
import tech.zmario.privatemessages.common.plugin.AbstractPrivateMessagesPlugin;
import tech.zmario.privatemessages.common.plugin.bootstrap.PrivateMessagesBootstrap;
import tech.zmario.privatemessages.velocity.commands.VelocityCommand;
import tech.zmario.privatemessages.velocity.factory.VelocitySenderFactory;
import tech.zmario.privatemessages.velocity.listeners.ConnectionListener;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class VelocityPlugin extends AbstractPrivateMessagesPlugin {

    private final VelocityBootstrap bootstrap;
    private VelocitySenderFactory senderFactory;

    public VelocityPlugin(PrivateMessagesBootstrap bootstrap) {
        super(bootstrap);
        this.bootstrap = (VelocityBootstrap) bootstrap;
    }

    @Override
    protected void finishLoad() {
        senderFactory = new VelocitySenderFactory(this);

        bootstrap.getProxyServer().getEventManager().register(bootstrap, new ConnectionListener(this));
    }

    public VelocitySenderFactory getSenderFactory() {
        return senderFactory;
    }

    @Override
    public List<Sender> getOnlineUsers() {
        return bootstrap.getProxyServer().getAllPlayers()
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
                new ToggleMessagesCommand(this),
                new ToggleSoundCommand(this));

        commands.forEach(command -> {
            CommandMeta meta = bootstrap.getProxyServer().getCommandManager().metaBuilder(command.getName())
                    .aliases(command.getAliases().toArray(new String[0]))
                    .plugin(bootstrap)
                    .build();

            bootstrap.getProxyServer().getCommandManager().register(meta, new VelocityCommand(this, command));
        });
    }

    @Override
    protected void loadLibraries() {
        LibraryManager libraryManager = getLibraryManager();
        Library hikariCp = Library.builder().groupId("com{}zaxxer").artifactId("HikariCP").version("4.0.3").build();
        Library simpleYaml = Library.builder().groupId("me{}carleslc").artifactId("Simple-YAML").version("1.8.3").build();
        Library miniMessage = Library.builder().groupId("net{}kyori").artifactId("adventure-text-minimessage").version("4.13.0").build();
        Library textSerializer = Library.builder().groupId("net{}kyori").artifactId("adventure-text-serializer-legacy<").version("4.13.0").build();

        libraryManager.addMavenCentral();
        libraryManager.addJitPack();

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
        libraryManager.loadLibrary(simpleYaml);
        libraryManager.loadLibrary(miniMessage);
        libraryManager.loadLibrary(textSerializer);
    }
}
