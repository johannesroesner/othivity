package de.oth.othivity.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface PagingService {
    Pageable createPageable(int page, int size, String sortBy, String direction);
}
