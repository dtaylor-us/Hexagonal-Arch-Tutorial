package us.dtaylor.filewatcher.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class TodoParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String parseLine(String line) throws JsonProcessingException {
        String[] tokens = getTokens(line);
        log.debug("Parsed line into {} tokens", tokens.length);

        if (tokens.length != 5) {
            throw new IllegalArgumentException("Invalid number of tokens");
        }

        return objectMapper.writeValueAsString(getTodo(tokens));
    }

    private static Todo getTodo(String[] tokens) {
        return new Todo(tokens[0], tokens[1], tokens[2], Boolean.parseBoolean(tokens[3]), tokens[4]);
    }

    private static String[] getTokens(String line) {
        return Arrays.stream(line.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }

    private record Todo(
            String id,
            String title,
            String description,
            boolean completed,
            String userId
    ) {}
}
