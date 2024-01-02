package clients.producer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.datagen.DataGenerator;

public class MockDataProducer {
  private static final Logger LOG = LoggerFactory.getLogger(MockDataProducer.class);

  private static Callback callback;

  private static Producer<String, String> producer;
  private static ExecutorService executorService = Executors.newFixedThreadPool(1);

  private static final int YELLING_APP_ITERATIONS = 5;
  private static final String YELLING_APP_TOPIC = "src-topic";

  /**
   * 
   */
  private static void init()
  {
    if (producer == null) {
      LOG.info("Initializing the producer...");

      Properties props = new Properties();
      props.put("bootstrap.servers", "localhost:9092");
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      props.put("value.serializer", "org.apache..kafka.common.serialization.StringSerializer");
      props.put("acks", "1");
      props.put("retries","3");

      producer = new KafkaProducer<>(props);
      
      callback = (metadata, exception) -> {
        if (exception != null) {
          exception.printStackTrace();
        }  
      };

      LOG.info("Producer initialized");
    }
  }

  public static void produceRandomTextData() 
  {
    Runnable generateTask = () -> {
      init();
      int counter = 0;
      while (counter++ < YELLING_APP_ITERATIONS) {
        List<String> textValues = DataGenerator.genearteRandomText();
        
        for(String value : textValues)
        {
          ProducerRecord<String, String> record = new ProducerRecord<>(YELLING_APP_TOPIC, null, value);
          producer.send(record, callback);
        }

        LOG.info("Text batch sent");

        try {
          Thread.sleep(6000);
        } catch (Exception e) {
          // TODO: handle exception
          Thread.interrupted();
        }
      }
      LOG.info("Done generating text data");
    };
    executorService.submit(generateTask);
  }

}
