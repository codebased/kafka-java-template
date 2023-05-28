package com.producer;

import com.KafkaConfig;
import com.avro.Employee;
import com.avro.Gender;
import com.avro.Profile;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class ProducerMain {

    private static Properties properties = null;

    static {

        // 1. Kafka Configuration such as broker address, topic.
        // create a property bag contains kafka broker configurations.
        properties = getProperties();
    }

    public static void main(String[] args) {

        // 2. Open Kafka Connection
        // create kafka producer instance
        KafkaProducer<String, Employee> producer = new KafkaProducer<>(properties);

        for (int idx=0;idx<10;idx++) {
            // 3. Prepare Kafka Topic Record (Your Message)
            // create record/ message to insert
            Employee employee = new Employee(idx +"", "Amit Malhotra " + idx, 1.1 + idx, Gender.Male, new Profile("Software Engineer", "I code"));

            // create an official kafka record with key and value. Also tell other metadata
            // such as kafka topic, partition (optional) you want to
            // send this record too.
            var record = new ProducerRecord<>(KafkaConfig.KAFKA_TOPIC, employee.toString(), employee);

            // 4. Send Kafka Message
            // officially send this record using kafka producer connection.
            producer.send(record, (metadata, exception) -> {
                handleException(record, metadata, exception);
            });
        }

        // 5. Close Kafka Connection
        // close your producer connection
        producer.close();
    }

    private static void handleException(ProducerRecord record, RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            System.out.println("Error publishing message: " + exception.getMessage());
        } else {

            // print record metadata that was just written by the producer.
            System.out.println("Published message: key=" + record.key() +
                    ", value=" + record.value() +
                    ", topic=" + metadata.topic() +
                    ", partition=" + metadata.partition() +
                    ", offset=" + metadata.offset());
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.BOOTSTRAP_SERVERS_CONFIG);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        // You need this so that the producer can call schema registry to get Avro scheme for key and value
        // to serialize data that you are going to pass.
        properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KafkaConfig.SCHEMA_REGISTRY_URL_CONFIG);

        return properties;
    }
}