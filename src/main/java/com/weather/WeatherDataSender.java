package com.weather;

import java.util.concurrent.TimeUnit;

public class WeatherDataSender {
    private static final long SEND_INTERVAL_SEC = 2;
    
    public static void start() {
        WeatherProducer producer = new WeatherProducer();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nEnd...");
            producer.close();
        }));

        try {
            while (true) {
                WeatherDTO weather = WeatherDataGenerator.generate();
                producer.send(weather);
                TimeUnit.SECONDS.sleep(SEND_INTERVAL_SEC);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Sending interrupt");
        } finally {
            producer.close();
        }
    }
}