package de.oth.othivity.service.impl;

import de.oth.othivity.service.PagingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PagingServiceImpl implements PagingService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 25;

    @Override
    public Pageable createPageable(int page, int size, String sortBy) {
        int validSize = size <= 0 ? DEFAULT_PAGE_SIZE : size;
        if (validSize > MAX_PAGE_SIZE) validSize = MAX_PAGE_SIZE;

        int validPage = Math.max(page, 0);

        return PageRequest.of(validPage, validSize, Sort.by(sortBy).ascending());
    }
}
