package com.example.meteoapp2;

public class City {
    private String name;
    private String weatherInfo;

    public City(String name) {
        this.name = name;
        this.weatherInfo = "Météo inconnue";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }
}
