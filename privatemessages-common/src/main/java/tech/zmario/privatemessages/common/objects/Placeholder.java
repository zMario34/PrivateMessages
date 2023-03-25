package tech.zmario.privatemessages.common.objects;

import lombok.Getter;

@Getter
public class Placeholder {

    private String placeholder;
    private String value;

    public Placeholder(String placeholder, String value) {
        this.placeholder = "%" + placeholder + "%";
        this.value = value;
    }
}
