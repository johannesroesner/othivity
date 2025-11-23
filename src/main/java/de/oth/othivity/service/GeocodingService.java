package de.oth.othivity.service;

import de.oth.othivity.model.helper.Address;
import org.springframework.stereotype.Service;

@Service
public interface GeocodingService {
    public Address geocode(Address address);
}

