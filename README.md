Kafka Weather Monitor - Quick Start Guide

First, start Kafka using Docker:
docker-compose up -d

Create the Kafka topic:
docker exec -it kafka kafka-topics --create --topic weather-data --bootstrap-server localhost:9092 --partitions 1

Build the project:
mvn clean package

Open two terminal windows.

In the first terminal, run the consumer:
java -jar target/weather-app.jar consumer

In the second terminal, run the producer:
java -jar target/weather-app.jar producer

You should see:

Producer will output messages like: "Sent: Moscow, 18°C, sunny" every 3 seconds

Consumer will show received messages and statistics every 10 messages
