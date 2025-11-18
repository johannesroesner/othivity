package de.oth.othivity.service.impl;

import de.oth.othivity.model.helper.Tag;
import de.oth.othivity.repository.helper.TagRepository;
import de.oth.othivity.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
