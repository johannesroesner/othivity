package de.oth.othivity.service.impl;

import de.oth.othivity.service.IPagingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PagingService implements IPagingService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 25;

    @Override
    public Pageable createPageable(int page, int size, String sortBy, String direction) {
        int validSize = size <= 0 ? DEFAULT_PAGE_SIZE : size;
        if (validSize > MAX_PAGE_SIZE) validSize = MAX_PAGE_SIZE;

        int validPage = Math.max(page, 0);

        String actualSortField = "size".equals(sortBy) ? "members.size" : sortBy;
        
        Sort sort = "desc".equalsIgnoreCase(direction) ? Sort.by(actualSortField).descending() : Sort.by(actualSortField).ascending();

        return PageRequest.of(validPage, validSize, sort);
    }
}
