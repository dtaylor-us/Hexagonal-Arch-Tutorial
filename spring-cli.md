
### Spring CLI

#### What is Spring CLI?

The Spring Command Line Interface (CLI) is a tool that allows for quick generation and running of Spring applications. It leverages the Spring Initializr web service to create project templates.

#### Installation

- **Download & Install**: You can download the Spring CLI from the [Spring website](https://spring.io/tools). Follow the instructions for your operating system to install it.
- **Verify Installation**: Run `spring --version` in your command line to verify that it is correctly installed.

#### Creating a Spring Boot Project

- **Basic Usage**: The basic syntax for creating a new project is:

  ```
  spring init [options] <project-name>
  ```

- **Options**: You can customize your project with various options such as `--dependencies`, `--build`, `--java-version`, etc. These options allow you to specify the project's dependencies, build system (Maven or Gradle), Java version, and more.

#### Generating Your Project

Here's how you can generate the `filewatcher-service` project with specific requirements:

1. **Open Command Line**: Launch your command line tool.

2. **Run the Command**:

   ```
   spring init --dependencies=data-mongodb-reactive,webflux,lombok,kafka --java-version=17 --build=gradle --groupId=us.dtaylor --artifactId=filewatcher-service --name=filewatcher-service --packageName=us.dtaylor.filewatcher --type=gradle-project filewatcher-service
   ```

   This command will create a Spring Boot project with the following specifications:
    - Dependencies: Spring Data MongoDB Reactive, Spring WebFlux, Lombok, and Kafka.
    - Java Version: 17.
    - Build System: Gradle.
    - Group ID: `us.dtaylor`.
    - Artifact ID: `filewatcher-service`.
    - Project Name: `filewatcher-service`.
    - Package Name: `us.dtaylor.filewatcher`.
    - Project Type: Gradle project (Java).

3. **Project Structure**: After running the command, a directory named `filewatcher-service` will be created with a basic project structure, including a Gradle build file and source directories.

4. **Customization**: You can then navigate to the project directory and modify the `build.gradle` file to include additional plugins, dependencies, and configurations as per your requirement.

5. **Build & Run**: Use Gradle commands like `gradle build` and `gradle bootRun` to build and run your application.

Remember, the Spring CLI is a powerful tool to kickstart your Spring Boot projects, but for detailed configurations and dependencies, manual editing of build files and source code is often necessary.
