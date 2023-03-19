package tech.zmario.privatemessages.common.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.FileConfiguration;

@UtilityClass
public class Utils {

    public String getServerDisplay(String server, FileConfiguration config) {
        ConfigurationSection section;

        if ((section = config.getConfigurationSection("servers-configuration")) != null &&
                section.getKeys(false).contains(server.toLowerCase())) {
            return MiniMessage.miniMessage().serialize(
                    Component.text(section.getString(server + ".color") +
                            section.getString(server + ".display-name")));
        }

        return server;
    }
}
