Creating a shared library for MongoDB document entities in a Java-based microservices architecture involves defining the domain objects as MongoDB documents, which can then be shared across different services. This approach helps maintain a consistent data model and reduces code duplication. Let's walk through creating such a shared library.

### Step 1: Initialize the Shared Library Project

1. **Create a New Java Library Project**:
    - Use your IDE or a build tool like Maven or Gradle to create a new Java project.
    - For Maven, create a new directory, add a `pom.xml` file, and initialize the project structure.
    - For Gradle, use `gradle init` to set up a new project.

2. **Add MongoDB Dependency**:
    - Your shared library needs the MongoDB Java driver, specifically the Spring Data MongoDB dependency.
    - For Maven, add to `pom.xml`:
      ```xml
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-data-mongodb</artifactId>
      </dependency>
      ```
    - For Gradle, add to `build.gradle`:
      ```groovy
      implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
      ```

### Step 2: Define MongoDB Document Entities

1. **Create Domain Classes**:
    - Define the domain classes in this project, annotating them as MongoDB documents.
    - Example `User` document class:
      ```java
      import org.springframework.data.annotation.Id;
      import org.springframework.data.mongodb.core.mapping.Document;
 
      @Document
      public class User {
          @Id
          private String id;
          private String name;
          private String email;
 
          // Constructors, getters, setters...
      }
      ```
    - Repeat the process for other domain entities like `Todo`, etc.

### Step 3: Build and Package the Library

1. **Configure the Build Tool**:
    - Configure Maven or Gradle to package the project as a JAR file.
    - This is usually the default behavior in Maven. For Gradle, ensure you're using the `java` or `java-library` plugin.

2. **Build the JAR File**:
    - Run `mvn package` or `gradle build` to compile and package your library.

### Step 4: Publish the Library

1. **Local Maven Repository**:
    - Use `mvn install` to install the JAR to your local Maven repository for immediate access across local projects.
    - For broader access, consider publishing to a remote repository like Maven Central or a private repository.

### Step 5: Use the Library in Microservices

1. **Add the Library as a Dependency**:
    - In your microservices (such as Todo and User services), add a dependency to your shared domain library.
    - Update the `pom.xml` or `build.gradle` with the dependency to your shared library.

2. **Use the Shared Entities**:
    - You can now use the shared domain entities in your microservice applications.

### Step 6: Best Practices

1. **Careful Management**:
    - Changes to the shared library can impact all services using it. Manage changes carefully and communicate them to all stakeholders.

2. **Semantic Versioning**:
    - Use semantic versioning for your library. Increment the major version number for breaking changes.

3. **Documentation**:
    - Document the library and its entities for clarity and ease of use among team members.

### Conclusion

By creating a shared library for MongoDB document entities, you centralize the domain model definitions, ensuring consistency and reducing redundancy across services. This setup requires careful management, especially in terms of version control and documentation, to maintain smooth operations across your microservices landscape.
