package de.oth.othivity.service;

import java.time.LocalDateTime;
import de.oth.othivity.model.weather.WeatherSnapshot;

public interface IWeatherService {
    WeatherSnapshot getForecastForTime(double lat, double lon, LocalDateTime targetTime);
}
