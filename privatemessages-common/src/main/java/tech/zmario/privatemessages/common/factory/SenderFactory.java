package tech.zmario.privatemessages.common.factory;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import tech.zmario.privatemessages.common.factory.user.Sender;

import java.util.Optional;
import java.util.UUID;

public interface SenderFactory<T> {

    Optional<Sender> wrapSender(String name);
    Optional<Sender> wrapSender(UUID uuid);

    Sender wrapSender(T player);

    String getName(T player);

    String getServerName(Sender sender);

    void sendMessage(T player, Component component);

    void playSound(T sender, Sound sound);

    boolean hasPermission(T player, String permission);

    boolean isPlayerOnline(String name);

    boolean isConsole(Sender sender);

}
