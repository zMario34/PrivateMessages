package tech.zmario.privatemessages.common.platform;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlatformType {

    BUNGEECORD("BungeeCord"),
    VELOCITY("Velocity"),
    ;

    private final String name;

}
