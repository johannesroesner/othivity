package de.oth.othivity.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.service.IGeocodingService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class NominatimGeocodingService implements IGeocodingService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Address geocode(Address address) {
        try {
            String addressString = String.format("%s %s, %s %s, %s",
                    address.getStreet(),
                    address.getHouseNumber(),
                    address.getPostalCode(),
                    address.getCity(),
                    address.getCountry() != null ? address.getCountry() : "");

            URI uri = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                    .queryParam("q", addressString)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .build()
                    .encode()
                    .toUri();

            JsonNode root = objectMapper.readTree(restTemplate.getForObject(uri, String.class));

            if (root.isArray() && !root.isEmpty()) {
                JsonNode first = root.get(0);
                address.setLatitude(first.get("lat").asDouble());
                address.setLongitude(first.get("lon").asDouble());
                return address;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }
}
