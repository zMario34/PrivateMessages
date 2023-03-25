package tech.zmario.privatemessages.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {
    public boolean startsWithIgnoreCase(String string, String prefix) {
        return string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
