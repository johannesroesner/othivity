package de.oth.othivity.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Language {
    ENGLISH(1, "English", "🇬🇧"),
    GERMAN(2, "German", "🇩🇪");

    private final int id;
    private final String name;
    private final String emoji;

    public static Map<Language, String> getFlags() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(lang -> lang, Language::getEmoji));
    }
}