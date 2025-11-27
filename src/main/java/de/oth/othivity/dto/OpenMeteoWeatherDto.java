package de.oth.othivity.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OpenMeteoWeatherDto(Hourly hourly) {

public record Hourly(
    List<String> time,
    @JsonProperty("temperature_2m") List<Double> temperature2m,
    @JsonProperty("apparent_temperature") List<Double> apparentTemperature,
    @JsonProperty("relative_humidity_2m") List<Integer> relativeHumidity2m,
    @JsonProperty("precipitation_probability") List<Integer> precipitationProbability,
    @JsonProperty("precipitation") List<Double> precipitation,
    @JsonProperty("weather_code") List<Integer> weatherCode,
    @JsonProperty("surface_pressure") List<Double> surfacePressure,
    @JsonProperty("cloud_cover") List<Integer> cloudCover,
    @JsonProperty("wind_speed_10m") List<Double> windSpeed10m,
    @JsonProperty("wind_direction_10m") List<Integer> windDirection10m
) {}
}
