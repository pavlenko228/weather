package com.weather;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WeatherConsumer {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_NAME = "weather-data";
    private static final String GROUP_ID = "weather-consumer-group";
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    public static void start() {
        Properties props = new Properties();
        int messageCount = 0;
        double tempSum = 0;
        Map<String, Integer> conditionCounts = new HashMap<>();
        
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC_NAME));
            System.out.println("Consumer started. Waiting for weather data...");

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        WeatherDTO weather = objectMapper.readValue(record.value(), WeatherDTO.class);
                        printWeather(weather);
                        
                        tempSum += weather.getTemperature();
                        conditionCounts.merge(weather.getCondition(), 1, Integer::sum);
                        messageCount++;
                        
                        if (messageCount == 10) {
                            printWeatherStats(tempSum, conditionCounts, messageCount);
                            
                            tempSum = 0;
                            conditionCounts.clear();
                            messageCount = 0;
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to parse message: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Consumer error: " + e.getMessage());
        }
    }

    private static void printWeather(WeatherDTO weather) {
        System.out.printf("\n[%s] %s: %d°C, %s%n",
                weather.getDate(),
                weather.getCity(),
                weather.getTemperature(),
                weather.getCondition());
    }

    private static void printWeatherStats(double tempSum, Map<String, Integer> conditionCounts, int messageCount) {
        System.out.println("\n=== Statistics for last " + messageCount + " messages ===");
        
        double avgTemp = tempSum / messageCount;
        System.out.printf("Average temperature: %.1f°C%n", avgTemp);
        
        System.out.println("Weather conditions breakdown:");
        conditionCounts.forEach((condition, count) -> {
            double percentage = (double) count / messageCount * 100;
            System.out.printf("- %s: %d times (%.1f%%)%n", condition, count, percentage);
        });
        
        System.out.println("=================================");
    } 
}