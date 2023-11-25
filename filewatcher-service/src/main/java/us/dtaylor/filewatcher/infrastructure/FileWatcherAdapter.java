package us.dtaylor.filewatcher.infrastructure;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import us.dtaylor.filewatcher.domain.FileWatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Component
public class FileWatcherAdapter implements FileWatcher {

    @Value("${filewatcher.directory.path}")
    private String dirToWatchString;
    private final CSVProcessor csvProcessor;

    public FileWatcherAdapter(CSVProcessor csvProcessor) {
        this.csvProcessor = csvProcessor;
    }

    @PostConstruct
    public void init() {
        watch();
    }

    public void watch() {
        Path targetDir = Paths.get(dirToWatchString);

        Flux.<WatchEvent<?>>create(sink -> {
                    try (WatchService watcher = targetDir.getFileSystem().newWatchService()) {
                        targetDir.register(watcher, ENTRY_CREATE);
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
                .map(targetDir::resolve)
                .subscribe(csvProcessor::process);
    }
}
