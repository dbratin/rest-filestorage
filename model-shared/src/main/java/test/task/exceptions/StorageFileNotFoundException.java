package test.task.exceptions;

import java.nio.file.Path;

public class StorageFileNotFoundException extends RuntimeException {
    public StorageFileNotFoundException () {
        super();
    }

    public StorageFileNotFoundException(Path file) {
        super("FILE " + file.toString() + " does not exists");
    }
}
