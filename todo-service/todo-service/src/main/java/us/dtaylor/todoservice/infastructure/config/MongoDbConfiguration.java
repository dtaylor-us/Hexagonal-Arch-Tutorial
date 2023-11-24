package us.dtaylor.todoservice.infastructure.config;

import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import us.dtaylor.todoservice.infastructure.repository.SpringDataMongoTodoRepository;

@EnableReactiveMongoRepositories(basePackageClasses = SpringDataMongoTodoRepository.class)
public class MongoDbConfiguration {
}
