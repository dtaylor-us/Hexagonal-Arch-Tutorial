### Implementing a Reactive File Watcher Service in Hexagonal Architecture

In this tutorial, we're going to build a Reactive File Watcher Microservice with Spring Boot and Kafka. This service will watch for new CSV files in a directory, process them, and publish the contents to a Kafka topic, following the hexagonal architecture pattern.

#### Project Structure

The project structure will be similar to the Todo service, adapted for the File Watcher Service:

```
./filewatcher-service
├── ./src
│   ├── ./src/main
│   │   ├── ./src/main/java
│   │   │   └── ./src/main/java/us
│   │   │       └── ./src/main/java/us/dtaylor
│   │   │           └── ./src/main/java/us/dtaylor/filewatcherservice
│   │   │               ├── ./src/main/java/us/dtaylor/filewatcherservice/FileWatcherServiceApplication.java
│   │   │               ├── ./src/main/java/us/dtaylor/filewatcherservice/application
│   │   │               │   ├── ./src/main/java/us/dtaylor/filewatcherservice/application/config
│   │   │               │   │   └── ... (configuration classes)
│   │   │               ├── ./src/main/java/us/dtaylor/filewatcherservice/domain
│   │   │               │   └── ... (domain models and exceptions)
│   │   │               ├── ./src/main/java/us/dtaylor/filewatcherservice/infastructure
│   │   │               │   ├── ./src/main/java/us/dtaylor/filewatcherservice/infastructure/adapter
│   │   │               │   │   └── ... (adapter implementations)
│   │   │               │   └── ./src/main/java/us/dtaylor/filewatcherservice/infastructure/client
│   │   │               │       └── ... (external client implementations)
│   │   └── ./src/main/resources
│   │       └── ./src/main/resources/application.properties
```

#### Step 1: Creating the File Watcher Service Application

1. **FileWatcherServiceApplication.java**:
   - Entry point of the Spring Boot application.
   ```java
   package us.dtaylor.filewatcherservice;

   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;

   @SpringBootApplication
   public class FileWatcherServiceApplication {
       public static void main(String[] args) {
           SpringApplication.run(FileWatcherServiceApplication.class, args);
       }
   }
   ```

### Step 2: Implementing the Reactive File Watcher Service (Secondary Adapter)

In this step, we'll implement the Reactive File Watcher Service as a Secondary Adapter in our hexagonal architecture. This service will monitor a specified directory for new CSV files, process them, and publish the relevant data to a Kafka topic.

#### Reactive File Watching with Java NIO and WebFlux

We'll use Java NIO for file watching and Spring WebFlux for creating a reactive pipeline.

1. **FileWatcherAdapter** (in `us.dtaylor.filewatcherservice.infastructure.adapter`):

   This adapter class will handle the file watching logic reactively:

   ```java
   package us.dtaylor.filewatcherservice.infastructure.adapter;

   import org.springframework.stereotype.Component;
   import reactor.core.publisher.Flux;
   import reactor.core.scheduler.Schedulers;
   import java.nio.file.*;
   import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

   @Component
   public class FileWatcherAdapter {

       private final Path dirToWatch = Paths.get("path/to/watch");
       private final CSVProcessor csvProcessor;

       public FileWatcherAdapter(CSVProcessor csvProcessor) {
           this.csvProcessor = csvProcessor;
           watchDirectoryReactively();
       }

       private void watchDirectoryReactively() {
           Flux.<WatchEvent<?>>create(sink -> {
               try (WatchService watcher = dirToWatch.getFileSystem().newWatchService()) {
                   dirToWatch.register(watcher, ENTRY_CREATE);
                   while (!sink.isCancelled()) {
                       WatchKey key = watcher.take();
                       key.pollEvents().forEach(sink::next);
                       key.reset();
                   }
               } catch (Exception e) {
                   sink.error(e);
               }
           })
           .subscribeOn(Schedulers.boundedElastic())
           .filter(event -> event.kind().equals(ENTRY_CREATE))
           .map(event -> (Path) event.context())
           .map(dirToWatch::resolve)
           .subscribe(csvProcessor::process);
       }
   }
   ```

   - Here, a `Flux` is created to emit file system events.
   - We filter only for `ENTRY_CREATE` events and transform each event into a `Path` object representing the newly created file.
   - The `subscribe` method is used to process each file using the `CSVProcessor`.

2. **CSVProcessor** (in `us.dtaylor.filewatcherservice.domain.service`):

   This service will handle the processing of CSV files:

   ```java
   package us.dtaylor.filewatcherservice.domain.service;

   import org.springframework.stereotype.Service;
   import us.dtaylor.filewatcherservice.infastructure.adapter.KafkaPublisherAdapter;
   import java.nio.file.Path;
   import java.util.stream.Stream;
   import java.nio.file.Files;

   @Service
   public class CSVProcessor {

       private final KafkaPublisherAdapter kafkaPublisher;

       public CSVProcessor(KafkaPublisherAdapter kafkaPublisher) {
           this.kafkaPublisher = kafkaPublisher;
       }

       public void process(Path filePath) {
           try (Stream<String> lines = Files.lines(filePath)) {
               lines.forEach(line -> {
                   // Process each line and publish to Kafka
                   kafkaPublisher.publish(line);
               });
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   }
   ```

   - The `process` method reads the CSV file line by line.
   - Each line is sent to the `KafkaPublisherAdapter` for publishing to a Kafka topic.

3. **KafkaPublisherAdapter** (in `us.dtaylor.filewatcherservice.infastructure.adapter`):

   This class will be responsible for publishing messages to Kafka:

   ```java
   package us.dtaylor.filewatcherservice.infastructure.adapter;

   import org.springframework.kafka.core.KafkaTemplate;
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.stereotype.Component;

   @Component
   public class KafkaPublisherAdapter {

       private final KafkaTemplate<String, String> kafkaTemplate;
       @Value("${kafka.topic.name}")
       private String topicName;

       public KafkaPublisherAdapter(KafkaTemplate<String, String> kafkaTemplate) {
           this.kafkaTemplate = kafkaTemplate;
       }

       public void publish(String message) {
           kafkaTemplate.send(topicName, message);
       }
   }
   ```

   - The `publish` method uses `KafkaTemplate` to send messages to the specified Kafka topic.


### Step 3: Implementing Reactive CSV Processor and Kafka Publisher (Secondary Adapter)

In this step, we'll detail the implementation of the CSV Processor and Kafka Publisher, both acting as secondary adapters in the hexagonal architecture. The CSV Processor will read and parse CSV files reactively, and the Kafka Publisher will send the parsed data to a Kafka topic.

#### Reactive CSV Processor

The CSV Processor will handle the reading and parsing of the CSV files. This service will be part of the domain service layer (`us.dtaylor.filewatcherservice.domain.service`).

1. **CSVProcessor.java**:
   - This class is responsible for processing CSV files and sending the parsed data to the Kafka Publisher.
   ```java
   package us.dtaylor.filewatcherservice.domain.service;

   import org.springframework.stereotype.Service;
   import reactor.core.publisher.Flux;
   import us.dtaylor.filewatcherservice.infastructure.adapter.KafkaPublisherAdapter;
   import java.nio.file.Files;
   import java.nio.file.Path;

   @Service
   public class CSVProcessor {

       private final KafkaPublisherAdapter kafkaPublisher;

       public CSVProcessor(KafkaPublisherAdapter kafkaPublisher) {
           this.kafkaPublisher = kafkaPublisher;
       }

       public void process(Path filePath) {
           Flux.using(() -> Files.lines(filePath),
                      Flux::fromStream,
                      BaseStream::close)
               .skip(1) // Assuming first line is header
               .map(this::parseLine)
               .subscribe(kafkaPublisher::publish);
       }

       private String parseLine(String line) {
           // Parse the CSV line into the desired format
           // Example: split by comma, transform data, etc.
           return line; // Placeholder, should be replaced with actual parsing logic
       }
   }
   ```

   - `Flux.using` is used to create a reactive stream from the lines of the CSV file.
   - The `skip(1)` method is used to skip the header of the CSV.
   - Each line is parsed and sent to the Kafka Publisher.

#### Kafka Publisher Adapter

The Kafka Publisher Adapter will be responsible for sending the processed data to Kafka. This adapter will be part of the infrastructure layer (`us.dtaylor.filewatcherservice.infastructure.adapter`).

2. **KafkaPublisherAdapter.java**:
   - This class uses `KafkaTemplate` to publish messages to a specified Kafka topic.
   ```java
   package us.dtaylor.filewatcherservice.infastructure.adapter;

   import org.springframework.kafka.core.KafkaTemplate;
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.stereotype.Component;

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
                        .addCallback(
                            result -> System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]"),
                            ex -> System.err.println("Unable to send message=[" + message + "] due to : " + ex.getMessage())
                        );
       }
   }
   ```

   - The `publish` method sends each processed line to the Kafka topic.
   - `addCallback` is used for simple logging of success or failure.

#### Integration and Usage

- The `CSVProcessor` is triggered by the `FileWatcherAdapter` when a new CSV file is detected.
- It reads, processes each line of the CSV file, and sends the data to the Kafka topic using the `KafkaPublisherAdapter`.
- This setup ensures that the processing of CSV files and communication with Kafka is handled reactively and efficiently, adhering to the principles of reactive programming.


### Step 4: Configuration and Application Properties for the Reactive File Watcher Service

In this step, we'll focus on setting up the necessary configurations and defining the application properties for our Reactive File Watcher Service. These configurations will mainly involve Kafka settings and file watching parameters.

#### Kafka Configuration

Setting up Kafka involves configuring the Kafka template and listener properties. This will be part of the infrastructure configuration layer (`us.dtaylor.filewatcherservice.infastructure.config`).

1. **KafkaConfig.java**:

   This configuration class will set up the Kafka template for publishing messages:

   ```java
   package us.dtaylor.filewatcherservice.infastructure.config;

   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.kafka.core.DefaultKafkaProducerFactory;
   import org.springframework.kafka.core.KafkaTemplate;
   import org.springframework.kafka.core.ProducerFactory;
   import org.springframework.kafka.support.serializer.JsonSerializer;
   import java.util.HashMap;
   import java.util.Map;
   import org.apache.kafka.clients.producer.ProducerConfig;
   import org.apache.kafka.common.serialization.StringSerializer;

   @Configuration
   public class KafkaConfig {

       @Bean
       public ProducerFactory<String, String> producerFactory() {
           Map<String, Object> configProps = new HashMap<>();
           configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
           configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
           configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
           return new DefaultKafkaProducerFactory<>(configProps);
       }

       @Bean
       public KafkaTemplate<String, String> kafkaTemplate() {
           return new KafkaTemplate<>(producerFactory());
       }
   }
   ```

   - This configuration sets up a `KafkaTemplate` with the necessary producer properties.

#### Application Properties

The `application.properties` file will include settings for Kafka and the file watching path.

2. **application.properties** (in `src/main/resources`):

   Define the necessary properties for Kafka and the directory to be watched:

   ```properties
   # Kafka Configuration
   kafka.bootstrap-servers=localhost:9092
   kafka.topic.name=file-watcher-topic

   # File Watcher Configuration
   file-watcher.directory-path=path/to/watch
   ```

   - `kafka.bootstrap-servers`: Specifies the address of your Kafka server.
   - `kafka.topic.name`: The name of the Kafka topic where messages will be published.
   - `file-watcher.directory-path`: The directory path that the File Watcher will monitor.

#### Integrating Configuration in the Service

In your main application or service components, you can inject these properties using `@Value` annotations. For instance:

- In the `FileWatcherAdapter`, inject the directory path:
  ```java
  @Value("${file-watcher.directory-path}")
  private String directoryPath;
  ```

- In the `KafkaPublisherAdapter`, inject the Kafka topic name:
  ```java
  @Value("${kafka.topic.name}")
  private String topicName;
  ```

### Step 5: Implementing Reactive Kafka Consumer in Todo Service

To integrate the Reactive File Watcher Service with the Todo Service reactively, we need to implement a reactive Kafka consumer in the Todo Service. This consumer will listen to the Kafka topic for messages (CSV file lines) published by the File Watcher Service and process them in a non-blocking manner.

#### Kafka Consumer Configuration

First, ensure that your Todo Service is configured to consume messages from Kafka. This configuration typically goes in the infrastructure layer (`us.dtaylor.todoservice.infrastructure.config`).

1. **ReactiveKafkaConsumerConfig.java**:

   This class sets up the reactive Kafka consumer configuration:

   ```java
   package us.dtaylor.todoservice.infrastructure.config;

   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.kafka.annotation.EnableKafka;
   import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
   import org.springframework.kafka.core.ConsumerFactory;
   import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
   import org.apache.kafka.clients.consumer.ConsumerConfig;
   import org.apache.kafka.common.serialization.StringDeserializer;
   import java.util.HashMap;
   import java.util.Map;

   @EnableKafka
   @Configuration
   public class ReactiveKafkaConsumerConfig {

       @Bean
       public ConsumerFactory<String, String> consumerFactory() {
           Map<String, Object> props = new HashMap<>();
           props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
           props.put(ConsumerConfig.GROUP_ID_CONFIG, "todo-group");
           props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
           props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
           return new DefaultKafkaConsumerFactory<>(props);
       }

       @Bean
       public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
           ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
           factory.setConsumerFactory(consumerFactory());
           return factory;
       }
   }
   ```

#### Implementing Reactive Kafka Consumer in Todo Service

2. **TodoServiceKafkaConsumer.java** (in `us.dtaylor.todoservice.domain.service`):

   Create a service that listens to the Kafka topic and processes the messages reactively:

   ```java
   package us.dtaylor.todoservice.domain.service;

   import org.springframework.kafka.annotation.KafkaListener;
   import org.springframework.stereotype.Service;
   import reactor.core.publisher.Mono;
   import us.dtaylor.todoservice.domain.Todo;
   import us.dtaylor.todoservice.domain.repository.ReactiveTodoRepository;

   @Service
   public class TodoServiceKafkaConsumer {

       private final ReactiveTodoRepository todoRepository;

       public TodoServiceKafkaConsumer(ReactiveTodoRepository todoRepository) {
           this.todoRepository = todoRepository;
       }

       @KafkaListener(topics = "file-watcher-topic", groupId = "todo-group")
       public void listen(String message) {
           parseMessageToTodo(message)
               .flatMap(todoRepository::save)
               .subscribe();
       }

       private Mono<Todo> parseMessageToTodo(String message) {
           // Logic to parse the message string into a Todo object
           // Example: split by comma, mapping fields, etc.
           // Return a Mono<Todo>
           return Mono.just(new Todo()); // Placeholder, replace with actual parsing logic
       }
   }
   ```

   - The `listen` method is annotated with `@KafkaListener` to consume messages from the specified Kafka topic.
   - Each message is parsed into a `Todo` object, and a reactive pipeline is created to save it to the repository.

#### Integration Test

After implementing the reactive Kafka consumer, you should test the integration:

- Ensure that your Kafka server is running and the `file-watcher-topic` is created.
- Start the File Watcher Service and drop a CSV file into the watched directory.
- Verify that the Todo Service consumes the message and processes it correctly.
