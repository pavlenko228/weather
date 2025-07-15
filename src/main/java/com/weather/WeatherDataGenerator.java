package com.weather;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WeatherDataGenerator {
    private static final List<String> CITIES = Arrays.asList(
        "Москва", "Санкт-Петербург", "Сочи"
    );
    
    private static final List<String> CONDITIONS = Arrays.asList(
        "солнечно", "облачно", "дождь", "гроза", "туман"
    );
    
    private static final Random random = new Random();

    public static WeatherDTO generate() {
        String city = getRandomCity();
        LocalDate date = LocalDate.now().minusDays(random.nextInt(7));
        int temperature = getTemperatureFor(city);
        String condition = getConditionFor(city, temperature);
        
        return new WeatherDTO(city, date, temperature, condition);
    }

    private static String getRandomCity() {
        return CITIES.get(random.nextInt(CITIES.size()));
    }

    private static int getTemperatureFor(String city) {
        switch (city) {
            case "Москва":
                return random.nextInt(30) - 10; 
            case "Санкт-Петербург":
                return random.nextInt(25) - 5;  
            case "Сочи":
                return random.nextInt(20) + 10; 
            default:
                return random.nextInt(25) - 5; 
        }
    }

    private static String getConditionFor(String city, int temp) {
        return CONDITIONS.get(random.nextInt(CONDITIONS.size()));
    }
}