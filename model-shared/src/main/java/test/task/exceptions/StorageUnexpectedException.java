package test.task.exceptions;

public class StorageUnexpectedException extends RuntimeException {
    public StorageUnexpectedException(Throwable throwable) {
        super(throwable);
    }
}
