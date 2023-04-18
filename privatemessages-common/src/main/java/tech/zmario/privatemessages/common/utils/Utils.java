package tech.zmario.privatemessages.common.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.regex.Pattern;

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

    public boolean isCaps(String message, int percentage) {
        int caps = 0;
        int total = 0;

        for (char c : message.toCharArray()) {
            if (Character.isUpperCase(c)) {
                caps++;
            }

            if (Character.isLetter(c)) {
                total++;
            }
        }

        return total != 0 && (caps * 100 / total) >= percentage;
    }

    public String filterSwear(String message, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        return pattern.matcher(message).replaceAll("***");
    }
}
