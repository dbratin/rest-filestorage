package test.task.db.storage;

import test.task.PageableListWrapper;
import test.task.FileRecord;

public interface FileRegisterService {
    PageableListWrapper<FileRecord> listFileRecords(int page);

    FileRecord getFileRecord(Long fileId);

    FileRecord removeFileRecord(Long fileId);

    FileRecord removeFileRecord(FileRecord fileRecord);

    FileRecord newFileRecord(String fileName, String owner);

    FileRecord saveFileRecord(FileRecord fileRecord);

    FileRecord aquireFileRecord(Long fileId, Integer version);
}
