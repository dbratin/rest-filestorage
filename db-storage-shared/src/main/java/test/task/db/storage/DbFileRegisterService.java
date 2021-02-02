package test.task.db.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import test.task.FileRecord;
import test.task.PageableListWrapper;
import test.task.db.storage.model.FileRecordEntity;
import test.task.exceptions.StorageFileModifiedException;
import test.task.exceptions.StorageFileNotFoundException;

import java.time.LocalDateTime;

import static java.util.stream.Collectors.toList;

public class DbFileRegisterService implements FileRegisterService {

    private static final int PAGE_SIZE = 10;
    private final FilesRegisterRepository filesRegisterRepository;

    public DbFileRegisterService(FilesRegisterRepository filesRegisterRepository) {
        this.filesRegisterRepository = filesRegisterRepository;
    }

    @Override
    public PageableListWrapper<FileRecord> listFileRecords(int page) {
        page = page < 0 ? 0 : page;

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<FileRecordEntity> records = filesRegisterRepository.findAll(pageable);
        return new PageableListWrapper<>(records.stream().collect(toList()), page, records.getTotalPages());
    }

    @Override
    public FileRecord getFileRecord(Long fileId) {
        return filesRegisterRepository.findById(fileId).orElseThrow(StorageFileNotFoundException::new);
    }

    @Override
    public FileRecord removeFileRecord(Long fileId) {
        FileRecordEntity fileRecord = (FileRecordEntity) getFileRecord(fileId);
        filesRegisterRepository.delete(fileRecord);
        return fileRecord;
    }

    @Override
    public FileRecord removeFileRecord(FileRecord fileRecord) {
        filesRegisterRepository.delete(castToEntity(fileRecord));
        return fileRecord;
    }

    @Override
    public FileRecord newFileRecord(String fileName, String owner) {
        var record = new FileRecordEntity();
        record.setName(fileName);
        record.setSize(-1);
        record.setCreated(LocalDateTime.now());
        record.setOwner(owner);
        return filesRegisterRepository.save(record);
    }

    @Override
    public FileRecord saveFileRecord(FileRecord fileRecord) {
        return filesRegisterRepository.save(castToEntity(fileRecord));
    }

    @Override
    public FileRecord aquireFileRecord(Long fileId, Integer version) {
        var status = filesRegisterRepository.acquireFileRecord(fileId, version);
        if(status > 0)
            return getFileRecord(fileId);
        else
            throw new StorageFileModifiedException();
    }

    private FileRecordEntity castToEntity(FileRecord fileRecord) {
        if(fileRecord instanceof FileRecordEntity)
            return (FileRecordEntity) fileRecord;
        else {
            var entity = new FileRecordEntity();
            entity.setId(fileRecord.getId());
            entity.setName(fileRecord.getName());
            entity.setChecksum(fileRecord.getChecksum());
            entity.setCreated(fileRecord.getCreated());
            entity.setModified(fileRecord.getModified());
            entity.setOwner(fileRecord.getOwner());
            entity.setSize(fileRecord.getSize());
            return entity;
        }
    }
}
