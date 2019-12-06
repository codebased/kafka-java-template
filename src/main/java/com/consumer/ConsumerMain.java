package com.consumer;

import com.KafkaConfig;
import com.avro.Employee;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public  class ConsumerMain {

    private static Properties properties = null;

    static {

        // 1. Kafka Configuration such as broker address, topic.
        // create a property bag contains kafka broker configurations.
        properties = getProperties();
    }

    public static void main(String[] args) {

        // 2. Open Kafka Connection
        // create kafka consumer instance
        KafkaConsumer<String, Employee> consumer = new KafkaConsumer<>(properties);
        // subscribe to a single or multiple topics.
        consumer.subscribe(Collections.singletonList(KafkaConfig.KAFKA_TOPIC));

        // infinite loop to poll kafka records and process.
        while (true) {
            // 3. Poll records from kafka server
            final ConsumerRecords<String, Employee> records = consumer.poll(Duration.ofMillis(100));
            // 4. Process each record
            for (final ConsumerRecord<String, Employee> record : records) {
                final String key = record.key();
                final Employee value = record.value();
                processRecord(key, value);
            }

            // 5. Inform kafka that you have consumed these records.

            // I am writing this statement because enable.auto.commit is set to false.
            consumer.commitSync();
        }
    }

    private static void processRecord(String key, Employee value) {
        System.out.println("key=" + key + ", value=" + value);
    }

    private static Properties getProperties() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.BOOTSTRAP_SERVERS_CONFIG);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");

        // I am internationally disabling auto commit, so that I have a better control on when I want to inform
        // kafka that I have processed the record. This is done to prevent any failure in processing data, after read.
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KafkaConfig.SCHEMA_REGISTRY_URL_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        return props;
    }
}