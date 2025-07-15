package com.weather;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Properties;

public class WeatherProducer {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_NAME = "weather-data";
    
    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        this.producer = new KafkaProducer<>(props);
        this.objectMapper.registerModule(new JavaTimeModule()); 
    }

    public void send(WeatherDTO weather) {
        try {
            String jsonWeather = objectMapper.writeValueAsString(weather);
            ProducerRecord<String, String> record = 
                new ProducerRecord<>(TOPIC_NAME, weather.getCity(), jsonWeather);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("[Producer] Ошибка отправки: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("[Producer] Ошибка сериализации: " + e.getMessage());
        }
    }

    public void close() {
        producer.close();
    }
}