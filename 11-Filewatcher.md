### Tutorial: Building a Reactive File Watcher Microservice with Spring Boot and Kafka

This tutorial guides you through creating a new microservice - a Reactive File Watcher Service. This service will watch a directory for new CSV files, process them, and publish their contents to a Kafka topic. This topic is then consumed by the Todo Service, which loads the data into the database.

#### Prerequisites

- Java 11 or newer
- Spring Boot 2.3 or newer with WebFlux
- Kafka Server running
- Maven or Gradle for dependency management

#### Step 1: Setting Up the File Watcher Microservice Project

1. **Create a New Spring Boot Project**:
    - Use [Spring Initializr](https://start.spring.io/) to generate a new project.
    - Choose Maven or Gradle, Java, and the necessary dependencies: Spring WebFlux, Spring for Apache Kafka.

2. **Add Dependencies**:
    - Add the Kafka and Lombok dependencies in your `pom.xml` or `build.gradle`.
    - Example Maven dependencies:
      ```xml
      <dependency>
          <groupId>org.springframework.kafka</groupId>
          <artifactId>spring-kafka</artifactId>
      </dependency>
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-webflux</artifactId>
      </dependency>
      <dependency>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
          <optional>true</optional>
      </dependency>
      ```

3. **Application Properties**:
    - In `src/main/resources/application.properties`, add:
      ```properties
      spring.kafka.bootstrap-servers=localhost:9092
      ```

#### Step 2: Implementing the Reactive File Watcher Service

1. **FileWatcherService.java**:
    - Create a service to watch a directory for new CSV files:
      ```java
      @Service
      public class FileWatcherService {
 
          private final Path dirToWatch = Paths.get("path/to/watch");
          private final CSVProcessor csvProcessor;
 
          @Autowired
          public FileWatcherService(CSVProcessor csvProcessor) {
              this.csvProcessor = csvProcessor;
              watchDirectory();
          }
 
          private void watchDirectory() {
              Flux.create(sink -> {
                  try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                      dirToWatch.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
                      while (true) {
                          WatchKey key = watcher.take();
                          key.pollEvents().stream()
                             .filter(event -> event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                             .map(event -> (Path) event.context())
                             .forEach(sink::next);
                          key.reset();
                      }
                  } catch (IOException | InterruptedException e) {
                      sink.error(e);
                  }
              }).subscribe(csvProcessor::process);
          }
      }
      ```

2. **CSVProcessor.java**:
    - Create a service to process CSV files and publish to Kafka:
      ```java
      @Service
      public class CSVProcessor {
 
          private final KafkaProducerService kafkaProducer;
 
          @Autowired
          public CSVProcessor(KafkaProducerService kafkaProducer) {
              this.kafkaProducer = kafkaProducer;
          }
 
          public void process(Path csvPath) {
              // Implement CSV reading logic and convert to domain objects
              // For each record, send it to Kafka
              kafkaProducer.send(record);
          }
      }
      ```

#### Step 3: Kafka Producer Configuration

1. **KafkaProducerService.java**:
    - Create a service to send messages to Kafka:
      ```java
      @Service
      public class KafkaProducerService {
 
          private final KafkaTemplate<String, String> kafkaTemplate;
 
          @Autowired
          public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
              this.kafkaTemplate = kafkaTemplate;
          }
 
          public void send(String message) {
              kafkaTemplate.send("todo-topic", message);
          }
      }
      ```

#### Step 4: Integrating with Todo Service

1. **Update Todo Service**:
    - Ensure the Todo service has a Kafka consumer configured to listen to the `"todo-topic"` topic.
    - Process and save the incoming data to the database.

#### Step 5: Running the Services

1. **Run File Watcher Service**:
    - Start the File Watcher microservice. It will monitor the specified directory and react to new CSV files.

2. **Run Todo Service**:
    - Ensure the Todo service is running and listening to the Kafka topic.

#### Step 6: Testing the Integration

1. **Place a CSV File in the Watched Directory**:
    - The File Watcher Service should detect the file, process it, and publish the contents to Kafka.

2. **Verify Todo Service Consumption**:
    - The Todo service should consume the message from Kafka and

perform the required operations (like saving the data to the database).
