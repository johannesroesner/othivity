package de.oth.othivity.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccessLevel {
    OPEN("accessLevel.open"),
    CLOSED("accessLevel.closed"),
    ON_INVITE("accessLevel.onInvite");

    private final String key;
}
