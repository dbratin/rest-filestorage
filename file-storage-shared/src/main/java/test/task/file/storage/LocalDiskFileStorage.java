package test.task.file.storage;

import test.task.FileRecord;
import test.task.exceptions.StorageFileNotFoundException;
import test.task.exceptions.StorageUnexpectedException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class LocalDiskFileStorage implements FileStorageService {

    public static final int READ_CHUNK_LEN = 1049000;
    private final Path rootPath;

    public LocalDiskFileStorage(Path rootPath) {
        if(Files.exists(rootPath) && !Files.isDirectory(rootPath))
            throw new IllegalArgumentException(rootPath.toString() + " should be a directory");

        if(!Files.exists(rootPath)) {
            try {
                Files.createDirectories(rootPath);
            } catch (IOException e) {
                throw new StorageUnexpectedException(e);
            }
        }

        this.rootPath = rootPath;
    }

    @Override
    public long write(FileRecord fileRecord, InputStream contents) {
        try {
            return Files.copy(contents, rootPath.resolve(toStorageFileName(fileRecord)), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageUnexpectedException(e);
        }
    }

    private String toStorageFileName(FileRecord fileRecord) {
        return String.format("%015x.filestorage", fileRecord.getId());
    }

    @Override
    public InputStream read(FileRecord fileRecord) {
        try {
            Path file = rootPath.resolve(toStorageFileName(fileRecord));

            if(!Files.exists(file))
                throw new StorageFileNotFoundException(file);

            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new StorageUnexpectedException(e);
        }
    }

    @Override
    public String generateCheckSum(FileRecord record) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try(var is = read(record)) {
                while(true) {
                    byte[] chunk = is.readNBytes(READ_CHUNK_LEN);
                    if(chunk.length > 0)
                        digest.update(chunk);
                    if(chunk.length < READ_CHUNK_LEN) break;
                }
            }
            byte[] checksum = digest.digest();
            return new String(Base64.getEncoder().encode(checksum));
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new StorageUnexpectedException(e);
        }
    }

    @Override
    public void delete(FileRecord fileRecord) {
        try {
            Path file = rootPath.resolve(toStorageFileName(fileRecord));

            if(!Files.exists(file))
                throw new StorageFileNotFoundException(file);

            Files.delete(file);
        } catch (IOException e) {
            throw new StorageUnexpectedException(e);
        }
    }
}
