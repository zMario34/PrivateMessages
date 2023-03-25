package tech.zmario.privatemessages.common.factory.user.impl;

import lombok.Data;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import tech.zmario.privatemessages.common.factory.SenderFactory;
import tech.zmario.privatemessages.common.factory.user.Sender;

import java.util.UUID;

@Data
public class BaseSender<T> implements Sender {

    private final UUID uniqueId;
    private final T sender;
    private final SenderFactory<T> senderFactory;

    @Override
    public boolean hasPermission(String permission) {
        return senderFactory.hasPermission(sender, permission);
    }

    @Override
    public void sendMessage(Component component) {
        senderFactory.sendMessage(sender, component);
    }

    @Override
    public void playSound(Sound sound) {
        senderFactory.playSound(sender, sound);
    }

    @Override
    public String getName() {
        return senderFactory.getName(sender);
    }
}