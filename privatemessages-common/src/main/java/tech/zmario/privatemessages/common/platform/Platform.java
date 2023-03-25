package tech.zmario.privatemessages.common.platform;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Platform {

    private final PlatformType platformType;
    private final String version;

}
