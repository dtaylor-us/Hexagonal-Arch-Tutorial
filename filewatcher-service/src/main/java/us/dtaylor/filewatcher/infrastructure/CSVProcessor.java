package us.dtaylor.filewatcher.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class CSVProcessor {

    private final KafkaPublisherAdapter kafkaPublisher;

    private final TodoParser todoParser;

    public CSVProcessor(KafkaPublisherAdapter kafkaPublisher, TodoParser todoParser) {
        this.kafkaPublisher = kafkaPublisher;
        this.todoParser = todoParser;
    }

    public void process(Path filePath) {
        AtomicInteger lineNumber = new AtomicInteger(1);
        getDataBufferFlux(filePath)
                .flatMap(CSVProcessor::convertDataBufferToLineFlux)
                .skip(1)
                .flatMap(line -> parseLineOrEmpty(line, lineNumber.getAndIncrement()))
                .subscribe(kafkaPublisher::publish);
    }

    private static Flux<DataBuffer> getDataBufferFlux(Path filePath) {
        return DataBufferUtils.read(filePath, new DefaultDataBufferFactory(), 4096, StandardOpenOption.READ);
    }

    private static Flux<String> convertDataBufferToLineFlux(DataBuffer dataBuffer) {
        // convert DataBuffer to String
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer); // Important to avoid memory leaks
        String content = new String(bytes, StandardCharsets.UTF_8);
        return Flux.fromArray(content.split("\n"));
    }

    private Mono<String> parseLineOrEmpty(String line, int lineNumber) {
        try {
            return Mono.just(todoParser.parseLine(line));
        } catch (Exception e) {
            log.error("Error processing line " + lineNumber + ": " + e.getMessage());
            return Mono.empty();
        }
    }

}
