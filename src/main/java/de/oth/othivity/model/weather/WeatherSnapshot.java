package de.oth.othivity.model.weather;

import java.time.LocalDateTime;

public record WeatherSnapshot(
    LocalDateTime time, 
    double temperature,
    double feelsLike,
    int humidity,
    int precipitationProb,
    double precipitation,
    int weatherCode,
    String iconName,
    double pressure,
    double windSpeed,
    int windDirection,
    int cloudCover
) {}
