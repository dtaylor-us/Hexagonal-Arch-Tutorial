//package us.dtaylor.todoservice.infastructure.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaTopicConfig {
//
//    @Value("${todo.topic.name}")
//    private String todoTopic;
//
//    @Bean
//    public NewTopic todoTopic() {
//        return TopicBuilder.name(todoTopic)
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }
//}
