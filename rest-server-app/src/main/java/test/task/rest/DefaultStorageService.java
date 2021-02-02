package test.task.rest;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import test.task.FileContentsWrapper;
import test.task.FileRecord;
import test.task.PageableListWrapper;
import test.task.db.storage.FileRegisterService;
import test.task.exceptions.StorageFileNotFoundException;
import test.task.file.storage.FileStorageService;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.time.LocalDateTime;

@Primary
@Service
public class DefaultStorageService implements StorageService {

    private final FileRegisterService fileRegister;
    private final FileStorageService fileStorage;

    public DefaultStorageService(FileRegisterService fileRegister, FileStorageService fileStorage) {
        this.fileRegister = fileRegister;
        this.fileStorage = fileStorage;
    }

    @Override
    public PageableListWrapper<FileRecord> listFiles(int page) {
        return fileRegister.listFileRecords(page);
    }

    @Override
    @Transactional
    public FileRecord storeFile(String fileName, InputStream contents) {
        var record = fileRegister.newFileRecord(fileName, getOwner());

        record.setSize(fileStorage.write(record, contents));
        record.setChecksum(fileStorage.generateCheckSum(record));

        return fileRegister.saveFileRecord(record);
    }

    @Override
    @Transactional
    public FileRecord modifyFile(Long fileId, Integer version, InputStream contents) {
        var record = fileRegister.aquireFileRecord(fileId, version);

        record.setSize(fileStorage.write(record, contents));
        record.setChecksum(fileStorage.generateCheckSum(record));
        record.setModified(LocalDateTime.now());

        return fileRegister.saveFileRecord(record);
    }

    @Override
    public FileContentsWrapper readFile(Long fileId) {
        var record = fileRegister.getFileRecord(fileId);
        return new FileContentsWrapper(fileStorage.read(record), record.getSize(), record.getName());
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId, Integer version) {
        var record = fileRegister.aquireFileRecord(fileId, version);
        try {
            fileStorage.delete(record);
        } catch (StorageFileNotFoundException e){
            // ignore this exception in case of deletion
        }
        fileRegister.removeFileRecord(fileId);
    }

    private String getOwner() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return "unknown";
    }
}
