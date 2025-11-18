package de.oth.othivity.service;

import de.oth.othivity.model.enumeration.Language;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface LanguageService {

    Map<Language, String> getFlags();
}
