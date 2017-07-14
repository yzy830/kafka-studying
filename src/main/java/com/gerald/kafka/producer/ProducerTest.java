package com.gerald.kafka.producer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerTest {
    private static Producer<String, String> producer;
    
    private static final Logger logger = LoggerFactory.getLogger(ProducerTest.class);
    
    static {
        Map<String, Object> properties = new HashMap<>();
        
        properties.put("bootstrap.servers", "192.168.1.100:9092");
        properties.put("acks", "1");
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", StringSerializer.class);
        properties.put("buffer.memory", "134217728");
        properties.put("retry.backoff.ms", "1000");
        properties.put("batch.size", String.valueOf(1024 * 50));
        properties.put("client.id", "test-1");
        properties.put("retries", "10");
        
        producer = new KafkaProducer<String, String>(properties);
    }
    
    public static void main( String[] args ) throws InterruptedException {
        for(int i = 0; i < 5; ++i) {
            ProducerRecord<String, String> record = new ProducerRecord<>("testTopic", String.valueOf(i), "test-value");
            Future<RecordMetadata> future = producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata,
                        Exception exception) {                    
                    if(exception == null) {
                        logger.warn("message send, topic = " + metadata.topic() + 
                                     ", partition = " + metadata.partition() + 
                                     ", offset = " + metadata.offset() + 
                                     ", timestamp = " + metadata.timestamp());
                    } else {
                        logger.error("message fail, key = " + record.key(), exception);
                    }
                }
            });
            
            TimeUnit.SECONDS.sleep(1);
        }
        
        TimeUnit.SECONDS.sleep(60);
    }
}
