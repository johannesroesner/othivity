package de.oth.othivity.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Language {
    ENGLISH(1, "English", "🇬🇧", "en"),
    GERMAN(2, "German", "🇩🇪", "de"),
    FRENCH(3, "French", "🇫🇷", "fr"),
    SPANISH(4, "Spanish", "🇪🇸", "es");

    private final int id;
    private final String name;
    private final String emoji;
    private final String localeCode;

    public static Map<Language, String> getFlags() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(lang -> lang, Language::getEmoji));
    }
}