/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorweb.models;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;
/**
 *
 * @author andyc
 */
public class WriteToKafka {
    private final static String TOPIC = "GrowData";
    private final static String BOOTSTRAP_SERVERS =
            "localhost:9092";
    
    private static Producer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                            BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                        LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                                    StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
    
    
    static public void runProducer(final int sendMessageCount) throws Exception {
      final Producer<Long, String> producer = createProducer();
      long time = System.currentTimeMillis();

      try {
          for (long index = time; index < time + sendMessageCount; index++) {
              final ProducerRecord<Long, String> record =
                      new ProducerRecord<>(TOPIC, index,
                                  "Hello Mom " + index);

              RecordMetadata metadata = producer.send(record).get();

              long elapsedTime = System.currentTimeMillis() - time;
              System.out.printf("sent record(key=%s value=%s) " +
                              "meta(partition=%d, offset=%d) time=%d\n",
                      record.key(), record.value(), metadata.partition(),
                      metadata.offset(), elapsedTime);

          }
      } finally {
          producer.flush();
          producer.close();
      }
    }
    
    static public void sendMessage(String Message) throws Exception {
      final Producer<Long, String> producer = createProducer();
      

      try {
         
              final ProducerRecord<Long, String> record =
                      new ProducerRecord<>(TOPIC, System.currentTimeMillis(),
                                  Message);

              RecordMetadata metadata = producer.send(record).get();


      
      } finally {
          producer.flush();
          producer.close();
      }
    }
    
}
