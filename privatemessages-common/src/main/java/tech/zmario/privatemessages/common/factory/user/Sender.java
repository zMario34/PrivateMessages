package tech.zmario.privatemessages.common.factory.user;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface Sender {

    UUID getUniqueId();

    String getName();

    boolean hasPermission(String permission);

    void sendMessage(Component component);

    void playSound(Sound sound);

}
