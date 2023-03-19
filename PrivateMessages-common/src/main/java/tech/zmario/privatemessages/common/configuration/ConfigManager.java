package tech.zmario.privatemessages.common.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.simpleyaml.configuration.file.YamlConfiguration;
import tech.zmario.privatemessages.common.plugin.bootstrap.PrivateMessagesBootstrap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RequiredArgsConstructor
public class ConfigManager {

    private final PrivateMessagesBootstrap bootstrap;

    private File configFile;
    private File messagesFile;

    @Getter
    private YamlConfiguration config;
    @Getter
    private YamlConfiguration messages;

    public void load() {
        bootstrap.getPluginLogger().info("Loading configuration files...");

        configFile = new File(bootstrap.getPluginFolder(), "config.yml");
        messagesFile = new File(bootstrap.getPluginFolder(), "messages.yml");

        if (!configFile.exists()) {
            bootstrap.getPluginLogger().info("Creating config.yml...");

            if (!saveResource(configFile, "config.yml")) return;

            bootstrap.getPluginLogger().info("Successfully created config.yml!");
        }

        if (!messagesFile.exists()) {
            bootstrap.getPluginLogger().info("Creating messages.yml...");
            if (!saveResource(messagesFile, "messages.yml")) return;

            bootstrap.getPluginLogger().info("Successfully created messages.yml!");
        }

        reload();
    }

    public void reload() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
            bootstrap.getPluginLogger().severe("Error while loading configuration files!");
        }
    }

    private boolean saveResource(File file, String name) {
        try (InputStream inputStream = bootstrap.getResource(name)) {
            Files.copy(inputStream, file.toPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            bootstrap.getPluginLogger().severe("Error while saving resource " + name + "!");
            return false;
        }
    }
}
