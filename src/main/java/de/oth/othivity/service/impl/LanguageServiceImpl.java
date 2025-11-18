package de.oth.othivity.service.impl;

import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.service.LanguageService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LanguageServiceImpl implements LanguageService {

    private static final Map<Language, String> FLAG_MAP = Map.of(
            Language.ENGLISH, "🇬🇧",
            Language.GERMAN, "🇩🇪"
    );

    public Map<Language, String> getFlags() {
        return FLAG_MAP;
    }
}