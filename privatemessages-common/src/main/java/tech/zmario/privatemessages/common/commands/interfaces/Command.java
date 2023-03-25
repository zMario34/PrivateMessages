package tech.zmario.privatemessages.common.commands.interfaces;

import tech.zmario.privatemessages.common.factory.user.Sender;

import java.util.ArrayList;
import java.util.List;

public interface Command {

    void execute(Sender sender, String[] args);

    default List<String> suggest(Sender sender, String[] args) {
        return new ArrayList<>();
    }

    String getName();

    String getPermission();

    default List<String> getAliases() {
        return new ArrayList<>();
    }
}
