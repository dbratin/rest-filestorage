package test.task.db.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import test.task.db.storage.model.FileRecordEntity;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ContextConfiguration(classes = {FilesRegisterRepositoryTest.TestConfiguration.class})
@RunWith(SpringRunner.class)
public class FilesRegisterRepositoryTest {

    @Configuration
    @PropertySource("classpath:application.properties")
    @EnableAutoConfiguration
    public static class TestConfiguration {

    }

    @Autowired
    private FilesRegisterRepository filesRegister;

    @Test
    @Transactional
    public void crudTest() {
        var fileEntity = new FileRecordEntity();
        fileEntity.setName("abcdef.txt");
        fileEntity.setChecksum("abcdef");
        fileEntity.setOwner("me");
        fileEntity.setSize(1024);
        var persistedFileEntity = filesRegister.save(fileEntity);

        assertThat(persistedFileEntity.getId(), not(nullValue()));

        var foundFileEntity = filesRegister.findById(persistedFileEntity.getId())
                .orElseThrow(() -> new AssertionError("Not found"));

        assertThat(foundFileEntity.getId(), equalTo(persistedFileEntity.getId()));
        assertThat(foundFileEntity.getName(), equalTo(fileEntity.getName()));
        assertThat(foundFileEntity.getChecksum(), equalTo(fileEntity.getChecksum()));
        assertThat(foundFileEntity.getOwner(), equalTo(fileEntity.getOwner()));
        assertThat(foundFileEntity.getModified(), nullValue());

        persistedFileEntity.setName("ghijk.txt");
        persistedFileEntity.setModified(LocalDateTime.now());
        filesRegister.save(persistedFileEntity);

        var updatedFileEntity = filesRegister.findById(persistedFileEntity.getId())
                .orElseThrow(() -> new AssertionError("Not found"));

        assertThat(updatedFileEntity.getId(), equalTo(persistedFileEntity.getId()));
        assertThat(updatedFileEntity.getName(), equalTo("ghijk.txt"));
        assertThat(updatedFileEntity.getChecksum(), equalTo(fileEntity.getChecksum()));
        assertThat(updatedFileEntity.getOwner(), equalTo(fileEntity.getOwner()));
        assertThat(updatedFileEntity.getModified(), not(nullValue()));

        assertThat(filesRegister.acquireFileRecord(updatedFileEntity.getId(), updatedFileEntity.getVersion()), equalTo(1));
        assertThat(filesRegister.acquireFileRecord(updatedFileEntity.getId(), updatedFileEntity.getVersion()), equalTo(0));

        filesRegister.delete(persistedFileEntity);

        assertThat(filesRegister.findById(persistedFileEntity.getId()).orElse(null), nullValue());
    }
}
