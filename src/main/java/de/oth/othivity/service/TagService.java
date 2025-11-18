package de.oth.othivity.service;

import de.oth.othivity.model.helper.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TagService {
    List<Tag> getAllTags();
}
