package us.dtaylor.filewatcher.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaPublisherAdapter {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    public KafkaPublisherAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String message) {
        kafkaTemplate.send(topicName, message)
                .whenComplete(
                        (result, error) -> {
                            if (error != null) {
                                log.error("Error publishing message to Kafka: {}", error.getMessage());
                            } else {
                                log.info("Message published to Kafka: {}", message);
                            }
                        }
                );
    }
}
