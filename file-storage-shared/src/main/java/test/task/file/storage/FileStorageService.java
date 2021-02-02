package test.task.file.storage;

import test.task.FileRecord;

import java.io.InputStream;

public interface FileStorageService {
    long write(FileRecord fileRecord, InputStream contents);

    InputStream read(FileRecord fileRecord);

    String generateCheckSum(FileRecord fileRecord);

    void delete(FileRecord fileRecord);
}
