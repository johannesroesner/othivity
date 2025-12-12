package de.oth.othivity.service.api;

import de.oth.othivity.model.helper.Address;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import de.oth.othivity.model.helper.WeatherSnapshot;
import de.oth.othivity.service.IWeatherService;
import de.oth.othivity.dto.OpenMeteoWeatherDto;
import de.oth.othivity.dto.OpenMeteoWeatherDto.Hourly;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class OpenMeteoWeatherService implements IWeatherService {

    private final RestClient restClient;

    public OpenMeteoWeatherService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.open-meteo.com/v1").build();
    }

   @Override
    public WeatherSnapshot getForecastForTime(Address address, LocalDateTime targetTime) {
        if(address == null || address.getLatitude() == null || address.getLongitude() == null) return null;


        long daysUntilEvent = ChronoUnit.DAYS.between(LocalDate.now(), targetTime.toLocalDate());
        if (daysUntilEvent > 7 || daysUntilEvent < 0) {
            return null; 
        }
        String dateString = targetTime.toLocalDate().toString();
    
        LocalDateTime roundedTime = targetTime.truncatedTo(ChronoUnit.HOURS);
        String uri = UriComponentsBuilder.fromPath("/forecast")
                .queryParam("latitude", address.getLatitude())
                .queryParam("longitude", address.getLongitude())
                .queryParam("hourly", String.join(",",
                        "temperature_2m", "apparent_temperature", "relative_humidity_2m",
                        "precipitation_probability", "precipitation", "weather_code",
                        "surface_pressure", "cloud_cover", "wind_speed_10m", "wind_direction_10m"))
                .queryParam("timezone", "auto")
                .queryParam("start_date", dateString)
                .queryParam("end_date", dateString) 
                .toUriString();

        try {
            OpenMeteoWeatherDto response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(OpenMeteoWeatherDto.class);

            if (response == null || response.hourly() == null) {
                return null;
            }
            String targetIso = roundedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            List<String> times = response.hourly().time();
            
            int index = times.indexOf(targetIso);

            if (index != -1) {
                return mapToSnapshot(response.hourly(), index);
            }
            
        } catch (Exception e) {
        }

        return null;
    }
    private WeatherSnapshot mapToSnapshot(Hourly h, int i) {
        int wmoCode = h.weatherCode().get(i);
        LocalDateTime parsedTime = LocalDateTime.parse(h.time().get(i));

        return new WeatherSnapshot(
            parsedTime,
            h.temperature2m().get(i),
            h.apparentTemperature().get(i),
            h.relativeHumidity2m().get(i),
            h.precipitationProbability().get(i),
            h.precipitation().get(i),
            wmoCode,
            resolveIconName(wmoCode),
            h.surfacePressure().get(i),
            h.windSpeed10m().get(i),
            h.windDirection10m().get(i),
            h.cloudCover().get(i)
    );
    }

    private String resolveIconName(int code) {
        if (code == 0) return "sun";
        if (code >= 1 && code <= 3) return "cloud";
        if (code == 45 || code == 48) return "menu-alt-2"; 
        if (code >= 51 && code <= 67) return "cloud-rain"; 
        if (code >= 71 && code <= 77) return "snowflake"; 
        if (code >= 80 && code <= 82) return "cloud-rain"; 
        if (code >= 95) return "lightning-bolt";           
        return "question-mark-circle"; 
    }
}