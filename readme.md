<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Introduction](#introduction)
  - [Basic Setup](#basic-setup)
    - [Kafka Topic](#kafka-topic)
    - [Topic Record Format](#topic-record-format)
    - [Schema Registry](#schema-registry)
    - [Bootstrap Server](#bootstrap-server)
  - [Producer](#producer)
  - [Consumer](#consumer)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Introduction 

You can use this template to create a producer as well as consumer for Kafka. 

## Basic Setup

### Kafka Topic

Topic is a common property between producer and consumer. While producer will send records (messages) to the topic (collection), consumer(s) will receive these records (messages) and process accordingly. 

In this sample project the topic name is defined as a constant in `KafkaConfig.java` (/kafka-producer-consumer-template/src/main/java/com/)
), with the value as "EMPLOYEES_TOPIC".

```java
    public static final String KAFKA_TOPIC = "EMPLOYEES_TOPIC";
```

### Topic Record Format

Topic record contains, key and value. In this sample project, I am using a  string data type for "key" and complex data type for "value" for Kafka record. The complex data type is defined using `avsc` files under `kafka-producer-consumer-template/src/main/avro` folder.

The Employee.avsc contains primitive as well as non primitive data types referencs, which is defined in Gender.avsc and Profile.avsc.

In order to build java classes, I have used Gradle Avro Plugin.

Gradle Avro Plugin

You will find lines in build.gradle

```js

repositories {

    maven { url 'https://packages.confluent.io/maven' }
}

plugins {

    id 'com.commercehub.gradle.plugin.avro' version '0.9.1'
}


dependencies {
    implementation 'io.confluent:kafka-avro-serializer:5.3.0'
    implementation 'org.apache.avro:avro:1.9.0'
}

```



Now open gradle task window (View / Tool Windows / Gradle), and run `generateAvroJava` task. On success, it will create class definitions for all your avro files, under `/build` folder.
 (generated-main-avro-java\com.avro).

 Or

 You can run this command by typing this command in the terminal: 

 `./gradlew generateAvroJava`

`For more detail on this Avro Plugin, please refer this link: https://github.com/davidmc24/gradle-avro-plugin`

### Schema Registry

Define a schema registry url where your data type schema will be stored and share between producer and consumer.

You can specify this url under KafkaConfig.java:

```java

public static final String SCHEMA_REGISTRY_URL_CONFIG = "http://localhost:8081";

```

### Bootstrap Server

Just like schema url, you will need to define a broker server address.

You can specify this url under KafkaConfig.java

```
public static final String BOOTSTRAP_SERVERS_CONFIG = "http://localhost:9092";
```

## Producer

Ref: `/kafka-producer-consumer-template/src/main/java/com/producer/ProducerMain.java`

In order to create a Producer, you will do the following:

1. Kafka Configuration such as broker address, key and value serializers, topic.
2. Open Kafka Connection
3. Prepare Kafka Topic Record (Your Message)
4. Send Kafka Message
5. Close Kafka Connection

Each of these points are well described in the ProducerMain.java file.

If everything is OK with your project, you should be able to successfully build the project.

You can now run this command from the terminal: 

 `./gradlew runProducer`

It should create a topic (if it does not exist) and send 10 records to the Kafka server.

## Consumer

Ref: `/kafka-producer-consumer-template/src/main/java/com/consumer/ConsumerMain.java`

In order to create a Consumer, you will do the following:

1. Kafka Configuration such as broker address, key and value *deserializers*.
2. Open Kafka Connection and subscribe to topic
3. Check for records in Kafka Server(Your Message)
4. Process Kafka records
5. Inform Kafka that you have consumed records.


Each of these points are well described in the ConsumerMain.java file.

If everything is OK with your project, you should be able to successfully build the project.

You can now run this command from the terminal: 

 `./gradlew runConsumer`


 