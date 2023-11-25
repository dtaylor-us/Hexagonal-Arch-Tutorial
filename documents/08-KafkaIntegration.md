### Integrating Kafka with Spring Boot Microservices Using WebFlux

Apache Kafka is a distributed streaming platform that can be used for building real-time data pipelines and streaming applications. Integrating Kafka with Spring Boot microservices using WebFlux allows for reactive processing of streams. Below is a detailed guide with code examples for integrating Kafka into Spring Boot microservices.

#### Prerequisites

1. **Apache Kafka**: Ensure you have Kafka installed and running. You can use the [official quickstart guide](https://kafka.apache.org/quickstart) to set up Kafka.

2. **Dependencies**: Add Spring Kafka and Spring WebFlux dependencies to your Spring Boot application's `pom.xml`:

   ```xml
   <dependency>
     <groupId>org.springframework.kafka</groupId>
     <artifactId>spring-kafka</artifactId>
   </dependency>
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-webflux</artifactId>
   </dependency>
   ```

### Step 1: Kafka Configuration

1. **Configure Kafka Properties**:
   - In `application.properties`, configure Kafka producer and consumer properties:

   ```properties
   spring.kafka.bootstrap-servers=localhost:9092
   spring.kafka.consumer.group-id=my-group
   spring.kafka.consumer.auto-offset-reset=earliest
   spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
   spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
   spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
   spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
   ```

2. **Create Kafka Topics**:
Kafka topics are categories or feed names to which records are published. Before producing or consuming messages, the corresponding topics need to be created. Here, we will cover two methods to create Kafka topics: using Kafka CLI and programmatically within a Spring Boot application.

#### Method 1: Using Kafka Command Line Interface (CLI)

1. **Accessing Kafka CLI**:
   - Kafka CLI tools are part of the Kafka distribution. Ensure Kafka and Zookeeper are up and running.

2. **Create a Topic**:
   - Use the `kafka-topics` script to create a topic.
   - Example command to create a topic named `example-topic`:
     ```bash
     kafka-topics.sh --create --topic example-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
     ```
   - This command creates a topic with a single partition and a replication factor of one. Adjust the `--partitions` and `--replication-factor` based on your requirements and cluster setup.

#### Method 2: Programmatically in Spring Boot

You can also create topics programmatically in your Spring Boot application. This approach is beneficial for automatically setting up topics as part of your application's startup process.

1. **Kafka Admin Configuration**:
   - Spring Boot provides `KafkaAdmin` class to create topics programmatically.
   - Add Kafka Admin configuration in your Spring Boot application:

   ```java
   @Configuration
   public class KafkaTopicConfig {

       @Value("${topic.name}")
       private String topicName;

       @Bean
       public NewTopic exampleTopic() {
           return TopicBuilder.name(topicName)
                              .partitions(1)
                              .replicas(1)
                              .build();
       }
   }
   ```

   - `TopicBuilder` is used to create a `NewTopic` bean. Spring Boot auto-configuration picks up `NewTopic` beans and creates corresponding topics in Kafka.

2. **Adding Properties to `application.properties`**:
   - Define the topic name in `application.properties`:
     ```properties
     topic.name=example-topic
     ```

#### Using Kafka Topics in Spring Boot

Once the topic is created, you can use it in your Kafka producer and consumer services.

1. **Producer Service**:
   - The `KafkaProducerService` sends messages to the `example-topic`:
     ```java
     public Mono<Void> sendMessage(String message) {
         return Mono.fromRunnable(() ->
             kafkaTemplate.send(topicName, message)
         ).subscribeOn(Schedulers.boundedElastic());
     }
     ```

2. **Consumer Service**:
   - The `KafkaConsumerService` listens to the `example-topic`:
     ```java
     @KafkaListener(topics = "${topic.name}", groupId = "${spring.kafka.consumer.group-id}")
     public void listen(String message) {
         System.out.println("Received message: " + message);
         // Process message
     }
     ```

### Step 2: Kafka Producer Configuration

Configuring Kafka producers in the User and Todo microservices involves setting up the necessary Spring Boot configurations and creating services to send messages to Kafka topics. Here's a detailed guide with examples.

#### Kafka Producer Configuration in Spring Boot

1. **Configure Producer Properties**:
   - Ensure that your `application.properties` or `application.yml` in both User and Todo services include Kafka producer configurations:
     ```properties
     spring.kafka.bootstrap-servers=localhost:9092
     spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
     spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
     ```

2. **Add Kafka Template Bean**:
   - In your Spring Boot configuration, define a `KafkaTemplate` bean. This template is a high-level abstraction for sending messages.
   - Example configuration in `KafkaConfig.java`:
     ```java
     @Configuration
     public class KafkaConfig {

         @Bean
         public ProducerFactory<String, String> producerFactory() {
             Map<String, Object> configProps = new HashMap<>();
             configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
             configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
             configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
             return new DefaultKafkaProducerFactory<>(configProps);
         }

         @Bean
         public KafkaTemplate<String, String> kafkaTemplate() {
             return new KafkaTemplate<>(producerFactory());
         }
     }
     ```

#### Kafka Producer Service

Create a service in both User and Todo microservices to send messages to Kafka topics.

1. **User Service Kafka Producer**:
   - This service sends messages related to user activities.
   - Example `UserServiceKafkaProducer.java`:
     ```java
     @Service
     public class UserServiceKafkaProducer {

         private final KafkaTemplate<String, String> kafkaTemplate;
         private final String userTopic = "user-topic";

         @Autowired
         public UserServiceKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
             this.kafkaTemplate = kafkaTemplate;
         }

         public void sendUserCreated(String message) {
             kafkaTemplate.send(userTopic, "User Created: " + message);
         }

         // Additional methods for other user-related events
     }
     ```

2. **Todo Service Kafka Producer**:
   - This service sends messages related to todo task activities.
   - Example `TodoServiceKafkaProducer.java`:
     ```java
     @Service
     public class TodoServiceKafkaProducer {

         private final KafkaTemplate<String, String> kafkaTemplate;
         private final String todoTopic = "todo-topic";

         @Autowired
         public TodoServiceKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
             this.kafkaTemplate = kafkaTemplate;
         }

         public void sendTodoCreated(String message) {
             kafkaTemplate.send(todoTopic, "Todo Created: " + message);
         }

         // Additional methods for other todo-related events
     }
     ```

#### Example Usage

Integrate the Kafka producers in your service logic. For instance, when a new user is created in the User service, send a message to the Kafka topic:

```java
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserServiceKafkaProducer kafkaProducer;

    // Constructor...

    public Mono<User> createUser(User user) {
        return userRepository.save(user)
                             .doOnSuccess(savedUser -> kafkaProducer.sendUserCreated(savedUser.toString()));
    }
}
```

Similarly, integrate the Kafka producer in the Todo service.



### Step 3: Kafka Consumer Configuration for User and Todo Microservices

Configuring Kafka consumers in the User and Todo microservices involves setting up listeners that react to messages from specific Kafka topics. This section provides a detailed guide with concrete examples for setting up Kafka consumers using Spring Boot.

#### Kafka Consumer Configuration in Spring Boot

1. **Configure Consumer Properties**:
   - In the `application.properties` or `application.yml` of both User and Todo services, include Kafka consumer configurations:
     ```properties
     spring.kafka.bootstrap-servers=localhost:9092
     spring.kafka.consumer.group-id=user-todo-group
     spring.kafka.consumer.auto-offset-reset=earliest
     spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
     spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
     ```

2. **Add Kafka Listener Container Factory**:
   - Define a `ConcurrentKafkaListenerContainerFactory` bean in your configuration. This factory is responsible for creating containers for Kafka listeners.
   - Example in `KafkaConfig.java`:
     ```java
     @Configuration
     public class KafkaConfig {

         @Autowired
         private ConsumerFactory<String, String> consumerFactory;

         @Bean
         public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
             ConcurrentKafkaListenerContainerFactory<String, String> factory =
                 new ConcurrentKafkaListenerContainerFactory<>();
             factory.setConsumerFactory(consumerFactory);
             return factory;
         }
     }
     ```

#### Kafka Consumer Service

Create a service in both User and Todo microservices to listen to and process messages from Kafka topics.

1. **User Service Kafka Consumer**:
   - This service listens for messages related to todo activities that might affect user data.
   - Example `UserServiceKafkaConsumer.java`:
     ```java
     @Service
     public class UserServiceKafkaConsumer {

         @KafkaListener(topics = "todo-topic", groupId = "user-todo-group")
         public void listenToTodoTopic(String message) {
             System.out.println("Received message in user service: " + message);
             // Process the message
         }

         // Additional methods for other topics if needed
     }
     ```

2. **Todo Service Kafka Consumer**:
   - This service listens for messages related to user activities that might affect todo data.
   - Example `TodoServiceKafkaConsumer.java`:
     ```java
     @Service
     public class TodoServiceKafkaConsumer {

         @KafkaListener(topics = "user-topic", groupId = "user-todo-group")
         public void listenToUserTopic(String message) {
             System.out.println("Received message in todo service: " + message);
             // Process the message
         }

         // Additional methods for other topics if needed
     }
     ```

#### Example: Reacting to Messages

Suppose the Todo service creates a todo item for a user. The User service could listen for this event and react accordingly, perhaps by updating a user's activity log:

```java
@Service
public class UserServiceKafkaConsumer {

    @KafkaListener(topics = "todo-topic", groupId = "user-todo-group")
    public void listenToTodoTopic(String message) {
        // Assuming the message contains the user ID and todo item details
        System.out.println("New todo created for user: " + message);
        // Logic to update the user's activity log
    }
}
```

#### Step 4: Sending and Receiving Messages

### Step 4: Sending and Receiving Messages with Kafka in Spring Boot Microservices

In this step, we'll focus on the practical implementation of sending and receiving messages using Kafka within the User and Todo microservices. We'll provide detailed examples showing how these services can produce and consume messages in a real-world scenario.

#### Sending Messages (Producing to Kafka)

1. **Use Case Example - Creating a Todo**:
   - When a new todo is created in the Todo service, it sends a message to a Kafka topic.

2. **Todo Service - Kafka Producer Implementation**:
   - Assume you have a `TodoServiceKafkaProducer` as described earlier.
   - Example usage in `TodoService.java`:
     ```java
     @Service
     public class TodoService {

         private final TodoRepository todoRepository;
         private final TodoServiceKafkaProducer kafkaProducer;

         // Constructor...

         public Mono<Todo> createTodo(Todo todo) {
             return todoRepository.save(todo)
                                  .doOnSuccess(savedTodo -> {
                                      String message = "New todo created: " + savedTodo.toString();
                                      kafkaProducer.sendTodoCreated(message);
                                  });
         }
     }
     ```

3. **Sending Messages from User Service**:
   - Similarly, when a user is created or updated, the User service can send messages.
   - Example in `UserService.java`:
     ```java
     @Service
     public class UserService {

         // Repository and KafkaProducer injection...

         public Mono<User> createUser(User user) {
             return userRepository.save(user)
                                  .doOnSuccess(savedUser -> {
                                      String message = "New user created: " + savedUser.toString();
                                      kafkaProducer.sendUserCreated(message);
                                  });
         }
     }
     ```

#### Receiving Messages (Consuming from Kafka)

1. **Use Case Example - User Service Reacting to Todo Creation**:
   - The User service listens for messages about new todos and might update its records or perform related actions.

2. **User Service - Kafka Consumer Implementation**:
   - Example in `UserServiceKafkaConsumer.java`:
     ```java
     @Service
     public class UserServiceKafkaConsumer {

         @KafkaListener(topics = "todo-topic", groupId = "user-todo-group")
         public void listenToTodoTopic(String message) {
             System.out.println("Received message in user service: " + message);
             // Process the message, e.g., update user's todo list or activity log
         }
     }
     ```

3. **Todo Service Reacting to User Updates**:
   - The Todo service might also need to react to changes in user data.
   - Example in `TodoServiceKafkaConsumer.java`:
     ```java
     @Service
     public class TodoServiceKafkaConsumer {

         @KafkaListener(topics = "user-topic", groupId = "user-todo-group")
         public void listenToUserTopic(String message) {
             System.out.println("Received message in todo service: " + message);
             // Process the message, e.g., validate todo items against updated user data
         }
     }
     ```

#### Real-World Scenario

In a real-world application, these Kafka messages might include more complex data structures like JSON objects. The services would then deserialize these messages into meaningful data models to act upon. For instance, a message could contain the ID of a user who created a new todo, and the User service might use this ID to fetch more details or update statistics.
