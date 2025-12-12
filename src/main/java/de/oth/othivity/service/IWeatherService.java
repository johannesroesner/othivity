package de.oth.othivity.service;

import java.time.LocalDateTime;

import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.helper.WeatherSnapshot;

public interface IWeatherService {
    WeatherSnapshot getForecastForTime(Address address, LocalDateTime targetTime);
}
