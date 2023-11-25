package us.dtaylor.filewatcher.domain;

import java.nio.file.Path;

public interface FileProcessor {
    void process(Path filePath);
}
