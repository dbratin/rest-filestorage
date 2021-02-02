package test.task.rest;

import test.task.FileContentsWrapper;
import test.task.PageableListWrapper;
import test.task.FileRecord;

import javax.transaction.Transactional;
import java.io.InputStream;

public interface StorageService {
    PageableListWrapper<FileRecord> listFiles(int page);

    FileRecord storeFile(String fileName, InputStream contents);

    @Transactional
    FileRecord modifyFile(Long fileId, Integer version, InputStream contents);

    FileContentsWrapper readFile(Long fileId);

    void deleteFile(Long fileId, Integer version);
}
