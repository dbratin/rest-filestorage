package test.task.rest.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import test.task.db.storage.DbFileRegisterService;
import test.task.db.storage.FileRegisterService;
import test.task.db.storage.FilesRegisterRepository;
import test.task.file.storage.FileStorageService;
import test.task.file.storage.LocalDiskFileStorage;

import java.nio.file.Path;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("test.task.db.storage")
@EntityScan("test.task.db.storage.model")
public class ServiceConfig {

    @Value("${file.storage.rootPath:./storage}")
    private String fileStorageRoot;

    @Bean
    public FileRegisterService fileRegisterService(FilesRegisterRepository filesRegisterRepository) {
        return new DbFileRegisterService(filesRegisterRepository);
    }

    @Bean
    public FileStorageService fileStorageService() {
        return new LocalDiskFileStorage(Path.of(fileStorageRoot));
    }
}
