package com.example.meteoapp2;

public class CityWeather {
    private String cityName;
    private String weatherDescription;
    private String temperature;
    private String iconCode;

    public CityWeather(String cityName, String weatherDescription, String temperature, String iconCode) {
        this.cityName = cityName;
        this.weatherDescription = weatherDescription;
        this.temperature = temperature;
        this.iconCode = iconCode;
    }

    public String getCityName() {
        return cityName;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getIconCode() {
        return iconCode;
    }
}
