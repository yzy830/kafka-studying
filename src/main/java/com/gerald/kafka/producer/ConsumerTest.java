package com.gerald.kafka.producer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class ConsumerTest {
    private static Consumer<String, String> consumer;
    
    static {
        Map<String, Object> configs = new HashMap<>();
        
        configs.put("bootstrap.servers", "192.168.1.100:9092");
        configs.put("group.id", "group-1");
        configs.put("connections.max.idle.ms", String.valueOf(TimeUnit.SECONDS.toMillis(60)));
        configs.put("enable.auto.commit", true);
        configs.put("client.id", "consumer-1");
        configs.put("zookeeper.connect", "192.168.1.100:2181");
        configs.put("num.consumer.fetchers", "1");
        configs.put("key.deserializer", StringDeserializer.class);
        configs.put("value.deserializer", StringDeserializer.class);
        
        consumer = new KafkaConsumer<>(configs);
    }
    
    public static void main(String[] args) {
        consumer.subscribe(Arrays.asList("testTopic"));
        
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(1000 * 10);
            
            if(records == null || records.isEmpty()) {
                System.out.println("no message");
                continue;
            }
            
            for(ConsumerRecord<String, String> record : records) {
                System.out.println("key = " + record.key());
                System.out.println("value = " + record.value());
            }
        }     
    }
}
